/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;



public abstract class Controller implements Initializable {

    @FXML private Label localTime;
    @FXML private Label easternTime;
    @FXML private Label universalTime;



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
        localTime.setText((String)LocalDateTime.now().toString());
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


    protected void initClocks() {

        Timeline localClock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            localTime.setText(LocalDateTime.now().format(formatter) + " " + ZoneId.systemDefault() + " UTC" + ZoneId.systemDefault().getRules().getOffset(Instant.now()));
        }), new KeyFrame(Duration.seconds(1)));

        localClock.setCycleCount(Animation.INDEFINITE);
        localClock.play();

        Timeline easternClock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            easternTime.setText(LocalDateTime.now(ZoneId.of("US/Eastern")).format(formatter) + " " + ZoneId.of("US/Eastern") + " UTC" + ZoneId.of("US/Eastern").getRules().getOffset(Instant.now()));
        }), new KeyFrame(Duration.seconds(1)));

        easternClock.setCycleCount(Animation.INDEFINITE);
        easternClock.play();

        Timeline utcClock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            universalTime.setText(LocalDateTime.now(ZoneOffset.UTC).format(formatter) + " " + ZoneId.of("UTC") + ZoneId.of("UTC").getRules().getOffset(Instant.now()));
        }), new KeyFrame(Duration.seconds(1)));

        utcClock.setCycleCount(Animation.INDEFINITE);
        utcClock.play();

    }
}

