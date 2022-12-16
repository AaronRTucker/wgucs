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
import java.util.ResourceBundle;

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

    Report report;


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
            Timestamp start = appointment.getStart();
            int year = start.getYear() + 1900;
            int month = start.getMonth();
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
        selectedContact = contactDropdown.getValue();
        System.out.println(contactDropdown.getValue());
        populateAppointmentsTable();
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



    protected class MonthTotal{


        private int year;
        private int month;
        private String[] names = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        private String name;
        private int total;

        private MonthTotal(int year, int month){
            this.year = year;
            this.month = month;
            this.name = names[month-1];
            this.total = 1;
        }

        public void addOne(){
            total++;
        }
        
        public String getName(){
            return this.name;
        }

        public int getYear() {
            return this.year;
        }

        public int getMonth() {
            return this.month;
        }

        public int getTotal() {
            return this.total;
        }
    }

    protected class TypeTotal{

        public String getTypeName() {
            return typeName;
        }

        public int getTypeTotal() {
            return typeTotal;
        }

        private String typeName;
        private int typeTotal;

        private TypeTotal(String name){
            this.typeName = name;
            typeTotal = 1;
        }

        public void addOne(){
            typeTotal++;
        }
    }
    
    
    private class Report{
        private final ObservableList<MonthTotal> allMonths;
        private final ObservableList<TypeTotal> allTypes;

        public Report(){
            allMonths = FXCollections.observableArrayList();
            allTypes = FXCollections.observableArrayList();
        }
        
        public void addMonth(int year, int month){
            if(checkMonthTotal(year,month)) {
                returnMonthTotal(year, month).addOne();
            } else {
                allMonths.add(new MonthTotal(year,month));
            }
        }

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

        public void addType(String name){
            if(checkTypeTotal(name)) {
                returnTypeTotal(name).addOne();
            } else {
                allTypes.add(new TypeTotal(name));
            }
        }

        public boolean checkTypeTotal(String name){
            for (TypeTotal typeTotal : this.allTypes) {
                if (typeTotal.getTypeName().equals(name)){
                    return true;
                }
            }
            return false;    //no type found
        }
        public TypeTotal returnTypeTotal(String name){
            for (TypeTotal typeTotal : this.allTypes) {
                if (typeTotal.getTypeName().equals(name)){
                    return typeTotal;
                }
            }
            return null;    //no type found
        }

        public ObservableList<MonthTotal> getAllMonthTotals(){
            return allMonths;
        }
        public ObservableList<TypeTotal> getAllTypeTotals(){
            return allTypes;
        }
    }
}
