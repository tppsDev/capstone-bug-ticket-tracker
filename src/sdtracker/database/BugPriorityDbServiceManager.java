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
import sdtracker.DAO.BugPriorityDaoImpl;
import sdtracker.model.BugPriority;

/**
 *
 * @author Tim Smith
 */
public class BugPriorityDbServiceManager {
    private BugPriorityDaoImpl bugPriorityDaoImpl;
    private ExecutorService executor;
    
    private static volatile BugPriorityDbServiceManager INSTANCE;
    
    private BugPriorityDbServiceManager() {
        bugPriorityDaoImpl = BugPriorityDaoImpl.getBugPriorityDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static BugPriorityDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (BugPriorityDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BugPriorityDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertBugPriorityService extends Service<Void> {
        private BugPriority bugPriority;
        
        public void setBugPriority(BugPriority bugPriority) {
            this.bugPriority = bugPriority;
        }
        
        public InsertBugPriorityService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    bugPriorityDaoImpl.insert(bugPriority);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id, check for duplicates
    public class GetAllBugPrioritiesService extends Service<ObservableList<BugPriority>> {
        public  GetAllBugPrioritiesService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<BugPriority>> createTask() {
            return new Task<ObservableList<BugPriority>>() {
                @Override
                protected ObservableList<BugPriority> call() throws Exception {
                    return bugPriorityDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetBugPriorityByIdService extends Service<BugPriority> {
        private int id;
        
        public GetBugPriorityByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<BugPriority> createTask() {
            return new Task<BugPriority>() {
                @Override
                protected BugPriority call() throws Exception {
                    return bugPriorityDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    public class CheckForDuplicateBugPriorityService extends Service<Boolean> {
        private String name;
        
        public CheckForDuplicateBugPriorityService() {
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
                    return bugPriorityDaoImpl.checkForDuplicate(name);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateBugPriorityService extends Service<Void> {
        private BugPriority bugPriority;
        
        public void setBugPriority(BugPriority bugPriority) {
            this.bugPriority = bugPriority;
        }
        
        public UpdateBugPriorityService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    bugPriorityDaoImpl.update(bugPriority);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteBugPriorityService extends Service<Void> {
        private BugPriority bugPriority;
        
        public void setBugPriority(BugPriority bugPriority) {
            this.bugPriority = bugPriority;
        }
        
        public DeleteBugPriorityService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    bugPriorityDaoImpl.delete(bugPriority);
                    return null;
                }
                
            };
        }
        
    }
}
