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
import sdtracker.database.BugStatusDbServiceManager;
import sdtracker.database.BugStatusDbServiceManager.*;
import sdtracker.model.BugStatus;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class BugStatusFormController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private TextField nameTextField;
    @FXML private Label nameErrorLabel;
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    @FXML private Label systemMessageLabel;
    
    private FormMode formMode = FormMode.INSERT;
    private FormResult formResult;
    private BugStatusDbServiceManager bugStatusDbServiceManager = BugStatusDbServiceManager.getServiceManager();
    private InsertBugStatusService insertBugStatusService;
    private UpdateBugStatusService updateBugStatusService;
    private CheckForDuplicateBugStatusService checkForDuplicateBugStatusService;
    private BugStatus bugStatus;
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
        insertBugStatusService = bugStatusDbServiceManager.new InsertBugStatusService();
        updateBugStatusService = bugStatusDbServiceManager.new UpdateBugStatusService();
        checkForDuplicateBugStatusService = bugStatusDbServiceManager.new CheckForDuplicateBugStatusService();
        
        insertBugStatusService.setOnSucceeded(insertBugStatusSuccess);
        insertBugStatusService.setOnFailed(insertBugStatusFailure);
        
        updateBugStatusService.setOnSucceeded(updateBugStatusSuccess);
        updateBugStatusService.setOnFailed(updateBugStatusFailure);
        
        checkForDuplicateBugStatusService.setOnSucceeded(checkForDuplicateBugStatusSuccess);
        checkForDuplicateBugStatusService.setOnFailed(checkForDuplicateBugStatusFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = insertBugStatusService.runningProperty()
                                     .or(updateBugStatusService.runningProperty())
                                     .or(checkForDuplicateBugStatusService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            nameTextField.setText(bugStatus.getName());
            titleLabel.setText("Update Bug Status");
            addSaveButton.setText("Save");
        } else {
            bugStatus = new BugStatus();
            titleLabel.setText("Add Bug Status");
            addSaveButton.setText("Add");
        }
    }
    
    private void setCurrentStage() {
        currentStage = (Stage) titleLabel.getScene().getWindow();
    }
    
    private void buildBugStatus() {
        bugStatus.setName(nameTextField.getText());
        if (formMode.equals(FormMode.INSERT)) {
            runCheckForDuplicateBugStatusService();
        } else {
            runUpdateBugStatusService();
        }
    }
    
    // Service run handlers
    private void runInsertBugStatusService() {
        if (!insertBugStatusService.isRunning()) {
            insertBugStatusService.reset();
            insertBugStatusService.setBugStatus(bugStatus);
            insertBugStatusService.start();
        }
    }
    
    private void runUpdateBugStatusService() {
        if (!updateBugStatusService.isRunning()) {
            updateBugStatusService.reset();
            updateBugStatusService.setBugStatus(bugStatus);
            updateBugStatusService.start();
        }
    }
    
    private void runCheckForDuplicateBugStatusService() {
        if (!checkForDuplicateBugStatusService.isRunning()) {
            checkForDuplicateBugStatusService.reset();
            checkForDuplicateBugStatusService.setName(bugStatus.getName());
            checkForDuplicateBugStatusService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> insertBugStatusSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Bug Status " 
                + bugStatus.getName()
                + "was successfully added.");
        System.out.println("Insert successful");
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> insertBugStatusFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");

    };
    
    private EventHandler<WorkerStateEvent> updateBugStatusSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Bug Status " 
                + bugStatus.getName()
                + "was successfully changed.");
        System.out.println("Update successful");
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> updateBugStatusFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateBugStatusSuccess = (event) -> {
        if (checkForDuplicateBugStatusService.getValue()) {
            systemMessageLabel.setText("Bug Status already exists");
            systemMessageLabel.getStyleClass().removeAll("system-message-label");
            systemMessageLabel.getStyleClass().add("system-message-label-error");
        } else {
            runInsertBugStatusService();
        }
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateBugStatusFailure = (event) -> {
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
            buildBugStatus();
        }
    }
    
    public void setBugStatus(BugStatus bugStatus) {
        this.bugStatus = bugStatus;
        formMode = FormMode.UPDATE;
    }
    
    public FormResult getFormResult() {
        return formResult;
    }
    
    private boolean validateName() {
        String input = nameTextField.getText();
        if (input.isEmpty()) {
            nameErrorLabel.setText("Bug Status name required");
            return false;
        }
        
        if (input.length() > 25) {
            nameErrorLabel.setText("Bug Status name must be less than 25 characters");
            return false;
        }
        
        nameErrorLabel.setText("");
        return true;
    }
    
}
