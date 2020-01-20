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
import sdtracker.DAO.MfgDaoImpl;
import sdtracker.model.Mfg;

/**
 *
 * @author Tim Smith
 */
public class MfgDbServiceManager {
    private MfgDaoImpl mfgDaoImpl;
    private ExecutorService executor;
    
    private static volatile MfgDbServiceManager INSTANCE;
    
    private MfgDbServiceManager() {
        mfgDaoImpl = MfgDaoImpl.getMfgDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static MfgDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (MfgDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MfgDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertMfgService extends Service<Void> {
        private Mfg mfg;
        
        public void setMfg(Mfg mfg) {
            this.mfg = mfg;
        }
        
        public InsertMfgService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    mfgDaoImpl.insert(mfg);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id
    public class GetAllMfgsService extends Service<ObservableList<Mfg>> {
        public  GetAllMfgsService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<Mfg>> createTask() {
            return new Task<ObservableList<Mfg>>() {
                @Override
                protected ObservableList<Mfg> call() throws Exception {
                    return mfgDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetMfgByIdService extends Service<Mfg> {
        private int id;
        
        public GetMfgByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<Mfg> createTask() {
            return new Task<Mfg>() {
                @Override
                protected Mfg call() throws Exception {
                    return mfgDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateMfgService extends Service<Void> {
        private Mfg mfg;
        
        public void setMfg(Mfg mfg) {
            this.mfg = mfg;
        }
        
        public UpdateMfgService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    mfgDaoImpl.update(mfg);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteMfgService extends Service<Void> {
        private Mfg mfg;
        
        public void setMfg(Mfg mfg) {
            this.mfg = mfg;
        }
        
        public DeleteMfgService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    mfgDaoImpl.delete(mfg);
                    return null;
                }
                
            };
        }
        
    }
}
