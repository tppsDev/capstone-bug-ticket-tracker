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
import sdtracker.model.AppUser;
import sdtracker.model.Department;
import sdtracker.model.SecurityRole;

/**
 *
 * @author Tim Smith
 */
public class AppUserDaoImpl implements CrudDao<AppUser> {
    private static volatile AppUserDaoImpl appUserDaoImpl;
    
    //Constructor
    private AppUserDaoImpl() {
        // Prohibit multiple instances via reflection
        if (appUserDaoImpl != null) {
            throw new RuntimeException("AppUserDaoImpl already implemented, use getInstance() of this class");
        }

    }
    
    /**
     * Method returns the only instance of AppUserDaoImpl class, creating instance if not already created
     * @return
     */
    public static AppUserDaoImpl getAppUserDaoImpl() {
        // check for instance twice to ensure thread-safe
        if (appUserDaoImpl == null) { 
            synchronized (AppUserDaoImpl.class) {
                // Second check for instance, if there is no instance create it
                if (appUserDaoImpl == null) {
                    appUserDaoImpl = new AppUserDaoImpl();
                }
            }
        }
        return appUserDaoImpl;
    }
    
    /**
     * Method returns an ObservableList of all records in app_user table in database
     * @return
     * @throws DaoException
     */
    @Override
    public ObservableList<AppUser> getAll() throws DaoException {
        ObservableList<AppUser> appUserList = FXCollections.observableArrayList();
        
        String query = "SELECT user.id, "
                             +"user.first_name, "
                             +"user.last_name, "
                             +"user.mid_initial, "
                             +"user.email, "
                             +"user.phone1, "
                             +"user.phone1_type, "
                             +"user.phone2, "
                             +"user.phone2_type, "
                             +"user.username, "
                             +"user.password, "
                             +"user.salt, "
                             +"user.job_title, "
                             +"user.courtesy_title, "
                             +"dept.id, "
                             +"dept.name, "
                             +"mgr.id, "
                             +"mgr.first_name, "
                             +"mgr.last_name, "
                             +"sRole.id, "
                             +"sRole.name "
                      +"FROM app_user AS user "
                      +"LEFT JOIN department AS dept ON user.dept_id = dept.id "
                      +"LEFT JOIN app_user AS mgr ON user.mgr_id = mgr.id "
                      +"LEFT JOIN security_role AS sRole ON user.security_role_id = sRole.id ";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            while (result.next()) {
                int id = result.getInt("user.id");
                String firstName = result.getString("user.first_name");
                String lastName = result.getString("user.last_name");
                String midInitial = result.getString("user.mid_initial");
                String email = result.getString("user.email");
                String phone1 = result.getString("user.phone1");
                String phone1Type = result.getString("user.phone1_type");
                String phone2 = result.getString("user.phone2");
                String phone2Type = result.getString("user.phone2_type");
                String username = result.getString("user.username");
                String password = result.getString("user.password");
                String salt = result.getString("user.salt");
                String jobTitle = result.getString("user.job_title");
                String courtesyTitle = result.getString("user.courtesy_title");
                int deptId = result.getInt("dept.id");
                String deptName = result.getString("dept.name");
                AppUser mgr;
                if (result.getString("mgr.first_name") != null) {
                    int mgrId = result.getInt("mgr.id");
                    String mgrFirstName = result.getString("mgr.first_name");
                    String mgrLastName = result.getString("mgr.last_name");
                    mgr = new AppUser(mgrId, mgrFirstName, mgrLastName);
                } else {
                    mgr = null;
                }
                int sRoleId = result.getInt("sRole.id");
                String sRoleName = result.getString("sRole.name");
                
                Department department = new Department(deptId, deptName);
                
                SecurityRole securityRole = new SecurityRole(sRoleId, sRoleName);
                                
                appUserList.add(new AppUser(id, firstName, lastName, midInitial, email, 
                        phone1, phone1Type, phone2, phone2Type, username, password, salt, jobTitle, 
                        courtesyTitle, department, mgr, securityRole));
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return appUserList;
    }

    @Override
    public AppUser getById(int id) throws DaoException, Exception {
        AppUser appUser;
        System.out.println(id);
        String query = "SELECT user.id, "
                             +"user.first_name, "
                             +"user.last_name, "
                             +"user.mid_initial, "
                             +"user.email, "
                             +"user.phone1, "
                             +"user.phone1_type, "
                             +"user.phone2, "
                             +"user.phone2_type, "
                             +"user.username, "
                             +"user.password, "
                             +"user.salt, "
                             +"user.job_title, "
                             +"user.courtesy_title, "
                             +"dept.id, "
                             +"dept.name, "
                             +"mgr.id, "
                             +"mgr.first_name, "
                             +"mgr.last_name, "
                             +"sRole.id, "
                             +"sRole.name "
                      +"FROM app_user AS user "
                      +"LEFT JOIN department AS dept ON user.dept_id = dept.id "
                      +"LEFT JOIN app_user AS mgr ON user.mgr_id = mgr.id "
                      +"LEFT JOIN security_role AS sRole ON user.security_role_id = sRole.id "
                      +"WHERE user.id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int resultId = result.getInt("user.id");
                String firstName = result.getString("user.first_name");
                String lastName = result.getString("user.last_name");
                String midInitial = result.getString("user.mid_initial");
                String email = result.getString("user.email");
                String phone1 = result.getString("user.phone1");
                String phone1Type = result.getString("user.phone1_type");
                String phone2 = result.getString("user.phone1");
                String phone2Type = result.getString("user.phone1_type");
                String username = result.getString("user.username");
                String password = result.getString("user.password");
                String salt = result.getString("user.salt");
                String jobTitle = result.getString("user.job_title");
                String courtesyTitle = result.getString("user.courtesy_title");
                int deptId = result.getInt("dept.id");
                String deptName = result.getString("dept.name");
                int mgrId = result.getInt("mgr.id");
                String mgrFirstName = result.getString("mgr.first_name");
                String mgrLastName = result.getString("mgr.last_name");
                int sRoleId = result.getInt("sRole.id");
                String sRoleName = result.getString("sRole.name");
                
                Department department = new Department(deptId, deptName);
                AppUser mgr = new AppUser(mgrId, mgrFirstName, mgrLastName);
                SecurityRole securityRole = new SecurityRole(sRoleId, sRoleName);
                                
                appUser = new AppUser(resultId, firstName, lastName, midInitial, email, 
                        phone1, phone1Type, phone2, phone2Type, username, password, salt, jobTitle, 
                        courtesyTitle, department, mgr, securityRole);
                
                result.close();
            } else {
                result.close();
                appUser = new AppUser(-1);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DaoException("Database error: Try again later.");
        }
        
        return appUser;
    }
    
