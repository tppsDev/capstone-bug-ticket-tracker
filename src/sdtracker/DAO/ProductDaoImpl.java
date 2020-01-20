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
import sdtracker.model.Product;

/**
 *
 * @author Tim Smith
 */
public class ProductDaoImpl implements CrudDao<Product> {
    private static volatile ProductDaoImpl productDaoImpl;
    
    //Constructor
    private ProductDaoImpl() {
        // Prohibit multiple instances via reflection
        if (productDaoImpl != null) {
            throw new RuntimeException("ProductDaoImpl already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of ProductDaoImpl class, creating instance if not already created
     * @return
     */
    public static ProductDaoImpl getProductDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (productDaoImpl == null) { 
            synchronized (ProductDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (productDaoImpl == null) {
                    productDaoImpl = new ProductDaoImpl();
                }
            }
        }
        return productDaoImpl;
    }
    
    /**
     * Method returns an ObservableList of all records in product table in database
     * @return
     * @throws DaoException
     */
    @Override
    public ObservableList<Product> getAll() throws DaoException {
        ObservableList<Product> productList = FXCollections.observableArrayList();
        
        String query = "SELECT prod.id, prod.name, prod.version FROM product AS prod";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                int id = result.getInt("prod.id");
                String name = result.getString("prod.name");
                String version = result.getString("prod.version");
                
                productList.add(new Product(id, name, version));
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return productList;
    }

    @Override
    public Product getById(int id) throws DaoException, Exception {
        Product product;
        
        String query = "SELECT prod.id, prod.name, prod.version "
                        +"FROM product AS prod "
                        +"WHERE prod.id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int resultId = result.getInt("prod.id");
                String name = result.getString("prod.name");
                String version = result.getString("prod.version");
                
                product = new Product(resultId, name, version);
                
                result.close();
            } else {
                result.close();
                product = new Product(-1, null, null);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return product;
    }

    @Override
    public void insert(Product product) throws DaoException, Exception {
        String query = "INSERT INTO product (name, version) "
                      +"VALUES (?, ?)";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getVersion());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(Product product) throws DaoException, Exception {
        String query = "UPDATE product "
                      +"SET name = ?, version = ? "
                      +"WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getVersion());
            stmt.setInt(3, product.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(Product product) throws DaoException, Exception {
        String query = "DELETE FROM product WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, product.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Product assigned to 1 or more users.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
}
