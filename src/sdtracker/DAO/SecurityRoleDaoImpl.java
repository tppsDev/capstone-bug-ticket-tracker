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
import sdtracker.model.SecurityRole;

/**
 *
 * @author Tim Smith
 */
public class SecurityRoleDaoImpl implements CrudDao<SecurityRole>{
    private static volatile SecurityRoleDaoImpl securityRoleDaoImpl;
    
    //Constructor
    private SecurityRoleDaoImpl() {
        // Prohibit multiple instances via reflection
        if (securityRoleDaoImpl != null) {
            throw new RuntimeException("SecurityRoleDaoImpl already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of SecurityRoleDaoImpl class, creating instance if not already created
     * @return
     */
    public static SecurityRoleDaoImpl getSecurityRoleDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (securityRoleDaoImpl == null) { 
            synchronized (SecurityRoleDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (securityRoleDaoImpl == null) {
                    securityRoleDaoImpl = new SecurityRoleDaoImpl();
                }
            }
        }
        return securityRoleDaoImpl;
    }
    
    /**
     * Method returns an ObservableList of all records in security_role table in database
     * @return
     * @throws DaoException
     */
    @Override
    public ObservableList<SecurityRole> getAll() throws DaoException {
        ObservableList<SecurityRole> securityRoleList = FXCollections.observableArrayList();
        
        String query = "SELECT sRole.id, sRole.name FROM security_role AS sRole";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                int id = result.getInt("sRole.id");
                String name = result.getString("sRole.name");
                
                securityRoleList.add(new SecurityRole(id, name));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DaoException("Database error: Try again later.");
        }
        
        return securityRoleList;
    }

    @Override
    public SecurityRole getById(int id) throws DaoException, Exception {
        SecurityRole securityRole;
        
        String query = "SELECT sRole.id, sRole.name "
                        +"FROM security_role AS sRole "
                        +"WHERE sRole.id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int resultId = result.getInt("sRole.id");
                String name = result.getString("sRole.name");
                securityRole = new SecurityRole(resultId, name);
                result.close();
            } else {
                result.close();
                securityRole = new SecurityRole(-1, null);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return securityRole;
    }

    @Override
    public void insert(SecurityRole securityRole) throws DaoException, Exception {
        String query = "INSERT INTO security_role (name) "
                      +"VALUES (?)";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, securityRole.getName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(SecurityRole securityRole) throws DaoException, Exception {
        String query = "UPDATE security_role "
                      +"SET name = ? "
                      +"WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, securityRole.getName());
            stmt.setInt(2, securityRole.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(SecurityRole securityRole) throws DaoException, Exception {
        String query = "DELETE FROM security_role WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, securityRole.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Security Role assigned to 1 or more users.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
}
