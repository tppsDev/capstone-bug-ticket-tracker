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
import sdtracker.DAO.TicketStatusDaoImpl;
import sdtracker.model.TicketStatus;

/**
 *
 * @author Tim Smith
 */
public class TicketStatusDbServiceManager {
    private TicketStatusDaoImpl ticketStatusDaoImpl;
    private ExecutorService executor;
    
    private static volatile TicketStatusDbServiceManager INSTANCE;
    
    private TicketStatusDbServiceManager() {
        ticketStatusDaoImpl = TicketStatusDaoImpl.getTicketStatusDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static TicketStatusDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (TicketStatusDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TicketStatusDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertTicketStatusService extends Service<Void> {
        private TicketStatus ticketStatus;
        
        public void setTicketStatus(TicketStatus ticketStatus) {
            this.ticketStatus = ticketStatus;
        }
        
        public InsertTicketStatusService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ticketStatusDaoImpl.insert(ticketStatus);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id
    public class GetAllTicketStatussService extends Service<ObservableList<TicketStatus>> {
        public  GetAllTicketStatussService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<TicketStatus>> createTask() {
            return new Task<ObservableList<TicketStatus>>() {
                @Override
                protected ObservableList<TicketStatus> call() throws Exception {
                    return ticketStatusDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetTicketStatusByIdService extends Service<TicketStatus> {
        private int id;
        
        public GetTicketStatusByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<TicketStatus> createTask() {
            return new Task<TicketStatus>() {
                @Override
                protected TicketStatus call() throws Exception {
                    return ticketStatusDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateTicketStatusService extends Service<Void> {
        private TicketStatus ticketStatus;
        
        public void setTicketStatus(TicketStatus ticketStatus) {
            this.ticketStatus = ticketStatus;
        }
        
        public UpdateTicketStatusService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ticketStatusDaoImpl.update(ticketStatus);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteTicketStatusService extends Service<Void> {
        private TicketStatus ticketStatus;
        
        public void setTicketStatus(TicketStatus ticketStatus) {
            this.ticketStatus = ticketStatus;
        }
        
        public DeleteTicketStatusService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ticketStatusDaoImpl.delete(ticketStatus);
                    return null;
                }
                
            };
        }
        
    }
}
