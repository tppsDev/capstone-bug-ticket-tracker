/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.io.IOException;
import java.net.URL;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sdtracker.Session;
import sdtracker.database.AssetTypeDbServiceManager;
import sdtracker.database.AssetTypeDbServiceManager.*;
import sdtracker.model.AssetType;
import static sdtracker.view_controller.FormResult.FormResultStatus.SUCCESS;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class AssetTypeConfigScreenController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private Button addAssetTypeButton;
    @FXML private TextField searchTextField;
    @FXML private TableView<AssetType> assetTypeTableView;
    @FXML private TableColumn<AssetType, AssetType> deleteColumn;
    @FXML private TableColumn<AssetType, AssetType> editColumn;
    @FXML private TableColumn<AssetType, String> nameColumn;
    @FXML private Label systemMessageLabel;

    Session session = Session.getSession();
    
    AssetTypeDbServiceManager assetTypeDbServiceManager = AssetTypeDbServiceManager.getServiceManager();
    GetAllAssetTypesService getAllAssetTypesService;
    ObservableList<AssetType> allAssetTypeList = FXCollections.observableArrayList();
    DeleteAssetTypeService deleteAssetTypeService;
    FilteredList<AssetType> filteredAssetTypeList;
    SortedList<AssetType> sortedAssetTypeList;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeServices();
        establishBindings();
        initializeAssetTypeTableView();
        runGetAllAssetTypesService();
    }

    private void initializeServices() {
        getAllAssetTypesService = assetTypeDbServiceManager.new GetAllAssetTypesService();
        getAllAssetTypesService.setOnSucceeded(getAllAssetTypeSuccess);
        getAllAssetTypesService.setOnFailed(getAllAssetTypeFailure);
        
        deleteAssetTypeService = assetTypeDbServiceManager.new DeleteAssetTypeService();
        deleteAssetTypeService.setOnSucceeded(deleteAssetTypeSuccess);
        deleteAssetTypeService.setOnFailed(deleteAssetTypeFailure);
    }

    private void establishBindings() {
        BooleanBinding servicesRunning = getAllAssetTypesService.runningProperty()
                                        .or(deleteAssetTypeService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }

    private void initializeAssetTypeTableView() {
        deleteColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        deleteColumn.setStyle("-fx-alignment: CENTER;");
        deleteColumn.setCellFactory(param -> {
            return new TableCell<AssetType, AssetType>(){
                private Button deleteButton = new Button(null);
                
                @Override
                protected void updateItem(AssetType assetType, boolean empty) {
                    super.updateItem(assetType, empty);
                    
                    if (assetType == null) {
                        setGraphic(null);
                        return;
                    }
                    deleteButton.getStyleClass().add("table-delete-button");
                    setGraphic(deleteButton);
                    deleteButton.setOnAction((event) -> {
                        handleDeleteButton(assetType);
                    });
                }
            };
        });
        
        // Edit column
        editColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        editColumn.setStyle("-fx-alignment: CENTER;");
        editColumn.setCellFactory(param -> {
            return new TableCell<AssetType, AssetType>(){
                private Button editButton = new Button(null);
                
                @Override
                protected void updateItem(AssetType assetType, boolean empty) {
                    super.updateItem(assetType, empty);
                    
                    if (assetType == null) {
                        setGraphic(null);
                        return;
                    }
                    editButton.getStyleClass().add("table-edit-button");
                    setGraphic(editButton);
                    editButton.setOnAction((event) -> {
                        handleEditButton(assetType);
                    });
                }
            };
        });
        
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }
     
    private void loadAssetTypeTableView() {
        filteredAssetTypeList = new FilteredList<>(allAssetTypeList, (pred) -> true);
        sortedAssetTypeList = new SortedList<>(filteredAssetTypeList);
        sortedAssetTypeList.comparatorProperty().bind(assetTypeTableView.comparatorProperty());
        // TODO filter
        assetTypeTableView.refresh();
        assetTypeTableView.setItems(sortedAssetTypeList);

        deleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
    }
    
    private void handleDeleteButton(AssetType assetType) {
        runDeleteAssetTypeService(assetType);
    }
    
    private void handleEditButton(AssetType assetType) {
        showAssetTypeForm(FormMode.UPDATE, assetType);
    }
    
    @FXML
    private void handleAddAssetTypeButton(ActionEvent event) {
        showAssetTypeForm(FormMode.INSERT, new AssetType());
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
    
    private void showAssetTypeForm(FormMode formMode, AssetType assetType) {
        FXMLLoader assetTypeFormLoader = new FXMLLoader(getClass().getResource("AssetTypeForm.fxml"));
        Scene assetTypeFormScene;
        try {
            assetTypeFormScene = new Scene(assetTypeFormLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Stage assetTypeFormStage = new Stage();
        assetTypeFormStage.initOwner(systemMessageLabel.getScene().getWindow());
        assetTypeFormStage.initModality(Modality.APPLICATION_MODAL);
        assetTypeFormStage.setTitle("SDTracker - Asset Type");
        assetTypeFormStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
        });
        assetTypeFormStage.setScene(assetTypeFormScene);
        AssetTypeFormController assetTypeFormController = assetTypeFormLoader.getController();
        if (formMode.equals(FormMode.UPDATE)) {
            assetTypeFormController.specifyUpdateMode(assetType);
        }
        assetTypeFormStage.showAndWait();
        FormResult assetFormResult = assetTypeFormController.getFormResult();
        systemMessageLabel.setText(assetFormResult.getMessage());
        if (assetFormResult.getResultStatus().equals(SUCCESS)) {
            runGetAllAssetTypesService();
        }
    }

/***************************************************************************************
** Services methods
*/
    private void runGetAllAssetTypesService() {
        if (!getAllAssetTypesService.isRunning()) {
            getAllAssetTypesService.reset();
            getAllAssetTypesService.start();
        }
    }
    
    private void runDeleteAssetTypeService(AssetType assetType) {
        if (!deleteAssetTypeService.isRunning()) {
            deleteAssetTypeService.reset();
            deleteAssetTypeService.setAssetType(assetType);
            deleteAssetTypeService.start();
        }
    }
    
    private EventHandler<WorkerStateEvent> getAllAssetTypeSuccess = (event) -> {
        allAssetTypeList = getAllAssetTypesService.getValue();
        loadAssetTypeTableView();
    };
    
    private EventHandler<WorkerStateEvent> getAllAssetTypeFailure = (event) -> {
        displaySystemMessage("System error loading Asset Types, please close window and try again", true);
        
    };
    
    private EventHandler<WorkerStateEvent> deleteAssetTypeSuccess = (event) -> {
        displaySystemMessage("Asset Type deleted successfully", false);
        runGetAllAssetTypesService();
    };
    
    private EventHandler<WorkerStateEvent> deleteAssetTypeFailure = (event) -> {
        // TODO get details
        displaySystemMessage("Asset Type could not be deleted", true);
    };
   
}
