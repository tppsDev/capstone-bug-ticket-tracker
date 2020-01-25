/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanBinding;
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
    
    private Bug bug;
    
    private BugDbServiceManager bugDbServiceManager = BugDbServiceManager.getServiceManager();
    private InsertBugService insertBugService;
    private UpdateBugService updateBugService;
    
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
        runGetAllContactsService();
        
    }
    
    private void initializeServices() {
        getAllContactsService = contactDbServiceManager.new GetAllContactsService();
        
        
        insertBugService.setOnSucceeded(insertBugSuccess);
        insertBugService.setOnFailed(insertBugFailure);
        
        updateBugService.setOnSucceeded(updateBugSuccess);
        updateBugService.setOnFailed(updateBugFailure);

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
                                     .or(updateBugService.runningProperty())    
                                     .or(getAllBugStatusesService.runningProperty())    
                                     .or(getAllBugPrioritiesService.runningProperty())    
                                     .or(getAllContactsService.runningProperty())    
                                     .or(getAllProductsService.runningProperty())    
                                     .or(getAllAppUsersService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void initializeInputElements() {
        
    }
    
    private void applyFormMode() {
        
    }
    
    private void startEventHandlers() {
        
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
        
    };
    
    private EventHandler<WorkerStateEvent> insertBugFailure = (event) -> {
        
    };
    
    private EventHandler<WorkerStateEvent> updateBugSuccess = (event) -> {
        
    };

    private EventHandler<WorkerStateEvent> updateBugFailure = (event) -> {
        
    };
    
    private EventHandler<WorkerStateEvent> getAllBugStatusesSuccess = (event) -> {
        
    };

    private EventHandler<WorkerStateEvent> getAllBugStatusesFailure = (event) -> {
        
    };
    
    private EventHandler<WorkerStateEvent> getAllBugPrioritiesSuccess = (event) -> {
        
    };
    
    private EventHandler<WorkerStateEvent> getAllBugPrioritiesFailure = (event) -> {
        
    };

    private EventHandler<WorkerStateEvent> getAllContactsSuccess = (event) -> {
        allContactList = getAllContactsService.getValue();
    };
    
    private EventHandler<WorkerStateEvent> getAllContactsFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };

    private EventHandler<WorkerStateEvent> getAllProductsSuccess = (event) -> {
        
    };

    private EventHandler<WorkerStateEvent> getAllProductsFailure = (event) -> {
        
    };
    
    private EventHandler<WorkerStateEvent> getAllAppUsersSuccess = (event) -> {
        
    };
    
    private EventHandler<WorkerStateEvent> getAllAppUsersFailure = (event) -> {
        
    };
    
    @FXML
    private void handleAddSaveButton(ActionEvent event) {

    }

    @FXML
    private void handleCancelButton(ActionEvent event) {

    }
    
}
