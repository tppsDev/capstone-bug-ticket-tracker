/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import sdtracker.Session;
import sdtracker.service.LoginService;
import sdtracker.utility.ValidationResult;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class LoginController implements Initializable {
    @FXML private TextField usernameTextField;
    @FXML private TextField unmaskedPasswordTextField;

    @FXML private PasswordField maskedPasswordField;

    @FXML private Label errorLabel;

    @FXML private Button loginButton;
    @FXML private Button cancelButton;

    @FXML private ImageView showPasswordImageView;
    @FXML private ImageView maskPasswrdImageView;
    
    @FXML private ProgressIndicator progressIndicator;
    
    private LoginService service;
    private Session session = Session.getSession();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Create instance of the LoginService for UI thread management    
        service = new LoginService();
        
        establishBindings();
        
        service.setOnSucceeded((WorkerStateEvent workerStateEvent) -> {
            ValidationResult loginResult;
            try {
                loginResult = handleLoginResults();
                if (!loginResult.isValid()) {
                    errorLabel.setText(loginResult.getMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Some error: " + e.getMessage());
            }
            
            
        });
        service.setOnFailed((event) -> {
            System.out.println(service.getException().getMessage());
            service.getException().printStackTrace();
        });
    }    

    private void establishBindings() {
        loginButton.disableProperty().bind(service.runningProperty());
        progressIndicator.visibleProperty().bind(service.runningProperty());

        showPasswordImageView.visibleProperty().bind(maskedPasswordField.visibleProperty());
        maskPasswrdImageView.visibleProperty().bind(maskedPasswordField.visibleProperty().not());
        unmaskedPasswordTextField.visibleProperty().bind(maskedPasswordField.visibleProperty().not());

        unmaskedPasswordTextField.textProperty().bindBidirectional(maskedPasswordField.textProperty());
    }

    /**
     *Retrieves login credentials from the UI, reporting error to UI if fields are not populated
     * @return
     */
    @FXML
    public ValidationResult getLoginCredentials() {
        // Verify both fields contain data before setting service parameters
        if ((!usernameTextField.getText().isEmpty() && usernameTextField.getText() != null)  
                    || (!maskedPasswordField.getText().isEmpty() && maskedPasswordField.getText() != null)) {
            service.setUsername(usernameTextField.getText());
            service.setPassword(maskedPasswordField.getText());
        } else {
            return new ValidationResult(false, "Username and password required");
        }
        return new ValidationResult();
    }

    /**
     *Handles the results of the login service and manages UI accordingly.
     * @return
     * @throws IOException
     */

    @FXML
    public ValidationResult handleLoginResults() throws IOException {
        // Check returned success status of service
        if (service.getValue()) {
            System.out.println("Login success");
            // TODO goto main screen and remove text population
                        
            Stage window = (Stage) loginButton.getScene().getWindow();           
            Parent homeScreenParent;
            homeScreenParent = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
      
            Scene homeScreenScene = new Scene(homeScreenParent);
            
            window.setX(10);
            window.setY(10);
            window.setTitle("SDTracker 1.0");
            window.setScene(homeScreenScene);
            window.show();
                


        } else {
            // Handle failed login attempt
            int attemptsRemaining = 3 - Session.getSession().getFailedLoginAttempts();
            maskedPasswordField.clear();
            
            // TODO update logfile
            // Send error message to UI, after 3 attempts exit
            if (attemptsRemaining > 0) {
                return new ValidationResult(false, "The username and password do not match. "
                        + attemptsRemaining
                        + " more attempts before exit.");
            } else {
                System.exit(0);
            }
        }

        return new ValidationResult();
    }
    
    // Button handlers
    /*
     * Handle loginButton onAction event
     * @param event
    */
    @FXML
    private void handleLoginButton (ActionEvent event) {
        // Clear error message from any previous clicks
        errorLabel.setText("");
        // Check login service status ignore all but SUCCEEDED or READY
        if (service.getState() == Service.State.SUCCEEDED || service.getState() == Service.State.READY)  {
            
            if (service.getState() == Service.State.SUCCEEDED) {
                service.reset();  // reset puts service in ready status after previous clicks
            }
            
            // Retrieve the login credentials, handle FormException which will contain message for user
            ValidationResult credentialResult = getLoginCredentials();
            if (credentialResult.isValid()) {
                // Run the login service
                service.start();
            } else {
                errorLabel.setText(credentialResult.getMessage());
            }

        }
    }
    
    @FXML
    private void handleCancelButton (ActionEvent event) {
        // possibly check state for now close
        System.exit(0);
    }
    
    @FXML
    private void handleShowPassword() {
        System.out.println(maskedPasswordField.isVisible());
        maskedPasswordField.setVisible(!maskedPasswordField.isVisible());
    }
    
    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loginButton.fire();
        }
    }

}
