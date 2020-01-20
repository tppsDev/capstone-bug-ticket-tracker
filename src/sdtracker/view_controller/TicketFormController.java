/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.view_controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import sdtracker.database.ContactDbServiceManager;
import sdtracker.database.ContactDbServiceManager.GetAllContactsService;
import sdtracker.model.Contact;

/**
 * FXML Controller class
 *
 * @author Tim Smith
 */
public class TicketFormController implements Initializable {
    
    @FXML private Label titleLabel;
    @FXML private ComboBox<Contact> contactComboBox;
    
    private ContactDbServiceManager contactDbServiceManager = ContactDbServiceManager.getServiceManager();
    private GetAllContactsService getAllContactsService;
    
    ObservableList<Contact> allContactList = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        getAllContactsService = contactDbServiceManager.new GetAllContactsService();
        runGetAllContactsService();
        contactComboBox.setItems(allContactList);
    }
    
    private void runGetAllContactsService() {
        if (!getAllContactsService.isRunning()) {
            getAllContactsService.reset();
            getAllContactsService.start();
        }
    }
    
    private EventHandler<WorkerStateEvent> getAllContactsSuccess = (event) -> {
        allContactList = getAllContactsService.getValue();
    };
    
    private EventHandler<WorkerStateEvent> getAllContactsFailure = (event) -> {
        // TODO
    };
    
}
