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
import sdtracker.database.MfgDbServiceManager;
import sdtracker.database.MfgDbServiceManager.CheckForDuplicateMfgService;
import sdtracker.database.MfgDbServiceManager.InsertMfgService;
import sdtracker.database.MfgDbServiceManager.UpdateMfgService;
import sdtracker.model.Mfg;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class MfgFormController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private TextField nameTextField;
    @FXML private Label nameErrorLabel;
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    @FXML private Label systemMessageLabel;
    
    private FormMode formMode = FormMode.INSERT;
    private FormResult formResult;
    private MfgDbServiceManager mfgDbServiceManager = MfgDbServiceManager.getServiceManager();
    private InsertMfgService insertMfgService;
    private UpdateMfgService updateMfgService;
    private CheckForDuplicateMfgService checkForDuplicateMfgService;
    private Mfg mfg;
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
        insertMfgService = mfgDbServiceManager.new InsertMfgService();
        updateMfgService = mfgDbServiceManager.new UpdateMfgService();
        checkForDuplicateMfgService = mfgDbServiceManager.new CheckForDuplicateMfgService();
        
        insertMfgService.setOnSucceeded(insertMfgSuccess);
        insertMfgService.setOnFailed(insertMfgFailure);
        
        updateMfgService.setOnSucceeded(updateMfgSuccess);
        updateMfgService.setOnFailed(updateMfgFailure);
        
        checkForDuplicateMfgService.setOnSucceeded(checkForDuplicateMfgSuccess);
        checkForDuplicateMfgService.setOnFailed(checkForDuplicateMfgFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = insertMfgService.runningProperty()
                                     .or(updateMfgService.runningProperty())
                                     .or(checkForDuplicateMfgService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            nameTextField.setText(mfg.getName());
            titleLabel.setText("Update Mfg");
            addSaveButton.setText("Save");
        } else {
            mfg = new Mfg();
            titleLabel.setText("Add Mfg");
            addSaveButton.setText("Add");
        }
    }
    
    private void closeWindow() {
        currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.close();
    }
    
    private void buildMfg() {
        mfg.setName(nameTextField.getText());
        if (formMode.equals(FormMode.INSERT)) {
            runCheckForDuplicateMfgService();
        } else {
            runUpdateMfgService();
        }
    }
    
    // Service run handlers
    private void runInsertMfgService() {
        if (!insertMfgService.isRunning()) {
            insertMfgService.reset();
            insertMfgService.setMfg(mfg);
            insertMfgService.start();
        }
    }
    
    private void runUpdateMfgService() {
        if (!updateMfgService.isRunning()) {
            updateMfgService.reset();
            updateMfgService.setMfg(mfg);
            updateMfgService.start();
        }
    }
    
    private void runCheckForDuplicateMfgService() {
        if (!checkForDuplicateMfgService.isRunning()) {
            checkForDuplicateMfgService.reset();
            checkForDuplicateMfgService.setName(mfg.getName());
            checkForDuplicateMfgService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> insertMfgSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Mfg " 
                + mfg.getName()
                + "was successfully added.");
        System.out.println("Insert successful");
        closeWindow();
    };
    
    private EventHandler<WorkerStateEvent> insertMfgFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");

    };
    
    private EventHandler<WorkerStateEvent> updateMfgSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Mfg " 
                + mfg.getName()
                + "was successfully changed.");
        System.out.println("Update successful");
        closeWindow();
    };
    
    private EventHandler<WorkerStateEvent> updateMfgFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateMfgSuccess = (event) -> {
        if (checkForDuplicateMfgService.getValue()) {
            systemMessageLabel.setText("Mfg already exists");
            systemMessageLabel.getStyleClass().removeAll("system-message-label");
            systemMessageLabel.getStyleClass().add("system-message-label-error");
        } else {
            runInsertMfgService();
        }
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateMfgFailure = (event) -> {
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
            buildMfg();
        }
    }
    
    public void specifyUpdateMode(Mfg mfg) {
        this.mfg = mfg;
        formMode = FormMode.UPDATE;
        applyFormMode();
    }
    
    public FormResult getFormResult() {
        return formResult;
    }
    
    private boolean validateName() {
        String input = nameTextField.getText();
        if (input.isEmpty()) {
            nameErrorLabel.setText("Manufacturer name required");
            return false;
        }
        
        if (input.length() > 150) {
            nameErrorLabel.setText("Manufacturer name must be less than 150 characters");
            return false;
        }
        
        nameErrorLabel.setText("");
        return true;
    }
}
