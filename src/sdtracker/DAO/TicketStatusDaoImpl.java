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
import sdtracker.model.TicketStatus;

/**
 *
 * @author Tim Smith
 */
public class TicketStatusDaoImpl implements CrudDao<TicketStatus> {
    private static volatile TicketStatusDaoImpl ticketStatusDaoImpl;
    
    //Constructor
    private TicketStatusDaoImpl() {
        // Prohibit multiple instances via reflection
        if (ticketStatusDaoImpl != null) {
            throw new RuntimeException("TicketStatusDAOImpl already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of TicketStatusDaoImpl class, creating instance if not already created
     * @return
     */
    public static TicketStatusDaoImpl getTicketStatusDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (ticketStatusDaoImpl == null) { 
            synchronized (TicketStatusDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (ticketStatusDaoImpl == null) {
                    ticketStatusDaoImpl = new TicketStatusDaoImpl();
                }
            }
        }
        return ticketStatusDaoImpl;
    }
    
    /**
     * Method returns an ObservableList of all records in ticket_status table in database
     * @return
     * @throws DaoException
     */
    @Override
    public ObservableList<TicketStatus> getAll() throws DaoException {
        ObservableList<TicketStatus> ticketStatusList = FXCollections.observableArrayList();
        
        String query = "SELECT tStatus.id, tStatus.name FROM ticket_status AS tStatus";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                int id = result.getInt("tStatus.id");
                String name = result.getString("tStatus.name");
                
                ticketStatusList.add(new TicketStatus(id, name));
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return ticketStatusList;
    }

    @Override
    public TicketStatus getById(int id) throws DaoException, Exception {
        TicketStatus ticketStatus;
        
        String query = "SELECT tStatus.id, tStatus.name "
                        +"FROM ticket_status AS tStatus "
                        +"WHERE tStatus.id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int resultId = result.getInt("tStatus.id");
                String name = result.getString("tStatus.name");
                ticketStatus = new TicketStatus(resultId, name);
                result.close();
            } else {
                result.close();
                ticketStatus = new TicketStatus(-1, null);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return ticketStatus;
    }
    
    public boolean checkForDuplicate(String name) throws DaoException, Exception {
        int recCount = 0;
        String query = "SELECT COUNT(*) AS recCount "
                        +"FROM ticket_status AS tStatus "
                        +"WHERE tStatus.name = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, name);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                recCount = result.getInt("recCount");
                result.close();
            } else {
                result.close();
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return recCount > 0;
    }

    @Override
    public void insert(TicketStatus ticketStatus) throws DaoException, Exception {
        String query = "INSERT INTO ticket_status (name) "
                      +"VALUES (?)";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, ticketStatus.getName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(TicketStatus ticketStatus) throws DaoException, Exception {
        String query = "UPDATE ticket_status "
                      +"SET name = ? "
                      +"WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, ticketStatus.getName());
            stmt.setInt(2, ticketStatus.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(TicketStatus ticketStatus) throws DaoException, Exception {
        String query = "DELETE FROM ticket_status WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, ticketStatus.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Ticket Status assigned to 1 or more tickets.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
}
