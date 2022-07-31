/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.DatabaseQueryHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddAppointmentController extends Controller {

    //add appointment data fields
    public DatePicker startDateSelect;
    public Button cancel;
    public TextField appointmentIdField;
    public TextField appointmentTitleField;
    public TextField appointmentDescField;
    public TextField appointmentLocationField;
    public TextField appointmentTypeField;
    public Button save;
    public ComboBox<String> contactDropdown;
    public DatePicker endDateSelect;
    public ComboBox<Integer> customerIdDropdown;
    public ComboBox<Integer> userIdDropdown;

    public ComboBox<Integer> startHourBox;
    public ComboBox<Integer> endHourBox;
    public ComboBox<Integer> startMinuteBox;
    public ComboBox<Integer> endMinuteBox;
    private ResourceBundle bundle;

    private int nextAppointmentId;

    @FXML
    private int startHour;
    @FXML
    private int endHour;
    @FXML
    private int startMinute;
    @FXML
    private int endMinute;




    private int selectedUserID;
    private int selectedCustomerID;
    private int selectedContactId;
    private Timestamp selectedStart;
    private Timestamp selectedEnd;

    private String startDateString;
    private String endDateString;

    private final String userName;    //name to store in database associating which user added this customer





    //Constructor for new Controller object
    public AddAppointmentController(int nextAppointmentId, String userName){
        this.nextAppointmentId = nextAppointmentId;                                                    //set the index of the first Customer and Appointment IDs to be 1
        this.userName = userName;
        startHour = -1;
        endHour = -1;
        startMinute = -1;
        endMinute = -1;
        startDateString = "";
        endDateString = "";

    }




    /**
     * Called every time a screen is loaded
     * @param url the url
     * @param resourceBundle the resource bundle
     */
    @Override
    public void initialize( URL url, ResourceBundle resourceBundle) {

        initClocks();

        appointmentIdField.setEditable(false);
        appointmentIdField.setText(String.valueOf(this.nextAppointmentId));

        selectedContactId = -1;        //used to check if contact has been selected before creating appointment

        bundle = resourceBundle;


        //Populate hour and minute boxes
        for(int i = 0; i < 24; i++){
            startHourBox.getItems().addAll(i);
            endHourBox.getItems().addAll(i);
        }
        for(int i = 0; i < 60; i++){
            startMinuteBox.getItems().addAll(i);
            endMinuteBox.getItems().addAll(i);
        }


        //Set default prompts
        contactDropdown.setPromptText("Select a contact");
        customerIdDropdown.setPromptText("Select a customer ID");
        userIdDropdown.setPromptText("Select a user ID");


        //Get contacts list from database
        ArrayList<String> contacts = DatabaseQueryHelper.getContacts();

        //Get userID list from database
        ArrayList<Integer> userIDs = DatabaseQueryHelper.getUserIDs();

        //Get customerID list from database
        ArrayList<Integer> customerIDs = DatabaseQueryHelper.getCustomerIDs();


        //populate the contact combo box
        for (String country : contacts) {
            contactDropdown.getItems().addAll(country);
        }

        //populate the userID combo box
        for (Integer userID : userIDs) {
            userIdDropdown.getItems().addAll(userID);
        }

        //populate the customerID combo box
        for (Integer customerID : customerIDs) {
            customerIdDropdown.getItems().addAll(customerID);
        }

    }


    //EVENT HANDLERS

    /**
     * Handles contact combobox
     * @param event the action event
     */
    @FXML
    public void contactBoxPressed(ActionEvent event) {
        String selectedContact = contactDropdown.getValue();

        //Get contact ID from selected contact name
        selectedContactId = DatabaseQueryHelper.getContactID(selectedContact);
    }
    @FXML
    public void startDatePressed(ActionEvent event){
        startDateString = startDateSelect.getValue().toString();
    }
    @FXML
    public void endDatePressed(ActionEvent event){
        endDateString = endDateSelect.getValue().toString();
    }

    @FXML
    public void startHourPressed(ActionEvent event){
        startHour = startHourBox.getValue();
    }
    @FXML
    public void endHourPressed(ActionEvent event){
        endHour = endHourBox.getValue();
    }
    @FXML
    public void startMinutePressed(ActionEvent event){
        startMinute = startMinuteBox.getValue();
    }
    @FXML
    public void endMinutePressed(ActionEvent event){
        endMinute = endMinuteBox.getValue();
    }


    /**
     * Handles userID combobox
     * @param event the action event
     */
    @FXML
    public void userIDBoxPressed(ActionEvent event) {
        selectedUserID = userIdDropdown.getValue();
    }

    /**
     * Handles customerID combobox
     * @param event the action event
     */
    @FXML
    public void customerIDBoxPressed(ActionEvent event) {
        selectedCustomerID = customerIdDropdown.getValue();
    }




    /**
     * Handles saving appointments
     * @param event the action event
     */
    @FXML
    public void addAppointmentSavePressed(ActionEvent event){


        int id = this.nextAppointmentId;
        this.nextAppointmentId++;
            try {
                String title = appointmentTitleField.getText();
                String description = appointmentDescField.getText();
                String location = appointmentLocationField.getText();
                String type = appointmentTypeField.getText();


                //Handle any input errors

                if (title.equals("") || description.equals("") || location.equals("") || type.equals("") || selectedUserID==-1 || selectedContactId==-1 || selectedCustomerID==-1) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Input Fields must not be empty");
                    a.show();
                } else if(startMinute == -1 || startHour == -1 || endMinute == -1 || endHour == -1) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Time Fields must not be empty");
                    a.show();
                } else if(startDateString.equals("") || endDateString.equals("")){
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Date Fields must not be empty");
                    a.show();
                }else {
                    //Input is good

                    //Need to check if the app end time is after the start time
                    //Need to check if the app start and end time is between business hours EST time
                    //Need to make sure schedules don't overlap


                    //Format date inputs into a unified timestamp for start and end
                    LocalDate datePart = LocalDate.parse(startDateString);
                    LocalTime timePart = LocalTime.parse(startHour+":"+startMinute, DateTimeFormatter.ofPattern("H:m"));
                    LocalDateTime dt = LocalDateTime.of(datePart, timePart);
                    selectedStart = Timestamp.valueOf(dt);
                    datePart = LocalDate.parse(endDateString);
                    timePart = LocalTime.parse(endHour+":"+endMinute, DateTimeFormatter.ofPattern("H:m"));
                    dt = LocalDateTime.of(datePart, timePart);
                    selectedEnd = Timestamp.valueOf(dt);



                    System.out.println(selectedStart);

                    DatabaseQueryHelper.addAppointment(id, title, description, location, selectedContactId, type, selectedStart, selectedEnd, selectedUserID, selectedCustomerID, userName);

                    addAppointmentCancelPressed(event);        //return to home screen if there are no errors
                }
            } catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Inappropriate user input: " + e.getMessage());
                a.show();
            }
        }

    /**
     * Handles cancelling out of the add appointment screen
     * @param event the action event
     */
    @FXML
    public void addAppointmentCancelPressed(ActionEvent event){
        Controller c = new GuiController(userName);
        loadScene(c, event, "ScheduleManager/Views/gui.fxml", 900, 475, bundle);
    }

}
