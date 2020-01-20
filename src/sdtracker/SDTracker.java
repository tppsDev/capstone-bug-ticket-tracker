/*
 * Project written by: Tim Smith
 * 
 */
package sdtracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sdtracker.DAO.AppUserDaoImpl;
import sdtracker.model.AppUser;
import sdtracker.model.Department;
import sdtracker.model.SecurityRole;

/**
 *
 * @author Tim Smith
 */
public class SDTracker extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        //AppUser user = new AppUser(0, "Ben", "Kenobi", null, "oldben@jedicouncil.com", "5555550001", "Cell", 
//                                    null, null, "test", "test", "salt", "Jedi Master", "Mr", 
//                                    new Department(6, "Executive Leadership"), null, new SecurityRole(4, "Admin"));
        
//        AppUserDaoImpl auDaoImpl = AppUserDaoImpl.getAppUserDaoImpl();
//        
//
//        AppUser user =auDaoImpl.getById(3);
//        
//        System.out.println(user);
//
//        
//        System.out.println(user.checkPassword("test"));
        
        //user.setEncryptedPassword(user.getPassword());
        
        //Parent root = FXMLLoader.load(getClass().getResource("view_controller/Login.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("view_controller/ContactForm.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
