/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import sdtracker.DAO.UserStatBoardDao;
import sdtracker.model.AppUser;
import sdtracker.model.UserStatBoard;

/**
 *
 * @author Tim Smith
 */
public class UserStatBoardDbServiceManager {
    private UserStatBoardDao userStatBoardDao;
    private ExecutorService executor;
    private AppUser appUser;
    
    private static volatile UserStatBoardDbServiceManager INSTANCE;
    
    private UserStatBoardDbServiceManager() {
        userStatBoardDao = UserStatBoardDao.getUserStatBoardDao();
        executor = Executors.newFixedThreadPool(1);
    }
    
    public static UserStatBoardDbServiceManager getServiceManager() {
        if (INSTANCE == null) {
            synchronized (UserStatBoardDbServiceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserStatBoardDbServiceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
    
    public class GetUserStatBoardService extends Service<UserStatBoard> {
        public GetUserStatBoardService() {
            super();
            this.setExecutor(executor);
        }

        @Override
        protected Task<UserStatBoard> createTask() {
            return new Task<UserStatBoard>() {
                @Override
                protected UserStatBoard call() throws Exception {
                    int totalNotClosedTickets = userStatBoardDao.getAllNotClosedTicketCount(appUser);
                    int totalOpenTickets = userStatBoardDao.getAllOpenTicketCount(appUser);
                    int totalNotClosedBugs = userStatBoardDao.getAllNotClosedBugCount(appUser);
                    int totalOpenBugs = userStatBoardDao.getAllOpenBugCount(appUser);
                    
                    UserStatBoard userStatBoard = new UserStatBoard(
                            totalNotClosedTickets, totalOpenTickets, totalNotClosedBugs, totalOpenBugs);

                    return userStatBoard;
                }
            };
        }
        
    }
}
