/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sdtracker.database.ProductDbServiceManager;
import sdtracker.database.ProductDbServiceManager.CheckForDuplicateProductService;
import sdtracker.database.ProductDbServiceManager.InsertProductService;
import sdtracker.database.ProductDbServiceManager.UpdateProductService;
import sdtracker.model.Product;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class ProductFormController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private TextField nameTextField;
    @FXML private Label nameErrorLabel;
    @FXML private TextField versionTextField;
    @FXML private Label versionErrorLabel;
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    @FXML private Label systemMessageLabel;
    
    private FormMode formMode = FormMode.INSERT;
    private FormResult formResult;
    private ProductDbServiceManager productDbServiceManager = ProductDbServiceManager.getServiceManager();
    private InsertProductService insertProductService;
    private UpdateProductService updateProductService;
    private CheckForDuplicateProductService checkForDuplicateProductService;
    private Product product;
    private Stage currentStage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeServices();
        establishBindings();
        applyFormMode();
        setCurrentStage();
    }
    
    private void initializeServices() {
        insertProductService = productDbServiceManager.new InsertProductService();
        updateProductService = productDbServiceManager.new UpdateProductService();
        checkForDuplicateProductService = productDbServiceManager.new CheckForDuplicateProductService();
        
        insertProductService.setOnSucceeded(insertProductSuccess);
        insertProductService.setOnFailed(insertProductFailure);
        
        updateProductService.setOnSucceeded(updateProductSuccess);
        updateProductService.setOnFailed(updateProductFailure);
        
        checkForDuplicateProductService.setOnSucceeded(checkForDuplicateProductSuccess);
        checkForDuplicateProductService.setOnFailed(checkForDuplicateProductFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = insertProductService.runningProperty()
                                     .or(updateProductService.runningProperty())
                                     .or(checkForDuplicateProductService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            nameTextField.setText(product.getName());
            versionTextField.setText(product.getVersion());
            titleLabel.setText("Update Product");
            addSaveButton.setText("Save");
        } else {
            product = new Product();
            titleLabel.setText("Add Product");
            addSaveButton.setText("Add");
        }
    }
    
    private void setCurrentStage() {
        currentStage = (Stage) titleLabel.getScene().getWindow();
    }
    
    private void buildProduct() {
        product.setName(nameTextField.getText());
        product.setVersion(versionTextField.getText());
        if (formMode.equals(FormMode.INSERT)) {
            runCheckForDuplicateProductService();
        } else {
            runUpdateProductService();
        }
    }
    
    // Service run handlers
    private void runInsertProductService() {
        if (!insertProductService.isRunning()) {
            insertProductService.reset();
            insertProductService.setProduct(product);
            insertProductService.start();
        }
    }
    
    private void runUpdateProductService() {
        if (!updateProductService.isRunning()) {
            updateProductService.reset();
            updateProductService.setProduct(product);
            updateProductService.start();
        }
    }
    
    private void runCheckForDuplicateProductService() {
        if (!checkForDuplicateProductService.isRunning()) {
            checkForDuplicateProductService.reset();
            checkForDuplicateProductService.setName(product.getName());
            checkForDuplicateProductService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> insertProductSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Product " 
                + product.getName()
                + "was successfully added.");
        System.out.println("Insert successful");
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> insertProductFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");

    };
    
    private EventHandler<WorkerStateEvent> updateProductSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Product " 
                + product.getName()
                + "was successfully changed.");
        System.out.println("Update successful");
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> updateProductFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateProductSuccess = (event) -> {
        if (checkForDuplicateProductService.getValue()) {
            systemMessageLabel.setText("Product already exists");
            systemMessageLabel.getStyleClass().removeAll("system-message-label");
            systemMessageLabel.getStyleClass().add("system-message-label-error");
        } else {
            runInsertProductService();
        }
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateProductFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    @FXML
    private void handleCancelButton(ActionEvent event) {
        // TODO warning
        currentStage.close();
    }
    
    @FXML
    private void handleAddSaveButton() {
        if (validateAll()) {
            buildProduct();
        }
    }
    
    public void setProduct(Product product) {
        this.product = product;
        formMode = FormMode.UPDATE;
    }
    
    public FormResult getFormResult() {
        return formResult;
    }
    
    private boolean validateAll() {
        return validateName()
            && validateVersion();
    }
    
    private boolean validateName() {
        String input = nameTextField.getText();
        if (input.isEmpty()) {
            nameErrorLabel.setText("Product name required");
            return false;
        }
        
        if (input.length() > 50) {
            nameErrorLabel.setText("Product name must be less than 50 characters");
            return false;
        }
        
        nameErrorLabel.setText("");
        return true;
    }
    
    private boolean validateVersion() {
        String input = nameTextField.getText();

        if (!input.isEmpty() && input.length() > 50) {
            nameErrorLabel.setText("Product name must be less than 50 characters");
            return false;
        }
        
        nameErrorLabel.setText("");
        return true;
    }
    
}
