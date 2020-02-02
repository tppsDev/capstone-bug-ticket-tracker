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
import sdtracker.database.MfgDbServiceManager;
import sdtracker.database.MfgDbServiceManager.DeleteMfgService;
import sdtracker.database.MfgDbServiceManager.GetAllMfgsService;
import sdtracker.model.Mfg;
import static sdtracker.view_controller.FormResult.FormResultStatus.SUCCESS;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class MfgConfigScreenController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private Button addMfgButton;
    @FXML private TextField searchTextField;
    @FXML private ImageView clearSearchImageView;
    @FXML private TableView<Mfg> mfgTableView;
    @FXML private TableColumn<Mfg, Mfg> deleteColumn;
    @FXML private TableColumn<Mfg, Mfg> editColumn;
    @FXML private TableColumn<Mfg, String> nameColumn;
    @FXML private Label systemMessageLabel;
    
    private ObjectProperty<Predicate<Mfg>> searchFilter = new SimpleObjectProperty<>();

    Session session = Session.getSession();
    
    MfgDbServiceManager mfgDbServiceManager = MfgDbServiceManager.getServiceManager();
    GetAllMfgsService getAllMfgsService;
    ObservableList<Mfg> allMfgList = FXCollections.observableArrayList();
    DeleteMfgService deleteMfgService;
    FilteredList<Mfg> filteredMfgList;
    SortedList<Mfg> sortedMfgList;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeServices();
        establishBindings();
        initializeMfgTableView();
        runGetAllMfgsService();
        startClickListeners();
    }

    private void initializeServices() {
        getAllMfgsService = mfgDbServiceManager.new GetAllMfgsService();
        getAllMfgsService.setOnSucceeded(getAllMfgSuccess);
        getAllMfgsService.setOnFailed(getAllMfgFailure);
        
        deleteMfgService = mfgDbServiceManager.new DeleteMfgService();
        deleteMfgService.setOnSucceeded(deleteMfgSuccess);
        deleteMfgService.setOnFailed(deleteMfgFailure);
    }

    private void establishBindings() {
        BooleanBinding servicesRunning = getAllMfgsService.runningProperty()
                                        .or(deleteMfgService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }

    private void initializeMfgTableView() {
        deleteColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        deleteColumn.setStyle("-fx-alignment: CENTER;");
        deleteColumn.setCellFactory(param -> {
            return new TableCell<Mfg, Mfg>(){
                private Button deleteButton = new Button(null);
                
                @Override
                protected void updateItem(Mfg mfg, boolean empty) {
                    super.updateItem(mfg, empty);
                    
                    if (mfg == null) {
                        setGraphic(null);
                        return;
                    }
                    deleteButton.getStyleClass().add("table-delete-button");
                    setGraphic(deleteButton);
                    deleteButton.setOnAction((event) -> {
                        handleDeleteButton(mfg);
                    });
                }
            };
        });
        
        // Edit column
        editColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        editColumn.setStyle("-fx-alignment: CENTER;");
        editColumn.setCellFactory(param -> {
            return new TableCell<Mfg, Mfg>(){
                private Button editButton = new Button(null);
                
                @Override
                protected void updateItem(Mfg mfg, boolean empty) {
                    super.updateItem(mfg, empty);
                    
                    if (mfg == null) {
                        setGraphic(null);
                        return;
                    }
                    editButton.getStyleClass().add("table-edit-button");
                    setGraphic(editButton);
                    editButton.setOnAction((event) -> {
                        handleEditButton(mfg);
                    });
                }
            };
        });
        
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }
     
    private void loadMfgTableView() {
        filteredMfgList = new FilteredList<>(allMfgList, (pred) -> true);
        sortedMfgList = new SortedList<>(filteredMfgList);
        sortedMfgList.comparatorProperty().bind(mfgTableView.comparatorProperty());
        // TODO filter
        mfgTableView.refresh();
        mfgTableView.setItems(sortedMfgList);

        deleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
        bindFilters();
    }
    
    private void startClickListeners() {
        clearSearchImageView.setOnMouseClicked((event) -> {
            searchTextField.clear();
        });
    }
    
    private void handleDeleteButton(Mfg mfg) {
        runDeleteMfgService(mfg);
    }
    
    private void handleEditButton(Mfg mfg) {
        showMfgForm(FormMode.UPDATE, mfg);
    }
    
    @FXML
    private void handleAddMfgButton(ActionEvent event) {
        showMfgForm(FormMode.INSERT, new Mfg());
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
    
    private void showMfgForm(FormMode formMode, Mfg mfg) {
        FXMLLoader mfgFormLoader = new FXMLLoader(getClass().getResource("MfgForm.fxml"));
        Scene mfgFormScene;
        try {
            mfgFormScene = new Scene(mfgFormLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Stage mfgFormStage = new Stage();
        mfgFormStage.initOwner(systemMessageLabel.getScene().getWindow());
        mfgFormStage.initModality(Modality.APPLICATION_MODAL);
        mfgFormStage.setTitle("SDTracker - Contact Type");
        mfgFormStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
        });
        mfgFormStage.setScene(mfgFormScene);
        MfgFormController mfgFormController = mfgFormLoader.getController();
        if (formMode.equals(FormMode.UPDATE)) {
            mfgFormController.specifyUpdateMode(mfg);
        }
        mfgFormStage.showAndWait();
        FormResult contactFormResult = mfgFormController.getFormResult();
        systemMessageLabel.setText(contactFormResult.getMessage());
        if (contactFormResult.getResultStatus().equals(SUCCESS)) {
            runGetAllMfgsService();
        }
    }
    
    private void bindFilters() {
        searchFilter.bind(Bindings.createObjectBinding(() ->
            mfg -> {
                if (searchTextField.getText() != null && !searchTextField.getText().isEmpty()) {
                    return mfg.getName().toLowerCase().contains(searchTextField.getText().toLowerCase());
                } else {
                    return true;
                }
            },
            searchTextField.textProperty()
        ));
        
        filteredMfgList.predicateProperty().bind(searchFilter);
        
        filteredMfgList.predicateProperty().addListener((observable) -> {
            mfgTableView.refresh();
        });
    }

/***************************************************************************************
** Services methods
*/
    private void runGetAllMfgsService() {
        if (!getAllMfgsService.isRunning()) {
            getAllMfgsService.reset();
            getAllMfgsService.start();
        }
    }
    
    private void runDeleteMfgService(Mfg mfg) {
        if (!deleteMfgService.isRunning()) {
            deleteMfgService.reset();
            deleteMfgService.setMfg(mfg);
            deleteMfgService.start();
        }
    }
    
    private EventHandler<WorkerStateEvent> getAllMfgSuccess = (event) -> {
        allMfgList = getAllMfgsService.getValue();
        loadMfgTableView();
    };
    
    private EventHandler<WorkerStateEvent> getAllMfgFailure = (event) -> {
        displaySystemMessage("System error loading Contact Types, please close window and try again", true);
        
    };
    
    private EventHandler<WorkerStateEvent> deleteMfgSuccess = (event) -> {
        displaySystemMessage("Contact Type deleted successfully", false);
        runGetAllMfgsService();
    };
    
    private EventHandler<WorkerStateEvent> deleteMfgFailure = (event) -> {
        // TODO get details
        displaySystemMessage("Contact Type could not be deleted", true);
    };
}
