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
import javafx.scene.control.ComboBox;
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
import sdtracker.database.AppUserDbServiceManager;
import sdtracker.database.AppUserDbServiceManager.*;
import sdtracker.database.DepartmentDbServiceManager;
import sdtracker.database.DepartmentDbServiceManager.GetAllDepartmentsService;
import sdtracker.model.AppUser;
import sdtracker.model.Department;
import static sdtracker.view_controller.FormResult.FormResultStatus.SUCCESS;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class AppUserConfigScreenController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private Button addAppUserButton;
    @FXML private TextField searchNameTextField;
    @FXML private TextField searchUsernameTextField;
    @FXML private ComboBox<Department> departmentFilterComboBox;
    @FXML private ImageView nameClearSearchImageView;
    @FXML private ImageView usernameClearSearchImageView;
    @FXML private ImageView departmentClearSearchImageView;
    @FXML private TableView<AppUser> appUserTableView;
    @FXML private TableColumn<AppUser, AppUser> deleteColumn;
    @FXML private TableColumn<AppUser, AppUser> editColumn;
    @FXML private TableColumn<AppUser, AppUser> nameColumn;
    @FXML private TableColumn<AppUser, String> usernameColumn;
    @FXML private TableColumn<AppUser, Department> departmentColumn;
    @FXML private Label systemMessageLabel;
    
    private ObjectProperty<Predicate<AppUser>> nameSearchFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<AppUser>> usernameSearchFilter = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<AppUser>> departmentSearchFilter = new SimpleObjectProperty<>();

    Session session = Session.getSession();
    
    AppUserDbServiceManager appUserDbServiceManager = AppUserDbServiceManager.getServiceManager();
    GetAllAppUsersService getAllAppUsersService;
    ObservableList<AppUser> allAppUserList = FXCollections.observableArrayList();
    DeleteAppUserService deleteAppUserService;
    FilteredList<AppUser> filteredAppUserList;
    SortedList<AppUser> sortedAppUserList;
    
    DepartmentDbServiceManager departmentDbServiceManager = DepartmentDbServiceManager.getServiceManager();
    GetAllDepartmentsService getAllDepartmentsService;
    ObservableList<Department> allDepartmentsList = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeServices();
        establishBindings();
        initializeAppUserTableView();
        runGetAllAppUsersService();
        runGetAllDepartmentsService();
        startClickListeners();
    }

    private void initializeServices() {
        getAllAppUsersService = appUserDbServiceManager.new GetAllAppUsersService();
        getAllAppUsersService.setOnSucceeded(getAllAppUsersSuccess);
        getAllAppUsersService.setOnFailed(getAllAppUsersFailure);
        
        deleteAppUserService = appUserDbServiceManager.new DeleteAppUserService();
        deleteAppUserService.setOnSucceeded(deleteAppUserSuccess);
        deleteAppUserService.setOnFailed(deleteAppUserFailure);
        
        getAllDepartmentsService = departmentDbServiceManager.new GetAllDepartmentsService();
        getAllDepartmentsService.setOnSucceeded(getAllDepartmentsSuccess);
        getAllDepartmentsService.setOnFailed(getAllDepartmentsFailure);
    }

    private void establishBindings() {
        BooleanBinding servicesRunning = getAllAppUsersService.runningProperty()
                                        .or(deleteAppUserService.runningProperty())
                                        .or(getAllDepartmentsService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }

    private void initializeAppUserTableView() {
        deleteColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        deleteColumn.setStyle("-fx-alignment: CENTER;");
        deleteColumn.setCellFactory(param -> {
            return new TableCell<AppUser, AppUser>(){
                private Button deleteButton = new Button(null);
                
                @Override
                protected void updateItem(AppUser appUser, boolean empty) {
                    super.updateItem(appUser, empty);
                    
                    if (appUser == null) {
                        setGraphic(null);
                        return;
                    }
                    deleteButton.getStyleClass().add("table-delete-button");
                    setGraphic(deleteButton);
                    deleteButton.setOnAction((event) -> {
                        handleDeleteButton(appUser);
                    });
                }
            };
        });
        
        // Edit column
        editColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        editColumn.setStyle("-fx-alignment: CENTER;");
        editColumn.setCellFactory(param -> {
            return new TableCell<AppUser, AppUser>(){
                private Button editButton = new Button(null);
                
                @Override
                protected void updateItem(AppUser appUser, boolean empty) {
                    super.updateItem(appUser, empty);
                    
                    if (appUser == null) {
                        setGraphic(null);
                        return;
                    }
                    editButton.getStyleClass().add("table-edit-button");
                    setGraphic(editButton);
                    editButton.setOnAction((event) -> {
                        handleEditButton(appUser);
                    });
                }
            };
        });
        
        nameColumn.setCellValueFactory((param) -> new ReadOnlyObjectWrapper<>(param.getValue()));
        nameColumn.setCellFactory(appUser -> {
            return new TableCell<AppUser, AppUser>(){
              @Override
              protected void updateItem(AppUser appUser ,boolean empty) {
                  if (appUser == null) {
                      setText("");
                  } else {
                      setText(appUser.getDisplayName());
                  }
              }
            };
        });
        
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        departmentColumn.setCellFactory(appUser -> {
            return new TableCell<AppUser, Department>(){
              @Override
              protected void updateItem(Department department ,boolean empty) {
                  if (department == null) {
                      setText("");
                  } else {
                      setText(department.getName());
                  }
              }
            };
        });
        
    }
     
    private void loadAppUserTableView() {
        filteredAppUserList = new FilteredList<>(allAppUserList, (pred) -> true);
        sortedAppUserList = new SortedList<>(filteredAppUserList);
        sortedAppUserList.comparatorProperty().bind(appUserTableView.comparatorProperty());
        // TODO filter
        appUserTableView.refresh();
        appUserTableView.setItems(sortedAppUserList);

        deleteColumn.setVisible(session.getSessionUser().getSecurityRole().getId() > 1);
        bindFilters();
    }
    
    private void startClickListeners() {
        nameClearSearchImageView.setOnMouseClicked((event) -> {
            searchNameTextField.clear();
        });
        
        usernameClearSearchImageView.setOnMouseClicked((event) -> {
            searchUsernameTextField.clear();
        });
        
        departmentClearSearchImageView.setOnMouseClicked((event) -> {
            departmentFilterComboBox.getSelectionModel().clearSelection();
        });
    }
    
    private void handleDeleteButton(AppUser appUser) {
        runDeleteAppUserService(appUser);
    }
    
    private void handleEditButton(AppUser appUser) {
        showAppUserForm(FormMode.UPDATE, appUser);
    }
    
    @FXML
    private void handleAddAppUserButton(ActionEvent event) {
        showAppUserForm(FormMode.INSERT, new AppUser());
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
    
    private void showAppUserForm(FormMode formMode, AppUser appUser) {
        FXMLLoader appUserFormLoader = new FXMLLoader(getClass().getResource("AppUserForm.fxml"));
        Scene appUserFormScene;
        try {
            appUserFormScene = new Scene(appUserFormLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Stage appUserFormStage = new Stage();
        appUserFormStage.initOwner(systemMessageLabel.getScene().getWindow());
        appUserFormStage.initModality(Modality.APPLICATION_MODAL);
        appUserFormStage.setTitle("SDTracker - Contact Type");
        appUserFormStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
        });
        appUserFormStage.setScene(appUserFormScene);
        AppUserFormController appUserFormController = appUserFormLoader.getController();
        if (formMode.equals(FormMode.UPDATE)) {
            appUserFormController.specifyUpdateMode(appUser);
        }
        appUserFormStage.showAndWait();
        FormResult contactFormResult = appUserFormController.getFormResult();
        systemMessageLabel.setText(contactFormResult.getMessage());
        if (contactFormResult.getResultStatus().equals(SUCCESS)) {
            runGetAllAppUsersService();
        }
    }
    
    private void bindFilters() {
        nameSearchFilter.bind(Bindings.createObjectBinding(() ->
            appUser -> {
                if (searchNameTextField.getText() != null && !searchNameTextField.getText().isEmpty()) {
                    return appUser.getDisplayName().toLowerCase().contains(searchNameTextField.getText().toLowerCase());
                } else {
                    return true;
                } 
            },
            searchNameTextField.textProperty()
        ));
        
        usernameSearchFilter.bind(Bindings.createObjectBinding(() ->
            appUser -> {
                if (searchUsernameTextField.getText() != null && !searchUsernameTextField.getText().isEmpty()) {
                    return appUser.getUsername().toLowerCase().contains(searchUsernameTextField.getText().toLowerCase());
                } else {
                    return true;
                } 
            },
            searchUsernameTextField.textProperty()
        ));
        
        departmentSearchFilter.bind(Bindings.createObjectBinding(() ->
            appUser -> {
                if (departmentFilterComboBox.getValue() != null) {
                    return appUser.getDepartment().equals(departmentFilterComboBox.getValue());
                } else {
                    return true;
                } 
            },
            departmentFilterComboBox.valueProperty()
        ));
        
        filteredAppUserList.predicateProperty().bind(Bindings.createObjectBinding(
            () -> nameSearchFilter.get()
                .and(usernameSearchFilter.get())
                .and(departmentSearchFilter.get()),
                nameSearchFilter, usernameSearchFilter, departmentSearchFilter)
        );
        
        filteredAppUserList.predicateProperty().addListener((observable) -> {
            appUserTableView.refresh();
        });
    }

/***************************************************************************************
** Services methods
*/
    private void runGetAllAppUsersService() {
        if (!getAllAppUsersService.isRunning()) {
            getAllAppUsersService.reset();
            getAllAppUsersService.start();
        }
    }
    
    private void runDeleteAppUserService(AppUser appUser) {
        if (!deleteAppUserService.isRunning()) {
            deleteAppUserService.reset();
            deleteAppUserService.setAppUser(appUser);
            deleteAppUserService.start();
        }
    }
    
    private void runGetAllDepartmentsService() {
        if (!getAllDepartmentsService.isRunning()) {
            getAllDepartmentsService.reset();
            getAllDepartmentsService.start();
        }
    }
    
    private EventHandler<WorkerStateEvent> getAllAppUsersSuccess = (event) -> {
        allAppUserList = getAllAppUsersService.getValue();
        loadAppUserTableView();
    };
    
    private EventHandler<WorkerStateEvent> getAllAppUsersFailure = (event) -> {
        displaySystemMessage("System error loading Users, please close window and try again", true);
        
    };
    
    private EventHandler<WorkerStateEvent> deleteAppUserSuccess = (event) -> {
        displaySystemMessage("User deleted successfully", false);
        runGetAllAppUsersService();
    };
    
    private EventHandler<WorkerStateEvent> deleteAppUserFailure = (event) -> {
        // TODO get details
        displaySystemMessage("User could not be deleted", true);
    };
    
    private EventHandler<WorkerStateEvent> getAllDepartmentsSuccess = (event) -> {
        allDepartmentsList = getAllDepartmentsService.getValue();
        departmentFilterComboBox.getItems().setAll(allDepartmentsList);
    };
    
    private EventHandler<WorkerStateEvent> getAllDepartmentsFailure = (event) -> {
        displaySystemMessage("System error loading Departments, please close window and try again", true);
        
    };
}
