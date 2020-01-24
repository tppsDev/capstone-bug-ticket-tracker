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
import sdtracker.DAO.AssetTypeDaoImpl;
import sdtracker.model.AssetType;


/**
 *
 * @author Tim Smith
 */
public class AssetTypeDbServiceManager {
    private AssetTypeDaoImpl assetTypeDaoImpl;
    private ExecutorService executor;
    
    private static volatile AssetTypeDbServiceManager INSTANCE;
    
    private AssetTypeDbServiceManager() {
        assetTypeDaoImpl = AssetTypeDaoImpl.getAssetTypeDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static AssetTypeDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (AssetTypeDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AssetTypeDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertAssetTypeService extends Service<Void> {
        private AssetType assetType;
        
        public void setAssetType(AssetType assetType) {
            this.assetType = assetType;
        }
        
        public InsertAssetTypeService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    assetTypeDaoImpl.insert(assetType);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id, check for duplicate
    public class GetAllAssetTypesService extends Service<ObservableList<AssetType>> {
        public  GetAllAssetTypesService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<AssetType>> createTask() {
            return new Task<ObservableList<AssetType>>() {
                @Override
                protected ObservableList<AssetType> call() throws Exception {
                    return assetTypeDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetAssetTypeByIdService extends Service<AssetType> {
        private int id;
        
        public GetAssetTypeByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<AssetType> createTask() {
            return new Task<AssetType>() {
                @Override
                protected AssetType call() throws Exception {
                    return assetTypeDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    public class CheckForDuplicateAssetTypeService extends Service<Boolean> {
        private String assetTypeName;
        
        public CheckForDuplicateAssetTypeService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setAssetTypeName(String assetTypeName) {
            this.assetTypeName = assetTypeName;
        }

        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return assetTypeDaoImpl.checkForDuplicate(assetTypeName);
                }
                
            };
        }
    }
    
    // Update service
    public class UpdateAssetTypeService extends Service<Void> {
        private AssetType assetType;
        
        public void setAssetType(AssetType assetType) {
            this.assetType = assetType;
        }
        
        public UpdateAssetTypeService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    assetTypeDaoImpl.update(assetType);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteAssetTypeService extends Service<Void> {
        private AssetType assetType;
        
        public void setAssetType(AssetType assetType) {
            this.assetType = assetType;
        }
        
        public DeleteAssetTypeService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    assetTypeDaoImpl.delete(assetType);
                    return null;
                }
                
            };
        }
        
    }
}
