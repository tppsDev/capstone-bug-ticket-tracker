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
import sdtracker.database.DepartmentDbServiceManager;
import sdtracker.database.DepartmentDbServiceManager.*;
import sdtracker.model.Department;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class DepartmentFormController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private TextField nameTextField;
    @FXML private Label nameErrorLabel;
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    @FXML private Label systemMessageLabel;
    
    private FormMode formMode = FormMode.INSERT;
    private FormResult formResult;
    private DepartmentDbServiceManager departmentDbServiceManager = DepartmentDbServiceManager.getServiceManager();
    private InsertDepartmentService insertDepartmentService;
    private UpdateDepartmentService updateDepartmentService;
    private CheckForDuplicateDepartmentService checkForDuplicateDepartmentService;
    private Department department;
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
        insertDepartmentService = departmentDbServiceManager.new InsertDepartmentService();
        updateDepartmentService = departmentDbServiceManager.new UpdateDepartmentService();
        checkForDuplicateDepartmentService = departmentDbServiceManager.new CheckForDuplicateDepartmentService();
        
        insertDepartmentService.setOnSucceeded(insertDepartmentSuccess);
        insertDepartmentService.setOnFailed(insertDepartmentFailure);
        
        updateDepartmentService.setOnSucceeded(updateDepartmentSuccess);
        updateDepartmentService.setOnFailed(updateDepartmentFailure);
        
        checkForDuplicateDepartmentService.setOnSucceeded(checkForDuplicateDepartmentSuccess);
        checkForDuplicateDepartmentService.setOnFailed(checkForDuplicateDepartmentFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = insertDepartmentService.runningProperty()
                                     .or(updateDepartmentService.runningProperty())
                                     .or(checkForDuplicateDepartmentService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            nameTextField.setText(department.getName());
            titleLabel.setText("Update Department");
            addSaveButton.setText("Save");
        } else {
            department = new Department();
            titleLabel.setText("Add Department");
            addSaveButton.setText("Add");
        }
    }
    
    private void closeWindow() {
        currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.close();
    }
    
    private void buildDepartment() {
        department.setName(nameTextField.getText());
        if (formMode.equals(FormMode.INSERT)) {
            runCheckForDuplicateDepartmentService();
        } else {
            runUpdateDepartmentService();
        }
    }
    
    // Service run handlers
    private void runInsertDepartmentService() {
        if (!insertDepartmentService.isRunning()) {
            insertDepartmentService.reset();
            insertDepartmentService.setDepartment(department);
            insertDepartmentService.start();
        }
    }
    
    private void runUpdateDepartmentService() {
        if (!updateDepartmentService.isRunning()) {
            updateDepartmentService.reset();
            updateDepartmentService.setDepartment(department);
            updateDepartmentService.start();
        }
    }
    
    private void runCheckForDuplicateDepartmentService() {
        if (!checkForDuplicateDepartmentService.isRunning()) {
            checkForDuplicateDepartmentService.reset();
            checkForDuplicateDepartmentService.setName(department.getName());
            checkForDuplicateDepartmentService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> insertDepartmentSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Department " 
                + department.getName()
                + "was successfully added.");
        System.out.println("Insert successful");
        closeWindow();
    };
    
    private EventHandler<WorkerStateEvent> insertDepartmentFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");

    };
    
    private EventHandler<WorkerStateEvent> updateDepartmentSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Department " 
                + department.getName()
                + "was successfully changed.");
        System.out.println("Update successful");
        closeWindow();
    };
    
    private EventHandler<WorkerStateEvent> updateDepartmentFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateDepartmentSuccess = (event) -> {
        if (checkForDuplicateDepartmentService.getValue()) {
            systemMessageLabel.setText("Department already exists");
            systemMessageLabel.getStyleClass().removeAll("system-message-label");
            systemMessageLabel.getStyleClass().add("system-message-label-error");
        } else {
            runInsertDepartmentService();
        }
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateDepartmentFailure = (event) -> {
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
            buildDepartment();
        }
    }
    
    public void specifyUpdateMode(Department department) {
        this.department = department;
        formMode = FormMode.UPDATE;
        applyFormMode();
    }
    
    public FormResult getFormResult() {
        return formResult;
    }
    
    private boolean validateName() {
        String input = nameTextField.getText();
        if (input.isEmpty()) {
            nameErrorLabel.setText("Department name required");
            return false;
        }
        
        if (input.length() > 25) {
            nameErrorLabel.setText("Department name must be less than 25 characters");
            return false;
        }
        
        nameErrorLabel.setText("");
        return true;
    }
    
}
