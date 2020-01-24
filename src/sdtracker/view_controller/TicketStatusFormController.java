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
import sdtracker.database.TicketStatusDbServiceManager;
import sdtracker.database.TicketStatusDbServiceManager.CheckForDuplicateTicketStatusService;
import sdtracker.database.TicketStatusDbServiceManager.InsertTicketStatusService;
import sdtracker.database.TicketStatusDbServiceManager.UpdateTicketStatusService;
import sdtracker.model.TicketStatus;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class TicketStatusFormController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private TextField nameTextField;
    @FXML private Label nameErrorLabel;
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    @FXML private Label systemMessageLabel;
    
    private FormMode formMode = FormMode.INSERT;
    private FormResult formResult;
    private TicketStatusDbServiceManager ticketStatusDbServiceManager = TicketStatusDbServiceManager.getServiceManager();
    private InsertTicketStatusService insertTicketStatusService;
    private UpdateTicketStatusService updateTicketStatusService;
    private CheckForDuplicateTicketStatusService checkForDuplicateTicketStatusService;
    private TicketStatus ticketStatus;
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
        insertTicketStatusService = ticketStatusDbServiceManager.new InsertTicketStatusService();
        updateTicketStatusService = ticketStatusDbServiceManager.new UpdateTicketStatusService();
        checkForDuplicateTicketStatusService = ticketStatusDbServiceManager.new CheckForDuplicateTicketStatusService();
        
        insertTicketStatusService.setOnSucceeded(insertTicketStatusSuccess);
        insertTicketStatusService.setOnFailed(insertTicketStatusFailure);
        
        updateTicketStatusService.setOnSucceeded(updateTicketStatusSuccess);
        updateTicketStatusService.setOnFailed(updateTicketStatusFailure);
        
        checkForDuplicateTicketStatusService.setOnSucceeded(checkForDuplicateTicketStatusSuccess);
        checkForDuplicateTicketStatusService.setOnFailed(checkForDuplicateTicketStatusFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = insertTicketStatusService.runningProperty()
                                     .or(updateTicketStatusService.runningProperty())
                                     .or(checkForDuplicateTicketStatusService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            nameTextField.setText(ticketStatus.getName());
            titleLabel.setText("Update TicketStatus");
            addSaveButton.setText("Save");
        } else {
            ticketStatus = new TicketStatus();
            titleLabel.setText("Add TicketStatus");
            addSaveButton.setText("Add");
        }
    }
    
    private void setCurrentStage() {
        currentStage = (Stage) titleLabel.getScene().getWindow();
    }
    
    private void buildTicketStatus() {
        ticketStatus.setName(nameTextField.getText());
        if (formMode.equals(FormMode.INSERT)) {
            runCheckForDuplicateTicketStatusService();
        } else {
            runUpdateTicketStatusService();
        }
    }
    
    // Service run handlers
    private void runInsertTicketStatusService() {
        if (!insertTicketStatusService.isRunning()) {
            insertTicketStatusService.reset();
            insertTicketStatusService.setTicketStatus(ticketStatus);
            insertTicketStatusService.start();
        }
    }
    
    private void runUpdateTicketStatusService() {
        if (!updateTicketStatusService.isRunning()) {
            updateTicketStatusService.reset();
            updateTicketStatusService.setTicketStatus(ticketStatus);
            updateTicketStatusService.start();
        }
    }
    
    private void runCheckForDuplicateTicketStatusService() {
        if (!checkForDuplicateTicketStatusService.isRunning()) {
            checkForDuplicateTicketStatusService.reset();
            checkForDuplicateTicketStatusService.setName(ticketStatus.getName());
            checkForDuplicateTicketStatusService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> insertTicketStatusSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "TicketStatus " 
                + ticketStatus.getName()
                + "was successfully added.");
        System.out.println("Insert successful");
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> insertTicketStatusFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");

    };
    
    private EventHandler<WorkerStateEvent> updateTicketStatusSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "TicketStatus " 
                + ticketStatus.getName()
                + "was successfully changed.");
        System.out.println("Update successful");
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> updateTicketStatusFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateTicketStatusSuccess = (event) -> {
        if (checkForDuplicateTicketStatusService.getValue()) {
            systemMessageLabel.setText("TicketStatus already exists");
            systemMessageLabel.getStyleClass().removeAll("system-message-label");
            systemMessageLabel.getStyleClass().add("system-message-label-error");
        } else {
            runInsertTicketStatusService();
        }
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateTicketStatusFailure = (event) -> {
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
            buildTicketStatus();
        }
    }
    
    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
        formMode = FormMode.UPDATE;
    }
    
    public FormResult getFormResult() {
        return formResult;
    }
    
    private boolean validateName() {
        String input = nameTextField.getText();
        if (input.isEmpty()) {
            nameErrorLabel.setText("TicketStatus name required");
            return false;
        }
        
        if (input.length() > 25) {
            nameErrorLabel.setText("TicketStatus name must be less than 25 characters");
            return false;
        }
        
        nameErrorLabel.setText("");
        return true;
    }
}