    public AppUser getByUsername(String username) throws DaoException, Exception {
        AppUser appUser;
        
        String query = "SELECT user.id, "
                             +"user.first_name, "
                             +"user.last_name, "
                             +"user.mid_initial, "
                             +"user.email, "
                             +"user.phone1, "
                             +"user.phone1_type, "
                             +"user.phone2, "
                             +"user.phone2_type, "
                             +"user.username, "
                             +"user.password, "
                             +"user.salt, "
                             +"user.job_title, "
                             +"user.courtesy_title, "
                             +"dept.id, "
                             +"dept.name, "
                             +"mgr.id, "
                             +"mgr.first_name, "
                             +"mgr.last_name, "
                             +"sRole.id, "
                             +"sRole.name "
                      +"FROM app_user AS user "
                      +"LEFT JOIN department AS dept ON user.dept_id = dept.id "
                      +"LEFT JOIN app_user AS mgr ON user.mgr_id = mgr.id "
                      +"LEFT JOIN security_role AS sRole ON user.security_role_id = sRole.id "
                      +"WHERE user.username = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, username);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int id = result.getInt("user.id");
                String firstName = result.getString("user.first_name");
                String lastName = result.getString("user.last_name");
                String midInitial = result.getString("user.mid_initial");
                String email = result.getString("user.email");
                String phone1 = result.getString("user.phone1");
                String phone1Type = result.getString("user.phone1_type");
                String phone2 = result.getString("user.phone1");
                String phone2Type = result.getString("user.phone1_type");
                String resultUsername = result.getString("user.username");
                String password = result.getString("user.password");
                String salt = result.getString("user.salt");
                String jobTitle = result.getString("user.job_title");
                String courtesyTitle = result.getString("user.courtesy_title");
                int deptId = result.getInt("dept.id");
                String deptName = result.getString("dept.name");
                int mgrId = result.getInt("mgr.id");
                String mgrFirstName = result.getString("mgr.first_name");
                String mgrLastName = result.getString("mgr.last_name");
                int sRoleId = result.getInt("sRole.id");
                String sRoleName = result.getString("sRole.name");
                
                Department department = new Department(deptId, deptName);
                AppUser mgr = new AppUser(mgrId, mgrFirstName, mgrLastName);
                SecurityRole securityRole = new SecurityRole(sRoleId, sRoleName);
                                
                appUser = new AppUser(id, firstName, lastName, midInitial, email, 
                        phone1, phone1Type, phone2, phone2Type, resultUsername, password, salt, jobTitle, 
                        courtesyTitle, department, mgr, securityRole);
                
                result.close();
            } else {
                result.close();
                appUser = new AppUser(-1);
            }
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
        
