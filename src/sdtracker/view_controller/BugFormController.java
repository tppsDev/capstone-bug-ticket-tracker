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
import sdtracker.database.BugDbServiceManager;
import sdtracker.database.BugDbServiceManager.*;
import sdtracker.database.BugPriorityDbServiceManager;
import sdtracker.database.BugPriorityDbServiceManager.GetAllBugPrioritysService;
import sdtracker.database.BugStatusDbServiceManager;
import sdtracker.database.BugStatusDbServiceManager.GetAllBugStatussService;
import sdtracker.database.ContactDbServiceManager;
import sdtracker.database.ContactDbServiceManager.GetAllContactsService;
import sdtracker.database.ProductDbServiceManager;
import sdtracker.database.ProductDbServiceManager.GetAllProductsService;
import sdtracker.model.AppUser;
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
    
    private BugDbServiceManager bugDbServiceManager = BugDbServiceManager.getServiceManager();
    private InsertBugService insertBugService;
    private UpdateBugService updateBugService;
    
    private BugStatusDbServiceManager bugStatusDbServiceManager = BugStatusDbServiceManager.getServiceManager();
    private GetAllBugStatussService getAllBugStatussService;
    
    private BugPriorityDbServiceManager bugPriorityDbServiceManager = BugPriorityDbServiceManager.getServiceManager();
    private GetAllBugPrioritysService getAllBugPrioritysService;
    
    private ContactDbServiceManager contactDbServiceManager = ContactDbServiceManager.getServiceManager();
    private GetAllContactsService getAllContactsService;
    private ObservableList<Contact> allContactList = FXCollections.observableArrayList();
    
    private ProductDbServiceManager productDbServiceManager = ProductDbServiceManager.getServiceManager();
    private GetAllProductsService getAllProductsService;
    private ObservableList<Product> allProductList = FXCollections.observableArrayList();
    
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
        
        getAllContactsService.setOnSucceeded(getAllContactsSuccess);
        getAllContactsService.setOnFailed(getAllContactsFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = getAllContactsService.runningProperty()
                                        .or(getAllContactsService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void initializeInputElements() {
        
    }
    
    private void applyFormMode() {
        
    }
    
    private void startEventHandlers() {
        
    }
    
    // Service run handlers
    private void runGetAllContactsService() {
        if (!getAllContactsService.isRunning()) {
            getAllContactsService.reset();
            getAllContactsService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> getAllContactsSuccess = (event) -> {
        allContactList = getAllContactsService.getValue();
    };
    
    private EventHandler<WorkerStateEvent> getAllContactsFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
        @FXML
    private void handleAddSaveButton(ActionEvent event) {

    }

    @FXML
    private void handleCancelButton(ActionEvent event) {

    }
    
}
