/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.DatabaseQueryHelper;
import ScheduleManager.DBHelper.JDBC;
import ScheduleManager.Models.Appointment;
import ScheduleManager.Models.Schedule;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
        initClocks();

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

                    int userID = DatabaseQueryHelper.getUserID(userName);

                    AtomicBoolean upcomingAppt = new AtomicBoolean(false);
                    //check for upcoming appointment in next 15 minutes

                    //create new Schedule object with just the customer's appointments
                    Schedule userSchedule = new Schedule();
                    DatabaseQueryHelper.getUserAppointments(userSchedule, userID);

                    //Extract appointments from schedule object
                    ObservableList<Appointment> userAppointments = userSchedule.getAllAppointments();
                    AtomicReference<Appointment> upcoming = null;

                    //add the local time offset
                    LocalDateTime dt = LocalDateTime.now();
                    ZoneId zone = ZoneId.systemDefault();
                    ZonedDateTime zdt = dt.atZone(zone);
                    ZoneOffset offset = zdt.getOffset();
                    Timestamp currentTime = Timestamp.valueOf(dt.minus(offset.getTotalSeconds(), ChronoUnit.SECONDS));
                    Timestamp currentPlusFifteen = Timestamp.valueOf(dt.minus(offset.getTotalSeconds() -900 , ChronoUnit.SECONDS));

                    //check if any of the user's appointments' start times are in the next 15 minutes
                    userAppointments.forEach(appointment -> {
                        if(appointment.getStart().after(currentTime)){
                            if(appointment.getStart().before(currentPlusFifteen)){
                                Alert a = new Alert(Alert.AlertType.INFORMATION);
                                Timestamp start = appointment.getStart();
                                start = Timestamp.valueOf(start.toLocalDateTime().plus(offset.getTotalSeconds(), ChronoUnit.SECONDS));      //offset database time to local user time
                                a.setContentText("There is an upcoming appointment for you: \n Appointment ID: " + appointment.getId() + "\n Appointment Date: " + start);
                                a.show();
                                upcomingAppt.set(true);
                                return;
                            }
                        }
                    });


                    if(upcomingAppt.get()){
                    } else {
                        Alert a = new Alert(Alert.AlertType.INFORMATION);
                        a.setContentText("No upcoming appointments for you.");
                        a.show();
                    }




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
