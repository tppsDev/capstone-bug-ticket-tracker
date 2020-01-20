/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sdtracker.database.DatabaseMgr;
import sdtracker.model.Contact;
import sdtracker.model.ContactType;


/**
 *
 * @author Tim Smith
 */
public class ContactDaoImpl implements CrudDao<Contact> {
    private static volatile ContactDaoImpl contactDaoImpl;
    
    //Constructor
    private ContactDaoImpl() {
        // Prohibit multiple instances via reflection
        if (contactDaoImpl != null) {
            throw new RuntimeException("ContactDaoImpl already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of ContactDaoImpl class, creating instance if not already created
     * @return
     */
    public static ContactDaoImpl getContactDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (contactDaoImpl == null) { 
            synchronized (ContactDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (contactDaoImpl == null) {
                    contactDaoImpl = new ContactDaoImpl();
                }
            }
        }
        return contactDaoImpl;
    }
    
    /**
     * Method returns an ObservableList of all records in contact table in database
     * @return
     * @throws DaoException
     */
    @Override
    public ObservableList<Contact> getAll() throws DaoException {
        ObservableList<Contact> contactList = FXCollections.observableArrayList();
        
        String query = "SELECT cont.id, "
                             +"cont.first_name, "
                             +"cont.last_name, "
                             +"cont.mid_initial, "
                             +"cont.email, "
                             +"cont.phone, "
                             +"cont.phone_type, "
                             +"cont.address, "
                             +"cont.address2, "
                             +"cont.city, "
                             +"cont.state, "
                             +"cont.zipcode, "
                             +"cont.company, "
                             +"cont.job_title, "
                             +"cont.courtesy_title, "
                             +"cType.id, "
                             +"cType.name "
                      +"FROM contact AS cont "
                      +"INNER JOIN contact_type AS cType ON c.contact_type_id = cType.id";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                int id = result.getInt("cont.id");
                String firstName = result.getString("cont.last_name");
                String lastName = result.getString("cont.");
                String midInitial = result.getString("cont.mid_initial");
                String email = result.getString("cont.email");
                String phone = result.getString("cont.phone");
                String phoneType = result.getString("cont.phoneType");
                String address = result.getString("cont.address");
                String address2 = result.getString("cont.address2");
                String city = result.getString("cont.city");
                String state = result.getString("cont.state");
                String zipcode = result.getString("cont.zipcode");
                String company = result.getString("cont.company");
                String jobTitle = result.getString("cont.job_title");
                String courtesyTitle = result.getString("cont.courtesy_title");
                int cTypeId = result.getInt("cType.id");
                String cTypeName = result.getString("cType.name");
                
                ContactType contactType = new ContactType(cTypeId, cTypeName);
                                
                contactList.add(new Contact(id, firstName, lastName, midInitial, email, phone, phoneType, 
                        address, address2, city, state, zipcode, company, jobTitle, courtesyTitle, contactType));
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return contactList;
    }

    @Override
    public Contact getById(int id) throws DaoException, Exception {
        Contact contact;
        
        String query = "SELECT cont.id, "
                             +"cont.first_name, "
                             +"cont.last_name, "
                             +"cont.mid_initial, "
                             +"cont.email, "
                             +"cont.phone, "
                             +"cont.phone_type, "
                             +"cont.address, "
                             +"cont.address2, "
                             +"cont.city, "
                             +"cont.state, "
                             +"cont.zipcode, "
                             +"cont.company, "
                             +"cont.job_title, "
                             +"cont.courtesy_title, "
                             +"cType.id, "
                             +"cType.name "
                      +"FROM contact AS cont "
                      +"INNER JOIN contact_type AS cType ON c.contact_type_id = cType.id"
                      +"WHERE cont.id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int resultId = result.getInt("cont.id");
                String firstName = result.getString("cont.last_name");
                String lastName = result.getString("cont.");
                String midInitial = result.getString("cont.mid_initial");
                String email = result.getString("cont.email");
                String phone = result.getString("cont.phone");
                String phoneType = result.getString("cont.phoneType");
                String address = result.getString("cont.address");
                String address2 = result.getString("cont.address2");
                String city = result.getString("cont.city");
                String state = result.getString("cont.state");
                String zipcode = result.getString("cont.zipcode");
                String company = result.getString("cont.company");
                String jobTitle = result.getString("cont.job_title");
                String courtesyTitle = result.getString("cont.courtesy_title");
                int cTypeId = result.getInt("cType.id");
                String cTypeName = result.getString("cType.name");
                
                ContactType contactType = new ContactType(cTypeId, cTypeName);
                
                contact = new Contact(resultId, firstName, lastName, midInitial, email, phone, phoneType, 
                        address, address2, city, state, zipcode, company, jobTitle, courtesyTitle, contactType);
                result.close();
            } else {
                result.close();
                contact = new Contact(-1);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return contact;
    }

    @Override
    public void insert(Contact contact) throws DaoException, Exception {
        String query = "INSERT INTO contact (first_name, "
                                           +"last_name, "
                                           +"mid_initial, "
                                           +"email, "
                                           +"phone, "
                                           +"phone_type, "
                                           +"address, "
                                           +"address2, "
                                           +"city, "
                                           +"state, "
                                           +"zipcode, "
                                           +"company, "
                                           +"job_title, "
                                           +"courtesy_title, "
                                           +"contact_type_id ) "
                      +"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, contact.getFirstName());
            stmt.setString(2, contact.getLastName());
            if (contact.getMidInit() == null || contact.getMidInit().isEmpty()) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, contact.getMidInit());
            }
            stmt.setString(4, contact.getEmail());
            stmt.setString(5, contact.getPhone());
            stmt.setString(6, contact.getPhoneType());
            stmt.setString(7, contact.getAddress());
            if (contact.getAddress2() == null || contact.getAddress2().isEmpty()) {
                stmt.setNull(8, Types.VARCHAR);
            } else {
                stmt.setString(8, contact.getAddress2());
            }
            stmt.setString(9, contact.getCity());
            stmt.setString(10, contact.getState());
            stmt.setString(11, contact.getZipcode());
            if (contact.getCompany() == null || contact.getCompany().isEmpty()) {
                stmt.setNull(12, Types.VARCHAR);
            } else {
                stmt.setString(12, contact.getCompany());
            }
            if (contact.getJobTitle()== null || contact.getJobTitle().isEmpty()) {
                stmt.setNull(13, Types.VARCHAR);
            } else {
                stmt.setString(13, contact.getJobTitle());
            }
            if (contact.getCourtesyTitle()== null || contact.getCourtesyTitle().isEmpty()) {
                stmt.setNull(14, Types.VARCHAR);
            } else {
                stmt.setString(14, contact.getCourtesyTitle());
            }
            stmt.setInt(15, contact.getContactType().getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(Contact contact) throws DaoException, Exception {
        String query = "UPDATE contact SET first_name = ?, "
                                         +"last_name = ?, "
                                         +"mid_initial = ?, "
                                         +"email = ?, "
                                         +"phone = ?, "
                                         +"phone_type = ?, "
                                         +"address = ?, "
                                         +"address2 = ?, "
                                         +"city = ?, "
                                         +"state = ?, "
                                         +"zipcode = ?, "
                                         +"company = ?, "
                                         +"job_title = ?, "
                                         +"courtesy_title = ?, "
                                         +"contact_type_id = ? "
                      +"WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, contact.getFirstName());
            stmt.setString(2, contact.getLastName());
            if (contact.getMidInit() == null || contact.getMidInit().isEmpty()) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, contact.getMidInit());
            }
            stmt.setString(4, contact.getEmail());
            stmt.setString(5, contact.getPhone());
            stmt.setString(6, contact.getPhoneType());
            stmt.setString(7, contact.getAddress());
            if (contact.getAddress2() == null || contact.getAddress2().isEmpty()) {
                stmt.setNull(8, Types.VARCHAR);
            } else {
                stmt.setString(8, contact.getAddress2());
            }
            stmt.setString(9, contact.getCity());
            stmt.setString(10, contact.getState());
            stmt.setString(11, contact.getZipcode());
            if (contact.getCompany() == null || contact.getCompany().isEmpty()) {
                stmt.setNull(12, Types.VARCHAR);
            } else {
                stmt.setString(12, contact.getCompany());
            }
            if (contact.getJobTitle()== null || contact.getJobTitle().isEmpty()) {
                stmt.setNull(13, Types.VARCHAR);
            } else {
                stmt.setString(13, contact.getJobTitle());
            }
            if (contact.getCourtesyTitle()== null || contact.getCourtesyTitle().isEmpty()) {
                stmt.setNull(14, Types.VARCHAR);
            } else {
                stmt.setString(14, contact.getCourtesyTitle());
            }
            stmt.setInt(15, contact.getContactType().getId());
            stmt.setInt(16, contact.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(Contact contact) throws DaoException, Exception {
        String query = "DELETE FROM contact WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, contact.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Contact assigned to 1 or more tickets/bug reports.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
}
