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
import sdtracker.DAO.SecurityRoleDaoImpl;
import sdtracker.model.SecurityRole;

/**
 *
 * @author Tim Smith
 */
public class SecurityRoleDbServiceManager {
    private SecurityRoleDaoImpl securityRoleDaoImpl;
    private ExecutorService executor;
    
    private static volatile SecurityRoleDbServiceManager INSTANCE;
    
    private SecurityRoleDbServiceManager() {
        securityRoleDaoImpl = SecurityRoleDaoImpl.getSecurityRoleDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static SecurityRoleDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (SecurityRoleDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SecurityRoleDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertSecurityRoleService extends Service<Void> {
        private SecurityRole securityRole;
        
        public void setSecurityRole(SecurityRole securityRole) {
            this.securityRole = securityRole;
        }
        
        public InsertSecurityRoleService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    securityRoleDaoImpl.insert(securityRole);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id
    public class GetAllSecurityRolesService extends Service<ObservableList<SecurityRole>> {
        public  GetAllSecurityRolesService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<SecurityRole>> createTask() {
            return new Task<ObservableList<SecurityRole>>() {
                @Override
                protected ObservableList<SecurityRole> call() throws Exception {
                    return securityRoleDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetSecurityRoleByIdService extends Service<SecurityRole> {
        private int id;
        
        public GetSecurityRoleByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<SecurityRole> createTask() {
            return new Task<SecurityRole>() {
                @Override
                protected SecurityRole call() throws Exception {
                    return securityRoleDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateSecurityRoleService extends Service<Void> {
        private SecurityRole securityRole;
        
        public void setSecurityRole(SecurityRole securityRole) {
            this.securityRole = securityRole;
        }
        
        public UpdateSecurityRoleService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    securityRoleDaoImpl.update(securityRole);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteSecurityRoleService extends Service<Void> {
        private SecurityRole securityRole;
        
        public void setSecurityRole(SecurityRole securityRole) {
            this.securityRole = securityRole;
        }
        
        public DeleteSecurityRoleService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    securityRoleDaoImpl.delete(securityRole);
                    return null;
                }
                
            };
        }
        
    }
}
