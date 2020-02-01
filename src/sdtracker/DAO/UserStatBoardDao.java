/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sdtracker.database.DatabaseMgr;
import sdtracker.model.AppUser;

/**
 *
 * @author Tim Smith
 */
public class UserStatBoardDao {
    private static volatile UserStatBoardDao userStatBoardDao;
    
    private UserStatBoardDao() {
        if (userStatBoardDao != null) {
            throw new RuntimeException("UserStatBoardDao already instantiated, use getInstance() of this class");
        }
    }
    
    public static UserStatBoardDao getUserStatBoardDao() {
        if (userStatBoardDao == null) {
            synchronized (UserStatBoardDao.class) {
                if (userStatBoardDao == null) {
                    userStatBoardDao = new UserStatBoardDao();
                }
            }
        }
        
        return userStatBoardDao;
    }
    
    public int getAllNotClosedTicketCount(AppUser appUser) throws DaoException {
        int count = -1;
        
        String query = "SELECT COUNT(*) AS recCount "
                      +"FROM ticket AS tick "
                      +"WHERE tick.ticket_status_id NOT IN (4, 5, 6) "
                      +  "AND tick.assigned_app_user_id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            
            stmt.setInt(1, appUser.getId());
            ResultSet result = stmt.executeQuery();
            
            if (result.next()) {
                count = result.getInt("recCount");
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Database error: Try again later");
        }
        
        return count;
    }
    
    public int getAllOpenTicketCount(AppUser appUser) throws DaoException {
        int count = -1;
        String query = "SELECT COUNT(*) AS recCount "
                      +"FROM ticket AS tick "
                      +"WHERE tick.ticket_status_id IN (2, 3) "
                      +  "AND tick.assigned_app_user_id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            
            stmt.setInt(1, appUser.getId());
            ResultSet result = stmt.executeQuery();
            
            if (result.next()) {
                count = result.getInt("recCount");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Database error: Try again later");
        }

        return count;
    }
    
    public int getAllNotClosedBugCount(AppUser appUser) throws DaoException {
        int count = -1;
        
        String query = "SELECT COUNT(*) AS recCount "
                      +"FROM bug AS b "
                      +"WHERE b.bug_status_id NOT IN (6, 7, 8) "
                      +  "AND b.assigned_app_user_id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            
            stmt.setInt(1, appUser.getId());
            ResultSet result = stmt.executeQuery();
            
            if (result.next()) {
                count = result.getInt("recCount");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Database error: Try again later");
        }
        
        return count;
    }
    
    public int getAllOpenBugCount(AppUser appUser) throws DaoException {
        int count = -1;
        
        String query = "SELECT COUNT(*) AS recCount "
                      +"FROM bug AS b "
                      +"WHERE b.bug_status_id IN (2, 3, 4, 5) "
                      +  "AND b.assigned_app_user_id = ? ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);) {
            
            stmt.setInt(1, appUser.getId());
            ResultSet result = stmt.executeQuery();
            
            if (result.next()) {
                count = result.getInt("recCount");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Database error: Try again later");
        }
        
        return count;
    }
}
