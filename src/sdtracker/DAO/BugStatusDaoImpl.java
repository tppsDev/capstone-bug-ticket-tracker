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
import sdtracker.model.BugStatus;

/**
 *
 * @author Tim Smith
 */
public class BugStatusDaoImpl implements CrudDao<BugStatus> {
    private static volatile BugStatusDaoImpl bugStatusDaoImpl;
    
    //Constructor
    private BugStatusDaoImpl() {
        // Prohibit multiple instances via reflection
        if (bugStatusDaoImpl != null) {
            throw new RuntimeException("BugStatusDAO already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of BugStatusDaoImpl class, creating instance if not already created
     * @return
     */
    public static BugStatusDaoImpl getBugStatusDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (bugStatusDaoImpl == null) { 
            synchronized (BugStatusDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (bugStatusDaoImpl == null) {
                    bugStatusDaoImpl = new BugStatusDaoImpl();
                }
            }
        }
        return bugStatusDaoImpl;
    }
    
    /**
     * Method returns an ObservableList of all records in bug_status table in database
     * @return
     * @throws DaoException
     */
    @Override
    public ObservableList<BugStatus> getAll() throws DaoException {
        ObservableList<BugStatus> bugStatusList = FXCollections.observableArrayList();
        
        String query = "SELECT bStatus.id, bStatus.name FROM bug_status AS bStatus";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                int id = result.getInt("bStatus.id");
                String name = result.getString("bStatus.name");
                
                bugStatusList.add(new BugStatus(id, name));
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return bugStatusList;
    }

    @Override
    public BugStatus getById(int id) throws DaoException, Exception {
        BugStatus bugStatus;
        
        String query = "SELECT bStatus.id, bStatus.name "
                        +"FROM bug_status AS bStatus "
                        +"WHERE bStatus.id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int resultId = result.getInt("bStatus.id");
                String name = result.getString("bStatus.name");
                bugStatus = new BugStatus(resultId, name);
                result.close();
            } else {
                result.close();
                bugStatus = new BugStatus(-1, null);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return bugStatus;
    }
    
    public boolean checkForDuplicate(String name) throws DaoException, Exception {
        int recCount = 0;
        String query = "SELECT COUNT(*) AS recCount "
                        +"FROM bug_status AS bStatus "
                        +"WHERE bStatus.name = ? ";
        
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
    public void insert(BugStatus bugStatus) throws DaoException, Exception {
        String query = "INSERT INTO bug_status (name) "
                      +"VALUES (?)";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, bugStatus.getName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(BugStatus bugStatus) throws DaoException, Exception {
        String query = "UPDATE bug_status "
                      +"SET name = ? "
                      +"WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, bugStatus.getName());
            stmt.setInt(2, bugStatus.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(BugStatus bugStatus) throws DaoException, Exception {
        String query = "DELETE FROM bug_status WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, bugStatus.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Bug Status assigned to 1 or more bugs.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
}
