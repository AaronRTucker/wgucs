/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.DatabaseQueryHelper;
import ScheduleManager.Models.Appointment;
import ScheduleManager.Models.Customer;
import ScheduleManager.Models.Schedule;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.lang.reflect.Type;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The type Report controller.
 */
public class ReportController extends Controller {


    private ResourceBundle bundle;
    private final Schedule schedule;

    private String  selectedContact;

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
    /**
     * The Appointment contact col.
     */
    @FXML public TableColumn<Schedule, String> AppointmentContactCol;
    @FXML private TableColumn<Appointment, String> AppointmentStartCol;
    @FXML private TableColumn<Appointment, String> AppointmentEndCol;
    @FXML private TableColumn<Schedule, Integer> AppointmentCustomerIdCol;

    //Months table columns
    @FXML private TableColumn<MonthTotal, Integer> YearCol;
    @FXML private TableColumn<MonthTotal, String> MonthCol;
    @FXML private TableColumn<MonthTotal, Integer> MonthNumbersCol;


    //Types table columns
    @FXML private TableColumn<MonthTotal, String> TypeCol;
    @FXML private TableColumn<MonthTotal, Integer> TypeNumbersCol;

    @FXML private Button cancelButton;

    @FXML private ComboBox<String> contactDropdown;

    @FXML private TextField apptTotal;

    /**
     * The Report.
     */
    Report report;


    /**
     * Instantiates a new Report controller.
     *
     * @param userName the user name
     */
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

        contactDropdown.setPromptText(bundle.getString("SelectAContact"));
        //Get contacts list from database
        ArrayList<String> contacts = DatabaseQueryHelper.getContacts();

        //populate the contact combo box
        for (String contact : contacts) {
            contactDropdown.getItems().addAll(contact);
        }

        //Change default table placeholder messages
        MonthsTable.setPlaceholder(new Label(bundle.getString("MonthsListIsEmpty")));
        TypesTable.setPlaceholder(new Label(bundle.getString("TypesListIsEmpty")));
        AppointmentsTable.setPlaceholder(new Label(bundle.getString("AppointmentsListIsEmpty")));




        //Get appointment table data from MYSQL database
        DatabaseQueryHelper.getAllAppointments(schedule);

        //Add the report for the total number of appointments in the system
        apptTotal.setText("Total: " + schedule.getAllAppointments().size());

        //Process the schedule data into a report object
        report = new Report();

        ObservableList<Appointment> appointments = schedule.getAllAppointments();
        for (Appointment appointment : appointments) {
            Calendar start = Calendar.getInstance();
            start.setTime(appointment.getStart());
            int year = start.get(Calendar.YEAR);
            int month = start.get(Calendar.MONTH);
            report.addMonth(year, month);
            report.addType(appointment.getType());
        }

        //populate the table with Appointments data from the schedule
        //populateAppointmentsTable();

        populateMonthsTable();

