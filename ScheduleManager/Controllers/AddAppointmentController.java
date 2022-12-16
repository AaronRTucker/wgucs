/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.DatabaseQueryHelper;
import ScheduleManager.Models.Appointment;
import ScheduleManager.Models.Schedule;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

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

        String tzid = "EST";
        TimeZone tz = TimeZone.getTimeZone(tzid);


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
        contactDropdown.setPromptText(bundle.getString("SelectAContact"));
        customerIdDropdown.setPromptText(bundle.getString("SelectACustomerID"));
        userIdDropdown.setPromptText(bundle.getString("SelectAUserID"));


        //Get contacts list from database
        ArrayList<String> contacts = DatabaseQueryHelper.getContacts();

        //Get userID list from database
        ArrayList<Integer> userIDs = DatabaseQueryHelper.getUserIDs();

        //Get customerID list from database
        ArrayList<Integer> customerIDs = DatabaseQueryHelper.getCustomerIDs();


        //populate the contact combo box
        for (String contact : contacts) {
            contactDropdown.getItems().addAll(contact);
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
                    a.setContentText(bundle.getString("InputFieldsMustNotBeEmpty"));
                    a.show();
                } else if(startMinute == -1 || startHour == -1 || endMinute == -1 || endHour == -1) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText(bundle.getString("TimeFieldsMustNotBeEmpty"));
                    a.show();
                } else if(startDateString.equals("") || endDateString.equals("")){
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText(bundle.getString("DateFieldsMustNotBeEmpty"));
                    a.show();
                }else{
                    //User input is good



                    //Format date inputs into a unified timestamp for start and end
                    LocalDate datePart = LocalDate.parse(startDateString);
                    LocalTime timePart = LocalTime.parse(startHour+":"+startMinute, DateTimeFormatter.ofPattern("H:m"));
                    LocalDateTime dt = LocalDateTime.of(datePart, timePart);

                    //add the local time offset
                    ZoneId zone = ZoneId.systemDefault();
                    ZonedDateTime zdt = dt.atZone(zone);
                    ZoneOffset offset = zdt.getOffset();
                    selectedStart = Timestamp.valueOf(dt.minus(offset.getTotalSeconds(), ChronoUnit.SECONDS));

                    datePart = LocalDate.parse(endDateString);
                    timePart = LocalTime.parse(endHour+":"+endMinute, DateTimeFormatter.ofPattern("H:m"));
                    dt = LocalDateTime.of(datePart, timePart);

                    //add the local time offset
                    selectedEnd = Timestamp.valueOf(dt.minus(offset.getTotalSeconds(), ChronoUnit.SECONDS));



                    //check if end time is after start time or not during business hours

                    ZoneId eastern = ZoneId.of("US/Eastern");
                    ZonedDateTime edt = selectedStart.toLocalDateTime().atZone(eastern);
                    ZoneOffset easternOffset = edt.getOffset();

                    Timestamp easternStart = Timestamp.valueOf(selectedStart.toLocalDateTime().plus(easternOffset.getTotalSeconds(), ChronoUnit.SECONDS));
                    Timestamp easternEnd = Timestamp.valueOf(selectedEnd.toLocalDateTime().plus(easternOffset.getTotalSeconds(), ChronoUnit.SECONDS));

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(easternStart.getTime());
                    SimpleDateFormat sdf = new SimpleDateFormat("HH");
                    String easternStartHour = sdf.format(calendar.getTime());
                    int easternStartInt = Integer.parseInt(easternStartHour);
                    calendar.setTimeInMillis(easternEnd.getTime());
                    String easternEndHour = sdf.format(calendar.getTime());
                    int easternEndInt = Integer.parseInt(easternEndHour);

                    AtomicBoolean conflict = new AtomicBoolean(false);
                    //check for overlapping appointment

                    //create new Schedule object with just the customer's appointments
                    Schedule customerSchedule = new Schedule();
                    DatabaseQueryHelper.getCustomerAppointments(customerSchedule, selectedCustomerID);

                    //Extract appointments from schedule object
                    ObservableList<Appointment> customerAppointments = customerSchedule.getAllAppointments();

                    //check if start time equals or is during an existing appointment
                    customerAppointments.forEach(appointment -> {
                        if(selectedStart.equals(appointment.getStart())){   //check edges
                            conflict.set(true);
                        }
                        if(selectedStart.equals(appointment.getEnd())){   //check edges
                            conflict.set(true);
                        }
                        if(selectedEnd.equals(appointment.getStart())){   //check edges
                            conflict.set(true);
                        }
                        if(selectedEnd.equals(appointment.getEnd())){     //check edges
                            conflict.set(true);
                        }
                        if (selectedStart.after( appointment.getStart()) && selectedStart.before(appointment.getEnd())){    //check middle
                            conflict.set(true);
                        }
                        if (selectedEnd.after( appointment.getStart()) && selectedEnd.before(appointment.getEnd())){    //check middle
                            conflict.set(true);
                        }
                    });

                    if(selectedStart.after(selectedEnd)){
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText(bundle.getString("EndTimeMustBeAfterStartTime"));
                        a.show();
                    }else if(easternStartInt < 8){
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText(bundle.getString("StartTimeMustBeAfter8"));
                        a.show();
                    } else if(easternEndInt >= 20){
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText(bundle.getString("EndTimeMustBeBefore10"));
                        a.show();
                    } else if(conflict.get()){
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText(bundle.getString("AppointmentTimeConflicts"));
                        a.show();
                    }
                    else {
                        DatabaseQueryHelper.addAppointment(id, title, description, location, selectedContactId, type, selectedStart, selectedEnd, selectedUserID, selectedCustomerID, userName);
                        addAppointmentCancelPressed(event);        //return to home screen if there are no errors
                    }
                }
            } catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(bundle.getString("InappropriateUserInput") +": " + e.getMessage());
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
