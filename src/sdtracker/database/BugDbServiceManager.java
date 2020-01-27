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
import sdtracker.DAO.BugDaoImpl;
import sdtracker.model.Bug;

/**
 *
 * @author Tim Smith
 */
public class BugDbServiceManager {
    private BugDaoImpl bugDaoImpl;
    private ExecutorService executor;
    
    private static volatile BugDbServiceManager INSTANCE;
    
    private BugDbServiceManager() {
        bugDaoImpl = BugDaoImpl.getBugDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static BugDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (BugDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BugDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertBugService extends Service<Void> {
        private Bug bug;
        
        public void setBug(Bug bug) {
            this.bug = bug;
        }
        
        public InsertBugService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    bugDaoImpl.insert(bug);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id, check for duplicate
    public class GetAllBugsService extends Service<ObservableList<Bug>> {
        public  GetAllBugsService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<Bug>> createTask() {
            return new Task<ObservableList<Bug>>() {
                @Override
                protected ObservableList<Bug> call() throws Exception {
                    return bugDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetBugByIdService extends Service<Bug> {
        private int id;
        
        public GetBugByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<Bug> createTask() {
            return new Task<Bug>() {
                @Override
                protected Bug call() throws Exception {
                    return bugDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    public class CheckForDuplicateBugService extends Service<Boolean> {
        private String bugNumber;
        
        public CheckForDuplicateBugService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setBugNumber(String bugNumber) {
            this.bugNumber = bugNumber;
        }

        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return bugDaoImpl.checkForDuplicate(bugNumber);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateBugService extends Service<Void> {
        private Bug bug;
        
        public void setBug(Bug bug) {
            this.bug = bug;
        }
        
        public UpdateBugService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    bugDaoImpl.update(bug);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteBugService extends Service<Void> {
        private Bug bug;
        
        public void setBug(Bug bug) {
            this.bug = bug;
        }
        
        public DeleteBugService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    bugDaoImpl.delete(bug);
                    return null;
                }
                
            };
        }
        
    }
}
