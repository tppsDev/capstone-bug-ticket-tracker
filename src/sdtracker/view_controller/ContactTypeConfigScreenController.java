/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.io.IOException;
import java.net.URL;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sdtracker.Session;
import sdtracker.database.ContactTypeDbServiceManager;
import sdtracker.database.ContactTypeDbServiceManager.DeleteContactTypeService;
import sdtracker.database.ContactTypeDbServiceManager.GetAllContactTypesService;
import sdtracker.model.ContactType;
import static sdtracker.view_controller.FormResult.FormResultStatus.SUCCESS;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class ContactTypeConfigScreenController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private Button addContactTypeButton;
    @FXML private TextField searchTextField;
    @FXML private ImageView clearSearchImageView;
    @FXML private TableView<ContactType> contactTypeTableView;
    @FXML private TableColumn<ContactType, ContactType> deleteColumn;
    @FXML private TableColumn<ContactType, ContactType> editColumn;
    @FXML private TableColumn<ContactType, String> nameColumn;
    @FXML private Label systemMessageLabel;
    
    private ObjectProperty<Predicate<ContactType>> searchFilter = new SimpleObjectProperty<>();

    Session session = Session.getSession();
    
    ContactTypeDbServiceManager contactTypeDbServiceManager = ContactTypeDbServiceManager.getServiceManager();
    GetAllContactTypesService getAllContactTypesService;
    ObservableList<ContactType> allContactTypeList = FXCollections.observableArrayList();
    DeleteContactTypeService deleteContactTypeService;
    FilteredList<ContactType> filteredContactTypeList;
    SortedList<ContactType> sortedContactTypeList;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeServices();
        establishBindings();
        initializeContactTypeTableView();
        runGetAllContactTypesService();
        startClickListeners();
    }

    private void initializeServices() {
        getAllContactTypesService = contactTypeDbServiceManager.new GetAllContactTypesService();
        getAllContactTypesService.setOnSucceeded(getAllContactTypeSuccess);
        getAllContactTypesService.setOnFailed(getAllContactTypeFailure);
        
        deleteContactTypeService = contactTypeDbServiceManager.new DeleteContactTypeService();
        deleteContactTypeService.setOnSucceeded(deleteContactTypeSuccess);
        deleteContactTypeService.setOnFailed(deleteContactTypeFailure);
    }

    private void establishBindings() {
        BooleanBinding servicesRunning = getAllContactTypesService.runningProperty()
                                        .or(deleteContactTypeService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }

    private void initializeContactTypeTableView() {
        deleteColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        deleteColumn.setStyle("-fx-alignment: CENTER;");
        deleteColumn.setCellFactory(param -> {
            return new TableCell<ContactType, ContactType>(){
                private Button deleteButton = new Button(null);
                
                @Override
                protected void updateItem(ContactType contactType, boolean empty) {
                    super.updateItem(contactType, empty);
                    
                    if (contactType == null) {
                        setGraphic(null);
                        return;
                    }
                    deleteButton.getStyleClass().add("table-delete-button");
                    setGraphic(deleteButton);
                    deleteButton.setOnAction((event) -> {
                        handleDeleteButton(contactType);
                    });
                }
            };
        });
        
        // Edit column
        editColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        editColumn.setStyle("-fx-alignment: CENTER;");
        editColumn.setCellFactory(param -> {
            return new TableCell<ContactType, ContactType>(){
                private Button editButton = new Button(null);
                
                @Override
                protected void updateItem(ContactType contactType, boolean empty) {
                    super.updateItem(contactType, empty);
                    
                    if (contactType == null) {
                        setGraphic(null);
                        return;
                    }
                    editButton.getStyleClass().add("table-edit-button");
                    setGraphic(editButton);
                    editButton.setOnAction((event) -> {
                        handleEditButton(contactType);
                    });
                }
            };
        });
        
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }
     
    private void loadContactTypeTableView() {
        filteredContactTypeList = new FilteredList<>(allContactTypeList, (pred) -> true);
        sortedContactTypeList = new SortedList<>(filteredContactTypeList);
        sortedContactTypeList.comparatorProperty().bind(contactTypeTableView.comparatorProperty());
        // TODO filter
        contactTypeTableView.refresh();
        contactTypeTableView.setItems(sortedContactTypeList);

        deleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
        bindFilters();
    }
    
    private void startClickListeners() {
        clearSearchImageView.setOnMouseClicked((event) -> {
            searchTextField.clear();
        });
    }
    
    private void handleDeleteButton(ContactType contactType) {
        runDeleteContactTypeService(contactType);
    }
    
    private void handleEditButton(ContactType contactType) {
        showContactTypeForm(FormMode.UPDATE, contactType);
    }
    
    @FXML
    private void handleAddContactTypeButton(ActionEvent event) {
        showContactTypeForm(FormMode.INSERT, new ContactType());
    }
    
    private void displaySystemMessage(String message, boolean isError) {
        if (isError) {
            systemMessageLabel.getStyleClass().removeAll("system-message-label");
            systemMessageLabel.getStyleClass().add("system-message-label-error");
        } else {
            systemMessageLabel.getStyleClass().removeAll("system-message-label-error");
            systemMessageLabel.getStyleClass().add("system-message-label");
        }
        
        systemMessageLabel.setText(message);
    }
    
    private void showContactTypeForm(FormMode formMode, ContactType contactType) {
        FXMLLoader contactTypeFormLoader = new FXMLLoader(getClass().getResource("ContactTypeForm.fxml"));
        Scene contactTypeFormScene;
        try {
            contactTypeFormScene = new Scene(contactTypeFormLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Stage contactTypeFormStage = new Stage();
        contactTypeFormStage.initOwner(systemMessageLabel.getScene().getWindow());
        contactTypeFormStage.initModality(Modality.APPLICATION_MODAL);
        contactTypeFormStage.setTitle("SDTracker - Contact Type");
        contactTypeFormStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
        });
        contactTypeFormStage.setScene(contactTypeFormScene);
        ContactTypeFormController contactTypeFormController = contactTypeFormLoader.getController();
        if (formMode.equals(FormMode.UPDATE)) {
            contactTypeFormController.specifyUpdateMode(contactType);
        }
        contactTypeFormStage.showAndWait();
        FormResult contactFormResult = contactTypeFormController.getFormResult();
        systemMessageLabel.setText(contactFormResult.getMessage());
        if (contactFormResult.getResultStatus().equals(SUCCESS)) {
            runGetAllContactTypesService();
        }
    }
    
    private void bindFilters() {
        searchFilter.bind(Bindings.createObjectBinding(() ->
            contactType -> {
                if (searchTextField.getText() != null && !searchTextField.getText().isEmpty()) {
                    return contactType.getName().toLowerCase().contains(searchTextField.getText().toLowerCase());
                } else {
                    return true;
                }
            },
            searchTextField.textProperty()
        ));
        
        filteredContactTypeList.predicateProperty().bind(searchFilter);
        
        filteredContactTypeList.predicateProperty().addListener((observable) -> {
            contactTypeTableView.refresh();
        });
    }

/***************************************************************************************
** Services methods
*/
    private void runGetAllContactTypesService() {
        if (!getAllContactTypesService.isRunning()) {
            getAllContactTypesService.reset();
            getAllContactTypesService.start();
        }
    }
    
    private void runDeleteContactTypeService(ContactType contactType) {
        if (!deleteContactTypeService.isRunning()) {
            deleteContactTypeService.reset();
            deleteContactTypeService.setContactType(contactType);
            deleteContactTypeService.start();
        }
    }
    
    private EventHandler<WorkerStateEvent> getAllContactTypeSuccess = (event) -> {
        allContactTypeList = getAllContactTypesService.getValue();
        loadContactTypeTableView();
    };
    
    private EventHandler<WorkerStateEvent> getAllContactTypeFailure = (event) -> {
        displaySystemMessage("System error loading Contact Types, please close window and try again", true);
        
    };
    
    private EventHandler<WorkerStateEvent> deleteContactTypeSuccess = (event) -> {
        displaySystemMessage("Contact Type deleted successfully", false);
        runGetAllContactTypesService();
    };
    
    private EventHandler<WorkerStateEvent> deleteContactTypeFailure = (event) -> {
        // TODO get details
        displaySystemMessage("Contact Type could not be deleted", true);
    };
}
