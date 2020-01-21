/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import sdtracker.model.AppUser;
import sdtracker.model.Department;
import sdtracker.model.SecurityRole;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class AppUserFormController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    @FXML private TextField usernameTextField;
    @FXML private Button setPasswordButton;
    @FXML private ComboBox<SecurityRole> securityRoleComboBox;
    @FXML private TextField firstNameTextField;
    @FXML private TextField midInitialTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField courtesyTitleTextField;
    @FXML private TextField jobTitleTextField;
    @FXML private ComboBox<Department> deptComboBox;
    @FXML private ComboBox<AppUser> mgrComboBox;
    @FXML private TextField phone1TextField;
    @FXML private RadioButton phone1HomeRadioButton;
    @FXML private RadioButton phone1MobileRadioButton;
    @FXML private RadioButton phone1WorkRadioButton;
    @FXML private RadioButton phone1OtherRadioButton;
    @FXML private TextField phone2TextField;
    @FXML private RadioButton phone2HomeRadioButton;
    @FXML private RadioButton phone2MobileRadioButton;
    @FXML private RadioButton phone2WorkRadioButton;
    @FXML private RadioButton phone2OtherRadioButton;
    @FXML private TextField emailTextView;
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
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
