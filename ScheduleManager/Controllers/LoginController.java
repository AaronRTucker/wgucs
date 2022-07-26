/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.JDBC;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class LoginController extends Controller {
    public Button loginExit;
    public Button loginSubmit;
    private ResourceBundle bundle;

    private String userName;


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
     * @param url the url
     * @param resourceBundle the resource bundle
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
     */
    @FXML
    public void loginSubmitPressed(ActionEvent event){
        boolean userFound;
        String password;

        try {
            String userInputName = loginNameField.getText();
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT Password FROM `client_schedule`.`users` where User_Name = '" + userInputName +"';");
            ResultSet result = ps.executeQuery();
            if(result.next()){
                password = result.getString("password");
                userFound = true;
                userName = userInputName;
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
                    Controller c = new GuiController(userName);
                    loadScene(c, event, "ScheduleManager/Views/gui.fxml", 900, 475, bundle);
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle(bundle.getString("Error"));
                    a.setHeaderText(bundle.getString("Error"));
                    a.setContentText(bundle.getString("IncorrectPassword"));
                    a.show();
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
