/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sdtracker.database.AppUserDbServiceManager;
import sdtracker.database.AppUserDbServiceManager.GetAllAppUsersService;
import sdtracker.database.AssetDbServiceManager;
import sdtracker.database.AssetDbServiceManager.*;
import sdtracker.database.AssetTypeDbServiceManager;
import sdtracker.database.AssetTypeDbServiceManager.GetAllAssetTypesService;
import sdtracker.database.MfgDbServiceManager;
import sdtracker.database.MfgDbServiceManager.GetAllMfgsService;
import sdtracker.model.AppUser;
import sdtracker.model.Asset;
import sdtracker.model.AssetType;
import sdtracker.model.Mfg;
import sdtracker.utility.AssetNumberGenerator;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class AssetFormController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private Label assetNumberLabel;
    @FXML private TextField nameTextField;
    @FXML private ComboBox<AssetType> assetTypeComboBox;
    @FXML private TextField serialNumberTextField;
    @FXML private TextField modelNumberTextField;
    @FXML private ComboBox<Mfg> mfgComboBox;
    @FXML private ComboBox<AppUser> assignedToComboBox;
    @FXML private Label nameErrorLabel;
    @FXML private Label assetTypeErrorLabel;
    @FXML private Label serialNumberErrorLabel;
    @FXML private Label modelNumberErrorLabel;
    @FXML private Label mfgErrorLabel;
    @FXML private Label systemMessageLabel;
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    
    private ChangeListener<Boolean> nameFocusListener;
    private ChangeListener<Boolean> assetTypeFocusListener;
    private ChangeListener<Boolean> serialNumberFocusListener;
    private ChangeListener<Boolean> modelNumberFocusListener;
    private ChangeListener<Boolean> mfgFocusListener;
    
    private FormMode formMode = FormMode.INSERT;
    private FormResult formResult;
    
    private AssetDbServiceManager assetDbServiceManager = AssetDbServiceManager.getServiceManager();
    private InsertAssetService insertAssetService;
    private UpdateAssetService updateAssetService;
    private CheckForDuplicateAssetService checkForDuplicateAssetService;
    
    private AssetTypeDbServiceManager assetTypeDbServiceManager = AssetTypeDbServiceManager.getServiceManager();
    private GetAllAssetTypesService getAllAssetTypesService;
    private ObservableList<AssetType> allAssetTypeList = FXCollections.observableArrayList();
    
    private MfgDbServiceManager mfgDbServiceManager = MfgDbServiceManager.getServiceManager();
    private GetAllMfgsService getAllMfgsService;
    private ObservableList<Mfg> allMfgList = FXCollections.observableArrayList();
    
    private AppUserDbServiceManager appUserDbServiceManager = AppUserDbServiceManager.getServiceManager();
    private GetAllAppUsersService getAllAppUsersService;
    private ObservableList<AppUser> allAppUserList = FXCollections.observableArrayList();
    
    private Asset asset;
    private Stage currentStage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeServices();
        establishBindings();
        runGetAllAssetTypeService();
    }
    
    private void initializeServices() {
        insertAssetService = assetDbServiceManager.new InsertAssetService();
        updateAssetService = assetDbServiceManager.new UpdateAssetService();
        checkForDuplicateAssetService = assetDbServiceManager.new CheckForDuplicateAssetService();
        
        getAllAssetTypesService = assetTypeDbServiceManager.new GetAllAssetTypesService();
        getAllMfgsService = mfgDbServiceManager.new GetAllMfgsService();
        getAllAppUsersService = appUserDbServiceManager.new GetAllAppUsersService();
        
        insertAssetService.setOnSucceeded(insertAssetSuccess);
        insertAssetService.setOnFailed(insertAssetFailure);
        
        updateAssetService.setOnSucceeded(updateAssetSuccess);
        updateAssetService.setOnFailed(updateAssetFailure);
        
        checkForDuplicateAssetService.setOnSucceeded(checkForDuplicateAssetSuccess);
        checkForDuplicateAssetService.setOnFailed(checkForDuplicateAssetFailure);
        
        getAllAssetTypesService.setOnSucceeded(getAllAssetTypeSuccess);
        getAllAssetTypesService.setOnFailed(getAllAssetTypeFailure);
        
        getAllMfgsService.setOnSucceeded(getAllMfgSuccess);
        getAllMfgsService.setOnFailed(getAllMfgFailure);
        
        getAllAppUsersService.setOnSucceeded(getAllAppUserSuccess);
        getAllAppUsersService.setOnFailed(getAllAppUserFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = insertAssetService.runningProperty()
                                     .or(updateAssetService.runningProperty())
                                     .or(checkForDuplicateAssetService.runningProperty())
                                     .or(getAllAssetTypesService.runningProperty())
                                     .or(getAllMfgsService.runningProperty())
                                     .or(getAllAppUsersService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void initializeInputElements() {
        assetTypeComboBox.getItems().addAll(allAssetTypeList);
        mfgComboBox.getItems().addAll(allMfgList);
        assignedToComboBox.getItems().addAll(allAppUserList);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            titleLabel.setText("Update Asset");
            addSaveButton.setText("Save");
            
            assetNumberLabel.setText(asset.getAssetNumber());
            nameTextField.setText(asset.getName());
            assetTypeComboBox.getSelectionModel().select(asset.getAssetType());
            if (asset.getSerialNumber() != null) {
                serialNumberTextField.setText(asset.getSerialNumber());
            }
            if (asset.getModelNumber() != null) {
                modelNumberTextField.setText(asset.getModelNumber());
            }
            mfgComboBox.getSelectionModel().select(asset.getMfg());
            if (asset.getAssignedToAppUser() != null) {
                assignedToComboBox.getSelectionModel().select(asset.getAssignedToAppUser());
            }
        } else {
            asset = new Asset();
            assetNumberLabel.setText(AssetNumberGenerator.generateAssetNumber());
        }
    }
    
    private void insertAsset() {
        asset.setAssetNumber(assetNumberLabel.getText());
        buildAsset();
        runCheckForDuplicateAssetService();
    }
    
    private void updateAsset() {
        buildAsset();
        runUpdateAssetService();
    }
    
    private void buildAsset() {
        asset.setName(nameTextField.getText());
        asset.setAssetType(assetTypeComboBox.getValue());
        asset.setModelNumber(modelNumberTextField.getText());
        asset.setSerialNumber(serialNumberTextField.getText());
        asset.setMfg(mfgComboBox.getValue());
        if (!assignedToComboBox.getSelectionModel().isEmpty()) {
            asset.setAssignedToAppUser(assignedToComboBox.getValue());
        }
    }
    
    // Service run handlers
    private void runInsertAssetService() {
        if (!insertAssetService.isRunning()) {
            insertAssetService.reset();
            insertAssetService.setAsset(asset);
            insertAssetService.start();
        }
    }
    
    private void runUpdateAssetService() {
        if (!updateAssetService.isRunning()) {
            updateAssetService.reset();
            updateAssetService.setAsset(asset);
            updateAssetService.start();
        }
    }
    
    private void runCheckForDuplicateAssetService() {
        if (!checkForDuplicateAssetService.isRunning()) {
            checkForDuplicateAssetService.reset();
            checkForDuplicateAssetService.setAssetNumber(asset.getAssetNumber());
            checkForDuplicateAssetService.start();
        }
    }
    
    private void runGetAllAssetTypeService() {
        if (!getAllAssetTypesService.isRunning()) {
            getAllAssetTypesService.reset();
            getAllAssetTypesService.start();
        }
    }
    
    private void runGetAllMfgService() {
        if (!getAllMfgsService.isRunning()) {
            getAllMfgsService.reset();
            getAllMfgsService.start();
        }
    }
    
    private void runGetAllAppUserService() {
        if (!getAllAppUsersService.isRunning()) {
            getAllAppUsersService.reset();
            getAllAppUsersService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> insertAssetSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Asset " 
                + asset.getName()
                + "was successfully added.");
        System.out.println("Insert successful");
        currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> insertAssetFailure = (event) -> {
        systemMessageLabel.setText("System error on insert, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");

    };
    
    private EventHandler<WorkerStateEvent> updateAssetSuccess = (event) -> {
        // TODO create form result and close
        formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Asset " 
                + asset.getName()
                + "was successfully changed.");
        System.out.println("Update successful");
        currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.close();
    };
    
    private EventHandler<WorkerStateEvent> updateAssetFailure = (event) -> {
        systemMessageLabel.setText("System error on update, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateAssetSuccess = (event) -> {
        if (checkForDuplicateAssetService.getValue()) {
            systemMessageLabel.setText("Asset already exists");
            systemMessageLabel.getStyleClass().removeAll("system-message-label");
            systemMessageLabel.getStyleClass().add("system-message-label-error");
        } else {
            runInsertAssetService();
        }
    };
    
    private EventHandler<WorkerStateEvent> checkForDuplicateAssetFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> getAllAssetTypeSuccess = (event) -> {
        allAssetTypeList = getAllAssetTypesService.getValue();
        runGetAllMfgService();
    };
    
    private EventHandler<WorkerStateEvent> getAllAssetTypeFailure = (event) -> {
        displaySystemMessage("System error loading Asset Types, please try again", true);
        runGetAllMfgService();
    };
    
    private EventHandler<WorkerStateEvent> getAllMfgSuccess = (event) -> {
        allMfgList = getAllMfgsService.getValue();
        runGetAllAppUserService();
    };
    
    private EventHandler<WorkerStateEvent> getAllMfgFailure = (event) -> {
        displaySystemMessage("System error loading manufacturers, please try again", true);
        runGetAllAppUserService();
    };
    
    private EventHandler<WorkerStateEvent> getAllAppUserSuccess = (event) -> {
        allAppUserList = getAllAppUsersService.getValue();
        initializeInputElements();
        applyFormMode();
    };
    
    private EventHandler<WorkerStateEvent> getAllAppUserFailure = (event) -> {
        displaySystemMessage("System error loading users, please try again", true);
        initializeInputElements();
        applyFormMode();
    };
    
    @FXML
    void handleAddSaveButton(ActionEvent event) {
        if (validateAll()) {
            if (formMode.equals(FormMode.INSERT)) {
                insertAsset();
            } else {
                updateAsset();
            }
        }
    }

    @FXML
    void handleCancelButton(ActionEvent event) {
        currentStage = (Stage) titleLabel.getScene().getWindow();
        currentStage.close();
    }

    public void specifyUpdateMode(Asset asset) {
        this.asset = asset;
        formMode = FormMode.UPDATE;
    }
    
    public FormResult getFormResult() {
        return formResult;
    }
    
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
    
    private boolean validateAll() {
        return validateName()
            && validateAssetType()
            && validateSerialNumber()
            && validateModelNumber()
            && validateMfg();
    }
    
    private boolean validateName() {
        String input = nameTextField.getText();
        if (input.isEmpty()) {
            nameErrorLabel.setText("Asset name is required");
            return false;
        }
        
        if (input.length() > 50) {
            nameErrorLabel.setText("Asset name cannot be greater than 50 characters");
            return false;
        }
        nameErrorLabel.setText("");
        return true;
    }
    
    private boolean validateAssetType() {
        if (assetTypeComboBox.getValue() == null) {
            assetTypeErrorLabel.setText("Asset Type required");
            return false;
        }
        
        assetTypeErrorLabel.setText("");
        return true;
    }
    
    private boolean validateSerialNumber() {
        String input = serialNumberTextField.getText();
        if (input.length() > 50) {
             serialNumberErrorLabel.setText("Serial number cannot be greater than 50 characters");
            return false;
        }
        
        serialNumberErrorLabel.setText("");
        return true;
    }
    
    private boolean validateModelNumber() {
        String input = modelNumberTextField.getText();
        if (input.length() > 50) {
             modelNumberErrorLabel.setText("Model number cannot be greater than 50 characters");
            return false;
        }
        
        modelNumberErrorLabel.setText("");
        return true;
    }
    
    private boolean validateMfg() {
        if (mfgComboBox.getValue() == null) {
            mfgErrorLabel.setText("Manufacturer required");
            return false;
        }
        
        mfgErrorLabel.setText("");
        return true;
    }
}
