/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import sdtracker.DAO.TeamStatBoardDao;
import sdtracker.model.TeamStatBoard;

/**
 *
 * @author Tim Smith
 */
public class TeamStatBoardDbServiceManager {
    private TeamStatBoardDao teamStatBoardDao;
    private ExecutorService executor;
    
    private static volatile TeamStatBoardDbServiceManager INSTANCE;
    
    private TeamStatBoardDbServiceManager() {
        teamStatBoardDao = TeamStatBoardDao.getTeamStatBoardDao();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static TeamStatBoardDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (TeamStatBoardDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TeamStatBoardDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    public class GetTeamStatBoardService extends Service<TeamStatBoard> {
        public GetTeamStatBoardService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<TeamStatBoard> createTask() {
            return new Task<TeamStatBoard>() {
                @Override
                protected TeamStatBoard call() throws Exception {
                    int totalNotClosedTickets = teamStatBoardDao.getAllNotClosedTicketCount();
                    int totalOpenTickets = teamStatBoardDao.getAllOpenTicketCount();
                    int totalNotClosedBugs = teamStatBoardDao.getAllNotClosedBugCount();
                    int totalOpenBugs = teamStatBoardDao.getAllOpenBugCount();
                    TeamStatBoard teamStatBoard = new TeamStatBoard(
                            totalNotClosedTickets, totalOpenTickets, totalNotClosedBugs, totalOpenBugs);
                    
                    return teamStatBoard;
                }
            };
        }
        
    }
}
