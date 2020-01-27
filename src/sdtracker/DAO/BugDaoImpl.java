/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sdtracker.database.DatabaseMgr;
import sdtracker.model.AppUser;
import sdtracker.model.Asset;
import sdtracker.model.AssetType;
import sdtracker.model.Bug;
import sdtracker.model.Contact;
import sdtracker.model.Product;
import sdtracker.model.Bug;
import sdtracker.model.BugPriority;
import sdtracker.model.BugStatus;

/**
 *
 * @author Tim Smith
 */
public class BugDaoImpl implements CrudDao<Bug> {
    private static volatile BugDaoImpl bugDaoImpl;
    
    //Constructor
    private BugDaoImpl() {
        // Prohibit multiple instances via reflection
        if (bugDaoImpl != null) {
            throw new RuntimeException("BugDaoImpl already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of BugDaoImpl class, creating instance if not already created
     * @return
     */
    public static BugDaoImpl getBugDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (bugDaoImpl == null) { 
            synchronized (BugDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (bugDaoImpl == null) {
                    bugDaoImpl = new BugDaoImpl();
                }
            }
        }
        return bugDaoImpl;
    }
    
    @Override
    public ObservableList<Bug> getAll() throws DaoException, Exception {
        ObservableList<Bug> allBugs = FXCollections.observableArrayList();
        
        String query = "SELECT b.id, "
                             +"b.bug_number "
                             +"b.title "
                             +"b.description "
                             +"b.created_timestamp "
                             +"b.last_updated_timestamp "
                             +"prod.id "
                             +"prod.name "
                             +"prod.version "
                             +"cont.id "
                             +"cont.first_name "
                             +"cont.last_name "
                             +"cont.email "
                             +"cont.phone "
                             +"cont.phone_type "
                             +"cont.courtest_title "
                             +"aUser.id "
                             +"aUser.first_name "
                             +"aUser.last_name "
                             +"aUser.email "
                             +"aUser.phone1 "
                             +"cUser.id "
                             +"cUser.first_name "
                             +"cUser.last_name "
                             +"luUser.id "
                             +"luUser.first_name "
                             +"luUser.last_name "
                             +"bStat.id "
                             +"bStat.name "
                             +"bPri.id "
                             +"bPri.name "
                      +"FROM bug AS b "
                        +"INNER JOIN contact AS cont ON b.contact_id = cont.id "
                        +"INNER JOIN app_user AS aUser ON b.assigned_app_user_id = aUser.id "
                        +"INNER JOIN app_user AS cUser ON b.created_by_user_id = cUser.id "
                        +"INNER JOIN bug_status AS bStat ON b.bug_status_id = tStat.id "
                        +"INNER JOIN bug_priority AS bPri ON b.bug_priority_id = tPri.id "
                        +"LEFT JOIN product AS prod ON b.product_id = prod.id "
                        +"LEFT JOIN app_user AS luUser ON b.last_updated_by_user_id = luUser.id ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                // Bug object fields
                int id = result.getInt("b.id");
                String title = result.getString("b.title");
                String description = result.getString("b.description");
                LocalDateTime createdTimestamp = result.getTimestamp("b.created_timestamp").toLocalDateTime();
                LocalDateTime lastUpdatedTimestamp = result.getTimestamp("b.last_updated_timestamp").toLocalDateTime();
                String bugNumber= result.getString("b.bug_number");
                
                // Product object fields
                int prodId = result.getInt("prod.id");
                String prodName= result.getString("prod.name");
                String prodVersion= result.getString("prod.version");
                
                // Contact object fields
                int contId = result.getInt("cont.id");
                String contFirstName = result.getString("cont.first_name");
                String contLastName= result.getString("cont.last_name");
                String contEmail = result.getString("cont.email");
                String contPhone = result.getString("cont.phone");
                String contPhoneType = result.getString("cont.phone_type");
                String contCourtesyTitle = result.getString("cont.courtest_title");
                
                // Assigned user fields
                int aUserId = result.getInt("aUser.id");
                String aUserFirstName = result.getString("aUser.first_name");
                String aUserLastName = result.getString("aUser.last_name");
                String aUserEmail = result.getString("aUser.email");
                String aUserPhone1= result.getString("aUser.phone1");
                
                // Created by user fields
                int cUserId = result.getInt("cUser.id");
                String cUserFirstName = result.getString ("cUser.first_name");
                String cUserLastName = result.getString("cUser.last_name");
                
                // Last updated by user fields
                int luUserId = result.getInt("luUser.id");
                String luUserFirstName= result.getString("luUser.first_name");
                String luUserLastName = result.getString("luUser.last_name");
                
                // BugStatus fields
                int tStatId = result.getInt("bStat.id");
                String tStatName= result.getString("bStat.name");
                
                // BugPriority fields
                int tPriId = result.getInt("bPri.id");
                String tPriName= result.getString("bPri.name");

                // Create related object instances
                Product product = new Product(prodId, prodName, prodVersion);
                Contact contact = new Contact(contId, contFirstName, contLastName, contEmail, 
                        contPhone, contPhoneType, contCourtesyTitle);
                AppUser assignedAppUser = new AppUser(aUserId, aUserFirstName, aUserLastName, aUserEmail, aUserPhone1);
                AppUser createdByAppUser = new AppUser(cUserId, cUserFirstName, cUserLastName);
                AppUser lastUpdatedByAppUser = new AppUser(luUserId, luUserFirstName, luUserLastName);
                BugStatus bugStatus = new BugStatus(tStatId, tStatName);
                BugPriority bugPriority = new BugPriority(tPriId, tPriName);
               
                // add new Bug object to list
                allBugs.add(new Bug(id, title, description, bugStatus, assignedAppUser, 
                        createdTimestamp, createdByAppUser, lastUpdatedTimestamp, lastUpdatedByAppUser, 
                        product, contact, bugPriority, bugNumber));
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return allBugs;
    }

    @Override
    public Bug getById(int id) throws DaoException {
        Bug bug;
        
        String query = "SELECT b.id, "
                             +"b.bug_number "
                             +"b.title "
                             +"b.description "
                             +"b.created_timestamp "
                             +"b.last_updated_timestamp "
                             +"prod.id "
                             +"prod.name "
                             +"prod.version "
                             +"cont.id "
                             +"cont.first_name "
                             +"cont.last_name "
                             +"cont.email "
                             +"cont.phone "
                             +"cont.phone_type "
                             +"cont.courtest_title "
                             +"aUser.id "
                             +"aUser.first_name "
                             +"aUser.last_name "
                             +"aUser.email "
                             +"aUser.phone1 "
                             +"cUser.id "
                             +"cUser.first_name "
                             +"cUser.last_name "
                             +"luUser.id "
                             +"luUser.first_name "
                             +"luUser.last_name "
                             +"bStat.id "
                             +"bStat.name "
                             +"bPri.id "
                             +"bPri.name "
                      +"FROM bug AS b "
                        +"INNER JOIN contact AS cont ON b.contact_id = cont.id "
                        +"INNER JOIN app_user AS aUser ON b.assigned_app_user_id = aUser.id "
                        +"INNER JOIN app_user AS cUser ON b.created_by_user_id = cUser.id "
                        +"INNER JOIN bug_status AS bStat ON b.bug_status_id = tStat.id "
                        +"INNER JOIN bug_priority AS bPri ON b.bug_priority_id = tPri.id "
                        +"LEFT JOIN product AS prod ON b.product_id = prod.id "
                        +"LEFT JOIN app_user AS luUser ON b.last_updated_by_user_id = luUser.id "
                      +"WHERE b.id = ? ";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                // Bug object fields
                int resultId = result.getInt("b.id");
                String title = result.getString("b.title");
                String description = result.getString("b.description");
                LocalDateTime createdTimestamp = result.getTimestamp("b.created_timestamp").toLocalDateTime();
                LocalDateTime lastUpdatedTimestamp = result.getTimestamp("b.last_updated_timestamp").toLocalDateTime();
                String bugNumber= result.getString("b.bug_number");
                
                // Product object fields
                int prodId = result.getInt("prod.id");
                String prodName= result.getString("prod.name");
                String prodVersion= result.getString("prod.version");
                
                // Contact object fields
                int contId = result.getInt("cont.id");
                String contFirstName = result.getString("cont.first_name");
                String contLastName= result.getString("cont.last_name");
                String contEmail = result.getString("cont.email");
                String contPhone = result.getString("cont.phone");
                String contPhoneType = result.getString("cont.phone_type");
                String contCourtesyTitle = result.getString("cont.courtest_title");
                
                // Assigned user fields
                int aUserId = result.getInt("aUser.id");
                String aUserFirstName = result.getString("aUser.first_name");
                String aUserLastName = result.getString("aUser.last_name");
                String aUserEmail = result.getString("aUser.email");
                String aUserPhone1= result.getString("aUser.phone1");
                
                // Created by user fields
                int cUserId = result.getInt("cUser.id");
                String cUserFirstName = result.getString ("cUser.first_name");
                String cUserLastName = result.getString("cUser.last_name");
                
                // Last updated by user fields
                int luUserId = result.getInt("luUser.id");
                String luUserFirstName= result.getString("luUser.first_name");
                String luUserLastName = result.getString("luUser.last_name");
                
                // BugStatus fields
                int tStatId = result.getInt("bStat.id");
                String tStatName= result.getString("bStat.name");
                
                // BugPriority fields
                int tPriId = result.getInt("bPri.id");
                String tPriName= result.getString("bPri.name");

                // Create related object instances
                Product product = new Product(prodId, prodName, prodVersion);
                Contact contact = new Contact(contId, contFirstName, contLastName, contEmail, 
                        contPhone, contPhoneType, contCourtesyTitle);
                AppUser assignedAppUser = new AppUser(aUserId, aUserFirstName, aUserLastName, aUserEmail, aUserPhone1);
                AppUser createdByAppUser = new AppUser(cUserId, cUserFirstName, cUserLastName);
                AppUser lastUpdatedByAppUser = new AppUser(luUserId, luUserFirstName, luUserLastName);
                BugStatus bugStatus = new BugStatus(tStatId, tStatName);
                BugPriority bugPriority = new BugPriority(tPriId, tPriName);
               
                // add new Bug object to list
                bug = new Bug(resultId, title, description, bugStatus, assignedAppUser, 
                        createdTimestamp, createdByAppUser, lastUpdatedTimestamp, lastUpdatedByAppUser, 
                        product, contact, bugPriority, bugNumber);
                result.close();
            } else {
                result.close();
                bug = new Bug(-1);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }

        return bug;
    }
    
    public boolean checkForDuplicate(String bugNumber) throws DaoException, Exception {
        int recCount = 0;
        String query = "SELECT COUNT(*) AS recCount "
                      +"FROM bug AS b "
                      +"WHERE b.bug_number = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, bugNumber);
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
    public void insert(Bug bug) throws DaoException {
        String query = "INSERT INTO bug (title, "
                                        +"description, "
                                        +"product_id "
                                        +"contact_id "
                                        +"assigned_app_user_id "
                                        +"created_timestamp, "
                                        +"created_by_app_user_id "
                                        +"bug_status_id "
                                        +"bug_number, "
                                        +"bug_priority_id "
                      +"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, bug.getTitle());
            stmt.setString(2, bug.getDescription());
            stmt.setInt(3, bug.getProduct().getId());
            stmt.setInt(4, bug.getContact().getId());
            if (bug.getAssignedAppUser() == null) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, bug.getAssignedAppUser().getId());
            }
            stmt.setTimestamp(6, Timestamp.valueOf(bug.getCreatedTimestamp()));
            stmt.setInt(7, bug.getCreatedByAppUser().getId());
            stmt.setInt(8, bug.getStatus().getId());
            stmt.setString(9, bug.getBugNumber());
            stmt.setInt(10, bug.getPriority().getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(Bug bug) throws DaoException {
        String query = "UPDATE bug SET title = ?, "
                                          +"description = ?, "
                                          +"product_id = ?, "
                                          +"contact_id = ?, "
                                          +"assigned_app_user_id = ?, "
                                          +"last_update_timestamp = ?, "
                                          +"last_updated_by_user_id = ?, "
                                          +"bug_status_id = ?, "
                                          +"bug_number = ?, "
                                          +"bug_priority_id = ?, "
                      +"WHERE id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, bug.getTitle());
            stmt.setString(2, bug.getDescription());
            stmt.setInt(3, bug.getProduct().getId());
            stmt.setInt(4, bug.getContact().getId());
            stmt.setInt(5, bug.getAssignedAppUser().getId());
            stmt.setTimestamp(6, Timestamp.valueOf(bug.getLastUpdatedTimestamp()));
            stmt.setInt(7, bug.getLastUpdatedByAppUser().getId());
            stmt.setInt(8, bug.getStatus().getId());
            stmt.setString(9, bug.getBugNumber());
            stmt.setInt(10, bug.getPriority().getId());
            stmt.setInt(11, bug.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(Bug bug) throws DaoException {
        String query = "DELETE FROM bug WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, bug.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Bug assigned to another record.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
}
