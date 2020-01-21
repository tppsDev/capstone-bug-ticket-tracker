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
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sdtracker.database.DatabaseMgr;
import sdtracker.model.AppUser;
import sdtracker.model.Asset;
import sdtracker.model.AssetType;
import sdtracker.model.Contact;
import sdtracker.model.Product;
import sdtracker.model.Ticket;
import sdtracker.model.TicketPriority;
import sdtracker.model.TicketStatus;

/**
 *
 * @author Tim Smith
 */
public class TicketDaoImpl implements CrudDao<Ticket> {
    private static volatile TicketDaoImpl ticketDaoImpl;
    
    //Constructor
    private TicketDaoImpl() {
        // Prohibit multiple instances via reflection
        if (ticketDaoImpl != null) {
            throw new RuntimeException("TicketDaoImpl already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of TicketDaoImpl class, creating instance if not already created
     * @return
     */
    public static TicketDaoImpl getTicketDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (ticketDaoImpl == null) { 
            synchronized (TicketDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (ticketDaoImpl == null) {
                    ticketDaoImpl = new TicketDaoImpl();
                }
            }
        }
        return ticketDaoImpl;
    }
    
    
    @Override
    public ObservableList<Ticket> getAll() throws DaoException, Exception {
        ObservableList<Ticket> allTickets = FXCollections.observableArrayList();
        
        String query = "SELECT tick.id, "
                             +"tick.title "
                             +"tick.description "
                             +"tick.created_timestamp "
                             +"tick.last_updated_timestamp "
                             +"tick.ticket_number "
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
                             +"tStat.id "
                             +"tStat.name "
                             +"tPri.id "
                             +"tPri.name "
                             +"asst.id "
                             +"asst.name "
                             +"aType.id "
                             +"aType.name "
                      +"FROM ticket AS tick "
                        +"INNER JOIN contact AS cont ON tick.contact_id = cont.id "
                        +"INNER JOIN app_user AS aUser ON tick.assigned_app_user_id = aUser.id "
                        +"INNER JOIN app_user AS cUser ON tick.created_by_app_user_id = cUser.id "
                        +"INNER JOIN ticket_status AS tStat ON tick.ticket_status_id = tStat.id "
                        +"INNER JOIN ticket_priority AS tPri ON tick.ticket_priority_id = tPri.id "
                        +"LEFT JOIN product AS prod ON tick.product_id = prod.id "
                        +"LEFT JOIN asset AS asst ON tick.asset_id = asst.id "
                        +"INNER JOIN asset_type AS aType ON asst.asset_type_id = aType.id "
                        +"LEFT JOIN app_user AS luUser ON tick.last_updated_by_user_id = luUser.id ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                // Ticket object fields
                int id = result.getInt("tick.id");
                String title = result.getString("tick.title");
                String description = result.getString("tick.description");
                LocalDateTime createdTimestamp = result.getTimestamp("tick.created_timestamp").toLocalDateTime();
                LocalDateTime lastUpdatedTimestamp = result.getTimestamp("tick.last_updated_timestamp").toLocalDateTime();
                String ticketNumber= result.getString("tick.ticket_number");
                
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
                
                // TicketStatus fields
                int tStatId = result.getInt("tStat.id");
                String tStatName= result.getString("tStat.name");
                
                // TicketPriority fields
                int tPriId = result.getInt("tPri.id");
                String tPriName= result.getString("tPri.name");
                
                // Asset fields
                int asstId = result.getInt("asst.id");
                String asstName= result.getString("asst.name");
                
                // AssetType fields
                int aTypeId = result.getInt("aType.id");
                String aTypeName= result.getString("aType.name");
                
                // Create related object instances
                Product product = new Product(prodId, prodName, prodVersion);
                Contact contact = new Contact(contId, contFirstName, contLastName, contEmail, 
                        contPhone, contPhoneType, contCourtesyTitle);
                AppUser assignedAppUser = new AppUser(aUserId, aUserFirstName, aUserLastName, aUserEmail, aUserPhone1);
                AppUser createdByAppUser = new AppUser(cUserId, cUserFirstName, cUserLastName);
                AppUser lastUpdatedByAppUser = new AppUser(luUserId, luUserFirstName, luUserLastName);
                TicketStatus ticketStatus = new TicketStatus(tStatId, tStatName);
                TicketPriority ticketPriority = new TicketPriority(tPriId, tPriName);
                AssetType assetType = new AssetType(aTypeId, aTypeName);
                Asset asset = new Asset(asstId, asstName, assetType);
                
                // add new Ticket object to list
                allTickets.add(new Ticket(id, title, description, product, contact, assignedAppUser, 
                        createdTimestamp, createdByAppUser, lastUpdatedTimestamp, lastUpdatedByAppUser, 
                        ticketNumber, asset, ticketStatus, ticketPriority));
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return allTickets;
    }

    @Override
    public Ticket getById(int id) throws DaoException {
        Ticket ticket;
        
        String query = "SELECT tick.id, "
                             +"tick.title "
                             +"tick.description "
                             +"tick.created_timestamp "
                             +"tick.last_updated_timestamp "
                             +"tick.ticket_number "
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
                             +"tStat.id "
                             +"tStat.name "
                             +"tPri.id "
                             +"tPri.name "
                             +"asst.id "
                             +"asst.name "
                             +"aType.id "
                             +"aType.name "
                      +"FROM ticket AS tick "
                        +"INNER JOIN contact AS cont ON tick.contact_id = cont.id "
                        +"INNER JOIN app_user AS aUser ON tick.assigned_app_user_id = aUser.id "
                        +"INNER JOIN app_user AS cUser ON tick.created_by_app_user_id = cUser.id "
                        +"INNER JOIN ticket_status AS tStat ON tick.ticket_status_id = tStat.id "
                        +"INNER JOIN ticket_priority AS tPri ON tick.ticket_priority_id = tPri.id "
                        +"LEFT JOIN product AS prod ON tick.product_id = prod.id "
                        +"LEFT JOIN asset AS asst ON tick.asset_id = asst.id "
                        +"INNER JOIN asset_type AS aType ON asst.asset_type_id = aType.id "
                        +"LEFT JOIN app_user AS luUser ON tick.last_updated_by_user_id = luUser.id "
                      +"WHERE tick.id = ? ";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                // Ticket object fields
                int resultId = result.getInt("tick.id");
                String title = result.getString("tick.title");
                String description = result.getString("tick.description");
                LocalDateTime createdTimestamp = result.getTimestamp("tick.created_timestamp").toLocalDateTime();
                LocalDateTime lastUpdatedTimestamp = result.getTimestamp("tick.last_updated_timestamp").toLocalDateTime();
                String ticketNumber= result.getString("tick.ticket_number");
                
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
                
                // TicketStatus fields
                int tStatId = result.getInt("tStat.id");
                String tStatName= result.getString("tStat.name");
                
                // TicketPriority fields
                int tPriId = result.getInt("tPri.id");
                String tPriName= result.getString("tPri.name");
                
                // Asset fields
                int asstId = result.getInt("asst.id");
                String asstName= result.getString("asst.name");
                
                // AssetType fields
                int aTypeId = result.getInt("aType.id");
                String aTypeName= result.getString("aType.name");
                
                // Create related object instances
                Product product = new Product(prodId, prodName, prodVersion);
                Contact contact = new Contact(contId, contFirstName, contLastName, contEmail, 
                        contPhone, contPhoneType, contCourtesyTitle);
                AppUser assignedAppUser = new AppUser(aUserId, aUserFirstName, aUserLastName, aUserEmail, aUserPhone1);
                AppUser createdByAppUser = new AppUser(cUserId, cUserFirstName, cUserLastName);
                AppUser lastUpdatedByAppUser = new AppUser(luUserId, luUserFirstName, luUserLastName);
                TicketStatus ticketStatus = new TicketStatus(tStatId, tStatName);
                TicketPriority ticketPriority = new TicketPriority(tPriId, tPriName);
                AssetType assetType = new AssetType(aTypeId, aTypeName);
                Asset asset = new Asset(asstId, asstName, assetType);
                
                // add new Ticket object to list
                ticket = new Ticket(resultId, title, description, product, contact, assignedAppUser, 
                        createdTimestamp, createdByAppUser, lastUpdatedTimestamp, lastUpdatedByAppUser, 
                        ticketNumber, asset, ticketStatus, ticketPriority);
                result.close();
            } else {
                result.close();
                ticket = new Ticket(-1);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }

        return ticket;
    }

    @Override
    public void insert(Ticket ticket) throws DaoException {
        String query = "INSERT INTO ticket (title, "
                                          +"description,  "
                                          +"product_id "
                                          +"contact_id "
                                          +"assigned_app_user_id "
                                          +"created_timestamp, "
                                          +"created_by_app_user_id "
                                          +"last_update_timestamp "
                                          +"last_updated_by_user_id "
                                          +"ticket_status_id "
                                          +"ticket_number "
                                          +"asset_id "
                                          +"ticket_priority_id "
                      +"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, ticket.getTitle());
            stmt.setString(2, ticket.getDescription());
            stmt.setInt(3, ticket.getProduct().getId());
            stmt.setInt(4, ticket.getContact().getId());
            stmt.setInt(5, ticket.getAssignedAppUser().getId());
            stmt.setTimestamp(6, Timestamp.valueOf(ticket.getCreatedTimestamp()));
            stmt.setInt(7, ticket.getCreatedByAppUser().getId());
            stmt.setTimestamp(8, Timestamp.valueOf(ticket.getLastUpdatedTimestamp()));
            stmt.setInt(9, ticket.getLastUpdatedByAppUser().getId());
            stmt.setInt(10, ticket.getStatus().getId());
            stmt.setString(11, ticket.getTicketNumber());
            stmt.setInt(12, ticket.getAsset().getId());
            stmt.setInt(13, ticket.getPriority().getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(Ticket ticket) throws DaoException {
        String query = "INSERT INTO ticket (title, "
                                          +"description,  "
                                          +"product_id "
                                          +"contact_id "
                                          +"assigned_app_user_id "
                                          +"created_timestamp, "
                                          +"created_by_app_user_id "
                                          +"last_update_timestamp "
                                          +"last_updated_by_user_id "
                                          +"ticket_status_id "
                                          +"ticket_number "
                                          +"asset_id "
                                          +"ticket_priority_id "
                      +"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                      +"WHERE id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, ticket.getTitle());
            stmt.setString(2, ticket.getDescription());
            stmt.setInt(3, ticket.getProduct().getId());
            stmt.setInt(4, ticket.getContact().getId());
            stmt.setInt(5, ticket.getAssignedAppUser().getId());
            stmt.setTimestamp(6, Timestamp.valueOf(ticket.getCreatedTimestamp()));
            stmt.setInt(7, ticket.getCreatedByAppUser().getId());
            stmt.setTimestamp(8, Timestamp.valueOf(ticket.getLastUpdatedTimestamp()));
            stmt.setInt(9, ticket.getLastUpdatedByAppUser().getId());
            stmt.setInt(10, ticket.getStatus().getId());
            stmt.setString(11, ticket.getTicketNumber());
            stmt.setInt(12, ticket.getAsset().getId());
            stmt.setInt(13, ticket.getPriority().getId());
            stmt.setInt(14, ticket.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(Ticket ticket) throws DaoException {
        String query = "DELETE FROM ticket WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, ticket.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Ticket assigned to another record.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
    
}