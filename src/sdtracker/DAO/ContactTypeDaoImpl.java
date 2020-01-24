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
import sdtracker.model.ContactType;

/**
 *
 * @author Tim Smith
 */
public class ContactTypeDaoImpl implements CrudDao<ContactType> {
    private static volatile ContactTypeDaoImpl contactTypeDaoImpl;
    
    //Constructor
    private ContactTypeDaoImpl() {
        // Prohibit multiple instances via reflection
        if (contactTypeDaoImpl != null) {
            throw new RuntimeException("ContactTypeDaoImpl already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of ContactTypeDaoImpl class, creating instance if not already created
     * @return
     */
    public static ContactTypeDaoImpl getContactTypeDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (contactTypeDaoImpl == null) { 
            synchronized (ContactTypeDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (contactTypeDaoImpl == null) {
                    contactTypeDaoImpl = new ContactTypeDaoImpl();
                }
            }
        }
        return contactTypeDaoImpl;
    }
    
    /**
     * Method returns an ObservableList of all records in contact_type table in database
     * @return
     * @throws DaoException
     */
    @Override
    public ObservableList<ContactType> getAll() throws DaoException {
        ObservableList<ContactType> contactTypeList = FXCollections.observableArrayList();
        
        String query = "SELECT cType.id, cType.name FROM contact_type AS cType";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                int id = result.getInt("cType.id");
                String name = result.getString("cType.name");
                
                contactTypeList.add(new ContactType(id, name));
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return contactTypeList;
    }

    @Override
    public ContactType getById(int id) throws DaoException, Exception {
        ContactType contactType;
        
        String query = "SELECT cType.id, cType.name "
                        +"FROM contact_type AS cType "
                        +"WHERE cType.id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int resultId = result.getInt("cType.id");
                String name = result.getString("cType.name");
                contactType = new ContactType(resultId, name);
                result.close();
            } else {
                result.close();
                contactType = new ContactType(-1, null);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return contactType;
    }
    
    public boolean checkForDuplicate(String name) throws DaoException, Exception {
        int recCount = 0;
        String query = "SELECT COUNT(*) AS recCount "
                        +"FROM contact_type AS cType "
                        +"WHERE cType.name = ? ";
        
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
    public void insert(ContactType contactType) throws DaoException, Exception {
        String query = "INSERT INTO contact_type (name) "
                      +"VALUES (?)";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, contactType.getName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(ContactType contactType) throws DaoException, Exception {
        String query = "UPDATE contact_type "
                      +"SET name = ? "
                      +"WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, contactType.getName());
            stmt.setInt(2, contactType.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(ContactType contactType) throws DaoException, Exception {
        String query = "DELETE FROM contact_type WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, contactType.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Contact Type assigned to 1 or more contacts.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
}
