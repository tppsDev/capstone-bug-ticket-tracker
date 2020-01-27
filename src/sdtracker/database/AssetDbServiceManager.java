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
import sdtracker.DAO.AssetDaoImpl;
import sdtracker.model.Asset;

/**
 *
 * @author Tim Smith
 */
public class AssetDbServiceManager {
    private AssetDaoImpl assetDaoImpl;
    private ExecutorService executor;
    
    private static volatile AssetDbServiceManager INSTANCE;
    
    private AssetDbServiceManager() {
        assetDaoImpl = AssetDaoImpl.getAssetDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static AssetDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (AssetDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AssetDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertAssetService extends Service<Void> {
        private Asset asset;
        
        public void setAsset(Asset asset) {
            this.asset = asset;
        }
        
        public InsertAssetService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    assetDaoImpl.insert(asset);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id, check for duplicates
    public class GetAllAssetsService extends Service<ObservableList<Asset>> {
        public  GetAllAssetsService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<Asset>> createTask() {
            return new Task<ObservableList<Asset>>() {
                @Override
                protected ObservableList<Asset> call() throws Exception {
                    return assetDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetAssetByIdService extends Service<Asset> {
        private int id;
        
        public GetAssetByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<Asset> createTask() {
            return new Task<Asset>() {
                @Override
                protected Asset call() throws Exception {
                    return assetDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    public class CheckForDuplicateAssetService extends Service<Boolean> {
        private String assetNumber;
        
        public CheckForDuplicateAssetService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setAssetNumber(String assetNumber) {
            this.assetNumber = assetNumber;
        }

        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return assetDaoImpl.checkForDuplicate(assetNumber);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateAssetService extends Service<Void> {
        private Asset asset;
        
        public void setAsset(Asset asset) {
            this.asset = asset;
        }
        
        public UpdateAssetService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    assetDaoImpl.update(asset);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteAssetService extends Service<Void> {
        private Asset asset;
        
        public void setAsset(Asset asset) {
            this.asset = asset;
        }
        
        public DeleteAssetService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    assetDaoImpl.delete(asset);
                    return null;
                }
                
            };
        }
        
    }
}
