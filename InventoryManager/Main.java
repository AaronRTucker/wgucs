/**
 * @author Aaron Tucker
 */

package InventoryManager;

import InventoryManager.Controllers.Controller;
import InventoryManager.DBHelper.JDBC;
import InventoryManager.Models.Inventory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application{


    /**
     * Starts the GUI program and loads the opening stage
     * @param primaryStage the primary stage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            JDBC.openConnection();
            primaryStage.setTitle("Inventory Manager");
            Inventory inventory = new Inventory();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Views/gui.fxml"));
            Controller controller = new Controller(inventory);
            loader.setController(controller);
            //Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("gui.fxml")));
            Scene scene = new Scene((Pane)loader.load(), 900,475);
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
