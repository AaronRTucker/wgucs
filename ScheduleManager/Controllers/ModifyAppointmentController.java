/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.DatabaseQueryHelper;
import ScheduleManager.Models.Appointment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ModifyAppointmentController extends Controller {

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



    private int selectedUserID;
    private int selectedCustomerID;
    private int selectedContactId;
    private Timestamp selectedStart;
    private Timestamp selectedEnd;

    private final Appointment selectedAppointment;

    private final String userName;    //name to store in database associating which user added this customer

    @FXML
    private int startHour;
    @FXML
    private int endHour;
    @FXML
    private int startMinute;
    @FXML
    private int endMinute;

    private String startDateString;
    private String endDateString;





    //Constructor for new Controller object
    public ModifyAppointmentController(String userName, Appointment selectedAppointment){
        this.selectedAppointment= selectedAppointment;                                                    //set the index of the first Customer and Appointment IDs to be 1
        this.userName = userName;

    }




    /**
     * Called every time a screen is loaded
     * @param url the url
     * @param resourceBundle the resource bundle
     */
    @Override
    public void initialize( URL url, ResourceBundle resourceBundle) {

        bundle = resourceBundle;


        //populate hour and minute boxes
        for(int i = 0; i < 24; i++){
            startHourBox.getItems().addAll(i);
            endHourBox.getItems().addAll(i);
        }
        for(int i = 0; i < 60; i++){
            startMinuteBox.getItems().addAll(i);
            endMinuteBox.getItems().addAll(i);
        }



        //Write settings from imported appointment object to the local variables and populate text and dropdown fields
        importAppointmentData();

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
    public void modifyAppointmentSavePressed(ActionEvent event){


            try {
                int id = selectedAppointment.getId();
                String title = appointmentTitleField.getText();
                String description = appointmentDescField.getText();
                String location = appointmentLocationField.getText();
                String type = appointmentTypeField.getText();


                //Handle any input errors

                if (title.equals("") || description.equals("") || location.equals("") || type.equals("") || selectedUserID==-1 || selectedContactId==-1 || selectedCustomerID==-1) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Input Fields must not be empty");
                    a.show();
                }else {
                    //Input is good

                    DatabaseQueryHelper.modifyAppointment(id, title, description, location, selectedContactId, type, selectedStart, selectedEnd, selectedUserID, selectedCustomerID, userName);

                    modifyAppointmentCancelPressed(event);        //return to home screen if there are no errors
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
    public void modifyAppointmentCancelPressed(ActionEvent event){
        Controller c = new GuiController(userName);
        loadScene(c, event, "ScheduleManager/Views/gui.fxml", 900, 475, bundle);
    }

    public void importAppointmentData(){
        appointmentIdField.setEditable(false);
        appointmentIdField.setText(String.valueOf(selectedAppointment.getId()));

        appointmentDescField.setText(selectedAppointment.getDescription());
        appointmentLocationField.setText(selectedAppointment.getLocation());
        appointmentTitleField.setText(selectedAppointment.getTitle());
        appointmentTypeField.setText(selectedAppointment.getType());

        Timestamp st = selectedAppointment.getStart();
        LocalDateTime sdt = st.toLocalDateTime();
        startDateString = sdt.toLocalDate().toString();
        startHour = sdt.toLocalTime().getHour();
        startMinute = sdt.toLocalTime().getMinute();

        Timestamp et = selectedAppointment.getEnd();
        LocalDateTime edt = et.toLocalDateTime();
        endDateString = edt.toLocalDate().toString();
        endHour = edt.toLocalTime().getHour();
        endMinute = edt.toLocalTime().getMinute();

        startHourBox.setPromptText(String.valueOf(startHour));
        endHourBox.setPromptText(String.valueOf(endHour));
        startMinuteBox.setPromptText(String.valueOf(startMinute));
        endMinuteBox.setPromptText(String.valueOf(endMinute));

        startDateSelect.setPromptText(startDateString);
        endDateSelect.setPromptText(endDateString);


        selectedUserID = selectedAppointment.getUserId();
        selectedCustomerID = selectedAppointment.getCustomerId();
        selectedStart = selectedAppointment.getStart();
        selectedEnd = selectedAppointment.getEnd();

        selectedContactId = DatabaseQueryHelper.getContactID(selectedAppointment.getContact());

        contactDropdown.setPromptText(selectedAppointment.getContact());
        customerIdDropdown.setPromptText(String.valueOf(selectedAppointment.getCustomerId()));
        userIdDropdown.setPromptText(String.valueOf(selectedAppointment.getUserId()));

        startDateSelect.setPromptText(selectedAppointment.getStart().toString());
        endDateSelect.setPromptText(selectedAppointment.getEnd().toString());
    }

}
