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
import sdtracker.database.BugPriorityDbServiceManager;
import sdtracker.database.BugPriorityDbServiceManager.*;
import sdtracker.model.BugPriority;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class BugPriorityFormController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private TextField nameTextField;
    @FXML private Label nameErrorLabel;
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    @FXML private Label systemMessageLabel;
    
    private FormMode formMode = FormMode.INSERT;
    private FormResult formResult;
    private BugPriorityDbServiceManager bugPriorityDbServiceManager = BugPriorityDbServiceManager.getServiceManager();
    private InsertBugPriorityService insertBugPriorityService;
    private UpdateBugPriorityService updateBugPriorityService;
    private CheckForDuplicateBugPriorityService checkForDuplicateBugPriorityService;
    private BugPriority bugPriority;
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
        insertBugPriorityService = bugPriorityDbServiceManager.new InsertBugPriorityService();
        updateBugPriorityService = bugPriorityDbServiceManager.new UpdateBugPriorityService();
        checkForDuplicateBugPriorityService = bugPriorityDbServiceManager.new CheckForDuplicateBugPriorityService();
        
        insertBugPriorityService.setOnSucceeded(insertBugPrioritySuccess);
        insertBugPriorityService.setOnFailed(insertBugPriorityFailure);
        
        updateBugPriorityService.setOnSucceeded(updateBugPrioritySuccess);
        updateBugPriorityService.setOnFailed(updateBugPriorityFailure);
        
        checkForDuplicateBugPriorityService.setOnSucceeded(checkForDuplicateBugPrioritySuccess);
        checkForDuplicateBugPriorityService.setOnFailed(checkForDuplicateBugPriorityFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = insertBugPriorityService.runningProperty()
                                     .or(updateBugPriorityService.runningProperty())
                                     .or(checkForDuplicateBugPriorityService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            nameTextField.setText(bugPriority.getName());
            titleLabel.setText("Update Bug Priority");
            addSaveButton.setText("Save");
        } else {
            bugPriority = new BugPriority();
            titleLabel.setText("Add Bug Priority");
            addSaveButton.setText("Add");
        }
    }
    
    private void setCurrentStage() {
        currentStage = (Stage) titleLabel.getScene().getWindow();
    }
    
    private void buildBugPriority() {
        bugPriority.setName(nameTextField.getText());
        if (formMode.equals(FormMode.INSERT)) {
            runCheckForDuplicateBugPriorityService();
        } else {
            runUpdateBugPriorityService();
        }
    }
    
    // Service run handlers
    private void runInsertBugPriorityService() {
        if (!insertBugPriorityService.isRunning()) {
            insertBugPriorityService.reset();
            insertBugPriorityService.setBugPriority(bugPriority);
            insertBugPriorityService.start();
        }
    }
    
    private void runUpdateBugPriorityService() {
        if (!updateBugPriorityService.isRunning()) {
            updateBugPriorityService.reset();
            updateBugPriorityService.setBugPriority(bugPriority);
            updateBugPriorityService.start();
        }
    }
    
    private void runCheckForDuplicateBugPriorityService() {
        if (!checkForDuplicateBugPriorityService.isRunning()) {
            checkForDuplicateBugPriorityService.reset();
            checkForDuplicateBugPriorityService.setName(bugPriority.getName());
            checkForDuplicateBugPriorityService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> insertBugPrioritySuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Bug Priority " 
                + bugPriority.getName()
                + "was successfully added.");
        System.out.println("Insert successful");
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> insertBugPriorityFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");

    };
    
    private EventHandler<WorkerStateEvent> updateBugPrioritySuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Bug Priority " 
                + bugPriority.getName()
                + "was successfully changed.");
        System.out.println("Update successful");
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> updateBugPriorityFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateBugPrioritySuccess = (event) -> {
        if (checkForDuplicateBugPriorityService.getValue()) {
            systemMessageLabel.setText("Bug Priority already exists");
            systemMessageLabel.getStyleClass().removeAll("system-message-label");
            systemMessageLabel.getStyleClass().add("system-message-label-error");
        } else {
            runInsertBugPriorityService();
        }
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateBugPriorityFailure = (event) -> {
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
            buildBugPriority();
        }
    }
    
    public void setBugPriority(BugPriority bugPriority) {
        this.bugPriority = bugPriority;
        formMode = FormMode.UPDATE;
    }
    
    public FormResult getFormResult() {
        return formResult;
    }
    
    private boolean validateName() {
        String input = nameTextField.getText();
        if (input.isEmpty()) {
            nameErrorLabel.setText("Bug Priority name required");
            return false;
        }
        
        if (input.length() > 25) {
            nameErrorLabel.setText("Bug Priority name must be less than 25 characters");
            return false;
        }
        
        nameErrorLabel.setText("");
        return true;
    }
}
