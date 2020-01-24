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
import sdtracker.model.Department;

/**
 *
 * @author Tim Smith
 */
public class DepartmentDaoImpl implements CrudDao<Department> {
    private static volatile DepartmentDaoImpl departmentDaoImpl;
    
    //Constructor
    private DepartmentDaoImpl() {
        // Prohibit multiple instances via reflection
        if (departmentDaoImpl != null) {
            throw new RuntimeException("DepartmentDaoImpl already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of DepartmentDaoImpl class, creating instance if not already created
     * @return
     */
    public static DepartmentDaoImpl getDepartmentDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (departmentDaoImpl == null) { 
            synchronized (DepartmentDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (departmentDaoImpl == null) {
                    departmentDaoImpl = new DepartmentDaoImpl();
                }
            }
        }
        return departmentDaoImpl;
    }
    
    /**
     * Method returns an ObservableList of all records in department table in database
     * @return
     * @throws DaoException
     */
    @Override
    public ObservableList<Department> getAll() throws DaoException {
        ObservableList<Department> departmentList = FXCollections.observableArrayList();
        
        String query = "SELECT dept.id, dept.name FROM department AS dept";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                int id = result.getInt("dept.id");
                String name = result.getString("dept.name");
                
                departmentList.add(new Department(id, name));
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return departmentList;
    }

    @Override
    public Department getById(int id) throws DaoException, Exception {
        Department department;
        
        String query = "SELECT dept.id, dept.name "
                        +"FROM department AS dept "
                        +"WHERE dept.id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int resultId = result.getInt("dept.id");
                String name = result.getString("dept.name");
                department = new Department(resultId, name);
                result.close();
            } else {
                result.close();
                department = new Department(-1, null);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return department;
    }
    
    public boolean checkForDuplicate(String name) throws DaoException, Exception {
        int recCount = 0;
        String query = "SELECT COUNT(*) AS recCount "
                        +"FROM department AS dept "
                        +"WHERE dept.name = ? ";
        
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
    public void insert(Department department) throws DaoException, Exception {
        String query = "INSERT INTO department (name) "
                      +"VALUES (?)";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, department.getName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(Department department) throws DaoException, Exception {
        String query = "UPDATE department "
                      +"SET name = ? "
                      +"WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, department.getName());
            stmt.setInt(2, department.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(Department department) throws DaoException, Exception {
        String query = "DELETE FROM department WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, department.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, Department assigned to 1 or more users.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
}
