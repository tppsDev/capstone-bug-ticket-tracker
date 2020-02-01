/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.DAO;

import java.sql.*;
import sdtracker.database.DatabaseMgr;

/**
 *
 * @author Tim Smith
 */
public class TeamStatBoardDao {
    private static volatile TeamStatBoardDao teamStatBoardDao;
    
    private TeamStatBoardDao() {
        if (teamStatBoardDao != null) {
            throw new RuntimeException("TeamStatBoardDao already instantiated, use getInstance() of this class");
        }
    }
    
    public static TeamStatBoardDao getTeamStatBoardDao() {
        if (teamStatBoardDao == null) {
            synchronized (TeamStatBoardDao.class) {
                if (teamStatBoardDao == null) {
                    teamStatBoardDao = new TeamStatBoardDao();
                }
            }
        }
        
        return teamStatBoardDao;
    }
    
    public int getAllNotClosedTicketCount() throws DaoException {
        int count = -1;
        
        String query = "SELECT COUNT(*) AS recCount "
                      +"FROM ticket AS tick "
                      +"WHERE tick.ticket_status_id NOT IN (4, 5, 6) ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            
            if (result.next()) {
                count = result.getInt("recCount");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Database error: Try again later");
        }
        
        return count;
    }
    
    public int getAllOpenTicketCount() throws DaoException {
        int count = -1;
        
        String query = "SELECT COUNT(*) AS recCount "
                      +"FROM ticket AS tick "
                      +"WHERE tick.ticket_status_id IN (2, 3) ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            
            if (result.next()) {
                count = result.getInt("recCount");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Database error: Try again later");
        }
        
        return count;
    }
    
    public int getAllNotClosedBugCount() throws DaoException {
        int count = -1;
        
        String query = "SELECT COUNT(*) AS recCount "
                      +"FROM bug AS b "
                      +"WHERE b.bug_status_id NOT IN (6, 7, 8) ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            
            if (result.next()) {
                count = result.getInt("recCount");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DaoException("Database error: Try again later");
        }
        
        return count;
    }
    
    public int getAllOpenBugCount() throws DaoException {
        int count = -1;
        
        String query = "SELECT COUNT(*) AS recCount "
                      +"FROM bug AS b "
                      +"WHERE b.bug_status_id IN (2, 3, 4, 5) ";
        
        try (Connection conn = DatabaseMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();) {
            
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
