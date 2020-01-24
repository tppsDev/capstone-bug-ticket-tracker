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
import sdtracker.DAO.ContactTypeDaoImpl;
import sdtracker.model.ContactType;

/**
 *
 * @author Tim Smith
 */
public class ContactTypeDbServiceManager {
    private ContactTypeDaoImpl contactTypeDaoImpl;
    private ExecutorService executor;
    
    private static volatile ContactTypeDbServiceManager INSTANCE;
    
    private ContactTypeDbServiceManager() {
        contactTypeDaoImpl = ContactTypeDaoImpl.getContactTypeDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static ContactTypeDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (ContactTypeDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ContactTypeDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertContactTypeService extends Service<Void> {
        private ContactType contactType;
        
        public void setContactType(ContactType contactType) {
            this.contactType = contactType;
        }
        
        public InsertContactTypeService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    contactTypeDaoImpl.insert(contactType);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id, check for duplicate
    public class GetAllContactTypesService extends Service<ObservableList<ContactType>> {
        public  GetAllContactTypesService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<ContactType>> createTask() {
            return new Task<ObservableList<ContactType>>() {
                @Override
                protected ObservableList<ContactType> call() throws Exception {
                    return contactTypeDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetContactTypeByIdService extends Service<ContactType> {
        private int id;
        
        public GetContactTypeByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<ContactType> createTask() {
            return new Task<ContactType>() {
                @Override
                protected ContactType call() throws Exception {
                    return contactTypeDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    public class CheckForDuplicateContactTypeService extends Service<Boolean> {
        private String name;
        
        public CheckForDuplicateContactTypeService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setName(String name) {
            this.name = name;
        }

        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return contactTypeDaoImpl.checkForDuplicate(name);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateContactTypeService extends Service<Void> {
        private ContactType contactType;
        
        public void setContactType(ContactType contactType) {
            this.contactType = contactType;
        }
        
        public UpdateContactTypeService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    contactTypeDaoImpl.update(contactType);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteContactTypeService extends Service<Void> {
        private ContactType contactType;
        
        public void setContactType(ContactType contactType) {
            this.contactType = contactType;
        }
        
        public DeleteContactTypeService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    contactTypeDaoImpl.delete(contactType);
                    return null;
                }
                
            };
        }
        
    }
}
