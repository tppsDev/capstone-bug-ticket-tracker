/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.net.URL;
import java.time.LocalDateTime;
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
import sdtracker.Session;
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
import sdtracker.utility.BugNumberGenerator;

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
    
    Session session = Session.getSession();
    
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
        runGetAllBugStatusesService();
        
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
                                     .or(updateBugService.runningProperty())
                                     .or(checkForDuplicateBugService.runningProperty())
                                     .or(getAllBugStatusesService.runningProperty())    
                                     .or(getAllBugPrioritiesService.runningProperty())    
                                     .or(getAllContactsService.runningProperty())    
                                     .or(getAllProductsService.runningProperty())    
                                     .or(getAllAppUsersService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void initializeInputElements() {
        bugStatusComboBox.getItems().addAll(allBugStatusList);
        bugPriorityComboBox.getItems().addAll(allBugPriorityList);
        contactComboBox.getItems().addAll(allContactList);
        assignedToComboBox.getItems().addAll(allAppUserList);
        productComboBox.getItems().addAll(allProductList);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            bugNumberLabel.setText(bug.getBugNumber());
            bugStatusComboBox.getSelectionModel().select(bug.getStatus());
            bugPriorityComboBox.getSelectionModel().select(bug.getPriority());
            contactComboBox.getSelectionModel().select(bug.getContact());
            if (bug.getAssignedAppUser() != null) {
                assignedToComboBox.getSelectionModel().select(bug.getAssignedAppUser());
            }
            productComboBox.getSelectionModel().select(bug.getProduct());
            titleTextField.setText(bug.getTitle());
            descriptionTextArea.setText(bug.getDescription());
            addSaveButton.setText("Save");
        } else {
            addSaveButton.setText("Add");
            bug = new Bug();
            bugNumberLabel.setText(BugNumberGenerator.generateBugNumber());
        }
        startEventHandlers();
    }
    
    private void startEventHandlers() {
        startBugStatusFocusListener();
        startBugPriorityFocusListener();
        startContactFocusListener();
        startProductFocusListener();
        startTitleFocusListener();
        startDescriptionFocusListener();
    }
    
    private void insertBug() {
        buildBug();
        bug.setCreatedTimestamp(LocalDateTime.now());
        bug.setAssignedAppUser(session.getSessionUser());
        runCheckForDuplicateBugService();
    }
    
    private void updateBug() {
        buildBug();
        bug.setLastUpdatedTimestamp(LocalDateTime.now());
        bug.setLasUpdatedByAppUser(session.getSessionUser());
        runUpdateBugService();
    }
    
    private void buildBug() {
        bug.setBugNumber(bugNumberLabel.getText());
        bug.setTitle(titleTextField.getText());
        bug.setDescription(descriptionTextArea.getText());
        bug.setStatus(bugStatusComboBox.getValue());
        bug.setPriority(bugPriorityComboBox.getValue());
        bug.setContact(contactComboBox.getValue());
        bug.setAssignedAppUser(assignedToComboBox.getValue());
        bug.setProduct(productComboBox.getValue());
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
            checkForDuplicateBugService.start();
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
    private EventHandler<WorkerStateEvent> checkForDuplicateBugSuccess = (event) -> {
        if (checkForDuplicateBugService.getValue()) {
            displaySystemMessage("Bug number already exists, generating new bug number.", true);
            bugNumberLabel.setText(BugNumberGenerator.generateBugNumber());
        } else {
            runInsertBugService();
        }
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateBugFailure = (event) -> {
        displaySystemMessage("System error, please try your request again.", true);
    };
    
    private EventHandler<WorkerStateEvent> insertBugSuccess = (event) -> {
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Bug listing for " 
                + bug.getBugNumber()
                + " was successfully added.");
        currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> insertBugFailure = (event) -> {
        event.getSource().getException().printStackTrace();
        displaySystemMessage("System error on insert, please try your request again.", true);
    };
    
    private EventHandler<WorkerStateEvent> updateBugSuccess = (event) -> {
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Bug listing for " 
                + bug.getBugNumber()
                + " was successfully updated.");
        currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.close();
    };

    private EventHandler<WorkerStateEvent> updateBugFailure = (event) -> {
        displaySystemMessage("System error on update, please try your request again.", true);
    };
    
    private EventHandler<WorkerStateEvent> getAllBugStatusesSuccess = (event) -> {
        System.out.println("Get all status success");
        allBugStatusList = getAllBugStatusesService.getValue();
        System.out.println(bugStatusComboBox.getItems().size());
        runGetAllBugPrioritiesService();
    };

    private EventHandler<WorkerStateEvent> getAllBugStatusesFailure = (event) -> {
        event.getSource().getException().printStackTrace();
        displaySystemMessage("System error loading bug statuses, please try your request again.", true);
        runGetAllBugPrioritiesService();
    };
    
    private EventHandler<WorkerStateEvent> getAllBugPrioritiesSuccess = (event) -> {
        allBugPriorityList = getAllBugPrioritiesService.getValue();
        runGetAllContactsService();
    };
    
    private EventHandler<WorkerStateEvent> getAllBugPrioritiesFailure = (event) -> {
        displaySystemMessage("System error loading priorites, please try your request again.", true);
        runGetAllContactsService();
    };

    private EventHandler<WorkerStateEvent> getAllContactsSuccess = (event) -> {
        allContactList = getAllContactsService.getValue();
        runGetAllProductsService();
    };
    
    private EventHandler<WorkerStateEvent> getAllContactsFailure = (event) -> {
        displaySystemMessage("System error loading contacts, please try your request again.", true);
        runGetAllProductsService();
    };

    private EventHandler<WorkerStateEvent> getAllProductsSuccess = (event) -> {
        allProductList = getAllProductsService.getValue();
        runGetAllAppUsersService();
    };

    private EventHandler<WorkerStateEvent> getAllProductsFailure = (event) -> {
        displaySystemMessage("System error loading products, please try your request again.", true);
        runGetAllAppUsersService();
    };
    
    private EventHandler<WorkerStateEvent> getAllAppUsersSuccess = (event) -> {
        allAppUserList = getAllAppUsersService.getValue();
        initializeInputElements();
        applyFormMode();
    };
    
    private EventHandler<WorkerStateEvent> getAllAppUsersFailure = (event) -> {
        displaySystemMessage("System error loading users, please try your request again.", true);
        initializeInputElements();
        applyFormMode();
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
        if (validateAll()) {
            if (formMode.equals(FormMode.INSERT)) {
                insertBug();
            } else {
                updateBug();
            }
        }
    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
        formResult = new FormResult(FormResult.FormResultStatus.FAILURE, "Action cancelled by user");
        currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.close();
    }
    
    private void startBugStatusFocusListener() {
        if (bugStatusFocusListener == null) {
            bugStatusFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                    validateBugStatus();
                }
            };
        }
        bugStatusComboBox.focusedProperty().addListener(bugStatusFocusListener);
    }
    
    private void startBugPriorityFocusListener() {
        if (bugPriorityFocusListener == null) {
            bugPriorityFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                    validateBugPriority();
                }
            };
        }
        bugPriorityComboBox.focusedProperty().addListener(bugPriorityFocusListener);
    }
    
    private void startContactFocusListener() {
        if (contactFocusListener == null) {
            contactFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                    validateContact();
                }
            };
        }
        contactComboBox.focusedProperty().addListener(contactFocusListener);
    }
    
    private void startProductFocusListener() {
        if (productFocusListener == null) {
            productFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                    validateProduct();
                }
            };
        }
        productComboBox.focusedProperty().addListener(productFocusListener);
    }
    
    private void startTitleFocusListener() {
        if (titleFocusListener == null) {
            titleFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                    validateTitle();
                }
            };
        }
        titleTextField.focusedProperty().addListener(titleFocusListener);
    }
    
    private void startDescriptionFocusListener() {
        if (descriptionFocusListener == null) {
            descriptionFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                    validateDescription();
                }
            };
        }
        descriptionTextArea.focusedProperty().addListener(descriptionFocusListener);
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
