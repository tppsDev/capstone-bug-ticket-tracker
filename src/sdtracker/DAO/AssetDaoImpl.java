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
import sdtracker.model.AppUser;
import sdtracker.model.Asset;
import sdtracker.model.AssetType;
import sdtracker.model.Mfg;


/**
 *
 * @author Tim Smith
 */
public class AssetDaoImpl implements CrudDao<Asset> {
    private static volatile AssetDaoImpl assetDaoImpl;
    
    //Constructor
    private AssetDaoImpl() {
        // Prohibit multiple instances via reflection
        if (assetDaoImpl != null) {
            throw new RuntimeException("AssetDaoImpl already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of AssetDaoImpl class, creating instance if not already created
     * @return
     */
    public static AssetDaoImpl getAssetDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (assetDaoImpl == null) { 
            synchronized (AssetDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (assetDaoImpl == null) {
                    assetDaoImpl = new AssetDaoImpl();
                }
            }
        }
        return assetDaoImpl;
    }
    
    /**
     * Method returns an ObservableList of all records in security_role table in database
     * @return
     * @throws DaoException
     */
    @Override
    public ObservableList<Asset> getAll() throws DaoException {
        ObservableList<Asset> assetList = FXCollections.observableArrayList();
        
        String query = "SELECT a.id, "
                             +"a.name, "
                             +"a.asset_number, "
                             +"a.serial_number, "
                             +"a.model_number, "
                             +"aType.id, "
                             +"aType.name, "
                             +"m.id, "
                             +"m.name, "
                             +"u.id, "
                             +"u.first_name, "
                             +"u.last_name, "
                             +"u.email, "
                             +"u.phone1 "
                      +"FROM asset AS a "
                      +"INNER JOIN asset_type AS aType ON a.asset_type_id = aType.id "
                      +"INNER JOIN mfg AS m ON a.mfg_id = m.id "
                      +"LEFT JOIN app_user u ON a.assigned_to_app_user_id = u.id ";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                int id = result.getInt("a.id");
                String name = result.getString("a.name");
                String assetNumber = result.getString("a.asset_number");
                String serialNumber = result.getString("a.serial_number");
                String modelNumber = result.getString("a.model_number");
                int assetTypeId = result.getInt("aType.id");
                String assetTypeName = result.getString("aType.name");
                int mfgId = result.getInt("m.id");
                String mfgName = result.getString("m.name");
                int assignedToAppUserId = result.getInt("u.id");
                String assignedAppsUserFirstName = result.getString("u.first_name");
                String assignedAppsUserLastName = result.getString("u.last_name");
                String assignedAppsUserEmail = result.getString("u.email");
                String assignedAppsUserPhone1 = result.getString("u.phone1");
                
                AssetType assetType = new AssetType(assetTypeId, assetTypeName);
                Mfg mfg =  new Mfg(mfgId, mfgName);
                AppUser assignedToAppUser = new AppUser(assignedToAppUserId, assignedAppsUserFirstName, 
                        assignedAppsUserLastName, assignedAppsUserEmail, assignedAppsUserPhone1);
                
                assetList.add(new Asset(id, name, assetNumber, assetType, modelNumber, serialNumber, mfg, assignedToAppUser));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Database error: Try again later.");
        }
        
        return assetList;
    }

    @Override
    public Asset getById(int id) throws DaoException, Exception {
        Asset asset;
        
        String query = "SELECT a.id, "
                             +"a.name, "
                             +"a.asset_number, "
                             +"a.serial_number, "
                             +"a.model_number, "
                             +"aType.id, "
                             +"aType.name, "
                             +"m.id, "
                             +"m.name, "
                             +"u.id, "
                             +"u.first_name, "
                             +"u.last_name, "
                             +"u.email, "
                             +"u.phone1 "
                      +"FROM asset AS a "
                      +"INNER JOIN asset_type AS aType ON a.asset_type_id = aType.id "
                      +"INNER JOIN mfg AS m ON a.mfg_id = m.id "
                      +"LEFT JOIN app_user u ON a.assigned_to_app_user_id = u.id "
                      +"WHERE a.id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int resultId = result.getInt("a.id");
                String name = result.getString("a.name");
                String assetNumber = result.getString("a.asset_number");
                String serialNumber = result.getString("a.serial_number");
                String modelNumber = result.getString("a.model_number");
                int assetTypeId = result.getInt("aType.id");
                String assetTypeName = result.getString("aType.name");
                int mfgId = result.getInt("m.id");
                String mfgName = result.getString("m.name");
                int assignedToAppUserId = result.getInt("u.id");
                String assignedAppsUserFirstName = result.getString("u.first_name");
                String assignedAppsUserLastName = result.getString("u.last_name");
                String assignedAppsUserEmail = result.getString("u.email");
                String assignedAppsUserPhone1 = result.getString("u.phone1");
                
                AssetType assetType = new AssetType(assetTypeId, assetTypeName);
                Mfg mfg =  new Mfg(mfgId, mfgName);
                AppUser assignedToAppUser = new AppUser(assignedToAppUserId, assignedAppsUserFirstName, 
                        assignedAppsUserLastName, assignedAppsUserEmail, assignedAppsUserPhone1);
                
                asset = new Asset(resultId, name, assetNumber, assetType, modelNumber, serialNumber, mfg, assignedToAppUser);
                result.close();
            } else {
                result.close();
                asset = new Asset(-1);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return asset;
    }

    @Override
    public void insert(Asset asset) throws DaoException, Exception {
        String query = "INSERT INTO asset (name, "
                                                 +"asset_number, "
                                                 +"asset_type_id, "
                                                 +"serial_number, "
                                                 +"model_number, "
                                                 +"mfg_id, "
                                                 +"assigned_to_app_user_id ) "
                      +"VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, asset.getName());
            stmt.setString(2, asset.getAssetNumber());
            stmt.setInt(3, asset.getAssetType().getId());
            stmt.setString(4, asset.getSerialNumber());
            stmt.setString(5, asset.getModelNumber());
            stmt.setInt(6, asset.getMfg().getId());
            stmt.setInt(7, asset.getAssignedToAppUser().getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(Asset asset) throws DaoException, Exception {
        String query = "UPDATE asset "
                      +"SET name = ?, "
                            +"asset_number = ?, "
                            +"asset_type_id = ?, "
                            +"serial_number = ?, "
                            +"model_number = ?, "
                            +"mfg_id = ?, "
                            +"assigned_to_app_user_id = ? "
                      +"WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, asset.getName());
            stmt.setString(2, asset.getAssetNumber());
            stmt.setInt(3, asset.getAssetType().getId());
            stmt.setString(4, asset.getSerialNumber());
            stmt.setString(5, asset.getModelNumber());
            stmt.setInt(6, asset.getMfg().getId());
            stmt.setInt(7, asset.getAssignedToAppUser().getId());
            stmt.setInt(8, asset.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(Asset asset) throws DaoException, Exception {
        String query = "DELETE FROM asset WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, asset.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Asset assigned to 1 or more tickets.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
}
