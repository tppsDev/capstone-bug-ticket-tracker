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
import sdtracker.model.AssetType;

/**
 *
 * @author Tim Smith
 */
public class AssetTypeDaoImpl implements CrudDao<AssetType>{
    private static volatile AssetTypeDaoImpl assetTypeDaoImpl;
    
    //Constructor
    private AssetTypeDaoImpl() {
        // Prohibit multiple instances via reflection
        if (assetTypeDaoImpl != null) {
            throw new RuntimeException("AssetTypeDaoImpl already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of AssetTypeDaoImpl class, creating instance if not already created
     * @return
     */
    public static AssetTypeDaoImpl getAssetTypeDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (assetTypeDaoImpl == null) { 
            synchronized (AssetTypeDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (assetTypeDaoImpl == null) {
                    assetTypeDaoImpl = new AssetTypeDaoImpl();
                }
            }
        }
        return assetTypeDaoImpl;
    }
    
    /**
     * Method returns an ObservableList of all records in asset_type table in database
     * @return
     * @throws DaoException
     */
    @Override
    public ObservableList<AssetType> getAll() throws DaoException {
        ObservableList<AssetType> assetTypeList = FXCollections.observableArrayList();
        
        String query = "SELECT atype.id, atype.name FROM asset_type AS atype";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                int id = result.getInt("atype.id");
                String name = result.getString("atype.name");
                
                assetTypeList.add(new AssetType(id, name));
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return assetTypeList;
    }

    @Override
    public AssetType getById(int id) throws DaoException, Exception {
        AssetType assetType;
        
        String query = "SELECT atype.id, atype.name "
                        +"FROM asset_type AS atype "
                        +"WHERE atype.id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int resultId = result.getInt("atype.id");
                String name = result.getString("atype.name");
                assetType = new AssetType(resultId, name);
                result.close();
            } else {
                result.close();
                assetType = new AssetType(-1, null);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return assetType;
    }

    @Override
    public void insert(AssetType assetType) throws DaoException, Exception {
        String query = "INSERT INTO asset_type (name) "
                      +"VALUES (?)";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, assetType.getName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(AssetType assetType) throws DaoException, Exception {
        String query = "UPDATE asset_type "
                      +"SET name = ? "
                      +"WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, assetType.getName());
            stmt.setInt(2, assetType.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(AssetType assetType) throws DaoException, Exception {
        String query = "DELETE FROM asset_type WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, assetType.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Asset Type assigned to 1 or more assets.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
    
    public boolean checkForDuplicate(String name) throws DaoException {
        int recCount = 0;
        String query = "SELECT COUNT(*) AS recCount "
                        +"FROM asset_type AS atype "
                        +"WHERE atype.name = ? ";
        
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
}
