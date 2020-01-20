/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sdtracker.database.DatabaseMgr;
import sdtracker.model.TicketPriority;

/**
 *
 * @author Tim Smith
 */
public class TicketPriorityDaoImpl implements CrudDao<TicketPriority> {
    private static volatile TicketPriorityDaoImpl ticketPriorityDaoImpl;
    
    //Constructor
    private TicketPriorityDaoImpl() {
        // Prohibit multiple instances via reflection
        if (ticketPriorityDaoImpl != null) {
            throw new RuntimeException("TicketPriorityDAOImpl already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of TicketPriorityDaoImpl class, creating instance if not already created
     * @return
     */
    public static TicketPriorityDaoImpl getTicketPriorityDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (ticketPriorityDaoImpl == null) { 
            synchronized (TicketPriorityDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (ticketPriorityDaoImpl == null) {
                    ticketPriorityDaoImpl = new TicketPriorityDaoImpl();
                }
            }
        }
        return ticketPriorityDaoImpl;
    }
    
    /**
     * Method returns an ObservableList of all records in ticket_priority table in database
     * @return
     * @throws DaoException
     */
    @Override
    public ObservableList<TicketPriority> getAll() throws DaoException {
        ObservableList<TicketPriority> ticketPriorityList = FXCollections.observableArrayList();
        
        String query = "SELECT tPriority.id, tPriority.name FROM ticket_priority AS tPriority";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                int id = result.getInt("tPriority.id");
                String name = result.getString("tPriority.name");
                
                ticketPriorityList.add(new TicketPriority(id, name));
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return ticketPriorityList;
    }

    @Override
    public TicketPriority getById(int id) throws DaoException, Exception {
        TicketPriority ticketPriority;
        
        String query = "SELECT tPriority.id, tPriority.name "
                        +"FROM ticket_priority AS tPriority "
                        +"WHERE tPriority.id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int resultId = result.getInt("tPriority.id");
                String name = result.getString("tPriority.name");
                ticketPriority = new TicketPriority(resultId, name);
                result.close();
            } else {
                result.close();
                ticketPriority = new TicketPriority(-1, null);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return ticketPriority;
    }

    @Override
    public void insert(TicketPriority ticketPriority) throws DaoException, Exception {
        String query = "INSERT INTO ticket_priority (name) "
                      +"VALUES (?)";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, ticketPriority.getName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(TicketPriority ticketPriority) throws DaoException, Exception {
        String query = "UPDATE ticket_priority "
                      +"SET name = ? "
                      +"WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, ticketPriority.getName());
            stmt.setInt(2, ticketPriority.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(TicketPriority ticketPriority) throws DaoException, Exception {
        String query = "DELETE FROM ticket_priority WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, ticketPriority.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Ticket Priority assigned to 1 or more tickets.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
}
