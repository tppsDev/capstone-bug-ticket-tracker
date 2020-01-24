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
import sdtracker.database.ContactTypeDbServiceManager;
import sdtracker.database.ContactTypeDbServiceManager.*;
import sdtracker.model.ContactType;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class ContactTypeFormController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private TextField nameTextField;
    @FXML private Label nameErrorLabel;
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    @FXML private Label systemMessageLabel;
    
    private FormMode formMode = FormMode.INSERT;
    private FormResult formResult;
    private ContactTypeDbServiceManager contactTypeDbServiceManager = ContactTypeDbServiceManager.getServiceManager();
    private InsertContactTypeService insertContactTypeService;
    private UpdateContactTypeService updateContactTypeService;
    private CheckForDuplicateContactTypeService checkForDuplicateContactTypeService;
    private ContactType contactType;
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
        insertContactTypeService = contactTypeDbServiceManager.new InsertContactTypeService();
        updateContactTypeService = contactTypeDbServiceManager.new UpdateContactTypeService();
        checkForDuplicateContactTypeService = contactTypeDbServiceManager.new CheckForDuplicateContactTypeService();
        
        insertContactTypeService.setOnSucceeded(insertContactTypeSuccess);
        insertContactTypeService.setOnFailed(insertContactTypeFailure);
        
        updateContactTypeService.setOnSucceeded(updateContactTypeSuccess);
        updateContactTypeService.setOnFailed(updateContactTypeFailure);
        
        checkForDuplicateContactTypeService.setOnSucceeded(checkForDuplicateContactTypeSuccess);
        checkForDuplicateContactTypeService.setOnFailed(checkForDuplicateContactTypeFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = insertContactTypeService.runningProperty()
                                     .or(updateContactTypeService.runningProperty())
                                     .or(checkForDuplicateContactTypeService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            nameTextField.setText(contactType.getName());
            titleLabel.setText("Update Contact Type");
            addSaveButton.setText("Save");
        } else {
            contactType = new ContactType();
            titleLabel.setText("Add Contact Type");
            addSaveButton.setText("Add");
        }
    }
    
    private void setCurrentStage() {
        currentStage = (Stage) titleLabel.getScene().getWindow();
    }
    
    private void buildContactType() {
        contactType.setName(nameTextField.getText());
        if (formMode.equals(FormMode.INSERT)) {
            runCheckForDuplicateContactTypeService();
        } else {
            runUpdateContactTypeService();
        }
    }
    
    // Service run handlers
    private void runInsertContactTypeService() {
        if (!insertContactTypeService.isRunning()) {
            insertContactTypeService.reset();
            insertContactTypeService.setContactType(contactType);
            insertContactTypeService.start();
        }
    }
    
    private void runUpdateContactTypeService() {
        if (!updateContactTypeService.isRunning()) {
            updateContactTypeService.reset();
            updateContactTypeService.setContactType(contactType);
            updateContactTypeService.start();
        }
    }
    
    private void runCheckForDuplicateContactTypeService() {
        if (!checkForDuplicateContactTypeService.isRunning()) {
            checkForDuplicateContactTypeService.reset();
            checkForDuplicateContactTypeService.setName(contactType.getName());
            checkForDuplicateContactTypeService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> insertContactTypeSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Contact Type " 
                + contactType.getName()
                + "was successfully added.");
        System.out.println("Insert successful");
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> insertContactTypeFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");

    };
    
    private EventHandler<WorkerStateEvent> updateContactTypeSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Contact Type " 
                + contactType.getName()
                + "was successfully changed.");
        System.out.println("Update successful");
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> updateContactTypeFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateContactTypeSuccess = (event) -> {
        if (checkForDuplicateContactTypeService.getValue()) {
            systemMessageLabel.setText("Contact Type already exists");
            systemMessageLabel.getStyleClass().removeAll("system-message-label");
            systemMessageLabel.getStyleClass().add("system-message-label-error");
        } else {
            runInsertContactTypeService();
        }
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateContactTypeFailure = (event) -> {
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
        if (validateName()) {
            buildContactType();
        }
    }
    
    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
        formMode = FormMode.UPDATE;
    }
    
    public FormResult getFormResult() {
        return formResult;
    }
    
    private boolean validateName() {
        String input = nameTextField.getText();
        if (input.isEmpty()) {
            nameErrorLabel.setText("Contact Type name required");
            return false;
        }
        
        if (input.length() > 25) {
            nameErrorLabel.setText("Contact Type name must be less than 25 characters");
            return false;
        }
        
        nameErrorLabel.setText("");
        return true;
    }
}
