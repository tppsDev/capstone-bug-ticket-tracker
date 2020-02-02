/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sdtracker.Session;
import sdtracker.database.AssetDbServiceManager;
import sdtracker.database.AssetDbServiceManager.DeleteAssetService;
import sdtracker.database.AssetDbServiceManager.GetAllAssetsService;
import sdtracker.database.AssetTypeDbServiceManager;
import sdtracker.database.AssetTypeDbServiceManager.GetAllAssetTypesService;
import sdtracker.database.BugDbServiceManager;
import sdtracker.database.BugDbServiceManager.DeleteBugService;
import sdtracker.database.BugDbServiceManager.GetAllBugsService;
import sdtracker.database.BugPriorityDbServiceManager;
import sdtracker.database.BugPriorityDbServiceManager.GetAllBugPrioritiesService;
import sdtracker.database.BugStatusDbServiceManager;
import sdtracker.database.BugStatusDbServiceManager.GetAllBugStatusesService;
import sdtracker.database.ContactDbServiceManager;
import sdtracker.database.ContactDbServiceManager.DeleteContactService;
import sdtracker.database.ContactDbServiceManager.GetAllContactsService;
import sdtracker.database.MfgDbServiceManager;
import sdtracker.database.MfgDbServiceManager.GetAllMfgsService;
import sdtracker.database.ProductDbServiceManager;
import sdtracker.database.ProductDbServiceManager.DeleteProductService;
import sdtracker.database.ProductDbServiceManager.GetAllProductsService;
import sdtracker.database.TeamStatBoardDbServiceManager;
import sdtracker.database.TeamStatBoardDbServiceManager.GetTeamStatBoardService;
import sdtracker.database.TicketDbServiceManager;
import sdtracker.database.TicketDbServiceManager.*;
import sdtracker.database.TicketPriorityDbServiceManager;
import sdtracker.database.TicketPriorityDbServiceManager.GetAllTicketPrioritiesService;
import sdtracker.database.TicketStatusDbServiceManager;
import sdtracker.database.TicketStatusDbServiceManager.GetAllTicketStatusesService;
import sdtracker.database.UserStatBoardDbServiceManager;
import sdtracker.database.UserStatBoardDbServiceManager.GetUserStatBoardService;
import sdtracker.model.AppUser;
import sdtracker.model.Asset;
import sdtracker.model.AssetType;
import sdtracker.model.Bug;
import sdtracker.model.BugPriority;
import sdtracker.model.BugStatus;
import sdtracker.model.Contact;
import sdtracker.model.Mfg;
import sdtracker.model.Product;
import sdtracker.model.TeamStatBoard;
import sdtracker.model.Ticket;
import sdtracker.model.TicketPriority;
import sdtracker.model.TicketStatus;
import sdtracker.model.UserStatBoard;
import sdtracker.utility.DateTimeHandler;
import static sdtracker.view_controller.FormResult.FormResultStatus.SUCCESS;


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
    @FXML private Label totalNotClosedTickets;
    @FXML private Label totalOpenTickets;
    @FXML private Label totalNotClosedBugs;
    @FXML private Label totalOpenBugs;
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
    @FXML private ImageView ticketStatusClearFilterImageView;
    @FXML private ImageView ticketPriorityClearFilterImageView;
    @FXML private ImageView ticketNumberClearFilterImageView;
    @FXML private ImageView ticketContentClearFilterImageView;
    @FXML private Label systemMessageLabel;
    
    private ObjectProperty<Predicate<Ticket>> myTicketsFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Ticket>> ticketNumberFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Ticket>> ticketPriorityFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Ticket>> ticketContentFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Ticket>> ticketStatusFilter = new SimpleObjectProperty<>();
    
    private ObservableList<TicketPriority> allTicketPrioritiesList = FXCollections.observableArrayList();
    private ObservableList<TicketStatus> allTicketStatusesList = FXCollections.observableArrayList();
    
    // Bugs pane elements
    @FXML private AnchorPane bugsPane;
    @FXML private Button addBugButton;
    @FXML private Label bugPaneTitleLabel;
    @FXML private ComboBox<BugView> bugViewComboBox;
    @FXML private TextField bugNumberSearchField;
    @FXML private ComboBox<BugPriority> bugPriorityFilterComboBox;
    @FXML private TextField bugInfoSearchField;
    @FXML private ComboBox<BugStatus> bugStatusFilterComboBox;
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
    @FXML private ImageView bugStatusClearFilterImageView;
    @FXML private ImageView bugPriorityClearFilterImageView;
    @FXML private ImageView bugNumberClearFilterImageView;
    @FXML private ImageView bugContentClearFilterImageView;
    
    private ObjectProperty<Predicate<Bug>> myBugsFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Bug>> bugNumberFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Bug>> bugPriorityFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Bug>> bugContentFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Bug>> bugStatusFilter = new SimpleObjectProperty<>();
    
    private ObservableList<BugPriority> allBugPrioritiesList = FXCollections.observableArrayList();
    private ObservableList<BugStatus> allBugStatusesList = FXCollections.observableArrayList();
    
    // Assets pane elements
    @FXML private AnchorPane assetsPane;
    @FXML private Button assetAddButton;
    @FXML private TextField assetNumberSearchField;
    @FXML private ComboBox<AssetType> assetTypeComboBox;
    @FXML private TextField assetAssignedToSearchField;
    @FXML private ComboBox<Mfg> assetMfgComboBox;
    @FXML private TextField assetInfoSearchField;
    @FXML private TableView<Asset> assetTableView;
    @FXML private TableColumn<Asset, Asset> assetDeleteColumn;
    @FXML private TableColumn<Asset, Asset> assetEditColumn;
    @FXML private TableColumn<Asset, String> assetNumberColumn;
    @FXML private TableColumn<Asset, String> assetNameColumn;
    @FXML private TableColumn<Asset, AssetType> assetTypeColumn;
    @FXML private TableColumn<Asset, AppUser> assetAssignedToColumn;
    @FXML private TableColumn<Asset, Mfg> assetMfgColumn;
    @FXML private TableColumn<Asset, String> assetModelNumberColumn;
    @FXML private ImageView assetAssignedToClearFilterImageView;
    @FXML private ImageView assetTypeClearFilterImageView;
    @FXML private ImageView assetMfgClearFilterImageView;
    @FXML private ImageView assetNumberClearFilterImageView;
    @FXML private ImageView assetInfoClearFilterImageView;
    
    private ObjectProperty<Predicate<Asset>> assetTypeFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Asset>> assetNumberFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Asset>> assetAssignedToFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Asset>> assetInfoFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Asset>> assetMfgFilter = new SimpleObjectProperty<>();
    
    private ObservableList<AssetType> allAssetTypesList = FXCollections.observableArrayList();
    private ObservableList<Mfg> allMfgsList = FXCollections.observableArrayList();
    
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
    @FXML private Label report1Label;
    @FXML private Label report2Label;
    
    // Settings pane elements
    @FXML private AnchorPane settingsPane;
    @FXML private Button changePasswordButton;
    @FXML private AnchorPane systemConfigPane;
    @FXML private Label assetTypeConfigLabel;
    @FXML private Label contactTypeConfigLabel;
    @FXML private Label mfgConfigLabel;
    @FXML private Label deptConfigLabel;
    @FXML private Label appUserConfigLabel;

    private Session session = Session.getSession();
    
    private TicketDbServiceManager ticketDbServiceManager = TicketDbServiceManager.getServiceManager();
    private GetAllTicketsService getAllTicketsService;
    private DeleteTicketService deleteTicketService;
    
    private TicketPriorityDbServiceManager ticketPriorityDbServiceManager = TicketPriorityDbServiceManager.getServiceManager();
    private GetAllTicketPrioritiesService getAllTicketPrioritiesService;
    
    private TicketStatusDbServiceManager ticketStatusDbServiceManager = TicketStatusDbServiceManager.getServiceManager();
    private GetAllTicketStatusesService getAllTicketStatusesService;
    
    private BugDbServiceManager bugDbServiceManager = BugDbServiceManager.getServiceManager();
    private GetAllBugsService getAllBugsService;
    private DeleteBugService deleteBugService;
    
    private BugPriorityDbServiceManager bugPriorityDbServiceManager = BugPriorityDbServiceManager.getServiceManager();
    private GetAllBugPrioritiesService getAllBugPrioritiesService;
    
    private BugStatusDbServiceManager bugStatusDbServiceManager = BugStatusDbServiceManager.getServiceManager();
    private GetAllBugStatusesService getAllBugStatusesService;
    
    private AssetDbServiceManager assetDbServiceManager = AssetDbServiceManager.getServiceManager();
    private GetAllAssetsService getAllAssetsService;
    private DeleteAssetService deleteAssetService;
    
    private AssetTypeDbServiceManager assetTypeDbServiceManager = AssetTypeDbServiceManager.getServiceManager();
    private GetAllAssetTypesService getAllAssetTypesService;
    
    private MfgDbServiceManager mfgDbServiceManager = MfgDbServiceManager.getServiceManager();
    private GetAllMfgsService getAllMfgsService;
    
    private ProductDbServiceManager productDbServiceManager = ProductDbServiceManager.getServiceManager();
    private GetAllProductsService getAllProductsService;
    private DeleteProductService deleteProductService;
    
    private ContactDbServiceManager contactDbServiceManager = ContactDbServiceManager.getServiceManager();
    private GetAllContactsService getAllContactsService;
    private DeleteContactService deleteContactService;
    
    private TeamStatBoardDbServiceManager teamStatBoardDbServiceManager = TeamStatBoardDbServiceManager.
            getServiceManager();
    private GetTeamStatBoardService getTeamStatBoardService;
    private TeamStatBoard teamStatBoard;
    
    private UserStatBoardDbServiceManager userStatBoardDbServiceManager = UserStatBoardDbServiceManager.
            getServiceManager();
    private GetUserStatBoardService getUserStatBoardService;
    private UserStatBoard userStatBoard;
    
    private ObservableList<Ticket> allTicketList = FXCollections.observableArrayList();
    private FilteredList<Ticket> filteredTicketList;
    private SortedList<Ticket> sortedTicketList;
    
    private ObservableList<Bug> allBugList = FXCollections.observableArrayList();
    private FilteredList<Bug> filteredBugList;
    private SortedList<Bug> sortedBugList;
    
    private ObservableList<Asset> allAssetList = FXCollections.observableArrayList();
    private FilteredList<Asset> filteredAssetList;
    private SortedList<Asset> sortedAssetList;
    
    private ObservableList<Product> allProductList = FXCollections.observableArrayList();
    private FilteredList<Product> filteredProductList;
    private SortedList<Product> sortedProductList;
    
    private ObservableList<Contact> allContactList = FXCollections.observableArrayList();
    private FilteredList<Contact> filteredContactList;
    private SortedList<Contact> sortedContactList;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initalizeServices();
        initializeUserEnvironment();
        establishBindings();
        initializeTicketsPane();
        initializeBugsPane();
        initializeAssetsPane();
        initializeProductsPane();
        initializeContactsPane();
        runGetAllTicketsService();
        runGetAllTicketPrioritiesService();
        runGetAllTicketStatusesService();
        runGetAllBugPrioritiesService();
        runGetAllBugStatusesService();
        runGetAllAssetTypesService();
        runGetAllMfgsService();
        startClickHandlers();
    }
    
    private void initalizeServices() {
        getAllTicketsService = ticketDbServiceManager.new GetAllTicketsService();
        deleteTicketService = ticketDbServiceManager.new DeleteTicketService();
        getAllTicketPrioritiesService = ticketPriorityDbServiceManager.new GetAllTicketPrioritiesService();
        getAllTicketStatusesService = ticketStatusDbServiceManager.new GetAllTicketStatusesService();
        
        getAllBugsService = bugDbServiceManager.new GetAllBugsService();
        deleteBugService = bugDbServiceManager.new DeleteBugService();
        getAllBugPrioritiesService = bugPriorityDbServiceManager.new GetAllBugPrioritiesService();
        getAllBugStatusesService = bugStatusDbServiceManager.new GetAllBugStatusesService();
        
        getAllAssetsService = assetDbServiceManager.new GetAllAssetsService();
        deleteAssetService = assetDbServiceManager.new DeleteAssetService();
        getAllAssetTypesService = assetTypeDbServiceManager.new  GetAllAssetTypesService();
        getAllMfgsService = mfgDbServiceManager.new  GetAllMfgsService();
        
        getAllProductsService = productDbServiceManager.new GetAllProductsService();
        deleteProductService = productDbServiceManager.new DeleteProductService();
        
        getAllContactsService = contactDbServiceManager.new GetAllContactsService();
        deleteContactService = contactDbServiceManager.new DeleteContactService();
       
        getTeamStatBoardService = teamStatBoardDbServiceManager.new GetTeamStatBoardService();
        getUserStatBoardService = userStatBoardDbServiceManager.new GetUserStatBoardService();
        
        getAllTicketsService.setOnSucceeded(getAllTicketsSuccess);
        getAllTicketsService.setOnFailed(getAllTicketsFailure);
        getAllTicketPrioritiesService.setOnSucceeded(getAllTicketPrioritiesSuccess);
        getAllTicketPrioritiesService.setOnFailed(getAllTicketPrioritiesFailure);
        getAllTicketStatusesService.setOnSucceeded(getAllTicketStatusesSuccess);
        getAllTicketStatusesService.setOnFailed(getAllTicketStatusesFailure);
        
        getAllBugsService.setOnSucceeded(getAllBugsSuccess);
        getAllBugsService.setOnFailed(getAllBugsFailure);
        getAllBugPrioritiesService.setOnSucceeded(getAllBugPrioritiesSuccess);
        getAllBugPrioritiesService.setOnFailed(getAllBugPrioritiesFailure);
        getAllBugStatusesService.setOnSucceeded(getAllBugStatusesSuccess);
        getAllBugStatusesService.setOnFailed(getAllBugStatusesFailure);
        
        getAllAssetsService.setOnSucceeded(getAllAssetsSuccess);
        getAllAssetsService.setOnFailed(getAllAssetsFailure);
        getAllAssetTypesService.setOnSucceeded(getAllAssetTypesSuccess);
        getAllAssetTypesService.setOnFailed(getAllAssetTypesFailure);
        getAllMfgsService.setOnSucceeded(getAllMfgsSuccess);
        getAllMfgsService.setOnFailed(getAllMfgsFailure);
        
        getAllProductsService.setOnSucceeded(getAllProductsSuccess);
        getAllProductsService.setOnFailed(getAllProductsFailure);
        
        getAllContactsService.setOnSucceeded(getAllContactsSuccess);
        getAllContactsService.setOnFailed(getAllContactsFailure);
        
        getTeamStatBoardService.setOnSucceeded(getTeamStatBoardSuccess);
        getTeamStatBoardService.setOnFailed(getTeamStatBoardFailure);
        
        getUserStatBoardService.setOnSucceeded(getUserStatBoardSuccess);
        getUserStatBoardService.setOnFailed(getUserStatBoardFailure);
        
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
    
    private void bindTicketFilters() {
        myTicketsFilter.bind(Bindings.createObjectBinding(() ->
            ticket -> {
                if (ticketViewComboBox.getValue().equals(TicketView.MY_TICKETS)) {
                    return ticket.getAssignedAppUser() != null ?
                        ticket.getAssignedAppUser().getId() == session.getSessionUser().getId()
                            :
                        false;
                } else {
                    return true;
                }
        },
            ticketViewComboBox.valueProperty()
        ));
        
        ticketNumberFilter.bind(Bindings.createObjectBinding(() ->
            ticket -> {
                if (ticketNumberSearchField.getText() != null || !ticketNumberSearchField.getText().isEmpty()) {
                    return ticket.getTicketNumber().toLowerCase().contains(ticketNumberSearchField.getText().toLowerCase());
                } else {
                    return true;
                }
            },
            ticketNumberSearchField.textProperty()
        ));
        
        ticketPriorityFilter.bind(Bindings.createObjectBinding(() ->
            ticket -> {
                if (ticketPriorityFilterComboBox.getValue() != null) {
                    return ticket.getPriority().equals(ticketPriorityFilterComboBox.getValue());
                } else {
                    return true;
                }
            },
            ticketPriorityFilterComboBox.valueProperty()
        ));
        
        ticketContentFilter.bind(Bindings.createObjectBinding(() ->
            ticket -> {
                if (ticketInfoSearchField.getText() != null || !ticketInfoSearchField.getText().isEmpty()) {
                    return ticket.getTitle().toLowerCase().contains(ticketInfoSearchField.getText().toLowerCase())
                        || ticket.getDescription().toLowerCase().contains(ticketInfoSearchField.getText().toLowerCase());
                } else {
                    return true;
                }
            },
            ticketInfoSearchField.textProperty()
        ));
        
        ticketStatusFilter.bind(Bindings.createObjectBinding(() ->
            ticket -> {
                if (ticketStatusFilterComboBox.getValue() != null) {
                    return ticket.getStatus().equals(ticketStatusFilterComboBox.getValue());
                } else {
                    return true;
                }
            },
            ticketStatusFilterComboBox.valueProperty()
        ));
        
        filteredTicketList.predicateProperty().bind(Bindings.createObjectBinding(
            () -> myTicketsFilter.get()
                .and(ticketNumberFilter.get())
                .and(ticketPriorityFilter.get())
                .and(ticketContentFilter.get())
                .and(ticketStatusFilter.get()),
            myTicketsFilter, ticketNumberFilter, ticketPriorityFilter, ticketContentFilter, ticketStatusFilter)
        );
        
        filteredTicketList.predicateProperty().addListener((observable) -> {
            ticketTableView.refresh();
        });
    }
    
    private void bindBugFilters() {
        myBugsFilter.bind(Bindings.createObjectBinding(() ->
            bug -> {
                if (bugViewComboBox.getValue().equals(BugView.MY_BUGS)) {
                    return bug.getAssignedAppUser() != null ?
                        bug.getAssignedAppUser().getId() == session.getSessionUser().getId()
                            :
                        false;
                } else {
                    return true;
                }
        },
            bugViewComboBox.valueProperty()
        ));
        
        bugNumberFilter.bind(Bindings.createObjectBinding(() ->
            bug -> {
                if (bugNumberSearchField.getText() != null || !bugNumberSearchField.getText().isEmpty()) {
                    return bug.getBugNumber().toLowerCase().contains(bugNumberSearchField.getText().toLowerCase());
                } else {
                    return true;
                }
            },
            bugNumberSearchField.textProperty()
        ));
        
        bugPriorityFilter.bind(Bindings.createObjectBinding(() ->
            bug -> {
                if (bugPriorityFilterComboBox.getValue() != null) {
                    return bug.getPriority().equals(bugPriorityFilterComboBox.getValue());
                } else {
                    return true;
                }
            },
            bugPriorityFilterComboBox.valueProperty()
        ));
        
        bugContentFilter.bind(Bindings.createObjectBinding(() ->
            bug -> {
                if (bugInfoSearchField.getText() != null || !bugInfoSearchField.getText().isEmpty()) {
                    return bug.getTitle().toLowerCase().contains(bugInfoSearchField.getText().toLowerCase())
                        || bug.getDescription().toLowerCase().contains(bugInfoSearchField.getText().toLowerCase());
                } else {
                    return true;
                }
            },
            bugInfoSearchField.textProperty()
        ));
        
        bugStatusFilter.bind(Bindings.createObjectBinding(() ->
            bug -> {
                if (bugStatusFilterComboBox.getValue() != null) {
                    return bug.getStatus().equals(bugStatusFilterComboBox.getValue());
                } else {
                    return true;
                }
            },
            bugStatusFilterComboBox.valueProperty()
        ));
        
        filteredBugList.predicateProperty().bind(Bindings.createObjectBinding(
            () -> myBugsFilter.get()
                .and(bugNumberFilter.get())
                .and(bugPriorityFilter.get())
                .and(bugContentFilter.get())
                .and(bugStatusFilter.get()),
            myBugsFilter, bugNumberFilter, bugPriorityFilter, bugContentFilter, bugStatusFilter)
        );
        
        filteredBugList.predicateProperty().addListener((observable) -> {
            bugTableView.refresh();
        });
    }
    
    private void bindAssetFilters() {
        assetTypeFilter.bind(Bindings.createObjectBinding(() ->
            asset ->{
                if (assetTypeComboBox.getValue() != null) {
                    return asset.getAssetType().equals(assetTypeComboBox.getValue());
                } else {
                    return true;
                }
            },
            assetTypeComboBox.valueProperty()
        ));
        
        assetNumberFilter.bind(Bindings.createObjectBinding(() ->
            asset ->{
                if (assetNumberSearchField.getText() != null || !assetNumberSearchField.getText().isEmpty()) {
                    return asset.getAssetNumber().toLowerCase().contains(assetNumberSearchField.getText().toLowerCase());
                } else {
                    return true;
                }
            },
            assetNumberSearchField.textProperty()
        ));
        
        assetAssignedToFilter.bind(Bindings.createObjectBinding(() ->
            asset ->{
                if (assetAssignedToSearchField.getText() != null || !assetAssignedToSearchField.getText().isEmpty()) {
                    return asset.getAssignedToAppUser().getDisplayName().toLowerCase()
                            .contains(assetAssignedToSearchField.getText().toLowerCase());
                } else {
                    return true;
                }
            },
            assetAssignedToSearchField.textProperty()
        ));
        
        assetInfoFilter.bind(Bindings.createObjectBinding(() ->
            asset ->{
                if (assetInfoSearchField.getText() != null || !assetInfoSearchField.getText().isEmpty()) {
                    return asset.getName().toLowerCase().contains(assetInfoSearchField.getText().toLowerCase())
                        || asset.getModelNumber().toLowerCase().contains(assetInfoSearchField.getText().toLowerCase())
                        || asset.getSerialNumber().toLowerCase().contains(assetInfoSearchField.getText().toLowerCase());
                } else {
                    return true;
                }
            },
            assetInfoSearchField.textProperty()
        ));
        
        assetMfgFilter.bind(Bindings.createObjectBinding(() ->
            asset ->{
                if (assetMfgComboBox.getValue() != null) {
                    return asset.getMfg().equals(assetMfgComboBox.getValue());
                } else {
                    return true;
                }
            },
            assetMfgComboBox.valueProperty()
        ));
        
        filteredAssetList.predicateProperty().bind(Bindings.createObjectBinding(
            () -> assetTypeFilter.get()
             .and(assetNumberFilter.get())
             .and(assetAssignedToFilter.get())
             .and(assetInfoFilter.get())
             .and(assetMfgFilter.get()),
            assetTypeFilter, assetNumberFilter, assetAssignedToFilter, assetInfoFilter, assetMfgFilter)
        );
        
        filteredAssetList.predicateProperty().addListener((observable) -> {
            assetTableView.refresh();
        });
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
    
    private void displayStats() {
        runGetTeamStatBoardService();
        runGetUserStatBoardService();
    }
    
    private void displayTeamStats() {
        totalNotClosedTickets.setText(String.valueOf(teamStatBoard.getTotalNotClosedTickets()));
        totalOpenTickets.setText(String.valueOf(teamStatBoard.getTotalOpenTickets()));
        totalNotClosedBugs.setText(String.valueOf(teamStatBoard.getTotalNotClosedBugs()));
        totalOpenBugs.setText(String.valueOf(teamStatBoard.getTotalOpenBugs()));
    }
    
    private void displayUserStats() {
        ticketsAssignedLabel.setText(String.valueOf(userStatBoard.getTotalOpenTickets()));
        bugsAssignedLabel.setText(String.valueOf(userStatBoard.getTotalOpenBugs()));
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
                    
                    if (ticket == null || session.getSessionUser().getSecurityRole().getId() < 2) {
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
        ticketPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
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
        ticketStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
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
                    if (empty || assignedAppUser == null) {
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
                        setWrapText(true);
                        Text text = new Text(createdTimestamp.format(DateTimeHandler.DATE_OVER_TIME));
                        setGraphic(text);
                    }
                }
            };
        });
        
        // Ticket last update date column
        ticketLastUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedTimestamp"));
        ticketLastUpdatedColumn.setCellFactory(ticket -> {
            return new TableCell<Ticket, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime lastUpdatedTimestamp, boolean empty) {
                    super.updateItem(lastUpdatedTimestamp, empty);
                    if (empty || lastUpdatedTimestamp == null) {
                        setText("");
                    } else {
                        setWrapText(true);
                        Text text = new Text(lastUpdatedTimestamp.format(DateTimeHandler.DATE_OVER_TIME));
                        setGraphic(text);
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
        bugPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
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
        bugStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
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
                    if (empty || assignedAppUser == null) {
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
                        setWrapText(true);
                        Text text = new Text(createdTimestamp.format(DateTimeHandler.DATE_OVER_TIME));
                        setGraphic(text);
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
                    if (empty || lastUpdatedTimestamp == null) {
                        setText("");
                    } else {
                        setWrapText(true);
                        Text text = new Text(lastUpdatedTimestamp.format(DateTimeHandler.DATE_OVER_TIME));
                        setGraphic(text);
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
        assetAssignedToColumn.setCellValueFactory(new PropertyValueFactory<>("assignedToAppUser"));
        assetAssignedToColumn.setCellFactory(bug -> {
            return new TableCell<Asset, AppUser>(){
                @Override
                protected void updateItem(AppUser assignedToAppUser, boolean empty) {
                    super.updateItem(assignedToAppUser, empty);
                    if (empty || assignedToAppUser == null) {
                        setText("");
                    } else {
                        setText(assignedToAppUser.getDisplayName());
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
        
        contactEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        contactPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        contactCompanyColumn.setCellValueFactory(new PropertyValueFactory<>("company"));
    }
    
    private void initializeContactFilters() {

    }
    
    // Table load methods
    private void loadTicketTableView() {
        filteredTicketList = new FilteredList<>(allTicketList, (pred) -> true);
        sortedTicketList = new SortedList<>(filteredTicketList);
        sortedTicketList.comparatorProperty().bind(ticketTableView.comparatorProperty());
        // TODO filters
        ticketTableView.refresh();
        ticketTableView.setItems(sortedTicketList);
        ticketTableView.getColumns().get(0).setVisible(false);
        ticketTableView.getColumns().get(0).setVisible(true);

        ticketDeleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
        bindTicketFilters();
    }
    
    private void loadBugTableView() {
        filteredBugList = new FilteredList<>(allBugList, (pred) -> true);
        sortedBugList = new SortedList<>(filteredBugList);
        sortedBugList.comparatorProperty().bind(bugTableView.comparatorProperty());
        // TODO filters
        bugTableView.refresh();
        bugTableView.setItems(sortedBugList);
        bugTableView.getColumns().get(0).setVisible(false);
        bugTableView.getColumns().get(0).setVisible(true);
        bugDeleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
        bindBugFilters();
    }
    
    private void loadAssetTableView() {
        filteredAssetList = new FilteredList<>(allAssetList, (pred) -> true);
        sortedAssetList = new SortedList<>(filteredAssetList);
        sortedAssetList.comparatorProperty().bind(assetTableView.comparatorProperty());
        // TODO filters
        assetTableView.refresh();
        assetTableView.setItems(sortedAssetList);

        assetDeleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
        bindAssetFilters();
    }
    
    private void loadProductTableView() {
        filteredProductList = new FilteredList<>(allProductList, (pred) -> true);
        sortedProductList = new SortedList<>(filteredProductList);
        sortedProductList.comparatorProperty().bind(productTableView.comparatorProperty());
        // TODO filters
        productTableView.refresh();
        productTableView.setItems(sortedProductList);

        productDeleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
    }
    
    private void loadContactTableView() {
        filteredContactList = new FilteredList<>(allContactList, (pred) -> true);
        sortedContactList = new SortedList<>(filteredContactList);
        sortedContactList.comparatorProperty().bind(contactTableView.comparatorProperty());
        // TODO filters
        contactTableView.refresh();
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
            loadTicketForm(FormMode.UPDATE, (Ticket) item);
            return;
        }
        
        if (item.getClass() == Bug.class) {
            loadBugForm(FormMode.UPDATE, (Bug) item);
            return;
        }
        
        if (item.getClass() == Asset.class) {
            loadAssetForm(FormMode.UPDATE, (Asset) item);
            return;
        }
        if (item.getClass() == Product.class) {
            loadProductForm(FormMode.UPDATE, (Product) item);
            return;
        }
        
        if (item.getClass() == Contact.class) {
            loadContactForm(FormMode.UPDATE, (Contact) item);
        }
    }
    
    @FXML
    private void handleAddTicketButton() {
        loadTicketForm(FormMode.INSERT, null);
    }
    
    @FXML
    private void handleAddBugButton() {
        loadBugForm(FormMode.INSERT, null);
    }
    
    @FXML
    private void handleAddAssetButton() {
        loadAssetForm(FormMode.INSERT, null);
    }
    
    @FXML
    private void handleAddProductButton() {
        loadProductForm(FormMode.INSERT, null);
    }
    
    @FXML
    private void handleAddContactButton() {
        loadContactForm(FormMode.INSERT, null);
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
            
    private void startClickHandlers() {
        ticketNumberClearFilterImageView.setOnMouseClicked((event) -> {
            ticketNumberSearchField.clear();
        });
        
        ticketPriorityClearFilterImageView.setOnMouseClicked((event) -> {
            ticketPriorityFilterComboBox.getSelectionModel().clearSelection();
        });
        
        ticketContentClearFilterImageView.setOnMouseClicked((event) -> {
            ticketInfoSearchField.clear();
        });
        
        ticketStatusClearFilterImageView.setOnMouseClicked((event) -> {
            ticketStatusFilterComboBox.getSelectionModel().clearSelection();
        });
        
        bugNumberClearFilterImageView.setOnMouseClicked((event) -> {
            bugNumberSearchField.clear();
        });
        
        bugPriorityClearFilterImageView.setOnMouseClicked((event) -> {
            bugPriorityFilterComboBox.getSelectionModel().clearSelection();
        });

        bugContentClearFilterImageView.setOnMouseClicked((event) -> {
            bugInfoSearchField.clear();
        });
        
        bugStatusClearFilterImageView.setOnMouseClicked((event) -> {
            bugStatusFilterComboBox.getSelectionModel().clearSelection();
        });
        
        assetAssignedToClearFilterImageView.setOnMouseClicked((event) -> {
            assetAssignedToSearchField.clear();
        });
        
        assetTypeClearFilterImageView.setOnMouseClicked((event) -> {
            assetTypeComboBox.getSelectionModel().clearSelection();
        });
        
        assetMfgClearFilterImageView.setOnMouseClicked((event) -> {
            assetMfgComboBox.getSelectionModel().clearSelection();
        });
        
        assetNumberClearFilterImageView.setOnMouseClicked((event) -> {
            assetNumberSearchField.clear();
        });
        
        assetInfoClearFilterImageView.setOnMouseClicked((event) -> {
            assetInfoSearchField.clear();
        });
        
        report1Label.setOnMouseClicked((event) -> {
            
        });
        
        report2Label.setOnMouseClicked((event) -> {
            
        });
        
        assetTypeConfigLabel.setOnMouseClicked((event) -> {
            FXMLLoader assetTypeConfigLoader = new FXMLLoader(getClass().getResource("AssetTypeConfigScreen.fxml"));
            Scene assetTypeConfigScene;
            try {
                assetTypeConfigScene = new Scene(assetTypeConfigLoader.load());
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            Stage assetTypeConfigStage = new Stage();
            assetTypeConfigStage.initOwner(activeUserLabel.getScene().getWindow());
            assetTypeConfigStage.initModality(Modality.APPLICATION_MODAL);
            assetTypeConfigStage.setTitle("SDTracker - Asset Type Configuration");
            assetTypeConfigStage.setScene(assetTypeConfigScene);
            assetTypeConfigStage.showAndWait();
        });
        
        contactTypeConfigLabel.setOnMouseClicked((event) -> {
            FXMLLoader contactTypeConfigLoader = new FXMLLoader(getClass().getResource("ContactTypeConfigScreen.fxml"));
            Scene contactTypeConfigScene;
            try {
                contactTypeConfigScene = new Scene(contactTypeConfigLoader.load());
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            Stage contactTypeConfigStage = new Stage();
            contactTypeConfigStage.initOwner(activeUserLabel.getScene().getWindow());
            contactTypeConfigStage.initModality(Modality.APPLICATION_MODAL);
            contactTypeConfigStage.setTitle("SDTracker - Contact Type Configuration");
            contactTypeConfigStage.setScene(contactTypeConfigScene);
            contactTypeConfigStage.showAndWait();
        });
        
        mfgConfigLabel.setOnMouseClicked((event) -> {
            FXMLLoader mfgConfigLoader = new FXMLLoader(getClass().getResource("MfgConfigScreen.fxml"));
            Scene mfgConfigScene;
            try {
                mfgConfigScene = new Scene(mfgConfigLoader.load());
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            Stage mfgConfigStage = new Stage();
            mfgConfigStage.initOwner(activeUserLabel.getScene().getWindow());
            mfgConfigStage.initModality(Modality.APPLICATION_MODAL);
            mfgConfigStage.setTitle("SDTracker - Manufacturer Configuration");
            mfgConfigStage.setScene(mfgConfigScene);
            mfgConfigStage.showAndWait();
        });
        
        deptConfigLabel.setOnMouseClicked((event) -> {
            FXMLLoader departmentConfigLoader = new FXMLLoader(getClass().getResource("DepartmentConfigScreen.fxml"));
            Scene departmentConfigScene;
            try {
                departmentConfigScene = new Scene(departmentConfigLoader.load());
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            Stage departmentConfigStage = new Stage();
            departmentConfigStage.initOwner(activeUserLabel.getScene().getWindow());
            departmentConfigStage.initModality(Modality.APPLICATION_MODAL);
            departmentConfigStage.setTitle("SDTracker - Department Configuration");
            departmentConfigStage.setScene(departmentConfigScene);
            departmentConfigStage.showAndWait();
        });
        
        appUserConfigLabel.setOnMouseClicked((event) -> {
            FXMLLoader appUserConfigLoader = new FXMLLoader(getClass().getResource("AppUserConfigScreen.fxml"));
            Scene appUserConfigScene;
            try {
                appUserConfigScene = new Scene(appUserConfigLoader.load());
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            Stage appUserConfigStage = new Stage();
            appUserConfigStage.initOwner(activeUserLabel.getScene().getWindow());
            appUserConfigStage.initModality(Modality.APPLICATION_MODAL);
            appUserConfigStage.setTitle("SDTracker - User Configuration");
            appUserConfigStage.setScene(appUserConfigScene);
            appUserConfigStage.showAndWait();
        });
    }
        
    private void loadTicketForm(FormMode formMode, Ticket ticket) {
        FXMLLoader ticketFormLoader = new FXMLLoader(getClass().getResource("TicketForm.fxml"));
        Scene ticketFormScene;
        try {
            ticketFormScene = new Scene(ticketFormLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Stage ticketFormStage = new Stage();
        ticketFormStage.initOwner(activeUserLabel.getScene().getWindow());
        ticketFormStage.initModality(Modality.APPLICATION_MODAL);
        ticketFormStage.setTitle("SDTracker - Ticket");
        ticketFormStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
        });
        ticketFormStage.setScene(ticketFormScene);
        TicketFormController ticketFormController = ticketFormLoader.getController();
        if (formMode.equals(FormMode.UPDATE)) {
            ticketFormController.setUpdateMode(ticket);
        }
        ticketFormStage.showAndWait();
        FormResult ticketFormResult = ticketFormController.getFormResult();
        systemMessageLabel.setText(ticketFormResult.getMessage());
        if (ticketFormResult.getResultStatus().equals(SUCCESS)) {
            runGetAllTicketsService();
        }
    }
    
    private void loadBugForm(FormMode formMode, Bug bug) {
        FXMLLoader bugFormLoader = new FXMLLoader(getClass().getResource("BugForm.fxml"));
        Scene bugFormScene;
        try {
            bugFormScene = new Scene(bugFormLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Stage bugFormStage = new Stage();
        bugFormStage.initOwner(activeUserLabel.getScene().getWindow());
        bugFormStage.initModality(Modality.APPLICATION_MODAL);
        bugFormStage.setTitle("SDTracker - Bug");
        bugFormStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
        });
        bugFormStage.setScene(bugFormScene);
        BugFormController bugFormController = bugFormLoader.getController();
        if (formMode.equals(FormMode.UPDATE)) {
            bugFormController.setUpdateMode(bug);
        }
        bugFormStage.showAndWait();
        FormResult bugFormResult = bugFormController.getFormResult();
        systemMessageLabel.setText(bugFormResult.getMessage());
        if (bugFormResult.getResultStatus().equals(SUCCESS)) {
            runGetAllBugsService();
        }
    }
    
    private void loadAssetForm(FormMode formMode, Asset asset) {
        FXMLLoader assetFormLoader = new FXMLLoader(getClass().getResource("AssetForm.fxml"));
        Scene assetFormScene;
        try {
            assetFormScene = new Scene(assetFormLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Stage assetFormStage = new Stage();
        assetFormStage.initOwner(activeUserLabel.getScene().getWindow());
        assetFormStage.initModality(Modality.APPLICATION_MODAL);
        assetFormStage.setTitle("SDTracker - Asset");
        assetFormStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
        });
        assetFormStage.setScene(assetFormScene);
        AssetFormController assetFormController = assetFormLoader.getController();
        if (formMode.equals(FormMode.UPDATE)) {
            assetFormController.specifyUpdateMode(asset);
        }
        assetFormStage.showAndWait();
        FormResult assetFormResult = assetFormController.getFormResult();
        systemMessageLabel.setText(assetFormResult.getMessage());
        if (assetFormResult.getResultStatus().equals(SUCCESS)) {
            runGetAllAssetsService();
        }
    }
    
    private void loadProductForm(FormMode formMode, Product product) {
        FXMLLoader productFormLoader = new FXMLLoader(getClass().getResource("ProductForm.fxml"));
        Scene productFormScene;
        try {
            productFormScene = new Scene(productFormLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Stage productFormStage = new Stage();
        productFormStage.initOwner(activeUserLabel.getScene().getWindow());
        productFormStage.initModality(Modality.APPLICATION_MODAL);
        productFormStage.setTitle("SDTracker - Product");
        productFormStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
        });
        productFormStage.setScene(productFormScene);
        ProductFormController productFormController = productFormLoader.getController();
        if (formMode.equals(FormMode.UPDATE)) {
            productFormController.specifyUpdateMode(product);
        }
        productFormStage.showAndWait();
        FormResult productFormResult = productFormController.getFormResult();
        systemMessageLabel.setText(productFormResult.getMessage());
        if (productFormResult.getResultStatus().equals(SUCCESS)) {
            runGetAllProductsService();
        }
    }
    
    private void loadContactForm(FormMode formMode, Contact contact) {
        FXMLLoader contactFormLoader = new FXMLLoader(getClass().getResource("ContactForm.fxml"));
        Scene contactFormScene;
        try {
            contactFormScene = new Scene(contactFormLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Stage contactFormStage = new Stage();
        contactFormStage.initOwner(activeUserLabel.getScene().getWindow());
        contactFormStage.initModality(Modality.APPLICATION_MODAL);
        contactFormStage.setTitle("SDTracker - Contact");
        contactFormStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
        });
        contactFormStage.setScene(contactFormScene);
        ContactFormController contactFormController = contactFormLoader.getController();
        if (formMode.equals(FormMode.UPDATE)) {
            contactFormController.specifyUpdateMode(contact);
        }
        contactFormStage.showAndWait();
        FormResult contactFormResult = contactFormController.getFormResult();
        systemMessageLabel.setText(contactFormResult.getMessage());
        if (contactFormResult.getResultStatus().equals(SUCCESS)) {
            runGetAllContactsService();
        }
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
    
    private void runGetAllTicketPrioritiesService() {
        if (!getAllTicketPrioritiesService.isRunning()) {
            getAllTicketPrioritiesService.reset();
            getAllTicketPrioritiesService.start();
        }
    }
    
    private void runGetAllTicketStatusesService() {
        if (!getAllTicketStatusesService.isRunning()) {
            getAllTicketStatusesService.reset();
            getAllTicketStatusesService.start();
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
    
    private void runGetAllBugPrioritiesService() {
        if (!getAllBugPrioritiesService.isRunning()) {
            getAllBugPrioritiesService.reset();
            getAllBugPrioritiesService.start();
        }
    }
    
    private void runGetAllBugStatusesService() {
        if (!getAllBugStatusesService.isRunning()) {
            getAllBugStatusesService.reset();
            getAllBugStatusesService.start();
        }
    }
    
    private void runGetAllAssetsService() {
        if (!getAllAssetsService.isRunning()) {
            getAllAssetsService.reset();
            getAllAssetsService.start();
        }
    }
    
    private void runGetAllAssetTypesService() {
        if (!getAllAssetTypesService.isRunning()) {
            getAllAssetTypesService.reset();
            getAllAssetTypesService.start();
        }
    }
    
    private void runGetAllMfgsService() {
        if (!getAllMfgsService.isRunning()) {
            getAllMfgsService.reset();
            getAllMfgsService.start();
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
    
    private void runGetTeamStatBoardService() {
        if (!getTeamStatBoardService.isRunning()) {
            getTeamStatBoardService.reset();
            getTeamStatBoardService.start();
        }
    }
    
    private void runGetUserStatBoardService() {
        if (!getUserStatBoardService.isRunning()) {
            getUserStatBoardService.reset();
            userStatBoardDbServiceManager.setAppUser(session.getSessionUser());
            getUserStatBoardService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> getAllTicketsSuccess = (event) -> {
        allTicketList.clear();
        allTicketList = getAllTicketsService.getValue();
        loadTicketTableView();
        displayStats();
    };
    
    private EventHandler<WorkerStateEvent> getAllTicketsFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> getAllTicketPrioritiesSuccess = (event) -> {
        allTicketPrioritiesList.clear();
        allTicketPrioritiesList = getAllTicketPrioritiesService.getValue();
        ticketPriorityFilterComboBox.getItems().addAll(allTicketPrioritiesList);
    };
    
    private EventHandler<WorkerStateEvent> getAllTicketPrioritiesFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
        private EventHandler<WorkerStateEvent> getAllTicketStatusesSuccess = (event) -> {
        allTicketStatusesList.clear();
        allTicketStatusesList = getAllTicketStatusesService.getValue();
        ticketStatusFilterComboBox.getItems().addAll(allTicketStatusesList);
    };
    
    private EventHandler<WorkerStateEvent> getAllTicketStatusesFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> getAllBugsSuccess = (event) -> {
        allBugList.clear();
        allBugList = getAllBugsService.getValue();
        loadBugTableView();
        displayStats();
    };
    
    private EventHandler<WorkerStateEvent> getAllBugsFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> getAllBugPrioritiesSuccess = (event) -> {
        allBugPrioritiesList.clear();
        allBugPrioritiesList = getAllBugPrioritiesService.getValue();
        bugPriorityFilterComboBox.getItems().addAll(allBugPrioritiesList);
    };
    
    private EventHandler<WorkerStateEvent> getAllBugPrioritiesFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> getAllBugStatusesSuccess = (event) -> {
        allBugStatusesList.clear();
        allBugStatusesList = getAllBugStatusesService.getValue();
        bugStatusFilterComboBox.getItems().addAll(allBugStatusesList);
    };
    
    private EventHandler<WorkerStateEvent> getAllBugStatusesFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> getAllAssetsSuccess = (event) -> {
        allAssetList = getAllAssetsService.getValue();
        loadAssetTableView();
    };
    
    private EventHandler<WorkerStateEvent> getAllAssetsFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> getAllAssetTypesSuccess = (event) -> {
        allAssetTypesList.clear();
        allAssetTypesList = getAllAssetTypesService.getValue();
        assetTypeComboBox.getItems().addAll(allAssetTypesList);
    };
    
    private EventHandler<WorkerStateEvent> getAllAssetTypesFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };

    private EventHandler<WorkerStateEvent> getAllMfgsSuccess = (event) -> {
        allMfgsList.clear();
        allMfgsList = getAllMfgsService.getValue();
        assetMfgComboBox.getItems().addAll(allMfgsList);
    };
    
    private EventHandler<WorkerStateEvent> getAllMfgsFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> getAllProductsSuccess = (event) -> {
        allProductList = getAllProductsService.getValue();
        loadProductTableView();
    };
    
    private EventHandler<WorkerStateEvent> getAllProductsFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> getAllContactsSuccess = (event) -> {
        allContactList = getAllContactsService.getValue();
        loadContactTableView();
    };
    
    private EventHandler<WorkerStateEvent> getAllContactsFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> deleteTicketSuccess = (event) -> {
        runGetAllTicketsService();
    };
    
    private EventHandler<WorkerStateEvent> deleteTicketFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> deleteBugSuccess = (event) -> {
        runGetAllBugsService();
    };
    
    private EventHandler<WorkerStateEvent> deleteBugFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> deleteAssetSuccess = (event) -> {
        runGetAllAssetsService();
    };
    
    private EventHandler<WorkerStateEvent> deleteAssetsFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> deleteProductSuccess = (event) -> {
        runGetAllProductsService();
    };
    
    private EventHandler<WorkerStateEvent> deleteProductFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> deleteContactSuccess = (event) -> {
        runGetAllContactsService();
    };
    
    private EventHandler<WorkerStateEvent> deleteContactFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> getTeamStatBoardSuccess = (event) -> {
        teamStatBoard = getTeamStatBoardService.getValue();
        displayTeamStats();
    };
    
    private EventHandler<WorkerStateEvent> getTeamStatBoardFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private EventHandler<WorkerStateEvent> getUserStatBoardSuccess = (event) -> {
        userStatBoard = getUserStatBoardService.getValue();
        displayUserStats();
    };
    
    private EventHandler<WorkerStateEvent> getUserStatBoardFailure = (event) -> {
        event.getSource().getException().printStackTrace();
    };
    
    private enum TicketView {
        MY_TICKETS("My Tickets"),
        ALL_TICKETS("All Tickets");
        
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
        ALL_BUGS("All Bugs");
        
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