        populateTypesTable();

    }

    /**
     * Handle cancelling out
     *
     * @param event the action event
     */
    @FXML
    public void cancelButtonPressed(ActionEvent event){
        Controller c = new GuiController(userName);
        loadScene(c, event, "ScheduleManager/Views/gui.fxml", 900, 475, bundle);
    }


    /**
     * Handles contact combobox
     *
     * @param event the action event
     */
    @FXML
    public void contactBoxPressed(ActionEvent event) {
        selectedContact = contactDropdown.getValue();
        System.out.println(contactDropdown.getValue());
        populateAppointmentsTable();
    }


    /**
     * LAMBDA
     * Populates the appointment table.
     * Lambda method is used as an anonymous callback function, taking in the appt object and returning an object with the correct local timezone offset
     */
    public void populateAppointmentsTable(){
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
            return Bindings.createStringBinding(() -> "" + startTime.toLocalDateTime() + " " + ZoneId.systemDefault());
        });



        //AppointmentEndCol.setCellValueFactory(new PropertyValueFactory<>("end")); //replaced with timezone offset version below
        AppointmentEndCol.setCellValueFactory(appt -> {
            //Convert from UTC stored in the database to local timezone in the app
            Timestamp endTime = appt.getValue().getEnd();
            ZoneId zone = ZoneId.systemDefault();
            ZonedDateTime zdt = endTime.toLocalDateTime().atZone(zone);
            ZoneOffset offset = zdt.getOffset();
            return Bindings.createStringBinding(() -> "" + endTime.toLocalDateTime() + " " + ZoneId.systemDefault());
        });
        AppointmentCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        //Set labels according to locale:
        AppointmentIdCol.setText(bundle.getString("ID"));
        AppointmentTitleCol.setText(bundle.getString("Title"));
        AppointmentDescriptionCol.setText(bundle.getString("Description"));
        AppointmentContactCol.setText(bundle.getString("Contact"));
        AppointmentTypeCol.setText(bundle.getString("Type"));
        AppointmentStartCol.setText(bundle.getString("Start"));
        AppointmentEndCol.setText(bundle.getString("End"));
        AppointmentCustomerIdCol.setText(bundle.getString("CustomerID"));


        AppointmentsTable.setItems(schedule.getContactsAppointments(selectedContact));
        AppointmentsTable.refresh();
    }


    private void populateMonthsTable(){
        YearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        MonthCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        MonthNumbersCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        MonthsTable.setItems(report.getAllMonthTotals());
    }

    private void populateTypesTable() {
        TypeCol.setCellValueFactory(new PropertyValueFactory<>("typeName"));
        TypeNumbersCol.setCellValueFactory(new PropertyValueFactory<>("typeTotal"));
        TypesTable.setItems(report.getAllTypeTotals());
    }


    /**
     * The type Month total.
     */
    protected static class MonthTotal{


        private final int year;
        private final int month;
        private final String name;
        private int total;

        private MonthTotal(int year, int month){
            this.year = year;
            this.month = month;
            String[] names = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            this.name = names[month];
            this.total = 1;
        }

        /**
         * Add one.
         */
        public void addOne(){
            total++;
        }

        /**
         * Get name string.
         *
         * @return the string
         */
        public String getName(){
            return this.name;
        }

        /**
         * Gets year.
         *
         * @return the year
         */
        public int getYear() {
            return this.year;
        }

        /**
         * Gets month.
         *
         * @return the month
         */
        public int getMonth() {
            return this.month;
        }

        /**
         * Gets total.
         *
         * @return the total
         */
        public int getTotal() {
            return this.total;
        }
    }

    /**
     * The type Type total.
     */
    protected static class TypeTotal{

        /**
         * Gets type name.
         *
         * @return the type name
         */
        public String getTypeName() {
            return typeName;
        }

        /**
         * Gets type total.
         *
         * @return the type total
         */
        public int getTypeTotal() {
            return typeTotal;
        }

        private final String typeName;
        private int typeTotal;

        private TypeTotal(String name){
            this.typeName = name;
            typeTotal = 1;
        }

        /**
         * Add one.
         */
        public void addOne(){
            typeTotal++;
        }
    }
    
    
    private static class Report{
        private final ObservableList<MonthTotal> allMonths;
        private final ObservableList<TypeTotal> allTypes;

        /**
         * Instantiates a new Report.
         */
        public Report(){
            allMonths = FXCollections.observableArrayList();
            allTypes = FXCollections.observableArrayList();
        }

        /**
         * Add month.
         *
         * @param year  the year
         * @param month the month
         */
        public void addMonth(int year, int month){
            if(checkMonthTotal(year,month)) {
                Objects.requireNonNull(returnMonthTotal(year, month)).addOne();
            } else {
                allMonths.add(new MonthTotal(year, month));
            }
        }

        /**
         * Check month total.
         *
         * @param year  the year
         * @param month the month
         * @return the boolean
         */
        public boolean checkMonthTotal(int year, int month){
            for (MonthTotal monthTotal : this.allMonths) {
                if (monthTotal.getMonth() == month) {
                    if(monthTotal.getYear() == year){
                        return true;
                    }
                }
            }
            return false;    //no month found
        }

        /**
         * Return month total.
         *
         * @param year  the year
         * @param month the month
         * @return the month total
         */
        public MonthTotal returnMonthTotal(int year, int month){
            for (MonthTotal monthTotal : this.allMonths) {
                if (monthTotal.getMonth() == month) {
                    if(monthTotal.getYear() == year){
                        return monthTotal;
                    }
                }
            }
            return null;    //no month found
        }

        /**
         * Add type.
         *
         * @param name the name
         */
        public void addType(String name){
            if(checkTypeTotal(name)) {
                Objects.requireNonNull(returnTypeTotal(name)).addOne();
            } else {
                allTypes.add(new TypeTotal(name));
            }
        }

        /**
         * Check type total boolean.
         *
         * @param name the name
         * @return the boolean
         */
        public boolean checkTypeTotal(String name){
            for (TypeTotal typeTotal : this.allTypes) {
                if (typeTotal.getTypeName().equals(name)){
                    return true;
                }
            }
            return false;    //no type found
        }

        /**
         * Return type total.
         *
         * @param name the name
         * @return the type total
         */
        public TypeTotal returnTypeTotal(String name){
            for (TypeTotal typeTotal : this.allTypes) {
                if (typeTotal.getTypeName().equals(name)){
                    return typeTotal;
                }
            }
            return null;    //no type found
        }

        /**
         * Get all month totals.
         *
         * @return the observable list
         */
        public ObservableList<MonthTotal> getAllMonthTotals(){
            return allMonths;
        }

        /**
         * Get all type totals.
         *
         * @return the observable list
         */
        public ObservableList<TypeTotal> getAllTypeTotals(){
            return allTypes;
        }
    }
}
