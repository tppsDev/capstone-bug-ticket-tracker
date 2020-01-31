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
import sdtracker.database.AssetTypeDbServiceManager;
import sdtracker.database.AssetTypeDbServiceManager.*;

import sdtracker.model.AssetType;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class AssetTypeFormController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private TextField nameTextField;
    @FXML private Label nameErrorLabel;
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    @FXML private Label systemMessageLabel;
    
    private FormMode formMode = FormMode.INSERT;
    private FormResult formResult;
    private AssetTypeDbServiceManager assetTypeDbServiceManager = AssetTypeDbServiceManager.getServiceManager();
    private InsertAssetTypeService insertAssetTypeService;
    private UpdateAssetTypeService updateAssetTypeService;
    private CheckForDuplicateAssetTypeService checkForDuplicateAssetTypeService;
    private AssetType assetType;
    private Stage currentStage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeServices();
        establishBindings();
        applyFormMode();
    }
    
    private void initializeServices() {
        insertAssetTypeService = assetTypeDbServiceManager.new InsertAssetTypeService();
        updateAssetTypeService = assetTypeDbServiceManager.new UpdateAssetTypeService();
        checkForDuplicateAssetTypeService = assetTypeDbServiceManager.new CheckForDuplicateAssetTypeService();
        
        insertAssetTypeService.setOnSucceeded(insertAssetTypeSuccess);
        insertAssetTypeService.setOnFailed(insertAssetTypeFailure);
        
        updateAssetTypeService.setOnSucceeded(updateAssetTypeSuccess);
        updateAssetTypeService.setOnFailed(updateAssetTypeFailure);
        
        checkForDuplicateAssetTypeService.setOnSucceeded(checkForDuplicateAssetTypeSuccess);
        checkForDuplicateAssetTypeService.setOnFailed(checkForDuplicateAssetTypeFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = insertAssetTypeService.runningProperty()
                                     .or(updateAssetTypeService.runningProperty())
                                     .or(checkForDuplicateAssetTypeService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            nameTextField.setText(assetType.getName());
            titleLabel.setText("Update Asset Type");
            addSaveButton.setText("Save");
        } else {
            assetType = new AssetType();
            titleLabel.setText("Add Asset Type");
            addSaveButton.setText("Add");
        }
    }
    
    private void closeWindow() {
        currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.close();
    }
    
    private void buildAssetType() {
        assetType.setName(nameTextField.getText());
        if (formMode.equals(FormMode.INSERT)) {
            runCheckForDuplicateAssetTypeService();
        } else {
            runUpdateAssetTypeService();
        }
    }
    
    // Service run handlers
    private void runInsertAssetTypeService() {
        if (!insertAssetTypeService.isRunning()) {
            insertAssetTypeService.reset();
            insertAssetTypeService.setAssetType(assetType);
            insertAssetTypeService.start();
        }
    }
    
    private void runUpdateAssetTypeService() {
        if (!updateAssetTypeService.isRunning()) {
            updateAssetTypeService.reset();
            updateAssetTypeService.setAssetType(assetType);
            updateAssetTypeService.start();
        }
    }
    
    private void runCheckForDuplicateAssetTypeService() {
        if (!checkForDuplicateAssetTypeService.isRunning()) {
            checkForDuplicateAssetTypeService.reset();
            checkForDuplicateAssetTypeService.setAssetTypeName(assetType.getName());
            checkForDuplicateAssetTypeService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> insertAssetTypeSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Asset Type " 
                + assetType.getName()
                + "was successfully added.");
        System.out.println("Insert successful");
        closeWindow();
    };
    
    private EventHandler<WorkerStateEvent> insertAssetTypeFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");

    };
    
    private EventHandler<WorkerStateEvent> updateAssetTypeSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Asset Type " 
                + assetType.getName()
                + "was successfully changed.");
        System.out.println("Update successful");
        closeWindow();
    };
    
    private EventHandler<WorkerStateEvent> updateAssetTypeFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateAssetTypeSuccess = (event) -> {
        if (checkForDuplicateAssetTypeService.getValue()) {
            systemMessageLabel.setText("Asset Type already exists");
            systemMessageLabel.getStyleClass().removeAll("system-message-label");
            systemMessageLabel.getStyleClass().add("system-message-label-error");
        } else {
            runInsertAssetTypeService();
        }
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateAssetTypeFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    @FXML
    private void handleCancelButton(ActionEvent event) {
        formResult = new FormResult(FormResult.FormResultStatus.FAILURE, "Action cancelled by user");
        closeWindow();
    }
    
    @FXML
    private void handleAddSaveButton() {
        if (validateName()) {
            buildAssetType();
        }
    }
    
    public void specifyUpdateMode(AssetType assetType) {
        this.assetType = assetType;
        formMode = FormMode.UPDATE;
        applyFormMode();
    }
    
    public FormResult getFormResult() {
        return formResult;
    }
    
    private boolean validateName() {
        String input = nameTextField.getText();
        if (input.isEmpty()) {
            nameErrorLabel.setText("Asset Type name required");
            return false;
        }
        
        if (input.length() > 50) {
            nameErrorLabel.setText("Asset Type name must be less than 50 characters");
            return false;
        }
        
        nameErrorLabel.setText("");
        return true;
    }
}
