/**
 * @author Aaron Tucker
 */

package ScheduleManager;

import ScheduleManager.Controllers.*;
import ScheduleManager.DBHelper.JDBC;
import ScheduleManager.Models.Schedule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;


public class Main extends Application{


    /**
     * Starts the GUI program and loads the opening stage
     * @param primaryStage the primary stage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        try {

            Locale currentLocale = Locale.getDefault();
            //currentLocale = new Locale("fr");         //force language to french
            ResourceBundle bundle = ResourceBundle.getBundle("ScheduleManager.Resources.language", currentLocale);

            JDBC.openConnection();
            if(currentLocale.getLanguage() == "fr"){
                primaryStage.setTitle("Gestionnaire d'Horaire");
            } else {
                primaryStage.setTitle("Schedule Manager");
            }
            System.out.println(ZoneId.systemDefault());
            Schedule schedule = new Schedule();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Views/userLogin.fxml"), bundle);
            LoginController controller = new LoginController();
            loader.setController(controller);
            //Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("gui.fxml")));
            Scene scene = new Scene((Pane)loader.load(), 600,375);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e){
            e.printStackTrace();    //runs when there is no gui.fxml to load
            JDBC.closeConnection();
        }
    }

    /**
     * Javadoc located in root folder of project zip file
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
