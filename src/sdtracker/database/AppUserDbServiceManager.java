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
import sdtracker.DAO.AppUserDaoImpl;
import sdtracker.model.AppUser;

/**
 *
 * @author Tim Smith
 */
public class AppUserDbServiceManager {
    private AppUserDaoImpl appUserDaoImpl;
    private ExecutorService executor;
    
    private static volatile AppUserDbServiceManager INSTANCE;
    
    private AppUserDbServiceManager() {
        appUserDaoImpl = AppUserDaoImpl.getAppUserDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static AppUserDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (AppUserDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppUserDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertAppUserService extends Service<Void> {
        private AppUser appUser;
        
        public void setAppUser(AppUser appUser) {
            this.appUser = appUser;
        }
        
        public InsertAppUserService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    appUserDaoImpl.insert(appUser);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id, get by username
    public class GetAllAppUsersService extends Service<ObservableList<AppUser>> {
        public  GetAllAppUsersService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<AppUser>> createTask() {
            return new Task<ObservableList<AppUser>>() {
                @Override
                protected ObservableList<AppUser> call() throws Exception {
                    return appUserDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetAppUserByIdService extends Service<AppUser> {
        private int id;
        
        public GetAppUserByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<AppUser> createTask() {
            return new Task<AppUser>() {
                @Override
                protected AppUser call() throws Exception {
                    return appUserDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    public class GetAppUserByUsernameService extends Service<AppUser> {
        private String username;
        
        public GetAppUserByUsernameService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(String username) {
            this.username = username;
        }
        
        @Override
        protected Task<AppUser> createTask() {
            return new Task<AppUser>() {
                @Override
                protected AppUser call() throws Exception {
                    return appUserDaoImpl.getByUsername(username);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateAppUserService extends Service<Void> {
        private AppUser appUser;
        
        public void setAppUser(AppUser appUser) {
            this.appUser = appUser;
        }
        
        public UpdateAppUserService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    appUserDaoImpl.update(appUser);
                    return null;
                }
                
            };
        }
        
    }
    
    public class UpdatePasswordAppUserService extends Service<Void> {
        private AppUser appUser;
        
        public void setAppUser(AppUser appUser) {
            this.appUser = appUser;
        }
        
        public UpdatePasswordAppUserService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    appUserDaoImpl.updatePassword(appUser);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteAppUserService extends Service<Void> {
        private AppUser appUser;
        
        public void setAppUser(AppUser appUser) {
            this.appUser = appUser;
        }
        
        public DeleteAppUserService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    appUserDaoImpl.delete(appUser);
                    return null;
                }
                
            };
        }
        
    }
}
