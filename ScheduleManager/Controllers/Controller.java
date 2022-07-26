/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class Controller implements Initializable {



    //Constructor for new Controller object
    public Controller(){

    }




    /**
     * Called every time a screen is loaded
     * @param url the url
     * @param resourceBundle the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    public void setReturnController(Controller c){
    }

    /**
     * Loads a new scene with a given file, width, and height
     *
     * @param event  the action event
     * @param width  the width of the scene
     * @param height the height of the scene
     */
    //Private helper function
    //Handle switching between fxml file scenes
    protected void loadScene(Controller controller, ActionEvent event, String location, int width, int height, ResourceBundle bundle){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(location), bundle);      //absolute reference for file path of scene
            loader.setController(controller);
            Scene scene = new Scene(loader.load(), width, height);                                       //set width and height of scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }
}

