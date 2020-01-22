/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import sdtracker.database.AppUserDbServiceManager;
import sdtracker.database.AppUserDbServiceManager.UpdateAppUserService;
import sdtracker.model.AppUser;
import sdtracker.utility.PasswordUtil;
import sdtracker.utility.ValidationResult;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class SetPasswordFormController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField unmaskedCurrentPasswordTextField;
    @FXML private TextField unmaskedNewPasswordTextField;
    @FXML private TextField unmaskedConfirmPasswordTextField;
    @FXML private ImageView showCurrentPasswordImageView;
    @FXML private ImageView maskCurrentPasswordImageView;
    @FXML private ImageView showNewPasswordImageView;
    @FXML private ImageView maskNewPasswordImageView;
    @FXML private ImageView showConfirmPasswordImageView;
    @FXML private ImageView maskConfirmPasswordImageView;
    @FXML private Label currentPasswordErrorLabel;
    @FXML private Label newPasswordErrorLabel;
    @FXML private Label confirmPasswordErrorLabel;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private Label systemMessageLabel;
    
    private AppUser appUser;
    private final BooleanProperty adminSetPassword = new SimpleBooleanProperty(false);
    private AppUserDbServiceManager appUserDbServiceManager = AppUserDbServiceManager.getServiceManager();
    private UpdateAppUserService updateAppUserService = appUserDbServiceManager.new UpdateAppUserService();
    private FormResult formResult;

    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeServices();
        establishBindings();
    }
    
    private void initializeServices() {
        updateAppUserService.setOnSucceeded(updateAppUserSuccess);
        updateAppUserService.setOnFailed(updateAppUserFailure);
    }
    
    private void establishBindings() {
        progressIndicator.visibleProperty().bind(updateAppUserService.runningProperty());
        currentPasswordField.textProperty().bindBidirectional(unmaskedCurrentPasswordTextField.textProperty());
        newPasswordField.textProperty().bindBidirectional(unmaskedNewPasswordTextField.textProperty());
        confirmPasswordField.textProperty().bindBidirectional(unmaskedConfirmPasswordTextField.textProperty());
        
        showCurrentPasswordImageView.visibleProperty().bind(currentPasswordField.visibleProperty());
        maskCurrentPasswordImageView.visibleProperty().bind(currentPasswordField.visibleProperty().not());
        unmaskedCurrentPasswordTextField.visibleProperty().bind(currentPasswordField.visibleProperty().not());
        
        showNewPasswordImageView.visibleProperty().bind(newPasswordField.visibleProperty());
        maskNewPasswordImageView.visibleProperty().bind(newPasswordField.visibleProperty().not());
        unmaskedNewPasswordTextField.visibleProperty().bind(newPasswordField.visibleProperty().not());
        
        showConfirmPasswordImageView.visibleProperty().bind(confirmPasswordField.visibleProperty());
        maskConfirmPasswordImageView.visibleProperty().bind(confirmPasswordField.visibleProperty().not());
        unmaskedConfirmPasswordTextField.visibleProperty().bind(confirmPasswordField.visibleProperty().not());
    }
    
    private void runUpdateAppUserService() {
        if (!updateAppUserService.isRunning()) {
            updateAppUserService.reset();
            updateAppUserService.setAppUser(appUser);
            updateAppUserService.start();
        }
    }
    
    private EventHandler<WorkerStateEvent> updateAppUserSuccess = (event) -> {
        // TODO create form result and close
        this.formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Password for " 
                + this.appUser.getDisplayName()
                + " was successfully changed.");
        System.out.println("Update successful");
        
    };
    
    private EventHandler<WorkerStateEvent> updateAppUserFailure = (event) -> {
        systemMessageLabel.setText("System error, please try your request again.");
        systemMessageLabel.getStyleClass().removeAll("system-message-label");
        systemMessageLabel.getStyleClass().add("system-message-label-error");
    };
    
    @FXML
    private void handleCancelButton(ActionEvent event) {
        
    }
    
    @FXML
    private void handleSaveButton(ActionEvent event) {
        if (appUser.getUsername().equals("test")) {
            systemMessageLabel.setText("Test user password cannot be changed");
            return;
        }
        if (validateAll()) {
            System.out.println(appUser.getSalt());
            System.out.println(appUser.getPassword());
            appUser.setEncryptedPassword(newPasswordField.getText());
            System.out.println(appUser.getSalt());
            System.out.println(appUser.getPassword());
            runUpdateAppUserService();
        }
    }
    
    @FXML
    private void handleShowCurrentPassword() {
        currentPasswordField.setVisible(!currentPasswordField.isVisible());
    }
    
    @FXML
    private void handleShowNewPassword() {
        newPasswordField.setVisible(!newPasswordField.isVisible());
    }
    
    @FXML
    private void handleShowConfirmPassword() {
        confirmPasswordField.setVisible(!confirmPasswordField.isVisible());
    }
    
    private boolean validateAll() {
        if (adminSetPassword.get()) {
            return validateNewPassword()
                && validateConfirmPassword();
        }
        return validateCurrentPassword()
            && validateNewPassword()
            && validateConfirmPassword();
    }
    
    private boolean validateCurrentPassword() {
        String input = currentPasswordField.getText();
        if (input.isEmpty()) {
            currentPasswordErrorLabel.setText("Current password required");
            return false;
        }
        
        if (!appUser.checkPassword(input)) {
            currentPasswordErrorLabel.setText("Current password incorrect");
            return false;
        }
        
        currentPasswordErrorLabel.setText("");
        return true;
    }
    
    private boolean validateNewPassword() {
        String input = newPasswordField.getText();
        if (input.isEmpty()) {
            newPasswordErrorLabel.setText("New password required");
            return false;
        }
        
        ValidationResult passwordResult = PasswordUtil.validatePassword(input);
        if (!passwordResult.isValid()) {
            newPasswordErrorLabel.setText(passwordResult.getMessage());
            return false;
        }
        
        String confirmInput = confirmPasswordField.getText();
        if (!confirmInput.isEmpty() && !input.equals(confirmInput)) {
            newPasswordErrorLabel.setText("Passwords do not match");
            confirmPasswordErrorLabel.setText("Passwords do not match");
            return false;
        }
                
        newPasswordErrorLabel.setText("");
        return true;
    }
    
    private boolean validateConfirmPassword() {
        String input = confirmPasswordField.getText();
        if (input.isEmpty()) {
            confirmPasswordErrorLabel.setText("Confirm password required");
            return false;
        }
        
        ValidationResult passwordResult = PasswordUtil.validatePassword(input);
        if (!passwordResult.isValid()) {
            confirmPasswordErrorLabel.setText(passwordResult.getMessage());
            return false;
        }
        
        String newInput = confirmPasswordField.getText();
        if (!newInput.isEmpty() && !input.equals(newInput)) {
            newPasswordErrorLabel.setText("Passwords do not match");
            confirmPasswordErrorLabel.setText("Passwords do not match");
            return false;
        }
        
        confirmPasswordErrorLabel.setText("");
        return true;
    }
    
    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
    
    private boolean isAdminPasswordSet() {
        return adminSetPassword.get();
    }

    public void setAdminPasswordSet(boolean value) {
        adminSetPassword.set(value);
    }

    private BooleanProperty adminPasswordSetProperty() {
        return adminSetPassword;
    }

    public FormResult getFormResult() {
        return formResult;
    }
    
}
