/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sdtracker.DAO.AppUserDaoImpl;
import sdtracker.database.DatabaseMgr;
import sdtracker.model.AppUser;
import sdtracker.model.Department;
import sdtracker.model.SecurityRole;
import sdtracker.utility.BugNumberGenerator;
import sdtracker.utility.TicketNumberGenerator;

/**
 *
 * @author Tim Smith
 */
public class SDTracker extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("view_controller/CurrentBugBreakdownReport.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        
//        String[] titleStrings = {
//            "Password Locked",
//            "Blue Screen Of Death",
//            "Cracked display",
//            "Microsoft Office upgrade",
//            "Network outage",
//            "PC won't boot",
//            "Spam filter needs adjusted",
//            "New user request"
//        };
        
//        String[] titleStrings = {
//            "Receiving NullPointerExecption",
//            "Calculation error",
//            "Increase font size",
//            "Reporting in wrong number format",
//            "Fails on save",
//            "Slow response time",
//            "Add a code status",
//            "Remove priority code"
//        };
//        
//        String[] descStrings = {
//            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Aliquet bibendum enim facilisis gravida. In iaculis nunc sed augue lacus viverra vitae congue eu.",
//            "Semper feugiat nibh sed pulvinar proin gravida. Sed adipiscing diam donec adipiscing tristique. Aliquet enim tortor at auctor urna. Mus mauris vitae ultricies leo integer.",
//            "Ac turpis egestas maecenas pharetra convallis posuere morbi. Turpis tincidunt id aliquet risus. Mauris in aliquam sem fringilla ut morbi.",
//            "Eget felis eget nunc lobortis mattis aliquam faucibus. Facilisis magna etiam tempor orci eu lobortis elementum. Auctor urna nunc id cursus metus. Ultrices eros in cursus turpis massa. Risus nec feugiat in fermentum.",
//            "Dictum at tempor commodo ullamcorper a lacus. Purus sit amet luctus venenatis. Adipiscing diam donec adipiscing tristique risus nec feugiat in.",
//            "Ut ornare lectus sit amet est placerat in. Quis eleifend quam adipiscing vitae proin sagittis.",
//            "Mattis aliquam faucibus purus in massa tempor. Hendrerit dolor magna eget est lorem ipsum dolor. Tincidunt praesent semper feugiat nibh sed pulvinar.",
//            "Mi bibendum neque egestas congue quisque. Adipiscing diam donec adipiscing tristique risus nec feugiat. Amet nisl suscipit adipiscing bibendum est ultricies integer quis auctor."
//        };
//        
//        final int MIN_PROD_ID = 1;
//        final int MAX_PROD_ID = 1;
//        
//        final int MIN_USER_ID = 3;
//        final int MAX_USER_ID = 11;
//        
//        final int MIN_CONT_ID = 1;
//        final int MAX_CONT_ID = 4;
//        
//        final int MIN_PRIORITY_ID = 1;
//        final int MAX_PRIORITY_ID = 4;
//        
//        final int MIN_STATUS_ID = 1;
//        final int MAX_STATUS_ID = 8;
//        
//        String query = "INSERT INTO bug (title, "
//                                          +"description,  "
//                                          +"product_id, "
//                                          +"contact_id, "
//                                          +"assigned_app_user_id, "
//                                          +"created_timestamp, "
//                                          +"created_by_user_id, "
//                                          +"bug_status_id, "
//                                          +"bug_number, "
//                                          +"bug_priority_id, "
//                                          +"last_updated_timestamp, "
//                                          +"last_updated_by_user_id )"
//                      +"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
//        System.out.println("Starting record generation . . .");
//        for (int i = 0; i < 50; i++) {
//            System.out.println("\tAdding record " + (i + 1));
//            try(Connection conn = DatabaseMgr.getConnection();
//                PreparedStatement stmt = conn.prepareStatement(query);) {
//                LocalDateTime createdDate = getRandomCreatedDate();
//                stmt.setString(1, titleStrings[getRandomInt(7, 0)]);
//                stmt.setString(2, descStrings[getRandomInt(7, 0)]);
//                stmt.setInt(3, getRandomInt(MAX_PROD_ID, MIN_PROD_ID));
//                stmt.setInt(4, getRandomInt(MAX_CONT_ID, MIN_CONT_ID));
//                if(getCoinFlipResult() && getCoinFlipResult()) {
//                    stmt.setNull(5, Types.INTEGER);
//                    stmt.setInt(8, 1);
//                    stmt.setNull(11, Types.TIMESTAMP);
//                    stmt.setNull(12, Types.VARCHAR);
//                } else {
//                    stmt.setInt(5, getRandomInt(MAX_USER_ID, MIN_USER_ID));
//                    stmt.setInt(8, getRandomInt(MAX_STATUS_ID, MIN_STATUS_ID));
//                    stmt.setTimestamp(11, Timestamp.valueOf(createdDate.plusHours(getRandomInt(72, 3))));
//                    stmt.setInt(12, getRandomInt(MAX_USER_ID, MIN_USER_ID));
//                }
//                stmt.setTimestamp(6, Timestamp.valueOf(createdDate));
//                stmt.setInt(7, getRandomInt(MAX_USER_ID, MIN_USER_ID));
//                stmt.setString(9, BugNumberGenerator.generateBugNumber());
//                stmt.setInt(10, getRandomInt(MAX_PRIORITY_ID, MIN_PRIORITY_ID));
//                stmt.executeUpdate();
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        }
//        
//        System.out.println("Generation complete!");
    }
    
//    public boolean getCoinFlipResult() {
//        return getRandomInt(2, 1) == 1;
//    }
//    
//    public int getRandomInt(int max, int min) {
//        return ThreadLocalRandom.current().nextInt(min, max + 1);
//    }
//    
//    public LocalDateTime getRandomCreatedDate() {
//        return LocalDateTime.of(2020, Month.JANUARY, getRandomInt(31, 1), getRandomInt(18, 7), getRandomInt(59, 0), getRandomInt(59, 0));
//    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        System.exit(0);
    }
    
}
