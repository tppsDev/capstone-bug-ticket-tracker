/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import sdtracker.model.CurrentBugBreakdownReport;
import sdtracker.model.CurrentTicketBreakdownReport;

/**
 *
 * @author Tim Smith
 */
public class ReportQueryServiceManager {
    private static volatile ReportQueryServiceManager INSTANCE;
    
    private ExecutorService executor;
    
    private ReportQueryServiceManager() {
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static ReportQueryServiceManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ReportQueryServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ReportQueryServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    public class GenerateCurrentTicketBreakdownReportService extends Service<CurrentTicketBreakdownReport> {

        @Override
        protected Task<CurrentTicketBreakdownReport> createTask() {
            return new Task<CurrentTicketBreakdownReport>() {
                @Override
                protected CurrentTicketBreakdownReport call() throws Exception {
                    CurrentTicketBreakdownReport report = new CurrentTicketBreakdownReport();
                    String statusCountQuery = "SELECT ticket_status_id AS stat, COUNT(*) AS statCount "
                                  +"FROM ticket "
                                  +"WHERE ticket_status_id NOT IN(4, 6) "
                                  +"GROUP BY ticket_status_id ";
                    try (Connection conn = DatabaseMgr.getConnection();
                            PreparedStatement stmt = conn.prepareStatement(statusCountQuery);
                            ResultSet result = stmt.executeQuery();) {
                        while (result.next()) {
                            switch (result.getInt("stat")) {
                                case 1:
                                    report.setNewTicketCount(result.getInt("statCount"));
                                    break;
                                case 2:
                                    report.setOpenTicketCount(result.getInt("statCount"));
                                    break;
                                case 3:
                                    report.setResolvedTicketCount(result.getInt("statCount"));
                                    break;
                                case 5:
                                    report.setOnHoldTicketCount(result.getInt("statCount"));
                                    break;
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    
                    String priorityCountQuery = "SELECT ticket_priority_id AS stat, COUNT(*) AS statCount "
                                  +"FROM ticket "
                                  +"WHERE ticket_status_id NOT IN(4, 6) "
                                  +"GROUP BY ticket_priority_id ";
                    try (Connection conn = DatabaseMgr.getConnection();
                            PreparedStatement stmt = conn.prepareStatement(priorityCountQuery);
                            ResultSet result = stmt.executeQuery();) {
                        while (result.next()) {
                            switch (result.getInt("stat")) {
                                case 1:
                                    report.setCriticalTicketCount(result.getInt("statCount"));
                                    break;
                                case 2:
                                    report.setUrgentTicketCount(result.getInt("statCount"));
                                    break;
                                case 3:
                                    report.setNormalTicketCount(result.getInt("statCount"));
                                    break;
                                case 4:
                                    report.setLowTicketCount(result.getInt("statCount"));
                                    break;
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    
                    String statusAgingQuery = "SELECT ticket_status_id AS stat, " 
                                                     + "AVG(timestampdiff(SECOND, created_timestamp, NOW())) AS statAvg "
                                               +"FROM ticket "
                                               +"WHERE ticket_status_id NOT IN(4, 6) "
                                               +"GROUP BY ticket_status_id ";
                    try (Connection conn = DatabaseMgr.getConnection();
                            PreparedStatement stmt = conn.prepareStatement(statusAgingQuery);
                            ResultSet result = stmt.executeQuery();) {
                        while (result.next()) {
                            switch (result.getInt("stat")) {
                                case 1:
                                    report.setNewTicketAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 2:
                                    report.setOpenTicketAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 3:
                                    report.setResolvedTicketAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 5:
                                    report.setOnHoldTicketAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    
                    String priorityAgingQuery = "SELECT ticket_priority_id AS stat, " 
                                                     + "AVG(timestampdiff(SECOND, created_timestamp, NOW())) AS statAvg "
                                               +"FROM ticket "
                                               +"WHERE ticket_status_id NOT IN(4, 6) "
                                               +"GROUP BY ticket_priority_id ";
                    try (Connection conn = DatabaseMgr.getConnection();
                            PreparedStatement stmt = conn.prepareStatement(priorityAgingQuery);
                            ResultSet result = stmt.executeQuery();) {
                        while (result.next()) {
                            switch (result.getInt("stat")) {
                                case 1:
                                    report.setCriticalTicketAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 2:
                                    report.setUrgentTicketAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 3:
                                    report.setNormalTicketAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 4:
                                    report.setLowTicketAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    
                    return report;
                }
                
            };
        }
        
    }
    
    public class GenerateCurrentBugBreakdownReportService extends Service<CurrentBugBreakdownReport> {

        @Override
        protected Task<CurrentBugBreakdownReport> createTask() {
            return new Task<CurrentBugBreakdownReport>() {
                @Override
                protected CurrentBugBreakdownReport call() throws Exception {
                    CurrentBugBreakdownReport report = new CurrentBugBreakdownReport();
                    String statusCountQuery = "SELECT bug_status_id AS stat, COUNT(*) AS statCount "
                                  +"FROM bug "
                                  +"WHERE bug_status_id NOT IN(6, 8) "
                                  +"GROUP BY bug_status_id ";
                    try (Connection conn = DatabaseMgr.getConnection();
                            PreparedStatement stmt = conn.prepareStatement(statusCountQuery);
                            ResultSet result = stmt.executeQuery();) {
                        while (result.next()) {
                            switch (result.getInt("stat")) {
                                case 1:
                                    report.setNewBugCount(result.getInt("statCount"));
                                    break;
                                case 2:
                                    report.setOpenBugCount(result.getInt("statCount"));
                                    break;
                                case 3:
                                    report.setFixInDevelopmentBugCount(result.getInt("statCount"));
                                    break;
                                case 4:
                                    report.setFixPendingTestingBugCount(result.getInt("statCount"));
                                    break;
                                case 5:
                                    report.setResolvedBugCount(result.getInt("statCount"));
                                    break;
                                case 7:
                                    report.setOnHoldBugCount(result.getInt("statCount"));
                                    break;
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    
                    String priorityCountQuery = "SELECT bug_priority_id AS stat, COUNT(*) AS statCount "
                                  +"FROM bug "
                                  +"WHERE bug_status_id NOT IN (6, 8) "
                                  +"GROUP BY bug_priority_id ";
                    try (Connection conn = DatabaseMgr.getConnection();
                            PreparedStatement stmt = conn.prepareStatement(priorityCountQuery);
                            ResultSet result = stmt.executeQuery();) {
                        while (result.next()) {
                            switch (result.getInt("stat")) {
                                case 1:
                                    report.setCriticalBugCount(result.getInt("statCount"));
                                    break;
                                case 2:
                                    report.setUrgentBugCount(result.getInt("statCount"));
                                    break;
                                case 3:
                                    report.setNormalBugCount(result.getInt("statCount"));
                                    break;
                                case 4:
                                    report.setLowBugCount(result.getInt("statCount"));
                                    break;
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    
                    String statusAgingQuery = "SELECT bug_status_id AS stat, " 
                                                     + "AVG(timestampdiff(SECOND, created_timestamp, NOW())) AS statAvg "
                                               +"FROM bug "
                                               +"WHERE bug_status_id NOT IN (6, 8) "
                                               +"GROUP BY bug_status_id ";
                    try (Connection conn = DatabaseMgr.getConnection();
                            PreparedStatement stmt = conn.prepareStatement(statusAgingQuery);
                            ResultSet result = stmt.executeQuery();) {
                        while (result.next()) {
                            switch (result.getInt("stat")) {
                                case 1:
                                    report.setNewBugAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 2:
                                    report.setOpenBugAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 3:
                                    report.setFixInDevelopmentBugAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 4:
                                    report.setFixPendingTestingBugAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 5:
                                    report.setResolvedBugAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 7:
                                    report.setOnHoldBugAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    
                    String priorityAgingQuery = "SELECT bug_priority_id AS stat, " 
                                                     + "AVG(timestampdiff(SECOND, created_timestamp, NOW())) AS statAvg "
                                               +"FROM bug "
                                               +"WHERE bug_status_id NOT IN (6, 8) "
                                               +"GROUP BY bug_priority_id ";
                    try (Connection conn = DatabaseMgr.getConnection();
                            PreparedStatement stmt = conn.prepareStatement(priorityAgingQuery);
                            ResultSet result = stmt.executeQuery();) {
                        while (result.next()) {
                            switch (result.getInt("stat")) {
                                case 1:
                                    report.setCriticalBugAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 2:
                                    report.setUrgentBugAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 3:
                                    report.setNormalBugAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                                case 4:
                                    report.setLowBugAgeInSeconds(result.getDouble("statAvg"));
                                    break;
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    
                    return report;
                }
                
            };
        }
        
    }
}
