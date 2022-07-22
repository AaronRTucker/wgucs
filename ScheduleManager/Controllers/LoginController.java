/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.JDBC;
import ScheduleManager.Models.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private ResourceBundle bundle;


    /* FXML definitions to link variables from fxml files to the controller*/
    //
    //
    //Login fields
    @FXML private TextField loginNameField;
    @FXML private TextField loginPasswordField;

    @FXML private TextField zoneID;


    //Constructor for new LoginController object
    public LoginController(){

    }




    /**
     * Called every time a screen is loaded
     * @futureenhancement add more search fields to find specific quantities
     * @runtimeerror discovered that file paths to fxml scenes need to be absolute instead of relative to work
     * @param url the url
     * @param resourceBundle the resource bundle
     * @return void
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bundle = resourceBundle;        //save resource bundle to use in callback functions
        zoneID.setText(String.valueOf(ZoneId.systemDefault()));

    }

    //LOGIN EVENT HANDLERS

    /**
     * Handle exiting the application
     * @param event the action event
     * @return void
     */
    @FXML
    public void pressExitButton(ActionEvent event){
        System.out.println("Application exiting");
        Platform.exit();
        JDBC.closeConnection();
    }

    /**
     * Handle logging into the application
     * @param event the action event
     * @return void
     */
    @FXML
    public void loginSubmitPressed(ActionEvent event){
        boolean userFound;
        String password;

        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT Password FROM `client_schedule`.`users` where User_Name = '" + loginNameField.getText() +"';");
            ResultSet result = ps.executeQuery();
            if(result.next()){
                password = result.getString(1);
                userFound = true;
            } else {
                password = null;
                userFound = false;
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle(bundle.getString("Error"));
                a.setHeaderText(bundle.getString("Error"));
                a.setContentText(bundle.getString("NoUserFound"));
                a.show();
            }

            if(userFound) {
                if (loginPasswordField.getText().equals(password)) {        //insecure, should be checked on database if you don't trust the client
                    loadScene(event, "ScheduleManager/Views/gui.fxml", 900, 675);
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle(bundle.getString("Error"));
                    a.setHeaderText(bundle.getString("Error"));
                    a.setContentText(bundle.getString("IncorrectPassword"));
                    a.show();
                }
            }

        } catch (SQLException e){
            System.out.println(e);
        }
    }

    /**
     * Loads a new scene with a given file, width, and height
     * @param event the action event
     * @param location the location of the scene file
     * @param width the width of the scene
     * @param height the height of the scene
     * @return void
     * @RUNTIME ERROR
     */
    //Private helper function
    //Handle switching between fxml file scenes
    private void loadScene(ActionEvent event, String location, int width, int height){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(location), bundle);      //absolute reference for file path of scene
            Schedule schedule = new Schedule();
            loader.setController(new GuiController(schedule));
            scene = new Scene((Pane) loader.load(), width, height);                                       //set width and height of scene
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception){
            System.out.println(exception);
        }
    }

}
