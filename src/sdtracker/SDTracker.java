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
        
        Parent root = FXMLLoader.load(getClass().getResource("view_controller/Login.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("view_controller/AssetForm.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        System.exit(0);
    }
    
}
