/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import sdtracker.DAO.ContactDaoImpl;
import sdtracker.model.Contact;

/**
 *
 * @author Tim Smith
 */
public class ContactDbServiceManager {
    private ContactDaoImpl contactDaoImpl;
    private ExecutorService executor;
    
    private static volatile ContactDbServiceManager INSTANCE;
    
    private ContactDbServiceManager() {
        contactDaoImpl = ContactDaoImpl.getContactDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static ContactDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (ContactDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ContactDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertContactService extends Service<Void> {
        private Contact contact;
        
        public void setContact(Contact contact) {
            this.contact = contact;
        }
        
        public InsertContactService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    contactDaoImpl.insert(contact);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id
    public class GetAllContactsService extends Service<ObservableList<Contact>> {
        public  GetAllContactsService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<Contact>> createTask() {
            return new Task<ObservableList<Contact>>() {
                @Override
                protected ObservableList<Contact> call() throws Exception {
                    return contactDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetContactByIdService extends Service<Contact> {
        private int id;
        
        public GetContactByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<Contact> createTask() {
            return new Task<Contact>() {
                @Override
                protected Contact call() throws Exception {
                    return contactDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateContactService extends Service<Void> {
        private Contact contact;
        
        public void setContact(Contact contact) {
            this.contact = contact;
        }
        
        public UpdateContactService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    contactDaoImpl.update(contact);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteContactService extends Service<Void> {
        private Contact contact;
        
        public void setContact(Contact contact) {
            this.contact = contact;
        }
        
        public DeleteContactService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    contactDaoImpl.delete(contact);
                    return null;
                }
                
            };
        }
        
    }
}
