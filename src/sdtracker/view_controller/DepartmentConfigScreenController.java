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
import sdtracker.database.DepartmentDbServiceManager;
import sdtracker.database.DepartmentDbServiceManager.DeleteDepartmentService;
import sdtracker.database.DepartmentDbServiceManager.GetAllDepartmentsService;
import sdtracker.model.Department;
import static sdtracker.view_controller.FormResult.FormResultStatus.SUCCESS;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class DepartmentConfigScreenController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private Button addDepartmentButton;
    @FXML private TextField searchTextField;
    @FXML private ImageView clearSearchImageView;
    @FXML private TableView<Department> departmentTableView;
    @FXML private TableColumn<Department, Department> deleteColumn;
    @FXML private TableColumn<Department, Department> editColumn;
    @FXML private TableColumn<Department, String> nameColumn;
    @FXML private Label systemMessageLabel;
    
    private ObjectProperty<Predicate<Department>> searchFilter = new SimpleObjectProperty<>();

    Session session = Session.getSession();
    
    DepartmentDbServiceManager departmentDbServiceManager = DepartmentDbServiceManager.getServiceManager();
    GetAllDepartmentsService getAllDepartmentsService;
    ObservableList<Department> allDepartmentList = FXCollections.observableArrayList();
    DeleteDepartmentService deleteDepartmentService;
    FilteredList<Department> filteredDepartmentList;
    SortedList<Department> sortedDepartmentList;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeServices();
        establishBindings();
        initializeDepartmentTableView();
        runGetAllDepartmentsService();
        startClickListeners();
    }

    private void initializeServices() {
        getAllDepartmentsService = departmentDbServiceManager.new GetAllDepartmentsService();
        getAllDepartmentsService.setOnSucceeded(getAllDepartmentSuccess);
        getAllDepartmentsService.setOnFailed(getAllDepartmentFailure);
        
        deleteDepartmentService = departmentDbServiceManager.new DeleteDepartmentService();
        deleteDepartmentService.setOnSucceeded(deleteDepartmentSuccess);
        deleteDepartmentService.setOnFailed(deleteDepartmentFailure);
    }

    private void establishBindings() {
        BooleanBinding servicesRunning = getAllDepartmentsService.runningProperty()
                                        .or(deleteDepartmentService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }

    private void initializeDepartmentTableView() {
        deleteColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        deleteColumn.setStyle("-fx-alignment: CENTER;");
        deleteColumn.setCellFactory(param -> {
            return new TableCell<Department, Department>(){
                private Button deleteButton = new Button(null);
                
                @Override
                protected void updateItem(Department department, boolean empty) {
                    super.updateItem(department, empty);
                    
                    if (department == null) {
                        setGraphic(null);
                        return;
                    }
                    deleteButton.getStyleClass().add("table-delete-button");
                    setGraphic(deleteButton);
                    deleteButton.setOnAction((event) -> {
                        handleDeleteButton(department);
                    });
                }
            };
        });
        
        // Edit column
        editColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        editColumn.setStyle("-fx-alignment: CENTER;");
        editColumn.setCellFactory(param -> {
            return new TableCell<Department, Department>(){
                private Button editButton = new Button(null);
                
                @Override
                protected void updateItem(Department department, boolean empty) {
                    super.updateItem(department, empty);
                    
                    if (department == null) {
                        setGraphic(null);
                        return;
                    }
                    editButton.getStyleClass().add("table-edit-button");
                    setGraphic(editButton);
                    editButton.setOnAction((event) -> {
                        handleEditButton(department);
                    });
                }
            };
        });
        
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }
     
    private void loadDepartmentTableView() {
        filteredDepartmentList = new FilteredList<>(allDepartmentList, (pred) -> true);
        sortedDepartmentList = new SortedList<>(filteredDepartmentList);
        sortedDepartmentList.comparatorProperty().bind(departmentTableView.comparatorProperty());
        // TODO filter
        departmentTableView.refresh();
        departmentTableView.setItems(sortedDepartmentList);

        deleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
        bindFilters();
    }
    
    private void startClickListeners() {
        clearSearchImageView.setOnMouseClicked((event) -> {
            searchTextField.clear();
        });
    }
    
    private void handleDeleteButton(Department department) {
        runDeleteDepartmentService(department);
    }
    
    private void handleEditButton(Department department) {
        showDepartmentForm(FormMode.UPDATE, department);
    }
    
    @FXML
    private void handleAddDepartmentButton(ActionEvent event) {
        showDepartmentForm(FormMode.INSERT, new Department());
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
    
    private void showDepartmentForm(FormMode formMode, Department department) {
        FXMLLoader departmentFormLoader = new FXMLLoader(getClass().getResource("DepartmentForm.fxml"));
        Scene departmentFormScene;
        try {
            departmentFormScene = new Scene(departmentFormLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Stage departmentFormStage = new Stage();
        departmentFormStage.initOwner(systemMessageLabel.getScene().getWindow());
        departmentFormStage.initModality(Modality.APPLICATION_MODAL);
        departmentFormStage.setTitle("SDTracker - Contact Type");
        departmentFormStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
        });
        departmentFormStage.setScene(departmentFormScene);
        DepartmentFormController departmentFormController = departmentFormLoader.getController();
        if (formMode.equals(FormMode.UPDATE)) {
            departmentFormController.specifyUpdateMode(department);
        }
        departmentFormStage.showAndWait();
        FormResult contactFormResult = departmentFormController.getFormResult();
        systemMessageLabel.setText(contactFormResult.getMessage());
        if (contactFormResult.getResultStatus().equals(SUCCESS)) {
            runGetAllDepartmentsService();
        }
    }
    
    private void bindFilters() {
        searchFilter.bind(Bindings.createObjectBinding(() ->
            department -> {
                if (searchTextField.getText() != null && !searchTextField.getText().isEmpty()) {
                    return department.getName().toLowerCase().contains(searchTextField.getText().toLowerCase());
                } else {
                    return true;
                }
            },
            searchTextField.textProperty()
        ));
        
        filteredDepartmentList.predicateProperty().bind(searchFilter);
        
        filteredDepartmentList.predicateProperty().addListener((observable) -> {
            departmentTableView.refresh();
        });
    }

/***************************************************************************************
** Services methods
*/
    private void runGetAllDepartmentsService() {
        if (!getAllDepartmentsService.isRunning()) {
            getAllDepartmentsService.reset();
            getAllDepartmentsService.start();
        }
    }
    
    private void runDeleteDepartmentService(Department department) {
        if (!deleteDepartmentService.isRunning()) {
            deleteDepartmentService.reset();
            deleteDepartmentService.setDepartment(department);
            deleteDepartmentService.start();
        }
    }
    
    private EventHandler<WorkerStateEvent> getAllDepartmentSuccess = (event) -> {
        allDepartmentList = getAllDepartmentsService.getValue();
        loadDepartmentTableView();
    };
    
    private EventHandler<WorkerStateEvent> getAllDepartmentFailure = (event) -> {
        displaySystemMessage("System error loading Departments, please close window and try again", true);
        
    };
    
    private EventHandler<WorkerStateEvent> deleteDepartmentSuccess = (event) -> {
        displaySystemMessage("Department deleted successfully", false);
        runGetAllDepartmentsService();
    };
    
    private EventHandler<WorkerStateEvent> deleteDepartmentFailure = (event) -> {
        // TODO get details
        displaySystemMessage("Department could not be deleted", true);
    };
}