        return appUser;
    }
    
    public boolean checkForDuplicate(String username) throws DaoException {
        int recCount = 0;
        String query = "SELECT COUNT(*) AS recCount "
                      +"FROM app_user AS user "
                      +"WHERE user.username = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, username);
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
    public void insert(AppUser appUser) throws DaoException, Exception {
        String query = "INSERT INTO app_user (first_name, "
                                           +"last_name, "
                                           +"mid_initial, "
                                           +"email, "
                                           +"phone1, "
                                           +"phone1_type, "
                                           +"phone2, "
                                           +"phone2_type, "
                                           +"username, "
                                           +"password, "
                                           +"job_title, "
                                           +"courtesy_title, "
                                           +"dept_id, "
                                           +"mgr_id, "
                                           +"security_role_id, "
                                           +"salt) "
                      +"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, appUser.getFirstName());
            stmt.setString(2, appUser.getLastName());
            stmt.setString(3, appUser.getMidInitial());
            stmt.setString(4, appUser.getEmail());
            stmt.setString(5, appUser.getPhone1());
            stmt.setString(6, appUser.getPhone1Type());
            stmt.setString(7, appUser.getPhone2());
            stmt.setString(8, appUser.getPhone2Type());
            stmt.setString(9, appUser.getUsername());
            stmt.setString(10, appUser.getPassword());
            stmt.setString(11, appUser.getJobTitle());
            stmt.setString(12, appUser.getCourtesyTitle());
            stmt.setInt(13, appUser.getDepartment().getId());
            if (appUser.getManager() == null) {
                stmt.setNull(14, Types.INTEGER);
            } else {
                stmt.setInt(14, appUser.getManager().getId());
            }
            stmt.setInt(15, appUser.getSecurityRole().getId());
            stmt.setString(16, appUser.getSalt());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void update(AppUser appUser) throws DaoException, Exception {
        String query = "UPDATE app_user SET first_name = ?, "
                                         +"last_name = ?, "
                                           +"mid_initial = ?, "
                                           +"email = ?, "
                                           +"phone1 = ?, "
                                           +"phone1_type = ?, "
                                           +"phone2 = ?, "
                                           +"phone2_type = ?, "
                                           +"username = ?, "
                                           +"password = ?, "
                                           +"job_title = ?, "
                                           +"courtesy_title = ?, "
                                           +"dept_id = ?, "
                                           +"mgr_id = ?, "
                                           +"security_role_id = ?, "
                                           +"salt = ? "
                      +"WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, appUser.getFirstName());
            stmt.setString(2, appUser.getLastName());
            stmt.setString(3, appUser.getMidInitial());
            stmt.setString(4, appUser.getEmail());
            stmt.setString(5, appUser.getPhone1());
            stmt.setString(6, appUser.getPhone1Type());
            stmt.setString(7, appUser.getPhone2());
            stmt.setString(8, appUser.getPhone2Type());
            stmt.setString(9, appUser.getUsername());
            stmt.setString(10, appUser.getPassword());
            stmt.setString(11, appUser.getJobTitle());
            stmt.setString(12, appUser.getCourtesyTitle());
            stmt.setInt(13, appUser.getDepartment().getId());
            if (appUser.getManager() == null) {
                stmt.setNull(14, Types.INTEGER);
            } else {
                stmt.setInt(14, appUser.getManager().getId());
            }
            stmt.setInt(15, appUser.getSecurityRole().getId());
            stmt.setString(16, appUser.getSalt());
            stmt.setInt(17, appUser.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Database error: Try again later.");
        }
    }
    
    public void updatePassword(AppUser appUser) throws DaoException {
        String query = "UPDATE app_user SET password = ?, "
                                          +"salt = ? "
                      +"WHERE id = ?";
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, appUser.getPassword());
            stmt.setString(2, appUser.getSalt());
            stmt.setInt(3, appUser.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Database error: Try again later.");
        }
    }

    @Override
    public void delete(AppUser appUser) throws DaoException, Exception {
        String query = "DELETE FROM appUser WHERE id = ?";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, appUser.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            if (ex.getSQLState().startsWith("23")) {
                throw new DaoException("Cannot delete, AppUser assigned to 1 or more tickets/bug reports.");
            }
            throw new DaoException("Database error: Try again later.");
        }
    }
}
