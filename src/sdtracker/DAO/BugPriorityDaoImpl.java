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
import sdtracker.model.BugPriority;

/**
 *
 * @author Tim Smith
 */
public class BugPriorityDaoImpl implements CrudDao<BugPriority> {
    private static volatile BugPriorityDaoImpl bugPriorityDaoImpl;
    
    //Constructor
    private BugPriorityDaoImpl() {
        // Prohibit multiple instances via reflection
        if (bugPriorityDaoImpl != null) {
            throw new RuntimeException("BugPriorityDAO already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of BugPriorityDaoImpl class, creating instance if not already created
     * @return
     */
    public static BugPriorityDaoImpl getBugPriorityDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (bugPriorityDaoImpl == null) { 
            synchronized (BugPriorityDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (bugPriorityDaoImpl == null) {
                    bugPriorityDaoImpl = new BugPriorityDaoImpl();
                }
            }
        }
        return bugPriorityDaoImpl;
    }
    
    /**
     * Method returns an ObservableList of all records in bug_priority table in database
     * @return
     * @throws DaoException
     */
    @Override
    public ObservableList<BugPriority> getAll() throws DaoException {
        ObservableList<BugPriority> bugPriorityList = FXCollections.observableArrayList();
        
        String query = "SELECT bPriority.id, bPriority.name FROM bug_priority AS bPriority";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                int id = result.getInt("bPriority.id");
                String name = result.getString("bPriority.name");
                
                bugPriorityList.add(new BugPriority(id, name));
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return bugPriorityList;
    }

    @Override
    public BugPriority getById(int id) throws DaoException, Exception {
        BugPriority bugPriority;
        
        String query = "SELECT bPriority.id, bPriority.name "
                        +"FROM bug_priority AS bPriority "
                        +"WHERE bPriority.id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int resultId = result.getInt("bPriority.id");
                String name = result.getString("bPriority.name");
                bugPriority = new BugPriority(resultId, name);
                result.close();
            } else {
                result.close();
                bugPriority = new BugPriority(-1, null);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return bugPriority;
    }

    @Override
    public void insert(BugPriority bugPriority) throws DaoException, Exception {
        String query = "INSERT INTO bug_priority (name) "
                      +"VALUES (?)";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, bugPriority.getName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(BugPriority bugPriority) throws DaoException, Exception {
        String query = "UPDATE bug_priority "
                      +"SET name = ? "
                      +"WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, bugPriority.getName());
            stmt.setInt(2, bugPriority.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(BugPriority bugPriority) throws DaoException, Exception {
        String query = "DELETE FROM bug_priority WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, bugPriority.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Bug Priority assigned to 1 or more bugs.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
}
