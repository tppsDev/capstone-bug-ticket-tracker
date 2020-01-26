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
import sdtracker.DAO.BugStatusDaoImpl;
import sdtracker.model.BugStatus;

/**
 *
 * @author Tim Smith
 */
public class BugStatusDbServiceManager {
    private BugStatusDaoImpl bugStatusDaoImpl;
    private ExecutorService executor;
    
    private static volatile BugStatusDbServiceManager INSTANCE;
    
    private BugStatusDbServiceManager() {
        bugStatusDaoImpl = BugStatusDaoImpl.getBugStatusDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static BugStatusDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (BugStatusDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BugStatusDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertBugStatusService extends Service<Void> {
        private BugStatus bugStatus;
        
        public void setBugStatus(BugStatus bugStatus) {
            this.bugStatus = bugStatus;
        }
        
        public InsertBugStatusService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    bugStatusDaoImpl.insert(bugStatus);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id, check for duplicate
    public class GetAllBugStatusesService extends Service<ObservableList<BugStatus>> {
        public  GetAllBugStatusesService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<BugStatus>> createTask() {
            return new Task<ObservableList<BugStatus>>() {
                @Override
                protected ObservableList<BugStatus> call() throws Exception {
                    return bugStatusDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetBugStatusByIdService extends Service<BugStatus> {
        private int id;
        
        public GetBugStatusByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<BugStatus> createTask() {
            return new Task<BugStatus>() {
                @Override
                protected BugStatus call() throws Exception {
                    return bugStatusDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    public class CheckForDuplicateBugStatusService extends Service<Boolean> {
        private String name;
        
        public CheckForDuplicateBugStatusService() {
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
                    return bugStatusDaoImpl.checkForDuplicate(name);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateBugStatusService extends Service<Void> {
        private BugStatus bugStatus;
        
        public void setBugStatus(BugStatus bugStatus) {
            this.bugStatus = bugStatus;
        }
        
        public UpdateBugStatusService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    bugStatusDaoImpl.update(bugStatus);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteBugStatusService extends Service<Void> {
        private BugStatus bugStatus;
        
        public void setBugStatus(BugStatus bugStatus) {
            this.bugStatus = bugStatus;
        }
        
        public DeleteBugStatusService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    bugStatusDaoImpl.delete(bugStatus);
                    return null;
                }
                
            };
        }
        
    }
}
