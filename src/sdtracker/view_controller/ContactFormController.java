/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import sdtracker.database.ContactDbServiceManager;
import sdtracker.database.ContactDbServiceManager.*;
import sdtracker.model.Contact;
import sdtracker.model.ContactType;
import sdtracker.utility.USState;
import sdtracker.utility.ValidationPattern;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class ContactFormController implements Initializable {
    // Header
    @FXML private Label titleLabel;
    @FXML private ProgressBar progressIndicator;
    
    // Contact type radio button bar
    @FXML private RadioButton prospectRadioButton;
    @FXML private RadioButton clientRadioButton;
    @FXML private RadioButton vendorRadioButton;
    @FXML private ToggleGroup contactTypeToggleGroup = new ToggleGroup();
   
    // Main form text fields and combo boxes
    @FXML private TextField firstNameTextField;
    @FXML private TextField midInitialTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField courtesyTitleTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField phoneTextField;
    @FXML private ComboBox<String> phoneTypeComboBox;
    @FXML private TextField addrTextField;
    @FXML private TextField addr2TextField;
    @FXML private TextField cityTextField;
    @FXML private ComboBox<USState> stateComboBox;
    @FXML private TextField zipcodeTextField;
    @FXML private TextField companyTextField;
    @FXML private TextField jobTitleTextField;
    
    // Error labels
    @FXML private Label firstNameErrorLabel;
    @FXML private Label midInitialErrorLabel;
    @FXML private Label lastNameErrorLabel;
    @FXML private Label courtesyTitleErrorLabel;
    @FXML private Label emailErrorLabel;
    @FXML private Label phoneTypeErrorLabel;
    @FXML private Label addrErrorLabel;
    @FXML private Label addr2ErrorLabel;
    @FXML private Label cityErrorLabel;
    @FXML private Label stateErrorLabel;
    @FXML private Label companyErrorLabel;
    @FXML private Label zipcodeErrorLabel;
    @FXML private Label jobTitleErrorLabel;
    @FXML private Label phoneErrorLabel;
    @FXML private Label generalErrorLabel;
    
    // Buttons
    @FXML private Button cancelButton;
    @FXML private Button addSaveButton;
    
    // Change listeners
    private ChangeListener<Boolean> firstNameFocusListener;
    private ChangeListener<Boolean> midInitialFocusListener;
    private ChangeListener<Boolean> lastNameFocusListener;
    private ChangeListener<Boolean> courtesyTitleFocusListener;
    private ChangeListener<Boolean> emailFocusListener;
    private ChangeListener<Boolean> phoneFocusListener;
    private ChangeListener<Boolean> phoneTypeFocusListener;
    private ChangeListener<Boolean> addrFocusListener;
    private ChangeListener<Boolean> addr2FocusListener;
    private ChangeListener<Boolean> cityFocusListener;
    private ChangeListener<Boolean> stateFocusListener;
    private ChangeListener<Boolean> zipcodeFocusListener;
    private ChangeListener<Boolean> companyFocusListener;
    private ChangeListener<Boolean> jobTitleFocusListener;
    
    private FormMode formMode = FormMode.INSERT;
    private ContactDbServiceManager contactDbServiceManager = ContactDbServiceManager.getServiceManager();
    private InsertContactService insertContactService;
    private UpdateContactService updateContactService;
    private GetAllContactsService getAllContactsService;
    private ObservableList<Contact> allContactList;
    private Contact contact;
    private FormResult formResult;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeServices();
        establishBindings();
        initializeInputElements();
        applyFormMode();
        startEventHandlers();
        // TODO remove after test
        if (formMode.equals(FormMode.INSERT)) contact = new Contact();
        
    }
    
    private void initializeServices() {
        getAllContactsService = contactDbServiceManager.new GetAllContactsService();
        insertContactService = contactDbServiceManager.new InsertContactService();
        updateContactService = contactDbServiceManager.new UpdateContactService();
        
        getAllContactsService.setOnSucceeded(getAllContactsSuccess);
        getAllContactsService.setOnFailed(getAllContactsFailure);
        insertContactService.setOnSucceeded(insertContactSuccess);
        insertContactService.setOnFailed(insertContactFailure);
        updateContactService.setOnSucceeded(updateContactSuccess);
        updateContactService.setOnFailed(updateContactFailure);
    }
    
    private void establishBindings() {
        BooleanBinding servicesRunning = getAllContactsService.runningProperty()
                                        .or(insertContactService.runningProperty())
                                        .or(updateContactService.runningProperty());
        progressIndicator.visibleProperty().bind(servicesRunning);
    }
    
    private void initializeInputElements() {
        stateComboBox.getItems().setAll(USState.values());
        phoneTypeComboBox.getItems().setAll("Home","Mobile","Work","Other");
        prospectRadioButton.setToggleGroup(contactTypeToggleGroup);
        clientRadioButton.setToggleGroup(contactTypeToggleGroup);
        vendorRadioButton.setToggleGroup(contactTypeToggleGroup);
    }
    
    private void applyFormMode() {
        if (formMode.equals(FormMode.UPDATE)) {
            // Set contact type
            switch (contact.getContactType().getName()) {
                case "Prospect":
                    prospectRadioButton.setSelected(true);
                    break;
                case "Client":
                    clientRadioButton.setSelected(true);
                    break;
                case "Vendor":
                    vendorRadioButton.setSelected(true);
                    break;
            }
            // load text fields and combo boxes
            firstNameTextField.setText(contact.getFirstName());
            if (contact.getMidInit() != null && !contact.getMidInit().isEmpty()) {
                midInitialTextField.setText(contact.getMidInit());
            }
            lastNameTextField.setText(contact.getLastName());
            if (contact.getCourtesyTitle() != null && !contact.getCourtesyTitle().isEmpty()) {
                courtesyTitleTextField.setText(contact.getCourtesyTitle());
            }
            emailTextField.setText(contact.getEmail());
            phoneTextField.setText(contact.getPhone());
            phoneTypeComboBox.getSelectionModel().select(contact.getPhoneType());
            addrTextField.setText(contact.getAddress());
            if (contact.getAddress2() != null && !contact.getAddress2().isEmpty()) {
                addr2TextField.setText(contact.getAddress2());
            }
            cityTextField.setText(contact.getCity());
            stateComboBox.getSelectionModel().select(USState.getUSState(contact.getState()));
            zipcodeTextField.setText(contact.getZipcode());
            if (contact.getCompany() != null && !contact.getCompany().isEmpty()) {
                companyTextField.setText(contact.getCompany());
            }
            if (contact.getJobTitle() != null && !contact.getJobTitle().isEmpty()) {
                jobTitleTextField.setText(contact.getCompany());
            }
            
            addSaveButton.setText("Save");
        } else {
            addSaveButton.setText("Add");
        }
        
    }
    
    private void startEventHandlers() {
        startFirstNameFocusListener();
        startMidInitialFocusListener();
        startLastNameFocusListener();
        startCourtesyTitleFocusListener();
        startEmailFocusListener();
        startPhoneFocusListener();
        startPhoneTypeFocusListener();
        startAddrFocusListener();
        startAddr2FocusListener();
        startCityFocusListener();
        startStateFocusListener();
        startZipcodeFocusListener();
        startCompanyFocusListener();
        startJobTitleFocusListener();
    }
    
    private void insertContact() {
        buildContact();
        runInsertContactService();
    }
    
    private void updateContact() {
        buildContact();
        runUpdateContactService();
    }
    
    private void buildContact() {
        contact.setFirstName(firstNameTextField.getText());
        contact.setMidInit(midInitialTextField.getText());
        contact.setLastName(lastNameTextField.getText());
        contact.setCourtesyTitle(courtesyTitleTextField.getText());
        contact.setEmail(emailTextField.getText());
        contact.setPhone(phoneTextField.getText());
        contact.setPhoneType(phoneTypeComboBox.getValue());
        contact.setAddress(addrTextField.getText());
        contact.setAddress2(addr2TextField.getText());
        contact.setCity(cityTextField.getText());
        contact.setState(stateComboBox.getValue().getName());
        contact.setZipcode(zipcodeTextField.getText());
        contact.setCompany(companyTextField.getText());
        contact.setJobTitle(jobTitleTextField.getText());
        if (prospectRadioButton.isSelected()) {
            contact.setContactType(new ContactType(1, "Prospect"));
        } else if (clientRadioButton.isSelected()) {
            contact.setContactType(new ContactType(2, "Client"));
        } else {
            contact.setContactType(new ContactType(3, "Vendor"));
        }
    }
    
    // Service run handlers
    private void runGetAllContactsService() {
        if (!getAllContactsService.isRunning()) {
            getAllContactsService.reset();
            getAllContactsService.start();
        }
    }
    
    private void runInsertContactService() {
        if (!insertContactService.isRunning()) {
            insertContactService.reset();
            insertContactService.setContact(contact);
            insertContactService.start();
        }
    }
    
    private void runUpdateContactService() {
        if (!updateContactService.isRunning()) {
            updateContactService.reset();
            updateContactService.setContact(contact);
            updateContactService.start();
        }
    }
    
    // Service status event handlers
    private EventHandler<WorkerStateEvent> getAllContactsSuccess = (event) -> {
        allContactList = getAllContactsService.getValue();
    };
    
    private EventHandler<WorkerStateEvent> getAllContactsFailure = (event) -> {
        generalErrorLabel.setText("System error, please try your request again.");
        generalErrorLabel.getStyleClass().removeAll("system-message-label");
        generalErrorLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> insertContactSuccess = (event) -> {
        // TODO create form result and close
        this.formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Contact for " 
                + this.contact.getDisplayName()
                + "was successfully added.");
        System.out.println("Insert successful");
    };
    
    private EventHandler<WorkerStateEvent> insertContactFailure = (event) -> {
        System.out.println(insertContactService.getException());
        generalErrorLabel.setText("System error, please try your request again.");
        generalErrorLabel.getStyleClass().removeAll("system-message-label");
        generalErrorLabel.getStyleClass().add("system-message-label-error");
    };
    
    private EventHandler<WorkerStateEvent> updateContactSuccess = (event) -> {
        // TODO create form result and close
        this.formResult = new FormResult(FormResult.FormResultStatus.SUCCESS, "Contact for " 
                + this.contact.getDisplayName()
                + "was successfully changed.");
        System.out.println("Update successful");
    };
    
    private EventHandler<WorkerStateEvent> updateContactFailure = (event) -> {
        generalErrorLabel.setText("System error, please try your request again.");
        generalErrorLabel.getStyleClass().removeAll("system-message-label");
        generalErrorLabel.getStyleClass().add("system-message-label-error");
    };
    
    // Button event handlers
    @FXML
    private void handleCancelButton(ActionEvent event) {
        
    }
    
    @FXML
    private void handleAddSaveButton(ActionEvent event) {
        if (validateAll()) {
            if (formMode.equals(FormMode.INSERT)) {
                insertContact();
            } else {
                updateContact();
            }
        } else {
            generalErrorLabel.setText("Please check all fields are filled out correctly");
            generalErrorLabel.getStyleClass().removeAll("system-message-label");
            generalErrorLabel.getStyleClass().add("system-message-label-error");
        }
    }
    
    // Change listeners. Start focus listeners are created in a way that in the future they could be stopped if desired
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

    private void startPhoneFocusListener() {
        if (phoneFocusListener == null) {
            phoneFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validatePhone();
                }
            };
        }
        phoneTextField.focusedProperty().addListener(phoneFocusListener);
    }

    private void startPhoneTypeFocusListener() {
        if (phoneTypeFocusListener == null) {
            phoneTypeFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validatePhoneType();
                }
            };
        }
        phoneTypeComboBox.focusedProperty().addListener(phoneTypeFocusListener);
    }

    private void startAddrFocusListener() {
        if (addrFocusListener == null) {
            addrFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateAddr();
                }
            };
        }
        addrTextField.focusedProperty().addListener(addrFocusListener);
    }

    private void startAddr2FocusListener() {
        if (addr2FocusListener == null) {
            addr2FocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateAddr2();
                }
            };
        }
        addr2TextField.focusedProperty().addListener(addr2FocusListener);
    }

    private void startCityFocusListener() {
        if (cityFocusListener == null) {
            cityFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateCity();
                }
            };
        }
        cityTextField.focusedProperty().addListener(cityFocusListener);
    }

    private void startStateFocusListener() {
        if (stateFocusListener == null) {
            stateFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateState();
                }
            };
        }
        stateComboBox.focusedProperty().addListener(stateFocusListener);
    }

    private void startZipcodeFocusListener() {
        if (zipcodeFocusListener == null) {
            zipcodeFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateZipcode();
                }
            };
        }
        zipcodeTextField.focusedProperty().addListener(zipcodeFocusListener);
    }

    private void startCompanyFocusListener() {
        if (companyFocusListener == null) {
            companyFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateCompany();
                }
            };
        }
        companyTextField.focusedProperty().addListener(companyFocusListener);
    }

    private void startJobTitleFocusListener() {
        if (jobTitleFocusListener == null) {
            jobTitleFocusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
                   validateJobTitle();
                }
            };
        }
        jobTitleTextField.focusedProperty().addListener(jobTitleFocusListener);
    }
    
    // Validation methods
    
    private boolean validateAll() {
        return validateFirstName()
                &&validateMidInitial()
                &&validateLastName()
                &&validateCourtesyTitle()
                &&validateEmail()
                &&validatePhone()
                &&validatePhoneType()
                &&validateAddr()
                &&validateAddr2()
                &&validateCity()
                &&validateState()
                &&validateZipcode()
                &&validateCompany()
                &&validateJobTitle();
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

    private boolean validatePhone() {
        String input = phoneTextField.getText();
        if (input.isEmpty()) {
            phoneErrorLabel.setText("Phone is required");
            return false;
        }
        
        if (!input.matches(ValidationPattern.PHONE)) {
            phoneErrorLabel.setText("Phone must match: 55555555555, 555-555-5555, (555)555-5555,\n(555)5555555");
            return false;
        }
        
        phoneErrorLabel.setText("");
        return true;
    }

    private boolean validatePhoneType() {
        if (phoneTypeComboBox.getSelectionModel().isEmpty()) {
            phoneTypeErrorLabel.setText("Phone type is required");
            return false;
        } else {
            phoneTypeErrorLabel.setText("");
            return true;
        }
    }

    private boolean validateAddr() {
        String input = addrTextField.getText();
        if (input.isEmpty()) {
            addrErrorLabel.setText("Address is required");
            return false;
        }
        if (input.length() > 255) {
            addrErrorLabel.setText("Address is limited to 255 characters or less");
            return false;
        }
        
        addrErrorLabel.setText("");
        return true;
    }

    private boolean validateAddr2() {
        String input = addr2TextField.getText();
        if (input.length() > 255) {
            addr2ErrorLabel.setText("Address is limited to 255 characters or less");
            return false;
        } else {
            addr2ErrorLabel.setText("");
            return true;
        }
    }

    private boolean validateCity() {
        String input = cityTextField.getText();
        if (input.isEmpty()) {
            cityErrorLabel.setText("City is required");
            return false;
        }
        if (input.length() > 100) {
            cityErrorLabel.setText("City is limited to 100 characters or less");
            return false;
        }
        
        cityErrorLabel.setText("");
        return true;
    }

    private boolean validateState() {
        if (stateComboBox.getSelectionModel().isEmpty()) {
            stateErrorLabel.setText("State is required");
            return false;
        } else {
            stateErrorLabel.setText("");
            return true;
        }
    }

    private boolean validateZipcode() {
        String input = zipcodeTextField.getText();
        if (input.isEmpty()) {
            zipcodeErrorLabel.setText("Zipcode is required");
            return false;
        }
        
        if (!input.matches(ValidationPattern.ZIPCODE)) {
            zipcodeErrorLabel.setText("Zipcode pattern: 12345 or 12345-1234");
            return false;
        }
        
        zipcodeErrorLabel.setText("");
        return true;
    }

    private boolean validateCompany() {
        String input = companyTextField.getText();
        if (input.length() > 255) {
            companyErrorLabel.setText("Company is limited to 255 characters or less");
            return false;
        } else {
            companyErrorLabel.setText("");
            return true;
        }
    }

    private boolean validateJobTitle() {
        String input = jobTitleTextField.getText();
        if (input.length() > 255) {
            jobTitleErrorLabel.setText("Job title is limited to 255 characters or less");
            return false;
        } else {
            jobTitleErrorLabel.setText("");
            return true;
        }
    }
    
    // Getters & setters

    public void setFormModeInsert() {
        this.formMode = FormMode.INSERT;
    }
    
    public void setFormModeUpdate(Contact contact) {
        this.formMode = FormMode.UPDATE;
        this.contact = contact;
    }

    public FormResult getFormResult() {
        return formResult;
    }
}
