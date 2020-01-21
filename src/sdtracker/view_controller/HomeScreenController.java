/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sdtracker.Session;
import sdtracker.database.AssetDbServiceManager;
import sdtracker.database.AssetDbServiceManager.DeleteAssetService;
import sdtracker.database.AssetDbServiceManager.GetAllAssetsService;
import sdtracker.database.BugDbServiceManager;
import sdtracker.database.BugDbServiceManager.DeleteBugService;
import sdtracker.database.BugDbServiceManager.GetAllBugsService;
import sdtracker.database.ContactDbServiceManager;
import sdtracker.database.ContactDbServiceManager.DeleteContactService;
import sdtracker.database.ContactDbServiceManager.GetAllContactsService;
import sdtracker.database.ProductDbServiceManager;
import sdtracker.database.ProductDbServiceManager.DeleteProductService;
import sdtracker.database.ProductDbServiceManager.GetAllProductsService;
import sdtracker.database.TicketDbServiceManager;
import sdtracker.database.TicketDbServiceManager.*;
import sdtracker.model.AppUser;
import sdtracker.model.Asset;
import sdtracker.model.AssetType;
import sdtracker.model.Bug;
import sdtracker.model.BugPriority;
import sdtracker.model.BugStatus;
import sdtracker.model.Contact;
import sdtracker.model.Mfg;
import sdtracker.model.Product;
import sdtracker.model.Ticket;
import sdtracker.model.TicketPriority;
import sdtracker.model.TicketStatus;
import sdtracker.utility.DateTimeHandler;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class HomeScreenController implements Initializable {
    // Main screen FXML elements (ie elements not in a sub-pane)
    @FXML private Label activeUserLabel;
    @FXML private Label currentDateLabel;
    @FXML private Label statBoxUserLabel;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private HBox statusBarHBox;
    @FXML private VBox navVBox;
    @FXML private Button ticketsNavButton;
    @FXML private Button bugsNavButton;
    @FXML private Button assetsNavButton;
    @FXML private Button productsNavButton;
    @FXML private Button contactsNavButton;
    @FXML private Button reportsNavButton;
    @FXML private Button settingsNavButton;
    @FXML private Label ticketsAssignedLabel;
    @FXML private Label bugsAssignedLabel;
    @FXML private Label avgTicketCloseTimeLabel;
    @FXML private Label avgBugCloseTimeLabel;
    @FXML private Label ticketClosuresLabel;
    @FXML private Label bugClosuresLabel;
    
    // Ticket pane elements
    @FXML private AnchorPane ticketsPane;
    @FXML private Button addTicketButton;
    @FXML private Label ticketPaneTitleLabel;
    @FXML private ComboBox<TicketView> ticketViewComboBox;
    @FXML private TextField ticketNumberSearchField;
    @FXML private ComboBox<TicketPriority> ticketPriorityFilterComboBox;
    @FXML private TextField ticketInfoSearchField;
    @FXML private ComboBox<TicketStatus> ticketStatusFilterComboBox;
    @FXML private TextField ticketAssignedToSearchField;
    @FXML private TableView<Ticket> ticketTableView;
    @FXML private TableColumn<Ticket, Ticket> ticketDeleteColumn;
    @FXML private TableColumn<Ticket, Ticket> ticketEditColumn;
    @FXML private TableColumn<Ticket, String> ticketNumberColumn;
    @FXML private TableColumn<Ticket, TicketPriority> ticketPriorityColumn;
    @FXML private TableColumn<Ticket, String> ticketTitleColumn;
    @FXML private TableColumn<Ticket, TicketStatus> ticketStatusColumn;
    @FXML private TableColumn<Ticket, AppUser> ticketAssignedToColumn;
    @FXML private TableColumn<Ticket, LocalDateTime> ticketCreatedColumn;
    @FXML private TableColumn<Ticket, LocalDateTime> ticketLastUpdatedColumn;
    @FXML private Label systemMessageLabel;
    
    // Bugs pane elements
    @FXML private AnchorPane bugsPane;
    @FXML private Button addBugButton;
    @FXML private Label bugPaneTitleLabel;
    @FXML private ComboBox<BugView> bugViewComboBox;
    @FXML private TextField bugNumberSearchField;
    @FXML private ComboBox<BugPriority> bugPriorityFilterComboBox;
    @FXML private TextField bugInfoSearchField;
    @FXML private ComboBox<BugStatus> bugStatusFilterComboBox;
    @FXML private TextField bugAssignedToSearchField;
    @FXML private TableView<Bug> bugTableView;
    @FXML private TableColumn<Bug, Bug> bugDeleteColumn;
    @FXML private TableColumn<Bug, Bug> bugEditColumn;
    @FXML private TableColumn<Bug, String> bugNumberColumn;
    @FXML private TableColumn<Bug, BugPriority> bugPriorityColumn;
    @FXML private TableColumn<Bug, String> bugTitleColumn;
    @FXML private TableColumn<Bug, BugStatus> bugStatusColumn;
    @FXML private TableColumn<Bug, AppUser> bugAssignedToColumn;
    @FXML private TableColumn<Bug, LocalDateTime> bugCreatedColumn;
    @FXML private TableColumn<Bug, LocalDateTime> bugLastUpdatedColumn;
    
    // Assets pane elements
    @FXML private AnchorPane assetsPane;
    @FXML private Button assetAddButton;
    @FXML private TextField assetNumberSearchField;
    @FXML private TextField assetNameSearchField;
    @FXML private ComboBox<AssetType> assetTypeComboBox;
    @FXML private TextField assetAssignedToSearchField;
    @FXML private ComboBox<Mfg> assetMfgComboBox;
    @FXML private TextField assetModelNumberSearchField;
    @FXML private TableView<Asset> assetTableView;
    @FXML private TableColumn<Asset, Asset> assetDeleteColumn;
    @FXML private TableColumn<Asset, Asset> assetEditColumn;
    @FXML private TableColumn<Asset, String> assetNumberColumn;
    @FXML private TableColumn<Asset, String> assetNameColumn;
    @FXML private TableColumn<Asset, AssetType> assetTypeColumn;
    @FXML private TableColumn<Asset, AppUser> assetAssignedToColumn;
    @FXML private TableColumn<Asset, Mfg> assetMfgColumn;
    @FXML private TableColumn<Asset, String> assetModelNumberColumn;
    
    // Products pane elements
    @FXML private AnchorPane productsPane;
    @FXML private Button productAddButton;
    @FXML private TextField productNameSearchField;
    @FXML private TextField productVersionSearchField;
    @FXML private TableView<Product> productTableView;
    @FXML private TableColumn<Product, Product> productDeleteColumn;
    @FXML private TableColumn<Product, Product> productEditColumn;
    @FXML private TableColumn<Product, String> productNameColumn;
    @FXML private TableColumn<Product, String> productVersionColumn;
    
    // Contacts pane elements
    @FXML private AnchorPane contactsPane;
    @FXML private Button contactAddButton;
    @FXML private TextField contactNameSearchField;
    @FXML private TextField contactEmailSearchField;
    @FXML private TextField contactPhoneSearchField;
    @FXML private TextField contactCompanySearchField;
    @FXML private TableView<Contact> contactTableView;
    @FXML private TableColumn<Contact, Contact> contactDeleteColumn;
    @FXML private TableColumn<Contact, Contact> contactEditColumn;
    @FXML private TableColumn<Contact, Contact> contactNameColumn;
    @FXML private TableColumn<Contact, String> contactEmailColumn;
    @FXML private TableColumn<Contact, String> contactPhoneColumn;
    @FXML private TableColumn<Contact, String> contactCompanyColumn;
    
    //Reports pane elements
    @FXML private AnchorPane reportsPane;
    @FXML private Label report4Label;
    @FXML private Label report1Label;
    @FXML private Label report2Label;
    @FXML private Label report3Label;
    
    // Settings pane elements
    @FXML private AnchorPane settingsPane;
    @FXML private Button changePasswordButton;
    @FXML private AnchorPane systemConfigPane;
    @FXML private Label bugStatusConfigLabel;
    @FXML private Label ticketStatusConfigLabel;
    @FXML private Label ticketPriorityConfigLabel;
    @FXML private Label assetTypeConfigLabel;
    @FXML private Label bugPriorityConfigLabel;
    @FXML private Label contactTypeConfigLabel;
    @FXML private Label mfgConfigLabel;
    @FXML private Label deptConfigLabel;
    @FXML private Label appUserConfigLabel;

    private Session session = Session.getSession();
    
    private TicketDbServiceManager ticketDbServiceManager = TicketDbServiceManager.getServiceManager();
    private GetAllTicketsService getAllTicketsService;
    private DeleteTicketService deleteTicketService;
    
    private BugDbServiceManager bugDbServiceManager = BugDbServiceManager.getServiceManager();
    private GetAllBugsService getAllBugsService;
    private DeleteBugService deleteBugService;
    
    private AssetDbServiceManager assetDbServiceManager = AssetDbServiceManager.getServiceManager();
    private GetAllAssetsService getAllAssetsService;
    private DeleteAssetService deleteAssetService;
    
    private ProductDbServiceManager productDbServiceManager = ProductDbServiceManager.getServiceManager();
    private GetAllProductsService getAllProductsService;
    private DeleteProductService deleteProductService;
    
    private ContactDbServiceManager contactDbServiceManager = ContactDbServiceManager.getServiceManager();
    private GetAllContactsService getAllContactsService;
    private DeleteContactService deleteContactService;
    
    private ObservableList<Ticket> allTicketList;
    private FilteredList<Ticket> filteredTicketList;
    private SortedList<Ticket> sortedTicketList;
    
    private ObservableList<Bug> allBugList;
    private FilteredList<Bug> filteredBugList;
    private SortedList<Bug> sortedBugList;
    
    private ObservableList<Asset> allAssetList;
    private FilteredList<Asset> filteredAssetList;
    private SortedList<Asset> sortedAssetList;
    
    private ObservableList<Product> allProductList;
    private FilteredList<Product> filteredProductList;
    private SortedList<Product> sortedProductList;
    
    private ObservableList<Contact> allContactList;
    private FilteredList<Contact> filteredContactList;
    private SortedList<Contact> sortedContactList;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        // 1) initialize services
        initalizeServices();
        // 2) initialize user specific configuration
        initializeUserEnvironment();
        // 3) establish bindings
        establishBindings();
        // 4) initialize panes
        initializeTicketsPane();
        initializeBugsPane();
        initializeAssetsPane();
        initializeProductsPane();
        initializeContactsPane();
        // 5) load ticket table
        runGetAllTicketsService();
        // 6) TODO generate stats for team & user
        
    }
    
    private void initalizeServices() {
        getAllTicketsService = ticketDbServiceManager.new GetAllTicketsService();
        deleteTicketService = ticketDbServiceManager.new DeleteTicketService();
        getAllBugsService = bugDbServiceManager.new GetAllBugsService();
        deleteBugService = bugDbServiceManager.new DeleteBugService();
        getAllAssetsService = assetDbServiceManager.new GetAllAssetsService();
        deleteAssetService = assetDbServiceManager.new DeleteAssetService();
        getAllProductsService = productDbServiceManager.new GetAllProductsService();
        deleteProductService = productDbServiceManager.new DeleteProductService();
        getAllContactsService = contactDbServiceManager.new GetAllContactsService();
        deleteContactService = contactDbServiceManager.new DeleteContactService();
        
        getAllTicketsService.setOnSucceeded(getAllTicketsSuccess);
        getAllTicketsService.setOnFailed(getAllTicketsFailure);
        getAllBugsService.setOnSucceeded(getAllBugsSuccess);
        getAllBugsService.setOnFailed(getAllBugsFailure);
        getAllAssetsService.setOnSucceeded(getAllAssetssSuccess);
        getAllAssetsService.setOnFailed(getAllAssetssFailure);
        getAllProductsService.setOnSucceeded(getAllProductsSuccess);
        getAllProductsService.setOnFailed(getAllProductsFailure);
        getAllContactsService.setOnSucceeded(getAllContactsSuccess);
        getAllContactsService.setOnFailed(getAllContactsFailure);
        
        deleteTicketService.setOnSucceeded(deleteTicketSuccess);
        deleteTicketService.setOnFailed(deleteTicketFailure);
        deleteBugService.setOnSucceeded(deleteBugSuccess);
        deleteBugService.setOnFailed(deleteBugFailure);
        deleteAssetService.setOnSucceeded(deleteAssetSuccess);
        deleteAssetService.setOnFailed(deleteAssetsFailure);
        deleteProductService.setOnSucceeded(deleteProductSuccess);
        deleteProductService.setOnFailed(deleteProductFailure);
        deleteContactService.setOnSucceeded(deleteContactSuccess);
        deleteContactService.setOnFailed(deleteContactFailure);
    }
    
    private void initializeUserEnvironment() {
        activeUserLabel.setText("Current user: " + session.getSessionUser().getUsername().toUpperCase());
        currentDateLabel.setText(LocalDate.now().format(DateTimeHandler.LONG_DATE));
        statBoxUserLabel.setText(session.getSessionUser().getDisplayName());
        if (session.getSessionUser().getSecurityRole().getId() != 4) {
            systemConfigPane.setVisible(false);
        }
        ticketsPane.toFront();
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = getAllTicketsService.runningProperty()
                                     .or(deleteTicketService.runningProperty())
                                     .or(getAllBugsService.runningProperty())
                                     .or(deleteBugService.runningProperty())
                                     .or(getAllAssetsService.runningProperty())
                                     .or(deleteAssetService.runningProperty())
                                     .or(getAllProductsService.runningProperty())
                                     .or(deleteProductService.runningProperty())
                                     .or(getAllContactsService.runningProperty())
                                     .or(deleteContactService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
        
        ticketPaneTitleLabel.textProperty().bind(ticketViewComboBox.valueProperty().asString());
        bugPaneTitleLabel.textProperty().bind(bugViewComboBox.valueProperty().asString());
    }
    
    private void initializeTicketsPane() {
        initializeTicketViewComboBox();
        initializeTicketTableView();
        initializeTicketFilters();
        
    }
    
    private void initializeBugsPane() {
        initializeBugViewComboBox();
        initializeBugTableView();
        initializeBugFilters();
    }
    
    private void initializeAssetsPane() {
        initializeAssetTableView();
        initializeAssetFilters();
    }
    
    private void initializeProductsPane() {
        initializeProductTableView();
        initializeProductFilters();
    }
    
    private void initializeContactsPane() {
        initializeContactTableView();
        initializeContactFilters();
    }
    
    // Pane inialization methods
    // Ticket Pane
    private void initializeTicketViewComboBox() {
        ticketViewComboBox.getItems().setAll(TicketView.values());
        ticketViewComboBox.getSelectionModel().select(TicketView.MY_TICKETS);
    }
    
    private void initializeTicketTableView() {
        // Delete column
        ticketDeleteColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        ticketDeleteColumn.setStyle("-fx-alignment: CENTER;");
        ticketDeleteColumn.setCellFactory(param -> {
            return new TableCell<Ticket, Ticket>(){
                private Button deleteButton = new Button(null);
                
                @Override
                protected void updateItem(Ticket ticket, boolean empty) {
                    super.updateItem(ticket, empty);
                    
                    if (ticket == null) {
                        setGraphic(null);
                        return;
                    }
                    deleteButton.getStyleClass().add("table-delete-button");
                    setGraphic(deleteButton);
                    deleteButton.setOnAction((event) -> {
                        handleDeleteButton(ticket);
                    });
                }
            };
        });
        
        // Edit column
        ticketEditColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        ticketEditColumn.setStyle("-fx-alignment: CENTER;");
        ticketEditColumn.setCellFactory(param -> {
            return new TableCell<Ticket, Ticket>(){
                private Button editButton = new Button(null);
                
                @Override
                protected void updateItem(Ticket ticket, boolean empty) {
                    super.updateItem(ticket, empty);
                    
                    if (ticket == null) {
                        setGraphic(null);
                        return;
                    }
                    editButton.getStyleClass().add("table-edit-button");
                    setGraphic(editButton);
                    editButton.setOnAction((event) -> {
                        handleEditButton(ticket);
                    });
                }
            };
        });
        
        // Ticket number column
        ticketNumberColumn.setCellValueFactory(new PropertyValueFactory<>("ticketNumber"));
        
        // Ticket priority column
        ticketPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ticketPriorityColumn.setCellFactory(ticket -> {
            return new TableCell<Ticket, TicketPriority>(){
                @Override
                protected void updateItem(TicketPriority ticketPriority, boolean empty) {
                    super.updateItem(ticketPriority, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(ticketPriority.getName());
                    }
                }
            };
        });
        
        // Ticket title column
        ticketTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        // Ticket status column
        ticketStatusColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ticketStatusColumn.setCellFactory(ticket -> {
            return new TableCell<Ticket, TicketStatus>(){
                @Override
                protected void updateItem(TicketStatus ticketStatus, boolean empty) {
                    super.updateItem(ticketStatus, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(ticketStatus.getName());
                    }
                }
            };
        });
        
        // Ticket assigned to column
        ticketAssignedToColumn.setCellValueFactory(new PropertyValueFactory<>("assignedAppUser"));
        ticketAssignedToColumn.setCellFactory(ticket -> {
            return new TableCell<Ticket, AppUser>() {
                @Override
                protected void updateItem(AppUser assignedAppUser, boolean empty) {
                    super.updateItem(assignedAppUser, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(assignedAppUser.getDisplayName());
                    }
                }
            };
        });
        
        // Ticket created date column
        ticketCreatedColumn.setCellValueFactory(new PropertyValueFactory<>("createdTimestamp"));
        ticketCreatedColumn.setCellFactory(ticket -> {
            return new TableCell<Ticket, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime createdTimestamp, boolean empty) {
                    super.updateItem(createdTimestamp, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(createdTimestamp.format(DateTimeHandler.DATE_TIME_STAMP));
                    }
                }
            };
        });
        
        // Ticket created date column
        ticketLastUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedTimestamp"));
        ticketLastUpdatedColumn.setCellFactory(ticket -> {
            return new TableCell<Ticket, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime lastUpdatedTimestamp, boolean empty) {
                    super.updateItem(lastUpdatedTimestamp, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(lastUpdatedTimestamp.format(DateTimeHandler.DATE_TIME_STAMP));
                    }
                }
            };
        });
    }
    
    private void initializeTicketFilters() {
        
    }
    
    // Bug pane
    private void initializeBugViewComboBox() {
        bugViewComboBox.getItems().setAll(BugView.values());
        bugViewComboBox.getSelectionModel().select(BugView.MY_BUGS);
    }
    
    private void initializeBugTableView() {
        // Delete column
        bugDeleteColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        bugDeleteColumn.setStyle("-fx-alignment: CENTER;");
        bugDeleteColumn.setCellFactory(param -> {
            return new TableCell<Bug, Bug>(){
                private Button deleteButton = new Button(null);
                
                @Override
                protected void updateItem(Bug bug, boolean empty) {
                    super.updateItem(bug, empty);
                    
                    if (bug == null) {
                        setGraphic(null);
                        return;
                    }
                    deleteButton.getStyleClass().add("table-delete-button");
                    setGraphic(deleteButton);
                    deleteButton.setOnAction((event) -> {
                        handleDeleteButton(bug);
                    });
                }
            };
        });
        
        // Edit column
        bugEditColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        bugEditColumn.setStyle("-fx-alignment: CENTER;");
        bugEditColumn.setCellFactory(param -> {
            return new TableCell<Bug, Bug>(){
                private Button editButton = new Button(null);
                
                @Override
                protected void updateItem(Bug bug, boolean empty) {
                    super.updateItem(bug, empty);
                    
                    if (bug == null) {
                        setGraphic(null);
                        return;
                    }
                    editButton.getStyleClass().add("table-edit-button");
                    setGraphic(editButton);
                    editButton.setOnAction((event) -> {
                        handleEditButton(bug);
                    });
                }
            };
        });
        
        // Bug number column
        bugNumberColumn.setCellValueFactory(new PropertyValueFactory<>("bugNumber"));
        
        // Bug priority column
        bugPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        bugPriorityColumn.setCellFactory(bug -> {
            return new TableCell<Bug, BugPriority>(){
                @Override
                protected void updateItem(BugPriority bugPriority, boolean empty) {
                    super.updateItem(bugPriority, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(bugPriority.getName());
                    }
                }
            };
        });
        
        // Bug title column
        bugTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        // Bug status column
        bugStatusColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        bugStatusColumn.setCellFactory(bug -> {
            return new TableCell<Bug, BugStatus>(){
                @Override
                protected void updateItem(BugStatus bugStatus, boolean empty) {
                    super.updateItem(bugStatus, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(bugStatus.getName());
                    }
                }
            };
        });
        
        // Bug assigned to column
        bugAssignedToColumn.setCellValueFactory(new PropertyValueFactory<>("assignedAppUser"));
        bugAssignedToColumn.setCellFactory(bug -> {
            return new TableCell<Bug, AppUser>() {
                @Override
                protected void updateItem(AppUser assignedAppUser, boolean empty) {
                    super.updateItem(assignedAppUser, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(assignedAppUser.getDisplayName());
                    }
                }
            };
        });
        
        // Bug created date column
        bugCreatedColumn.setCellValueFactory(new PropertyValueFactory<>("createdTimestamp"));
        bugCreatedColumn.setCellFactory(bug -> {
            return new TableCell<Bug, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime createdTimestamp, boolean empty) {
                    super.updateItem(createdTimestamp, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(createdTimestamp.format(DateTimeHandler.DATE_TIME_STAMP));
                    }
                }
            };
        });
        
        // Bug created date column
        bugLastUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedTimestamp"));
        bugLastUpdatedColumn.setCellFactory(bug -> {
            return new TableCell<Bug, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime lastUpdatedTimestamp, boolean empty) {
                    super.updateItem(lastUpdatedTimestamp, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(lastUpdatedTimestamp.format(DateTimeHandler.DATE_TIME_STAMP));
                    }
                }
            };
        });
    }
    
    private void initializeBugFilters() {
        
    }
    
    // Asset pane
    private void initializeAssetTableView() {
        // Delete column
        assetDeleteColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        assetDeleteColumn.setStyle("-fx-alignment: CENTER;");
        assetDeleteColumn.setCellFactory(param -> {
            return new TableCell<Asset, Asset>(){
                private Button deleteButton = new Button(null);
                
                @Override
                protected void updateItem(Asset asset, boolean empty) {
                    super.updateItem(asset, empty);
                    
                    if (asset == null) {
                        setGraphic(null);
                        return;
                    }
                    deleteButton.getStyleClass().add("table-delete-button");
                    setGraphic(deleteButton);
                    deleteButton.setOnAction((event) -> {
                        handleDeleteButton(asset);
                    });
                }
            };
        });
        
        // Edit column
        assetEditColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        assetEditColumn.setStyle("-fx-alignment: CENTER;");
        assetEditColumn.setCellFactory(param -> {
            return new TableCell<Asset, Asset>(){
                private Button editButton = new Button(null);
                
                @Override
                protected void updateItem(Asset asset, boolean empty) {
                    super.updateItem(asset, empty);
                    
                    if (asset == null) {
                        setGraphic(null);
                        return;
                    }
                    editButton.getStyleClass().add("table-edit-button");
                    setGraphic(editButton);
                    editButton.setOnAction((event) -> {
                        handleEditButton(asset);
                    });
                }
            };
        });
        
        // Asset number column
        assetNumberColumn.setCellValueFactory(new PropertyValueFactory<>("assetNumber"));
        
        // Asset name column
        assetNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Asset type column
        assetTypeColumn.setCellValueFactory(new PropertyValueFactory<>("assetType"));
        assetTypeColumn.setCellFactory(bug -> {
            return new TableCell<Asset, AssetType>(){
                @Override
                protected void updateItem(AssetType assetType, boolean empty) {
                    super.updateItem(assetType, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(assetType.getName());
                    }
                }
            };
        });
        
        // Asset assigned to column
        assetAssignedToColumn.setCellValueFactory(new PropertyValueFactory<>("assignedTo"));
        assetAssignedToColumn.setCellFactory(bug -> {
            return new TableCell<Asset, AppUser>(){
                @Override
                protected void updateItem(AppUser assignedTo, boolean empty) {
                    super.updateItem(assignedTo, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(assignedTo.getDisplayName());
                    }
                }
            };
        });
        
        // Asset mfg column
        assetMfgColumn.setCellValueFactory(new PropertyValueFactory<>("mfg"));
        assetMfgColumn.setCellFactory(bug -> {
            return new TableCell<Asset, Mfg>(){
                @Override
                protected void updateItem(Mfg mfg, boolean empty) {
                    super.updateItem(mfg, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(mfg.getName());
                    }
                }
            };
        });
        
        assetModelNumberColumn.setCellValueFactory(new PropertyValueFactory<>("modelNumber"));
    }
    
    private void initializeAssetFilters() {
        
    }
    
    // Product pane
    private void initializeProductTableView() {
        // Delete column
        productDeleteColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        productDeleteColumn.setStyle("-fx-alignment: CENTER;");
        productDeleteColumn.setCellFactory(param -> {
            return new TableCell<Product, Product>(){
                private Button deleteButton = new Button(null);
                
                @Override
                protected void updateItem(Product product, boolean empty) {
                    super.updateItem(product, empty);
                    
                    if (product == null) {
                        setGraphic(null);
                        return;
                    }
                    deleteButton.getStyleClass().add("table-delete-button");
                    setGraphic(deleteButton);
                    deleteButton.setOnAction((event) -> {
                        handleDeleteButton(product);
                    });
                }
            };
        });
        
        // Edit column
        productEditColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        productEditColumn.setStyle("-fx-alignment: CENTER;");
        productEditColumn.setCellFactory(param -> {
            return new TableCell<Product, Product>(){
                private Button editButton = new Button(null);
                
                @Override
                protected void updateItem(Product product, boolean empty) {
                    super.updateItem(product, empty);
                    
                    if (product == null) {
                        setGraphic(null);
                        return;
                    }
                    editButton.getStyleClass().add("table-edit-button");
                    setGraphic(editButton);
                    editButton.setOnAction((event) -> {
                        handleEditButton(product);
                    });
                }
            };
        });

        // Product name column
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Product version column
        productVersionColumn.setCellValueFactory(new PropertyValueFactory<>("version"));
    }
    
    private void initializeProductFilters() {
        
    }
    
    // Contact pane
    private void initializeContactTableView() {
        // Delete column
        contactDeleteColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        contactDeleteColumn.setStyle("-fx-alignment: CENTER;");
        contactDeleteColumn.setCellFactory(param -> {
            return new TableCell<Contact, Contact>(){
                private Button deleteButton = new Button(null);
                
                @Override
                protected void updateItem(Contact contact, boolean empty) {
                    super.updateItem(contact, empty);
                    
                    if (contact == null) {
                        setGraphic(null);
                        return;
                    }
                    deleteButton.getStyleClass().add("table-delete-button");
                    setGraphic(deleteButton);
                    deleteButton.setOnAction((event) -> {
                        handleDeleteButton(contact);
                    });
                }
            };
        });
        
        // Edit column
        contactEditColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        contactEditColumn.setStyle("-fx-alignment: CENTER;");
        contactEditColumn.setCellFactory(param -> {
            return new TableCell<Contact, Contact>(){
                private Button editButton = new Button(null);
                
                @Override
                protected void updateItem(Contact contact, boolean empty) {
                    super.updateItem(contact, empty);
                    
                    if (contact == null) {
                        setGraphic(null);
                        return;
                    }
                    editButton.getStyleClass().add("table-edit-button");
                    setGraphic(editButton);
                    editButton.setOnAction((event) -> {
                        handleEditButton(contact);
                    });
                }
            };
        });

        // Contact name column
        contactNameColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        contactNameColumn.setCellFactory(contact -> {
            return new TableCell<Contact, Contact>(){
              @Override
              protected void updateItem(Contact contact, boolean empty) {
                  if (contact == null) {
                      setText("");
                  } else {
                      setText(contact.getDisplayName());
                  }
              }
            };
        });
    }
    
    private void initializeContactFilters() {

    }
    
    // Table load methods
    private void loadTicketTableView() {
        filteredTicketList = new FilteredList<>(allTicketList, (pred) -> true);
        sortedTicketList = new SortedList<>(filteredTicketList);
        sortedTicketList.comparatorProperty().bind(ticketTableView.comparatorProperty());
        // TODO filters
        ticketTableView.setItems(sortedTicketList);

        ticketDeleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
    }
    
    private void loadBugTableView() {
        filteredBugList = new FilteredList<>(allBugList, (pred) -> true);
        sortedBugList = new SortedList<>(filteredBugList);
        sortedBugList.comparatorProperty().bind(bugTableView.comparatorProperty());
        // TODO filters
        bugTableView.setItems(sortedBugList);

        bugDeleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
    }
    
    private void loadAssetTableView() {
        filteredAssetList = new FilteredList<>(allAssetList, (pred) -> true);
        sortedAssetList = new SortedList<>(filteredAssetList);
        sortedAssetList.comparatorProperty().bind(assetTableView.comparatorProperty());
        // TODO filters
        assetTableView.setItems(sortedAssetList);

        assetDeleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
    }
    
    private void loadProductTableView() {
        filteredProductList = new FilteredList<>(allProductList, (pred) -> true);
        sortedProductList = new SortedList<>(filteredProductList);
        sortedProductList.comparatorProperty().bind(productTableView.comparatorProperty());
        // TODO filters
        productTableView.setItems(sortedProductList);

        productDeleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
    }
    
    private void loadContactTableView() {
        filteredContactList = new FilteredList<>(allContactList, (pred) -> true);
        sortedContactList = new SortedList<>(filteredContactList);
        sortedContactList.comparatorProperty().bind(contactTableView.comparatorProperty());
        // TODO filters
        contactTableView.setItems(sortedContactList);

        contactDeleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
    }
    
    // Event handlers
    
    private <T> void handleDeleteButton(T item) {
        if (item.getClass() == Ticket.class) {
            runDeleteTicketService((Ticket) item);
            return;
        }
        
        if (item.getClass() == Bug.class) {
            runDeleteBugService((Bug) item);
            return;
        }
        
        if (item.getClass() == Asset.class) {
            runDeleteAssetService((Asset) item);
            return;
        }
        if (item.getClass() == Product.class) {
            runDeleteProductService((Product) item);
            return;
        }
        
        if (item.getClass() == Contact.class) {
            runDeleteContactService((Contact) item);
        }
    }
    
    private <T> void handleEditButton(T item) {
        if (item.getClass() == Ticket.class) {
            System.out.println("Ticket update clicked");
            return;
        }
        
        if (item.getClass() == Bug.class) {
            System.out.println("Bug update clicked");
            return;
        }
        
        if (item.getClass() == Asset.class) {
            System.out.println("Asset update clicked");
            return;
        }
        if (item.getClass() == Product.class) {
            System.out.println("Product update clicked");
            return;
        }
        
        if (item.getClass() == Contact.class) {
            System.out.println("Contact update clicked");
        }
    }
    
    @FXML
    private void handleTicketsNavButton(ActionEvent event) {
        ticketsPane.toFront();
        runGetAllTicketsService();
    }
    
    @FXML
    private void handleBugsNavButton(ActionEvent event) {
        bugsPane.toFront();
        runGetAllBugsService();
    }
    
    @FXML
    private void handleAssetsNavButton(ActionEvent event) {
        assetsPane.toFront();
        runGetAllAssetsService();
    }
    
    @FXML
    private void handleProductsNavButton(ActionEvent event) {
        productsPane.toFront();
        runGetAllProductsService();
    }
    
    @FXML
    private void handleContactsNavButton(ActionEvent event) {
        contactsPane.toFront();
        runGetAllContactsService();
    }
    
    @FXML
    private void handleReportsNavButton(ActionEvent event) {
        reportsPane.toFront();
    }
    
    @FXML
    private void handleSettingsNavButton(ActionEvent event) {
        settingsPane.toFront();
    }
    
    // Service run handlers
    private void runGetAllTicketsService() {
        if (!getAllTicketsService.isRunning()) {
            getAllTicketsService.reset();
            getAllTicketsService.start();
        }
    }
    
    private void runDeleteTicketService(Ticket ticket) {
        if (!deleteTicketService.isRunning()) {
            deleteTicketService.reset();
            deleteTicketService.setTicket(ticket);
            deleteTicketService.start();
        }
    }
    
    private void runGetAllBugsService() {
        if (!getAllBugsService.isRunning()) {
            getAllBugsService.reset();
            getAllBugsService.start();
        }
    }
    
    private void runDeleteBugService(Bug bug) {
        if (!deleteBugService.isRunning()) {
            deleteBugService.reset();
            deleteBugService.setBug(bug);
            deleteBugService.start();
        }
    }
    
    private void runGetAllAssetsService() {
        if (!getAllAssetsService.isRunning()) {
            getAllAssetsService.reset();
            getAllAssetsService.start();
        }
    }
    
    private void runDeleteAssetService(Asset asset) {
        if (!deleteAssetService.isRunning()) {
            deleteAssetService.reset();
            deleteAssetService.setAsset(asset);
            deleteAssetService.start();
        }
    }
    
    private void runGetAllProductsService() {
        if (!getAllProductsService.isRunning()) {
            getAllProductsService.reset();
            getAllProductsService.start();
        }
    }
    
    private void runDeleteProductService(Product product) {
        if (!deleteProductService.isRunning()) {
            deleteProductService.reset();
            deleteProductService.setProduct(product);
            deleteProductService.start();
        }
    }
    
    private void runGetAllContactsService() {
        if (!getAllContactsService.isRunning()) {
            getAllContactsService.reset();
            getAllContactsService.start();
        }
    }
    
    private void runDeleteContactService(Contact contact) {
        if (!deleteContactService.isRunning()) {
            deleteContactService.reset();
            deleteContactService.setContact(contact);
            deleteContactService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> getAllTicketsSuccess = (event) -> {
        allTicketList = getAllTicketsService.getValue();
        loadTicketTableView();
    };
    
    private EventHandler<WorkerStateEvent> getAllTicketsFailure = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> getAllBugsSuccess = (event) -> {
        allBugList = getAllBugsService.getValue();
        loadBugTableView();
    };
    
    private EventHandler<WorkerStateEvent> getAllBugsFailure = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> getAllAssetssSuccess = (event) -> {
        allAssetList = getAllAssetsService.getValue();
        loadAssetTableView();
    };
    
    private EventHandler<WorkerStateEvent> getAllAssetssFailure = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> getAllProductsSuccess = (event) -> {
        allProductList = getAllProductsService.getValue();
        loadProductTableView();
    };
    
    private EventHandler<WorkerStateEvent> getAllProductsFailure = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> getAllContactsSuccess = (event) -> {
        allContactList = getAllContactsService.getValue();
        loadContactTableView();
    };
    
    private EventHandler<WorkerStateEvent> getAllContactsFailure = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> deleteTicketSuccess = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> deleteTicketFailure = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> deleteBugSuccess = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> deleteBugFailure = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> deleteAssetSuccess = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> deleteAssetsFailure = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> deleteProductSuccess = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> deleteProductFailure = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> deleteContactSuccess = (event) -> {
        // TODO
    };
    
    private EventHandler<WorkerStateEvent> deleteContactFailure = (event) -> {
        // TODO
    };
    
    private enum TicketView {
        MY_TICKETS("My Tickets"),
        ALL_TICKETS("All Tickets"),
        MY_OPEN_TICKETS("My Open Tickets"),
        ALL_OPEN_TICKETS("All Open Tickets"),
        NEW_TICKETS("New Tickets");
        
        private String label;
        
        TicketView(String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
    private enum BugView {
        MY_BUGS("My Bugs"),
        ALL_BUGS("All Bugs"),
        MY_OPEN_BUGS("My Open Bugs"),
        ALL_OPEN_BUGS("All Open Bugs"),
        NEW_BUGS("New Bugs");
        
        private String label;
        
        BugView(String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
}