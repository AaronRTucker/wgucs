/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.DatabaseQueryHelper;
import ScheduleManager.DBHelper.JDBC;
import ScheduleManager.Models.Appointment;
import ScheduleManager.Models.Customer;
import ScheduleManager.Models.Schedule;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReportController extends Controller {


    private ResourceBundle bundle;
    private final Schedule schedule;

    private int selectedContactId;

    private final String userName;    //name to store in database associating which user added this customer


    //maintain list of temporary associated Customers



    /* FXML definitions to link variables from fxml files to the controller*/
    //
    //
    //main page tables
    @FXML private TableView<MonthTotal> MonthsTable;

    @FXML private TableView<TypeTotal> TypesTable;
    @FXML private TableView<Appointment> AppointmentsTable;


    //Appointment table columns
    @FXML private TableColumn<Appointment, Integer> AppointmentIdCol;
    @FXML private TableColumn<Customer, String> AppointmentTitleCol;
    @FXML private TableColumn<Schedule, String> AppointmentDescriptionCol;
    @FXML private TableColumn<Schedule, String> AppointmentTypeCol;
    @FXML public TableColumn<Schedule, String> AppointmentContactCol;
    @FXML private TableColumn<Appointment, String> AppointmentStartCol;
    @FXML private TableColumn<Appointment, String> AppointmentEndCol;
    @FXML private TableColumn<Schedule, Integer> AppointmentCustomerIdCol;

    @FXML private Button cancelButton;

    @FXML public ComboBox<String> contactDropdown;







    //Constructor for new Controller object
    public ReportController(String userName){
        this.userName = userName;
        this.schedule = new Schedule();
    }


    //LOGIN EVENT HANDLERS







    /**
     * Called every time a screen is loaded
     * @param url the url
     * @param resourceBundle the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Need to alert user if there is an appt within 15 minutes from current time


        //Need to create a report button that generates info

        /*Write code that generates accurate information in each of the following reports and will display the reports in the user interface:
Note: You do not need to save and print the reports to a file or provide a screenshot.
•  the total number of customer appointments by type and month

•  a schedule for each contact in your organization that includes appointment ID, title, type and description, start date and time, end date and time, and customer ID

•  an additional report of your choice that is different from the two other required reports in this prompt and from the user log-in date and time stamp that will be tracked in part C
B.  Write at least two different lambda expressions to improve your code.
C.  Write code that provides the ability to track user activity by recording all user log-in attempts, dates, and time stamps and whether each attempt was successful in a file named login_activity.txt. Append each new record to the existing file, and save to the root folder of the application.


         */
        bundle = resourceBundle;


        //Change default table placeholder messages
        MonthsTable.setPlaceholder(new Label("Months list is empty"));
        TypesTable.setPlaceholder(new Label("Types list is empty"));
        AppointmentsTable.setPlaceholder(new Label("Appointments list is empty"));


        //Get appointment table data from MYSQL database
        DatabaseQueryHelper.getAllAppointments(schedule);

        //populate the table with Appointments data from the schedule
        populateAppointmentsTable();

    }

    /**
     * Handle cancelling out
     * @param event the action event
     */
    @FXML
    public void cancelButtonPressed(ActionEvent event){
        Controller c = new GuiController(userName);
        loadScene(c, event, "ScheduleManager/Views/gui.fxml", 900, 475, bundle);
    }


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



    private void populateAppointmentsTable(){
        AppointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        AppointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        AppointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        AppointmentContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        AppointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        //AppointmentStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));     //replaced with timezone offset version below
        AppointmentStartCol.setCellValueFactory(appt -> {
            //Convert from UTC stored in the database to local timezone in the app
            Timestamp startTime = appt.getValue().getStart();
            ZoneId zone = ZoneId.systemDefault();
            ZonedDateTime zdt = startTime.toLocalDateTime().atZone(zone);
            ZoneOffset offset = zdt.getOffset();
            return Bindings.createStringBinding(() -> "" + startTime.toLocalDateTime().plus(offset.getTotalSeconds(), ChronoUnit.SECONDS) + " " + ZoneId.systemDefault());
        });


        //AppointmentEndCol.setCellValueFactory(new PropertyValueFactory<>("end")); //replaced with timezone offset version below
        AppointmentEndCol.setCellValueFactory(appt -> {
            //Convert from UTC stored in the database to local timezone in the app
            Timestamp endTime = appt.getValue().getEnd();
            ZoneId zone = ZoneId.systemDefault();
            ZonedDateTime zdt = endTime.toLocalDateTime().atZone(zone);
            ZoneOffset offset = zdt.getOffset();
            return Bindings.createStringBinding(() -> "" + endTime.toLocalDateTime().plus(offset.getTotalSeconds(), ChronoUnit.SECONDS) + " " + ZoneId.systemDefault());
        });
        AppointmentCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        AppointmentsTable.setItems(schedule.getAllAppointments());
    }


    private class MonthTotal{
        private int month;
        private int total;

        MonthTotal(int month, int total){
            this.month = month;
            this.total = total;
        }


        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    private class TypeTotal{
        private String type;
        private int total;

        public TypeTotal(String type, int total) {
            this.type = type;
            this.total = total;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
