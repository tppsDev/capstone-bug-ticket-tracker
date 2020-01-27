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
import sdtracker.database.AssetDbServiceManager;
import sdtracker.database.AssetDbServiceManager.GetAllAssetsService;
import sdtracker.database.ContactDbServiceManager;
import sdtracker.database.ContactDbServiceManager.GetAllContactsService;
import sdtracker.database.ProductDbServiceManager;
import sdtracker.database.ProductDbServiceManager.GetAllProductsService;
import sdtracker.database.TicketDbServiceManager;
import sdtracker.database.TicketDbServiceManager.CheckForDuplicateTicketService;
import sdtracker.database.TicketDbServiceManager.InsertTicketService;
import sdtracker.database.TicketDbServiceManager.UpdateTicketService;
import sdtracker.database.TicketPriorityDbServiceManager;
import sdtracker.database.TicketPriorityDbServiceManager.GetAllTicketPrioritiesService;
import sdtracker.database.TicketStatusDbServiceManager;
import sdtracker.database.TicketStatusDbServiceManager.GetAllTicketStatusesService;
import sdtracker.model.AppUser;
import sdtracker.model.Asset;
import sdtracker.model.Contact;
import sdtracker.model.Product;
import sdtracker.model.Ticket;
import sdtracker.model.TicketPriority;
import sdtracker.model.TicketStatus;
import sdtracker.utility.TicketNumberGenerator;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class TicketFormController implements Initializable {
    
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private Label ticketNumberLabel;
    @FXML private ComboBox<TicketStatus> ticketStatusComboBox;
    @FXML private ComboBox<TicketPriority> ticketPriorityComboBox;
    @FXML private ComboBox<Contact> contactComboBox;
    @FXML private ComboBox<AppUser> assignedToComboBox;
    @FXML private ComboBox<Asset> assetComboBox;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField titleTextField;
    @FXML private TextArea descriptionTextArea;
    @FXML private Label ticketStatusErrorLabel;
    @FXML private Label ticketPriorityErrorLabel;
    @FXML private Label contactErrorLabel;
    @FXML private Label assignedToErrorLabel;
    @FXML private Label productErrorLabel;
    @FXML private Label titleErrorLabel;
    @FXML private Label descriptionErrorLabel;
    @FXML private Label systemMessageLabel;
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    
    private ChangeListener<Boolean> ticketStatusFocusListener ;
    private ChangeListener<Boolean> ticketPriorityFocusListener ;
    private ChangeListener<Boolean> contactFocusListener ;
    private ChangeListener<Boolean> titleFocusListener ;
    private ChangeListener<Boolean> descriptionFocusListener ;
    
    private Ticket ticket;
    private FormMode formMode = FormMode.INSERT;
    private FormResult formResult;
    
    Stage currentStage;
    
    Session session = Session.getSession();
    
    private TicketDbServiceManager ticketDbServiceManager = TicketDbServiceManager.getServiceManager();
    private InsertTicketService insertTicketService;
    private UpdateTicketService updateTicketService;
    private CheckForDuplicateTicketService checkForDuplicateTicketService;
    
    private TicketStatusDbServiceManager ticketStatusDbServiceManager = TicketStatusDbServiceManager.getServiceManager();
    private GetAllTicketStatusesService getAllTicketStatusesService;
    private ObservableList<TicketStatus> allTicketStatusList = FXCollections.observableArrayList();
    
    private TicketPriorityDbServiceManager ticketPriorityDbServiceManager = TicketPriorityDbServiceManager.getServiceManager();
    private GetAllTicketPrioritiesService getAllTicketPrioritiesService;
    private ObservableList<TicketPriority> allTicketPriorityList = FXCollections.observableArrayList();
    
    private ContactDbServiceManager contactDbServiceManager = ContactDbServiceManager.getServiceManager();
    private GetAllContactsService getAllContactsService;
    private ObservableList<Contact> allContactList = FXCollections.observableArrayList();
    
    private AssetDbServiceManager assetDbServiceManager = AssetDbServiceManager.getServiceManager();
    private GetAllAssetsService getAllAssetsService;
    private ObservableList<Asset> allAssetList = FXCollections.observableArrayList();
    
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
        runGetAllTicketStatusesService();
    }
    
    private void initializeServices() {
        insertTicketService = ticketDbServiceManager.new InsertTicketService();
        updateTicketService = ticketDbServiceManager.new UpdateTicketService();
        checkForDuplicateTicketService = ticketDbServiceManager.new CheckForDuplicateTicketService();
        
        getAllTicketStatusesService = ticketStatusDbServiceManager.new GetAllTicketStatusesService();
        getAllTicketPrioritiesService = ticketPriorityDbServiceManager.new GetAllTicketPrioritiesService();
        getAllContactsService = contactDbServiceManager.new GetAllContactsService();
        getAllAssetsService = assetDbServiceManager.new GetAllAssetsService();
        getAllProductsService = productDbServiceManager.new GetAllProductsService();
        getAllAppUsersService = appUserDbServiceManager.new GetAllAppUsersService();
        
        insertTicketService.setOnSucceeded(insertTicketSuccess);
        insertTicketService.setOnFailed(insertTicketFailure);
        
        updateTicketService.setOnSucceeded(updateTicketSuccess);
        updateTicketService.setOnFailed(updateTicketFailure);
        
        checkForDuplicateTicketService.setOnSucceeded(checkForDuplicateTicketSuccess);
        checkForDuplicateTicketService.setOnFailed(checkForDuplicateTicketFailure);

        getAllTicketStatusesService.setOnSucceeded(getAllTicketStatusesSuccess);
        getAllTicketStatusesService.setOnFailed(getAllTicketStatusesFailure);


        getAllTicketPrioritiesService.setOnSucceeded(getAllTicketPrioritiesSuccess);
        getAllTicketPrioritiesService.setOnFailed(getAllTicketPrioritiesFailure);
        
        getAllContactsService.setOnSucceeded(getAllContactsSuccess);
        getAllContactsService.setOnFailed(getAllContactsFailure);
        
        getAllAssetsService.setOnSucceeded(getAllAssetsSuccess);
        getAllAssetsService.setOnFailed(getAllAssetsFailure);

        getAllProductsService.setOnSucceeded(getAllProductsSuccess);
        getAllProductsService.setOnFailed(getAllProductsFailure);

        getAllAppUsersService.setOnSucceeded(getAllAppUsersSuccess);
        getAllAppUsersService.setOnFailed(getAllAppUsersFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = insertTicketService.runningProperty()
                                     .or(updateTicketService.runningProperty())
                                     .or(checkForDuplicateTicketService.runningProperty())
                                     .or(getAllTicketStatusesService.runningProperty())    
                                     .or(getAllTicketPrioritiesService.runningProperty())    
                                     .or(getAllContactsService.runningProperty())
                                     .or(getAllAssetsService.runningProperty())   
                                     .or(getAllProductsService.runningProperty())    
                                     .or(getAllAppUsersService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void initializeInputElements() {
        ticketStatusComboBox.getItems().addAll(allTicketStatusList);
        ticketPriorityComboBox.getItems().addAll(allTicketPriorityList);
        contactComboBox.getItems().addAll(allContactList);
        assignedToComboBox.getItems().addAll(allAppUserList);
        productComboBox.getItems().addAll(allProductList);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            ticketNumberLabel.setText(ticket.getTicketNumber());
            ticketStatusComboBox.getSelectionModel().select(ticket.getStatus());
            ticketPriorityComboBox.getSelectionModel().select(ticket.getPriority());
            contactComboBox.getSelectionModel().select(ticket.getContact());
            if (ticket.getAssignedAppUser() != null) {
                assignedToComboBox.getSelectionModel().select(ticket.getAssignedAppUser());
            }
            if (ticket.getAsset() != null) {
                assetComboBox.getSelectionModel().select(ticket.getAsset());
            }
            if (ticket.getProduct() != null) {
                productComboBox.getSelectionModel().select(ticket.getProduct());
            }
            titleTextField.setText(ticket.getTitle());
            descriptionTextArea.setText(ticket.getDescription());
            addSaveButton.setText("Save");
        } else {
            addSaveButton.setText("Add");
            ticket = new Ticket();
            ticketNumberLabel.setText(TicketNumberGenerator.generateTicketNumber());
        }
        startEventHandlers();
    }
    
    private void startEventHandlers() {
        startTicketStatusFocusListener();
        startTicketPriorityFocusListener();
        startContactFocusListener();
        startTitleFocusListener();
        startDescriptionFocusListener();
    }
    
    private void insertTicket() {
        buildTicket();
        ticket.setCreatedTimestamp(LocalDateTime.now());
        // TODO remove after test
        ticket.setCreatedByAppUser(assignedToComboBox.getItems().get(0));
        //ticket.setCreatedByAppUser(session.getSessionUser());
        runCheckForDuplicateTicketService();
    }
    
    private void updateTicket() {
        buildTicket();
        ticket.setLastUpdatedTimestamp(LocalDateTime.now());
        ticket.setLastUpdatedByAppUser(session.getSessionUser());
        runUpdateTicketService();
    }
    
    private void buildTicket() {
        ticket.setTicketNumber(ticketNumberLabel.getText());
        ticket.setTitle(titleTextField.getText());
        ticket.setDescription(descriptionTextArea.getText());
        ticket.setStatus(ticketStatusComboBox.getValue());
        ticket.setPriority(ticketPriorityComboBox.getValue());
        ticket.setContact(contactComboBox.getValue());
        ticket.setAssignedAppUser(assignedToComboBox.getValue());
        ticket.setProduct(productComboBox.getValue());
        ticket.setAsset(assetComboBox.getValue());
    }
    
    private void setUpdateMode(Ticket ticket) {
        formMode = FormMode.UPDATE;
        this.ticket = ticket;
    }
    
    // Service run handlers
    private void runInsertTicketService() {
        if (!insertTicketService.isRunning()) {
            insertTicketService.reset();
            insertTicketService.setTicket(ticket);
            insertTicketService.start();
        }
    }
    
    private void runUpdateTicketService() {
        if (!updateTicketService.isRunning()) {
            updateTicketService.reset();
            updateTicketService.setTicket(ticket);
            updateTicketService.start();
        }
    }
    
    private void runCheckForDuplicateTicketService() {
        if (!checkForDuplicateTicketService.isRunning()) {
            checkForDuplicateTicketService.reset();
            checkForDuplicateTicketService.setTicketNumber(ticket.getTicketNumber());
            checkForDuplicateTicketService.start();
        }
    }
    
    private void runGetAllTicketStatusesService() {
        if (!getAllTicketStatusesService.isRunning()) {
            getAllTicketStatusesService.reset();
            getAllTicketStatusesService.start();
        }
    }
    
    private void runGetAllTicketPrioritiesService() {
        if (!getAllTicketPrioritiesService.isRunning()) {
            getAllTicketPrioritiesService.reset();
            getAllTicketPrioritiesService.start();
        }
    }
    
    private void runGetAllContactsService() {
        if (!getAllContactsService.isRunning()) {
            getAllContactsService.reset();
            getAllContactsService.start();
        }
    }
    
    private void runGetAllAssetsService() {
        if (!getAllAssetsService.isRunning()) {
            getAllAssetsService.reset();
            getAllAssetsService.start();
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
    private EventHandler<WorkerStateEvent> checkForDuplicateTicketSuccess = (event) -> {
        if (checkForDuplicateTicketService.getValue()) {
            displaySystemMessage("Ticket number already exists, generating new ticket number.", true);
            ticketNumberLabel.setText(TicketNumberGenerator.generateTicketNumber());
        } else {
            runInsertTicketService();
        }
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateTicketFailure = (event) -> {
        displaySystemMessage("System error, please try your request again.", true);
    };
    
    private EventHandler<WorkerStateEvent> insertTicketSuccess = (event) -> {
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Ticket listing for " 
                + ticket.getTicketNumber()
                + " was successfully added.");
        currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> insertTicketFailure = (event) -> {
        event.getSource().getException().printStackTrace();
        displaySystemMessage("System error on insert, please try your request again.", true);
    };
    
    private EventHandler<WorkerStateEvent> updateTicketSuccess = (event) -> {
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Ticket listing for " 
                + ticket.getTicketNumber()
                + " was successfully updated.");
        currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.close();
    };

    private EventHandler<WorkerStateEvent> updateTicketFailure = (event) -> {
        displaySystemMessage("System error on update, please try your request again.", true);
    };
    
    private EventHandler<WorkerStateEvent> getAllTicketStatusesSuccess = (event) -> {
        allTicketStatusList = getAllTicketStatusesService.getValue();
        runGetAllTicketPrioritiesService();
    };

    private EventHandler<WorkerStateEvent> getAllTicketStatusesFailure = (event) -> {
        event.getSource().getException().printStackTrace();
        displaySystemMessage("System error loading ticket statuses, please try your request again.", true);
        runGetAllTicketPrioritiesService();
    };
    
    private EventHandler<WorkerStateEvent> getAllTicketPrioritiesSuccess = (event) -> {
        allTicketPriorityList = getAllTicketPrioritiesService.getValue();
        runGetAllContactsService();
    };
    
    private EventHandler<WorkerStateEvent> getAllTicketPrioritiesFailure = (event) -> {
        displaySystemMessage("System error loading priorites, please try your request again.", true);
        runGetAllContactsService();
    };

    private EventHandler<WorkerStateEvent> getAllContactsSuccess = (event) -> {
        allContactList = getAllContactsService.getValue();
        runGetAllAssetsService();
    };
    
    private EventHandler<WorkerStateEvent> getAllContactsFailure = (event) -> {
        displaySystemMessage("System error loading contacts, please try your request again.", true);
        runGetAllAssetsService();
    };
    
    private EventHandler<WorkerStateEvent> getAllAssetsSuccess = (event) -> {
        allAssetList = getAllAssetsService.getValue();
        runGetAllProductsService();
    };

    private EventHandler<WorkerStateEvent> getAllAssetsFailure = (event) -> {
        event.getSource().getException().printStackTrace();
        displaySystemMessage("System error loading assets, please try your request again.", true);
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
                insertTicket();
            } else {
                updateTicket();
            }
        }
    }

    @FXML
    private void handleCancelButton(ActionEvent event) {
        formResult = new FormResult(FormResult.FormResultStatus.FAILURE, "Action cancelled by user");
        currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.close();
    }
    
    private void startTicketStatusFocusListener() {
        if (ticketStatusFocusListener == null) {
            ticketStatusFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                    validateTicketStatus();
                }
            };
        }
        ticketStatusComboBox.focusedProperty().addListener(ticketStatusFocusListener);
    }
    
    private void startTicketPriorityFocusListener() {
        if (ticketPriorityFocusListener == null) {
            ticketPriorityFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                    validateTicketPriority();
                }
            };
        }
        ticketPriorityComboBox.focusedProperty().addListener(ticketPriorityFocusListener);
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
        return validateTicketStatus()
            && validateTicketPriority()
            && validateContact()
            && validateTitle()
            && validateDescription();
    }

    private boolean validateTicketStatus() {
        if (ticketStatusComboBox.getSelectionModel().isEmpty()) {
            ticketStatusErrorLabel.setText("Ticket Status is required");
            return false;
        }
        
        ticketStatusErrorLabel.setText("");
        return true;
    }

    private boolean validateTicketPriority() {
        if (ticketPriorityComboBox.getSelectionModel().isEmpty()) {
            ticketPriorityErrorLabel.setText("Ticket Priority is required");
            return false;
        }
        
        ticketPriorityErrorLabel.setText("");
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
