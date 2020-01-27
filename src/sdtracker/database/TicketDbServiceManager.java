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
import sdtracker.DAO.TicketDaoImpl;
import sdtracker.model.Ticket;

/**
 *
 * @author Tim Smith
 */
public class TicketDbServiceManager {
    private TicketDaoImpl ticketDaoImpl;
    private ExecutorService executor;
    
    private static volatile TicketDbServiceManager INSTANCE;
    
    private TicketDbServiceManager() {
        ticketDaoImpl = TicketDaoImpl.getTicketDaoImpl();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static TicketDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (TicketDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TicketDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    // CRUD Services
    
    // Create service
    public class InsertTicketService extends Service<Void> {
        private Ticket ticket;
        
        public void setTicket(Ticket ticket) {
            this.ticket = ticket;
        }
        
        public InsertTicketService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ticketDaoImpl.insert(ticket);
                    return null;
                }
                
            };
        }
        
    }
    
    // Read services - get all, get by id, check for duplicate
    public class GetAllTicketsService extends Service<ObservableList<Ticket>> {
        public  GetAllTicketsService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<ObservableList<Ticket>> createTask() {
            return new Task<ObservableList<Ticket>>() {
                @Override
                protected ObservableList<Ticket> call() throws Exception {
                    return ticketDaoImpl.getAll();
                }
                
            };
        }
        
    }
    
    public class GetTicketByIdService extends Service<Ticket> {
        private int id;
        
        public GetTicketByIdService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        protected Task<Ticket> createTask() {
            return new Task<Ticket>() {
                @Override
                protected Ticket call() throws Exception {
                    return ticketDaoImpl.getById(id);
                }
                
            };
        }
        
    }
    
    public class CheckForDuplicateTicketService extends Service<Boolean> {
        private String ticketNumber;
        
        public CheckForDuplicateTicketService() {
            super();
            this.setExecutor(executor);
        }
        
        public void setTicketNumber(String ticketNumber) {
            this.ticketNumber = ticketNumber;
        }

        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return ticketDaoImpl.checkForDuplicate(ticketNumber);
                }
                
            };
        }
        
    }
    
    // Update service
    public class UpdateTicketService extends Service<Void> {
        private Ticket ticket;
        
        public void setTicket(Ticket ticket) {
            this.ticket = ticket;
        }
        
        public UpdateTicketService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ticketDaoImpl.update(ticket);
                    return null;
                }
                
            };
        }
        
    }
    
    // Delete service
    public class DeleteTicketService extends Service<Void> {
        private Ticket ticket;
        
        public void setTicket(Ticket ticket) {
            this.ticket = ticket;
        }
        
        public DeleteTicketService() {
            super();
            this.setExecutor(executor);
        }
        
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ticketDaoImpl.delete(ticket);
                    return null;
                }
                
            };
        }
        
    }
}
