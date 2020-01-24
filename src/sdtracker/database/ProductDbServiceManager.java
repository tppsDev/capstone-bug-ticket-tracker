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
import sdtracker.DAO.ProductDaoImpl;
import sdtracker.model.Product;

/**
 *
 * @author Tim Smith
 */
public class ProductDbServiceManager {
    private ProductDaoImpl productDaoImpl;
    private ExecutorService executor;
    
    private static volatile ProductDbServiceManager INSTANCE;
    
    private ProductDbServiceManager() {
        productDaoImpl = ProductDaoImpl.getProductDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static ProductDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (ProductDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProductDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertProductService extends Service<Void> {
        private Product product;
        
        public void setProduct(Product product) {
            this.product = product;
        }
        
        public InsertProductService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    productDaoImpl.insert(product);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id, check for duplicate
    public class GetAllProductsService extends Service<ObservableList<Product>> {
        public  GetAllProductsService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<Product>> createTask() {
            return new Task<ObservableList<Product>>() {
                @Override
                protected ObservableList<Product> call() throws Exception {
                    return productDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetProductByIdService extends Service<Product> {
        private int id;
        
        public GetProductByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<Product> createTask() {
            return new Task<Product>() {
                @Override
                protected Product call() throws Exception {
                    return productDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    public class CheckForDuplicateProductService extends Service<Boolean> {
        private String name;
        private String version;
        
        public CheckForDuplicateProductService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public void setVersion(String version) {
            this.version = version;
        }

        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return productDaoImpl.checkForDuplicate(name, version);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateProductService extends Service<Void> {
        private Product product;
        
        public void setProduct(Product product) {
            this.product = product;
        }
        
        public UpdateProductService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    productDaoImpl.update(product);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteProductService extends Service<Void> {
        private Product product;
        
        public void setProduct(Product product) {
            this.product = product;
        }
        
        public DeleteProductService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    productDaoImpl.delete(product);
                    return null;
                }
                
            };
        }
        
    }
}
