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
import sdtracker.DAO.DepartmentDaoImpl;
import sdtracker.model.Department;

/**
 *
 * @author Tim Smith
 */
public class DepartmentDbServiceManager {
    private DepartmentDaoImpl departmentDaoImpl;
    private ExecutorService executor;
    
    private static volatile DepartmentDbServiceManager INSTANCE;
    
    private DepartmentDbServiceManager() {
        departmentDaoImpl = DepartmentDaoImpl.getDepartmentDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static DepartmentDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (DepartmentDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DepartmentDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertDepartmentService extends Service<Void> {
        private Department department;
        
        public void setDepartment(Department department) {
            this.department = department;
        }
        
        public InsertDepartmentService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    departmentDaoImpl.insert(department);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id
    public class GetAllDepartmentsService extends Service<ObservableList<Department>> {
        public  GetAllDepartmentsService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<Department>> createTask() {
            return new Task<ObservableList<Department>>() {
                @Override
                protected ObservableList<Department> call() throws Exception {
                    return departmentDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetDepartmentByIdService extends Service<Department> {
        private int id;
        
        public GetDepartmentByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<Department> createTask() {
            return new Task<Department>() {
                @Override
                protected Department call() throws Exception {
                    return departmentDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateDepartmentService extends Service<Void> {
        private Department department;
        
        public void setDepartment(Department department) {
            this.department = department;
        }
        
        public UpdateDepartmentService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    departmentDaoImpl.update(department);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteDepartmentService extends Service<Void> {
        private Department department;
        
        public void setDepartment(Department department) {
            this.department = department;
        }
        
        public DeleteDepartmentService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    departmentDaoImpl.delete(department);
                    return null;
                }
                
            };
        }
        
    }
}
