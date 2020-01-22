/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sdtracker.database.AppUserDbServiceManager;
import sdtracker.database.AppUserDbServiceManager.*;
import sdtracker.database.DepartmentDbServiceManager;
import sdtracker.database.DepartmentDbServiceManager.GetAllDepartmentsService;
import sdtracker.database.SecurityRoleDbServiceManager;
import sdtracker.database.SecurityRoleDbServiceManager.GetAllSecurityRolesService;
import sdtracker.model.AppUser;
import sdtracker.model.Department;
import sdtracker.model.SecurityRole;
import sdtracker.utility.ValidationPattern;
import sdtracker.view_controller.FormResult.FormResultStatus;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class AppUserFormController implements Initializable {
    // Header
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    
    // Main form elements
    @FXML private TextField usernameTextField;
    @FXML private ComboBox<SecurityRole> securityRoleComboBox;
    @FXML private TextField firstNameTextField;
    @FXML private TextField midInitialTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField courtesyTitleTextField;
    @FXML private TextField jobTitleTextField;
    @FXML private ComboBox<Department> deptComboBox;
    @FXML private ComboBox<AppUser> mgrComboBox;
    @FXML private TextField phone1TextField;
    @FXML private ToggleGroup phone1TypeToggleGroup = new ToggleGroup();
    @FXML private RadioButton phone1HomeRadioButton;
    @FXML private RadioButton phone1MobileRadioButton;
    @FXML private RadioButton phone1WorkRadioButton;
    @FXML private RadioButton phone1OtherRadioButton;
    @FXML private TextField phone2TextField;
    @FXML private ToggleGroup phone2TypeToggleGroup = new ToggleGroup();
    @FXML private RadioButton phone2HomeRadioButton;
    @FXML private RadioButton phone2MobileRadioButton;
    @FXML private RadioButton phone2WorkRadioButton;
    @FXML private RadioButton phone2OtherRadioButton;
    @FXML private TextField emailTextField;

    // Buttons
    @FXML private Button setPasswordButton;
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    
    // Error labels
    @FXML private Label systemMessageLabel;
    @FXML private Label usernameErrorLabel;
    @FXML private Label securityRoleErrorLabel;
    @FXML private Label firstNameErrorLabel;
    @FXML private Label midInitialErrorLabel;
    @FXML private Label lastNameErrorLabel;
    @FXML private Label courtesyTitleErrorLabel;
    @FXML private Label jobTitleErrorLabel;
    @FXML private Label deptErrorLabel;
    @FXML private Label mgrErrorLabel;
    @FXML private Label phone1ErrorLabel;
    @FXML private Label phone1TypeErrorLabel;
    @FXML private Label phone2ErrorLabel;
    @FXML private Label phone2TypeErrorLabel;
    @FXML private Label emailErrorLabel;
    
    // Change listeners
    private ChangeListener<Boolean> usernameFocusListener;
    private ChangeListener<Boolean> securityRoleFocusListener;
    private ChangeListener<Boolean> firstNameFocusListener;
    private ChangeListener<Boolean> midInitialFocusListener;
    private ChangeListener<Boolean> lastNameFocusListener;
    private ChangeListener<Boolean> courtesyTitleFocusListener;
    private ChangeListener<Boolean> jobTitleFocusListener;
    private ChangeListener<Boolean> deptFocusListener;
    private ChangeListener<Boolean> phone1FocusListener;
    private ChangeListener<Boolean> phone2FocusListener;
    private ChangeListener<Boolean> emailFocusListener;
    
    private FormMode formMode = FormMode.INSERT;
    private AppUserDbServiceManager appUserDbServiceManager = AppUserDbServiceManager.getServiceManager();
    private InsertAppUserService insertAppUserService;
    private UpdateAppUserService updateAppUserService;
    private GetAllAppUsersService getAllAppUsersService;
    private SecurityRoleDbServiceManager securityRoleDbServiceManager = SecurityRoleDbServiceManager.getServiceManager();
    private GetAllSecurityRolesService getAllSecurityRolesService;
    private DepartmentDbServiceManager departmentDbServiceManager = DepartmentDbServiceManager.getServiceManager();
    private GetAllDepartmentsService getAllDepartmentsService;
    private ObservableList<AppUser> allAppUserList = FXCollections.observableArrayList();
    private ObservableList<SecurityRole> securityRoleList = FXCollections.observableArrayList();
    private ObservableList<Department> departmentList = FXCollections.observableArrayList();
    private AppUser appUser;
    private boolean passwordSet = false;
    private FormResult formResult;
    
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
        // TODO remove after test
        if (formMode.equals(FormMode.INSERT)) appUser = new AppUser();
    }
    
    private void initializeServices() {
        getAllAppUsersService = appUserDbServiceManager.new GetAllAppUsersService();
        insertAppUserService = appUserDbServiceManager.new InsertAppUserService();
        updateAppUserService = appUserDbServiceManager.new UpdateAppUserService();
        getAllSecurityRolesService = securityRoleDbServiceManager.new GetAllSecurityRolesService();
        getAllDepartmentsService = departmentDbServiceManager.new GetAllDepartmentsService();
        
        getAllAppUsersService.setOnSucceeded(getAllAppUsersSuccess);
        getAllAppUsersService.setOnFailed(getAllAppUsersFailure);
        insertAppUserService.setOnSucceeded(insertAppUserSuccess);
        insertAppUserService.setOnFailed(insertAppUserFailure);
        updateAppUserService.setOnSucceeded(updateAppUserSuccess);
        updateAppUserService.setOnFailed(updateAppUserFailure);
        
        getAllSecurityRolesService.setOnSucceeded(getAllSecurityRolesSuccess);
        getAllSecurityRolesService.setOnFailed(getAllSecurityRolesFailure);
        
        getAllDepartmentsService.setOnSucceeded(getAllDepartmentsSuccess);
        getAllDepartmentsService.setOnFailed(getAllDepartmentsFailure);
        
        runGetAllSecurityRolesService();
        runGetAllDepartmentsService();
        runGetAllAppUsersService();
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = getAllAppUsersService.runningProperty()
                                        .or(insertAppUserService.runningProperty())
                                        .or(updateAppUserService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void initializeInputElements() {
        phone1HomeRadioButton.setToggleGroup(phone1TypeToggleGroup);
        phone1MobileRadioButton.setToggleGroup(phone1TypeToggleGroup);
        phone1WorkRadioButton.setToggleGroup(phone1TypeToggleGroup);
        phone1OtherRadioButton.setToggleGroup(phone1TypeToggleGroup);
        
        phone2HomeRadioButton.setToggleGroup(phone2TypeToggleGroup);
        phone2MobileRadioButton.setToggleGroup(phone2TypeToggleGroup);
        phone2WorkRadioButton.setToggleGroup(phone2TypeToggleGroup);
        phone2OtherRadioButton.setToggleGroup(phone2TypeToggleGroup);
        
        securityRoleComboBox.getItems().setAll(securityRoleList);
        deptComboBox.getItems().setAll(departmentList);
        mgrComboBox.getItems().setAll(allAppUserList);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            usernameTextField.setText(appUser.getUsername());
            securityRoleComboBox.getSelectionModel().select(appUser.getSecurityRole());
            firstNameTextField.setText(appUser.getFirstName());
            if (appUser.getMidInitial() != null && !appUser.getMidInitial().isEmpty()) {
                midInitialTextField.setText(appUser.getMidInitial());
            }
            lastNameTextField.setText(appUser.getLastName());
            if (appUser.getCourtesyTitle() != null && !appUser.getCourtesyTitle().isEmpty()) {
                courtesyTitleTextField.setText(appUser.getCourtesyTitle());
            }
            jobTitleTextField.setText(appUser.getJobTitle());
            deptComboBox.getSelectionModel().select(appUser.getDepartment());
            if (appUser.getMgr() != null) {
                mgrComboBox.getSelectionModel().select(appUser.getMgr());
            }
            phone1TextField.setText(appUser.getPhone1());
            switch (appUser.getPhone1Type()) {
                case "Home":
                    phone1HomeRadioButton.setSelected(true);
                    break;
                case "Mobile":
                    phone1MobileRadioButton.setSelected(true);
                    break;
                case "Work":
                    phone1WorkRadioButton.setSelected(true);
                    break;
                case "Other":
                    phone1OtherRadioButton.setSelected(true);
                    break;
            }
            if (appUser.getPhone2() != null && !appUser.getPhone2().isEmpty()) {
                phone2TextField.setText(appUser.getPhone2());
                switch (appUser.getPhone2Type()) {
                    case "Home":
                        phone2HomeRadioButton.setSelected(true);
                        break;
                    case "Mobile":
                        phone2MobileRadioButton.setSelected(true);
                        break;
                    case "Work":
                        phone2WorkRadioButton.setSelected(true);
                        break;
                    case "Other":
                        phone2OtherRadioButton.setSelected(true);
                        break;
                }
            }
            emailTextField.setText(appUser.getEmail());
            
            addSaveButton.setText("Save");
        } else {
            setPasswordButton.setVisible(false);
            addSaveButton.setText("Add");
        }
    }
    
    private void startEventHandlers() {
        startUsernameFocusListener();
        startSecurityRoleFocusListener();
        startFirstNameFocusListener();
        startMidInitialFocusListener();
        startLastNameFocusListener();
        startCourtesyTitleFocusListener();
        startJobTitleFocusListener();
        startDeptFocusListener();
        startPhone1FocusListener();
        startPhone2FocusListener();
        startEmailFocusListener();
    }
    
    private void insertAppUser() {
        buildAppUser();
        setPasswordButton.fire();
        runInsertAppUserService();
    }
    
    private void updateAppUser() {
        buildAppUser();
        runUpdateAppUserService();
    }
    
    private void buildAppUser() {
        appUser.setUsername(usernameTextField.getText());
        appUser.setSecurityRole(securityRoleComboBox.getValue());
        appUser.setFirstName(firstNameTextField.getText());
        appUser.setMidInitial(midInitialTextField.getText());
        appUser.setLastName(lastNameTextField.getText());
        appUser.setCourtesyTitle(courtesyTitleTextField.getText());
        appUser.setJobTitle(jobTitleTextField.getText());
        appUser.setDepartment(deptComboBox.getValue());
        appUser.setMgr(mgrComboBox.getValue());
        appUser.setPhone1(phone1TextField.getText());
        appUser.setPhone1Type(getPhone1TypeRadioSelection());
        appUser.setPhone2(phone2TextField.getText());
        appUser.setPhone2Type(getPhone2TypeRadioSelection());
        appUser.setEmail(emailTextField.getText());
    }
    
    private String getPhone1TypeRadioSelection() {
        if (phone1HomeRadioButton.isSelected()) {
            return "Home";
        }
        
        if (phone1MobileRadioButton.isSelected()) {
            return "Mobile";
        }
        
        if (phone1WorkRadioButton.isSelected()) {
            return "Work";
        }
        
        return "Other";
    }
    
    private String getPhone2TypeRadioSelection() {
        if (phone2HomeRadioButton.isSelected()) {
            return "Home";
        }
        
        if (phone2MobileRadioButton.isSelected()) {
            return "Mobile";
        }
        
        if (phone2WorkRadioButton.isSelected()) {
            return "Work";
        }
        
        return "Other";
    }
    
    // Service run handlers
    private void runGetAllAppUsersService() {
        if (!getAllAppUsersService.isRunning()) {
            getAllAppUsersService.reset();
            getAllAppUsersService.start();
        }
    }
    
    private void runInsertAppUserService() {
        if (!insertAppUserService.isRunning()) {
            insertAppUserService.reset();
            insertAppUserService.setAppUser(appUser);
            insertAppUserService.start();
        }
    }
    
    private void runUpdateAppUserService() {
        if (!updateAppUserService.isRunning()) {
            updateAppUserService.reset();
            updateAppUserService.setAppUser(appUser);
            updateAppUserService.start();
        }
    }
    
    private void runGetAllSecurityRolesService() {
        if (!getAllSecurityRolesService.isRunning()) {
            getAllSecurityRolesService.reset();
            getAllSecurityRolesService.start();
        }
    }
    
    private void runGetAllDepartmentsService() {
        if (!getAllDepartmentsService.isRunning()) {
            getAllDepartmentsService.reset();
            getAllDepartmentsService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> getAllAppUsersSuccess = (event) -> {
        allAppUserList = getAllAppUsersService.getValue();
        mgrComboBox.getItems().setAll(allAppUserList);
    };
    
    private EventHandler<WorkerStateEvent> getAllAppUsersFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> insertAppUserSuccess = (event) -> {
        // TODO create form result and close
        this.formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "AppUser for " 
                + this.appUser.getDisplayName()
                + "was successfully added.");
        System.out.println("Insert successful");
    };
    
    private EventHandler<WorkerStateEvent> insertAppUserFailure = (event) -> {
        System.out.println(insertAppUserService.getException());
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> updateAppUserSuccess = (event) -> {
        // TODO create form result and close
        this.formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "AppUser for " 
                + this.appUser.getDisplayName()
                + "was successfully changed.");
        System.out.println("Update successful");
    };
    
    private EventHandler<WorkerStateEvent> updateAppUserFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> getAllSecurityRolesSuccess = (event) -> {
        securityRoleList = getAllSecurityRolesService.getValue();
        securityRoleComboBox.getItems().setAll(securityRoleList);
    };
    
    private EventHandler<WorkerStateEvent> getAllSecurityRolesFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> getAllDepartmentsSuccess = (event) -> {
        departmentList = getAllDepartmentsService.getValue();
        deptComboBox.getItems().setAll(departmentList);
    };
    
    private EventHandler<WorkerStateEvent> getAllDepartmentsFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    // Button event handlers
    @FXML
    private void handleSetPasswordButton(ActionEvent event) {
        FXMLLoader setPasswordLoader = new FXMLLoader(getClass().getResource("SetPasswordForm.fxml"));
        Scene setPasswordScene;
        try {
            setPasswordScene = new Scene(setPasswordLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Stage setPasswordStage = new Stage();
        setPasswordStage.initOwner(titleLabel.getScene().getWindow());
        setPasswordStage.initModality(Modality.APPLICATION_MODAL);
        setPasswordStage.setTitle("SDTracker 1.0 - Set Password");
        setPasswordStage.setScene(setPasswordScene);
        SetPasswordFormController setPasswordFormController = setPasswordLoader.getController();
        setPasswordFormController.setAdminPasswordSet(true);
        setPasswordFormController.setAppUser(appUser);
        setPasswordStage.showAndWait();
        FormResult setPasswordResult = setPasswordFormController.getFormResult();
        passwordSet = setPasswordResult.getResultStatus().equals(FormResultStatus.SUCCESS);
        systemMessageLabel.setText(setPasswordResult.getMessage());
        System.out.println(appUser.getSalt());
        System.out.println(appUser.getPassword());
    }
    
    @FXML
    private void handleCancelButton(ActionEvent event) {
        // TODO check for set password
    }
    
    @FXML
    private void handleAddSaveButton(ActionEvent event) {
        if (validateAll()) {
            if (formMode.equals(FormMode.INSERT)) {
                insertAppUser();
            } else {
                updateAppUser();
            }
        } else {
            systemMessageLabel.setText("Please check all fields are filled out correctly");
            systemMessageLabel.getStyleClass().removeAll("system-message-label");
            systemMessageLabel.getStyleClass().add("system-message-label-error");
        }
    }
    
    // Change listeners. Start focus listeners are created in a way that in the future they could be stopped if desired    
    private void startUsernameFocusListener() {
        if (usernameFocusListener == null) {
            usernameFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateUsername();
                }
            };
        }
        usernameTextField.focusedProperty().addListener(usernameFocusListener);
    }
    
    private void startSecurityRoleFocusListener() {
        if (securityRoleFocusListener == null) {
            securityRoleFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateUsername();
                }
            };
        }
        securityRoleComboBox.focusedProperty().addListener(securityRoleFocusListener);
    }
    
    private void startFirstNameFocusListener() {
        if (firstNameFocusListener == null) {
            firstNameFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateFirstName();
                }
            };
        }
        firstNameTextField.focusedProperty().addListener(firstNameFocusListener);
    }
    
    private void startMidInitialFocusListener() {
         if (midInitialFocusListener == null) {
            midInitialFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateMidInitial();
                }
            };
        }
        midInitialTextField.focusedProperty().addListener(midInitialFocusListener);
    }
    
    private void startLastNameFocusListener() {
        if (lastNameFocusListener == null) {
            lastNameFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateLastName();
                }
            };
        }
        lastNameTextField.focusedProperty().addListener(lastNameFocusListener);
    }
    
    private void startCourtesyTitleFocusListener() {
         if (courtesyTitleFocusListener == null) {
            courtesyTitleFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateCourtesyTitle();
                }
            };
        }
        courtesyTitleTextField.focusedProperty().addListener(courtesyTitleFocusListener);
    }
    
    private void startJobTitleFocusListener() {
        if (jobTitleFocusListener == null) {
            jobTitleFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateEmail();
                }
            };
        }
        jobTitleTextField.focusedProperty().addListener(jobTitleFocusListener);
    }
    
    private void startDeptFocusListener() {
        if (deptFocusListener == null) {
            deptFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateDept();
                }
            };
        }
        deptComboBox.focusedProperty().addListener(deptFocusListener);
    }
    
    private void startPhone1FocusListener() {
        if (phone1FocusListener == null) {
            phone1FocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validatePhone1();
                }
            };
        }
        phone1TextField.focusedProperty().addListener(phone1FocusListener);
    }
    
    private void startPhone2FocusListener() {
        if (phone2FocusListener == null) {
            phone2FocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validatePhone2();
                }
            };
        }
        phone2TextField.focusedProperty().addListener(phone2FocusListener);
    }
    
    private void startEmailFocusListener() {
        if (emailFocusListener == null) {
            emailFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateEmail();
                }
            };
        }
        emailTextField.focusedProperty().addListener(emailFocusListener);
    }
    
    // Validation methods
    private boolean validateAll() {
        return validateUsername()
            && validateSecurityRole()
            && validateFirstName()
            && validateMidInitial()
            && validateLastName()
            && validateCourtesyTitle()
            && validateJobTitle()
            && validateDept()
            && validatePhone1()
            && validatePhone1Type()
            && validatePhone2()
            && validatePhone2Type()
            && validateEmail();
    }
    
    private boolean validateUsername() {
        String input = usernameTextField.getText();
        if (input.isEmpty()) {
            usernameErrorLabel.setText("Username is required");
            return false;
        }
        
        if (!input.matches(ValidationPattern.USER_NAME)) {
            usernameErrorLabel.setText("8-20 characters (a-z,0-9, . or _) starts with a letter");
            return false;
        }
        
        usernameErrorLabel.setText("");
        return true;
    }
    
    private boolean validateSecurityRole() {
        if (securityRoleComboBox.getSelectionModel().isEmpty()) {
            securityRoleErrorLabel.setText("Security Role is required");
            return false;
        }
        
        securityRoleErrorLabel.setText("");
        return true;
    }
    
    private boolean validateFirstName() {
        String input = firstNameTextField.getText();
        if (input.isEmpty()) {
            firstNameErrorLabel.setText("First name is required");
            return false;
        }
        
        if (input.length() > 50) {
            firstNameErrorLabel.setText("First name is limited to 50 characters or less");
            return false;
        }
        
        firstNameErrorLabel.setText("");
        return true;
    }
    
    private boolean validateMidInitial() {
        String input = midInitialTextField.getText();
        if (input.length() > 5) {
            midInitialErrorLabel.setText("Middle initial is limited to 5 characters or less");
            return false;
        } else {
            midInitialErrorLabel.setText("");
            return true;
        }
    }
    
    private boolean validateLastName() {
        String input = lastNameTextField.getText();
        if (input.isEmpty()) {
            lastNameErrorLabel.setText("Last name is required");
            return false;
        } 
        
        if (input.length() > 50) {
            lastNameErrorLabel.setText("Last name is limited to 50 characters or less");
            return false;
        }
        
        lastNameErrorLabel.setText("");
        return true;
    }
    
    private boolean validateCourtesyTitle() {
        String input = courtesyTitleTextField.getText();
        if (input.length() > 10) {
            courtesyTitleErrorLabel.setText("Courtesy title is limited to 10 characters or less");
            return false;
        } else {
            courtesyTitleErrorLabel.setText("");
            return true;
        }
    }
    
    private boolean validateJobTitle() {
        String input = jobTitleTextField.getText();
        if (input.isEmpty()) {
            jobTitleErrorLabel.setText("Job title is required");
            return false;
        } 
        
        if (input.length() > 255) {
            jobTitleErrorLabel.setText("Job title is limited to 255 characters or less");
            return false;
        }
        
        jobTitleErrorLabel.setText("");
        return true;
    }
    
    private boolean validateDept() {
        if (deptComboBox.getSelectionModel().isEmpty()) {
            deptErrorLabel.setText("Department is required");
            return false;
        } else {
            deptErrorLabel.setText("");
            return true;
        }
    }
    
    private boolean validatePhone1() {
        String input = phone1TextField.getText();
        if (input.isEmpty()) {
            phone1ErrorLabel.setText("Phone is required");
            return false;
        }
        
        if (!input.matches(ValidationPattern.PHONE)) {
            phone1ErrorLabel.setText("Phone must match: 55555555555, 555-555-5555, (555)555-5555,\n(555)5555555");
            return false;
        }
        
        phone1ErrorLabel.setText("");
        return true;
    }
    
    private boolean validatePhone1Type() {
        if (!phone1TextField.getText().isEmpty() && phone1TypeToggleGroup.getSelectedToggle() == null) {
            phone1TypeErrorLabel.setText("Phone type required");
            return false;
        }
        return true;
    }
    
    private boolean validatePhone2() {
        String input = phone2TextField.getText();
        
        if (!input.isEmpty() && !input.matches(ValidationPattern.PHONE)) {
            phone2ErrorLabel.setText("Phone must match: 55555555555, 555-555-5555, (555)555-5555,\n(555)5555555");
            return false;
        }
        
        phone2ErrorLabel.setText("");
        return true;
    }
    
    private boolean validatePhone2Type() {
        if (!phone2TextField.getText().isEmpty() && phone2TypeToggleGroup.getSelectedToggle() == null) {
            phone2TypeErrorLabel.setText("Phone type required");
            return false;
        }
        return true;
    }
    
    private boolean validateEmail() {
        String input = emailTextField.getText();
        if (input.isEmpty()) {
            emailErrorLabel.setText("Email is required");
            return false;
        } 
        
        if (input.length() > 255) {
            emailErrorLabel.setText("Email is limited to 255 characters or less");
            return false;
        }
        
        if (!input.matches(ValidationPattern.EMAIL)) {
            emailErrorLabel.setText("Invalid email address");
            return false;
        }
        
        emailErrorLabel.setText("");
        return true;
    }
    
}
