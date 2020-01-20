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
import sdtracker.DAO.TicketPriorityDaoImpl;
import sdtracker.model.TicketPriority;

/**
 *
 * @author Tim Smith
 */
public class TicketPriorityDbServiceManager {
    private TicketPriorityDaoImpl ticketPriorityDaoImpl;
    private ExecutorService executor;
    
    private static volatile TicketPriorityDbServiceManager INSTANCE;
    
    private TicketPriorityDbServiceManager() {
        ticketPriorityDaoImpl = TicketPriorityDaoImpl.getTicketPriorityDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static TicketPriorityDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (TicketPriorityDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TicketPriorityDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertTicketPriorityService extends Service<Void> {
        private TicketPriority ticketPriority;
        
        public void setTicketPriority(TicketPriority ticketPriority) {
            this.ticketPriority = ticketPriority;
        }
        
        public InsertTicketPriorityService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ticketPriorityDaoImpl.insert(ticketPriority);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id
    public class GetAllTicketPrioritysService extends Service<ObservableList<TicketPriority>> {
        public  GetAllTicketPrioritysService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<TicketPriority>> createTask() {
            return new Task<ObservableList<TicketPriority>>() {
                @Override
                protected ObservableList<TicketPriority> call() throws Exception {
                    return ticketPriorityDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetTicketPriorityByIdService extends Service<TicketPriority> {
        private int id;
        
        public GetTicketPriorityByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<TicketPriority> createTask() {
            return new Task<TicketPriority>() {
                @Override
                protected TicketPriority call() throws Exception {
                    return ticketPriorityDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateTicketPriorityService extends Service<Void> {
        private TicketPriority ticketPriority;
        
        public void setTicketPriority(TicketPriority ticketPriority) {
            this.ticketPriority = ticketPriority;
        }
        
        public UpdateTicketPriorityService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ticketPriorityDaoImpl.update(ticketPriority);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteTicketPriorityService extends Service<Void> {
        private TicketPriority ticketPriority;
        
        public void setTicketPriority(TicketPriority ticketPriority) {
            this.ticketPriority = ticketPriority;
        }
        
        public DeleteTicketPriorityService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ticketPriorityDaoImpl.delete(ticketPriority);
                    return null;
                }
                
            };
        }
        
    }
}
