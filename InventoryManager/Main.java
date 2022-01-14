package InventoryManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;


public class Main extends Application{




    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            primaryStage.setTitle("Inventory Manager");
            Inventory inventory = new Inventory();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
            Controller controller = new Controller(inventory);
            loader.setController(controller);
            //Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("gui.fxml")));
            Scene scene = new Scene((Pane)loader.load(), 900,475);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e){
            e.printStackTrace();    //runs when there is no gui.fxml to load
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
