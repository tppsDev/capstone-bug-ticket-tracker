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
import sdtracker.model.Mfg;

/**
 *
 * @author Tim Smith
 */
public class MfgDaoImpl implements CrudDao<Mfg> {
    private static volatile MfgDaoImpl mfgDaoImpl;
    
    //Constructor
    private MfgDaoImpl() {
        // Prohibit multiple instances via reflection
        if (mfgDaoImpl != null) {
            throw new RuntimeException("MfgDaoImpl already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of MfgDaoImpl class, creating instance if not already created
     * @return
     */
    public static MfgDaoImpl getMfgDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (mfgDaoImpl == null) { 
            synchronized (MfgDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (mfgDaoImpl == null) {
                    mfgDaoImpl = new MfgDaoImpl();
                }
            }
        }
        return mfgDaoImpl;
    }
    
    /**
     * Method returns an ObservableList of all records in mfg table in database
     * @return
     * @throws DaoException
     */
    @Override
    public ObservableList<Mfg> getAll() throws DaoException {
        ObservableList<Mfg> mfgList = FXCollections.observableArrayList();
        
        String query = "SELECT id, name FROM mfg ";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                int id = result.getInt("id");
                String name = result.getString("name");
                
                mfgList.add(new Mfg(id, name));
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return mfgList;
    }

    @Override
    public Mfg getById(int id) throws DaoException, Exception {
        Mfg mfg;
        
        String query = "SELECT id, name "
                        +"FROM mfg "
                        +"WHERE id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int resultId = result.getInt("id");
                String name = result.getString("name");
                mfg = new Mfg(resultId, name);
                result.close();
            } else {
                result.close();
                mfg = new Mfg(-1, null);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return mfg;
    }

    @Override
    public void insert(Mfg mfg) throws DaoException, Exception {
        String query = "INSERT INTO mfg (name) "
                      +"VALUES (?)";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, mfg.getName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(Mfg mfg) throws DaoException, Exception {
        String query = "UPDATE mfg "
                      +"SET name = ? "
                      +"WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, mfg.getName());
            stmt.setInt(2, mfg.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(Mfg mfg) throws DaoException, Exception {
        String query = "DELETE FROM mfg WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, mfg.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Manufacturer assigned to 1 or more assets.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
}
