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
import sdtracker.database.TicketPriorityDbServiceManager;
import sdtracker.database.TicketPriorityDbServiceManager.CheckForDuplicateTicketPriorityService;
import sdtracker.database.TicketPriorityDbServiceManager.InsertTicketPriorityService;
import sdtracker.database.TicketPriorityDbServiceManager.UpdateTicketPriorityService;
import sdtracker.model.TicketPriority;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class TicketPriorityFormController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private TextField nameTextField;
    @FXML private Label nameErrorLabel;
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    @FXML private Label systemMessageLabel;
    
    private FormMode formMode = FormMode.INSERT;
    private FormResult formResult;
    private TicketPriorityDbServiceManager ticketPriorityDbServiceManager = TicketPriorityDbServiceManager.getServiceManager();
    private InsertTicketPriorityService insertTicketPriorityService;
    private UpdateTicketPriorityService updateTicketPriorityService;
    private CheckForDuplicateTicketPriorityService checkForDuplicateTicketPriorityService;
    private TicketPriority ticketPriority;
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
        insertTicketPriorityService = ticketPriorityDbServiceManager.new InsertTicketPriorityService();
        updateTicketPriorityService = ticketPriorityDbServiceManager.new UpdateTicketPriorityService();
        checkForDuplicateTicketPriorityService = ticketPriorityDbServiceManager.new CheckForDuplicateTicketPriorityService();
        
        insertTicketPriorityService.setOnSucceeded(insertTicketPrioritySuccess);
        insertTicketPriorityService.setOnFailed(insertTicketPriorityFailure);
        
        updateTicketPriorityService.setOnSucceeded(updateTicketPrioritySuccess);
        updateTicketPriorityService.setOnFailed(updateTicketPriorityFailure);
        
        checkForDuplicateTicketPriorityService.setOnSucceeded(checkForDuplicateTicketPrioritySuccess);
        checkForDuplicateTicketPriorityService.setOnFailed(checkForDuplicateTicketPriorityFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = insertTicketPriorityService.runningProperty()
                                     .or(updateTicketPriorityService.runningProperty())
                                     .or(checkForDuplicateTicketPriorityService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            nameTextField.setText(ticketPriority.getName());
            titleLabel.setText("Update TicketPriority");
            addSaveButton.setText("Save");
        } else {
            ticketPriority = new TicketPriority();
            titleLabel.setText("Add TicketPriority");
            addSaveButton.setText("Add");
        }
    }
    
    private void setCurrentStage() {
        currentStage = (Stage) titleLabel.getScene().getWindow();
    }
    
    private void buildTicketPriority() {
        ticketPriority.setName(nameTextField.getText());
        if (formMode.equals(FormMode.INSERT)) {
            runCheckForDuplicateTicketPriorityService();
        } else {
            runUpdateTicketPriorityService();
        }
    }
    
    // Service run handlers
    private void runInsertTicketPriorityService() {
        if (!insertTicketPriorityService.isRunning()) {
            insertTicketPriorityService.reset();
            insertTicketPriorityService.setTicketPriority(ticketPriority);
            insertTicketPriorityService.start();
        }
    }
    
    private void runUpdateTicketPriorityService() {
        if (!updateTicketPriorityService.isRunning()) {
            updateTicketPriorityService.reset();
            updateTicketPriorityService.setTicketPriority(ticketPriority);
            updateTicketPriorityService.start();
        }
    }
    
    private void runCheckForDuplicateTicketPriorityService() {
        if (!checkForDuplicateTicketPriorityService.isRunning()) {
            checkForDuplicateTicketPriorityService.reset();
            checkForDuplicateTicketPriorityService.setName(ticketPriority.getName());
            checkForDuplicateTicketPriorityService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> insertTicketPrioritySuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "TicketPriority " 
                + ticketPriority.getName()
                + "was successfully added.");
        System.out.println("Insert successful");
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> insertTicketPriorityFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");

    };
    
    private EventHandler<WorkerStateEvent> updateTicketPrioritySuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "TicketPriority " 
                + ticketPriority.getName()
                + "was successfully changed.");
        System.out.println("Update successful");
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> updateTicketPriorityFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateTicketPrioritySuccess = (event) -> {
        if (checkForDuplicateTicketPriorityService.getValue()) {
            systemMessageLabel.setText("TicketPriority already exists");
            systemMessageLabel.getStyleClass().removeAll("system-message-label");
            systemMessageLabel.getStyleClass().add("system-message-label-error");
        } else {
            runInsertTicketPriorityService();
        }
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateTicketPriorityFailure = (event) -> {
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
            buildTicketPriority();
        }
    }
    
    public void setTicketPriority(TicketPriority ticketPriority) {
        this.ticketPriority = ticketPriority;
        formMode = FormMode.UPDATE;
    }
    
    public FormResult getFormResult() {
        return formResult;
    }
    
    private boolean validateName() {
        String input = nameTextField.getText();
        if (input.isEmpty()) {
            nameErrorLabel.setText("Ticket Priority name required");
            return false;
        }
        
        if (input.length() > 25) {
            nameErrorLabel.setText("Ticket Priority name must be less than 25 characters");
            return false;
        }
        
        nameErrorLabel.setText("");
        return true;
    }
}
