/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sdtracker.database.AppUserDbServiceManager;
import sdtracker.database.AppUserDbServiceManager.GetAllAppUsersService;
import sdtracker.database.BugDbServiceManager;
import sdtracker.database.BugDbServiceManager.*;
import sdtracker.database.BugPriorityDbServiceManager;
import sdtracker.database.BugPriorityDbServiceManager.GetAllBugPrioritiesService;
import sdtracker.database.BugStatusDbServiceManager;
import sdtracker.database.BugStatusDbServiceManager.GetAllBugStatusesService;
import sdtracker.database.ContactDbServiceManager;
import sdtracker.database.ContactDbServiceManager.GetAllContactsService;
import sdtracker.database.ProductDbServiceManager;
import sdtracker.database.ProductDbServiceManager.GetAllProductsService;
import sdtracker.model.AppUser;
import sdtracker.model.Bug;
import sdtracker.model.BugPriority;
import sdtracker.model.BugStatus;
import sdtracker.model.Contact;
import sdtracker.model.Product;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class BugFormController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private Label bugNumberLabel;
    @FXML private ComboBox<BugStatus> bugStatusComboBox;
    @FXML private ComboBox<BugPriority> bugPriorityComboBox;
    @FXML private ComboBox<Contact> contactComboBox;
    @FXML private ComboBox<AppUser> assignedToComboBox;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField titleTextField;
    @FXML private TextArea descriptionTextArea;
    @FXML private Label bugStatusErrorLabel;
    @FXML private Label bugPriorityErrorLabel;
    @FXML private Label contactErrorLabel;
    @FXML private Label assignedToErrorLabel;
    @FXML private Label productErrorLabel;
    @FXML private Label titleErrorLabel;
    @FXML private Label descriptionErrorLabel;
    @FXML private Label systemMessageLabel;
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    
    private ChangeListener<Boolean> bugStatusFocusListener ;
    private ChangeListener<Boolean> bugPriorityFocusListener ;
    private ChangeListener<Boolean> contactFocusListener ;
    private ChangeListener<Boolean> assignedToFocusListener ;
    private ChangeListener<Boolean> productFocusListener ;
    private ChangeListener<Boolean> titleFocusListener ;
    private ChangeListener<Boolean> descriptionFocusListener ;
    
    private Bug bug;
    private FormMode formMode = FormMode.INSERT;
    private FormResult formResult;
    
    Stage currentStage;
    
    private BugDbServiceManager bugDbServiceManager = BugDbServiceManager.getServiceManager();
    private InsertBugService insertBugService;
    private UpdateBugService updateBugService;
    private CheckForDuplicateBugService checkForDuplicateBugService;
    
    private BugStatusDbServiceManager bugStatusDbServiceManager = BugStatusDbServiceManager.getServiceManager();
    private GetAllBugStatusesService getAllBugStatusesService;
    private ObservableList<BugStatus> allBugStatusList = FXCollections.observableArrayList();
    
    private BugPriorityDbServiceManager bugPriorityDbServiceManager = BugPriorityDbServiceManager.getServiceManager();
    private GetAllBugPrioritiesService getAllBugPrioritiesService;
    private ObservableList<BugPriority> allBugPriorityList = FXCollections.observableArrayList();
    
    private ContactDbServiceManager contactDbServiceManager = ContactDbServiceManager.getServiceManager();
    private GetAllContactsService getAllContactsService;
    private ObservableList<Contact> allContactList = FXCollections.observableArrayList();
    
    private ProductDbServiceManager productDbServiceManager = ProductDbServiceManager.getServiceManager();
    private GetAllProductsService getAllProductsService;
    private ObservableList<Product> allProductList = FXCollections.observableArrayList();
    
    private AppUserDbServiceManager appUserDbServiceManager = AppUserDbServiceManager.getServiceManager();
    private GetAllAppUsersService getAllAppUsersService;
    private ObservableList<AppUser> allAppUserList = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeServices();
        establishBindings();
        initializeInputElements();
        applyFormMode();
        startEventHandlers();
    }
    
    private void initializeServices() {
        insertBugService = bugDbServiceManager.new InsertBugService();
        updateBugService = bugDbServiceManager.new UpdateBugService();
        checkForDuplicateBugService = bugDbServiceManager.new CheckForDuplicateBugService();
        
        getAllBugStatusesService = bugStatusDbServiceManager.new GetAllBugStatusesService();
        getAllBugPrioritiesService = bugPriorityDbServiceManager.new GetAllBugPrioritiesService();
        getAllContactsService = contactDbServiceManager.new GetAllContactsService();
        getAllProductsService = productDbServiceManager.new GetAllProductsService();
        getAllAppUsersService = appUserDbServiceManager.new GetAllAppUsersService();
        
        insertBugService.setOnSucceeded(insertBugSuccess);
        insertBugService.setOnFailed(insertBugFailure);
        
        updateBugService.setOnSucceeded(updateBugSuccess);
        updateBugService.setOnFailed(updateBugFailure);
        
        checkForDuplicateBugService.setOnSucceeded(checkForDuplicateBugSuccess);
        checkForDuplicateBugService.setOnFailed(checkForDuplicateBugFailure);

        getAllBugStatusesService.setOnSucceeded(getAllBugStatusesSuccess);
        getAllBugStatusesService.setOnFailed(getAllBugStatusesFailure);


        getAllBugPrioritiesService.setOnSucceeded(getAllBugPrioritiesSuccess);
        getAllBugPrioritiesService.setOnFailed(getAllBugPrioritiesFailure);
        
        getAllContactsService.setOnSucceeded(getAllContactsSuccess);
        getAllContactsService.setOnFailed(getAllContactsFailure);

        getAllProductsService.setOnSucceeded(getAllProductsSuccess);
        getAllProductsService.setOnFailed(getAllProductsFailure);

        getAllAppUsersService.setOnSucceeded(getAllAppUsersSuccess);
        getAllAppUsersService.setOnFailed(getAllAppUsersFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = insertBugService.runningProperty()
                                     .or(checkForDuplicateBugService.runningProperty())
                                     .or(updateBugService.runningProperty())    
                                     .or(getAllBugStatusesService.runningProperty())    
                                     .or(getAllBugPrioritiesService.runningProperty())    
                                     .or(getAllContactsService.runningProperty())    
                                     .or(getAllProductsService.runningProperty())    
                                     .or(getAllAppUsersService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void initializeInputElements() {
        currentStage = (Stage) titleLabel.getScene().getWindow();
    }
    
    private void applyFormMode() {
        
    }
    
    private void startEventHandlers() {
        
    }
    
    private void setUpdateMode(Bug bug) {
        formMode = FormMode.UPDATE;
        this.bug = bug;
    }
    
    // Service run handlers
    private void runInsertBugService() {
        if (!insertBugService.isRunning()) {
            insertBugService.reset();
            insertBugService.setBug(bug);
            insertBugService.start();
        }
    }
    
    private void runUpdateBugService() {
        if (!updateBugService.isRunning()) {
            updateBugService.reset();
            updateBugService.setBug(bug);
            updateBugService.start();
        }
    }
    
    private void runCheckForDuplicateBugService() {
        if (!checkForDuplicateBugService.isRunning()) {
            checkForDuplicateBugService.reset();
            checkForDuplicateBugService.setBugNumber(bug.getBugNumber());
        }
    }
    
    private void runGetAllBugStatusesService() {
        if (!getAllBugStatusesService.isRunning()) {
            getAllBugStatusesService.reset();
            getAllBugStatusesService.start();
        }
    }
    
    private void runGetAllBugPrioritiesService() {
        if (!getAllBugPrioritiesService.isRunning()) {
            getAllBugPrioritiesService.reset();
            getAllBugPrioritiesService.start();
        }
    }
    
    private void runGetAllContactsService() {
        if (!getAllContactsService.isRunning()) {
            getAllContactsService.reset();
            getAllContactsService.start();
        }
    }
    
    private void runGetAllProductsService() {
        if (!getAllProductsService.isRunning()) {
            getAllProductsService.reset();
            getAllProductsService.start();
        }
    }
    
    private void runGetAllAppUsersService() {
        if (!getAllAppUsersService.isRunning()) {
            getAllAppUsersService.reset();
            getAllAppUsersService.start();
        }
    }

    // Service status event handlers
    private EventHandler<WorkerStateEvent> insertBugSuccess = (event) -> {
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Bug listing for " 
                + bug.getBugNumber()
                + " was successfully added.");
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> insertBugFailure = (event) -> {
        displaySystemMessage("System error on insert, please try your request again.", true);
    };
    
    private EventHandler<WorkerStateEvent> updateBugSuccess = (event) -> {
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Bug listing for " 
                + bug.getBugNumber()
                + " was successfully updated.");
        currentStage.close();
    };

    private EventHandler<WorkerStateEvent> updateBugFailure = (event) -> {
        displaySystemMessage("System error on update, please try your request again.", true);
    };
    
    private EventHandler<WorkerStateEvent> getAllBugStatusesSuccess = (event) -> {
        allBugStatusList = getAllBugStatusesService.getValue();
    };

    private EventHandler<WorkerStateEvent> getAllBugStatusesFailure = (event) -> {
        displaySystemMessage("System error loading bug statuses, please try your request again.", true);
    };
    
    private EventHandler<WorkerStateEvent> getAllBugPrioritiesSuccess = (event) -> {
        allBugPriorityList = getAllBugPrioritiesService.getValue();
    };
    
    private EventHandler<WorkerStateEvent> getAllBugPrioritiesFailure = (event) -> {
        displaySystemMessage("System error loading priorites, please try your request again.", true);
    };

    private EventHandler<WorkerStateEvent> getAllContactsSuccess = (event) -> {
        allContactList = getAllContactsService.getValue();
    };
    
    private EventHandler<WorkerStateEvent> getAllContactsFailure = (event) -> {
        displaySystemMessage("System error loading contacts, please try your request again.", true);
        
    };

    private EventHandler<WorkerStateEvent> getAllProductsSuccess = (event) -> {
        allProductList = getAllProductsService.getValue();
    };

    private EventHandler<WorkerStateEvent> getAllProductsFailure = (event) -> {
        displaySystemMessage("System error loading products, please try your request again.", true);
    };
    
    private EventHandler<WorkerStateEvent> getAllAppUsersSuccess = (event) -> {
        allAppUserList = getAllAppUsersService.getValue();
    };
    
    private EventHandler<WorkerStateEvent> getAllAppUsersFailure = (event) -> {
        displaySystemMessage("System error loading users, please try your request again.", true);
    };
    
    private void displaySystemMessage(String message, boolean errorMessage) {
        if (errorMessage) {
            systemMessageLabel.getStyleClass().removeAll("system-message-label");
            systemMessageLabel.getStyleClass().add("system-message-label-error");
        } else {
            systemMessageLabel.getStyleClass().removeAll("system-message-label-error");
            systemMessageLabel.getStyleClass().add("system-message-label");
        }
        
        systemMessageLabel.setText(message);
    }
    
    @FXML
    private void handleAddSaveButton(ActionEvent event) {
        
    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
        formResult = new FormResult(FormResult.FormResultStatus.FAILURE, "Action cancelled by user");
        currentStage.close();
    }
    
    private void startBugStatusFocusListener() {
        
    }
    
    private boolean validateAll() {
        return validateBugStatus()
            && validateBugPriority()
            && validateContact()
            && validateProduct()
            && validateTitle()
            && validateDescription();
    }

    private boolean validateBugStatus() {
        if (bugStatusComboBox.getSelectionModel().isEmpty()) {
            bugStatusErrorLabel.setText("Bug Status is required");
            return false;
        }
        
        bugStatusErrorLabel.setText("");
        return true;
    }

    private boolean validateBugPriority() {
        if (bugPriorityComboBox.getSelectionModel().isEmpty()) {
            bugPriorityErrorLabel.setText("Bug Priority is required");
            return false;
        }
        
        bugPriorityErrorLabel.setText("");
        return true;
    }

    private boolean validateContact() {
        if (contactComboBox.getSelectionModel().isEmpty()) {
            contactErrorLabel.setText("Contact is required");
            return false;
        }
        
        contactErrorLabel.setText("");
        return true;
    }

    private boolean validateProduct() {
        if (productComboBox.getSelectionModel().isEmpty()) {
            productErrorLabel.setText("Product is required");
            return false;
        }
        
        productErrorLabel.setText("");
        return true;
    }

    private boolean validateTitle() {
        String input = titleTextField.getText();
        if (input.isEmpty()) {
            titleErrorLabel.setText("Title is required");
            return false;
        }
        
        if (input.length() > 70) {
            titleErrorLabel.setText("Title must be 70 characters or less");
            return false;
        }
        
        titleErrorLabel.setText("");
        return true;
    }

    private boolean validateDescription() {
        String input = descriptionTextArea.getText();
        if (input.isEmpty()) {
            descriptionErrorLabel.setText("Description is required");
            return false;
        }
        
        if (input.length() > 1000) {
            descriptionErrorLabel.setText("Description must be 1000 characters or less");
            return false;
        }
        
        descriptionErrorLabel.setText("");
        return true;
    }
}
