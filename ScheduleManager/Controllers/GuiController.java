/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.JDBC;
import ScheduleManager.DBHelper.DatabaseQueryHelper;
import ScheduleManager.Models.Appointment;
import ScheduleManager.Models.Customer;
import ScheduleManager.Models.Schedule;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class GuiController extends Controller {


    private ResourceBundle bundle;
    private final Schedule schedule;

    private int nextCustomerId;
    private int nextAppointmentId;
    private Customer selectedCustomer;
    private Appointment selectedAppointment;

    //maintain list of temporary associated Customers


    //filtered Customers and Appointments list used in searches
    FilteredList<Customer> filteredCustomers;
    FilteredList<Appointment> filteredAppointments;

    /* FXML definitions to link variables from fxml files to the controller*/
    //
    //
    //main page tables
    @FXML private TableView<Customer> CustomersTable;
    @FXML private TableView<Appointment> AppointmentsTable;

    //Customer table columns
    @FXML private TableColumn<Customer, Integer> CustomerIdCol;
    @FXML private TableColumn<Customer, String> CustomerNameCol;
    @FXML private TableColumn<Customer, String> CustomerAddressCol;

    @FXML private TableColumn<Customer, String> CustomerDivisionCol;

    @FXML private TableColumn<Customer, String> CustomerCountryCol;
    @FXML private TableColumn<Customer, String> CustomerPostalCodeCol;
    @FXML private TableColumn<Customer, String> CustomerPhoneNumberCol;

    //Appointment table columns
    @FXML private TableColumn<Appointment, Integer> AppointmentIdCol;
    @FXML private TableColumn<Customer, String> AppointmentTitleCol;
    @FXML private TableColumn<Schedule, String> AppointmentDescriptionCol;
    @FXML private TableColumn<Schedule, String> AppointmentTypeCol;
    @FXML public TableColumn<Schedule, String> AppointmentContactCol;
    @FXML private TableColumn<Schedule, String> AppointmentLocationCol;
    @FXML private TableColumn<Schedule, java.sql.Timestamp> AppointmentStartCol;
    @FXML private TableColumn<Schedule, java.sql.Timestamp> AppointmentEndCol;
    @FXML private TableColumn<Schedule, Integer> AppointmentCustomerIdCol;
    @FXML private TableColumn<Schedule, Integer> AppointmentUserIdCol;

    @FXML private Button exitBtn;
    @FXML private Button addCustomerBtn;
    @FXML private Button addAppointmentBtn;
    @FXML private Button modCustomerBtn;
    @FXML private Button delCustomerBtn;
    @FXML private Button delAppointmentBtn;
    @FXML private Button modAppointmentBtn;

    @FXML private RadioButton weekRadio;
    @FXML private RadioButton monthRadio;
    @FXML private DatePicker dateFilter;



    private boolean weekSelected;
    private int weekNumberSelected;
    private int monthNumberSelected;



    private final String userName;        //name of the logged-in user






    //Search fields
    @FXML private TextField CustomerSearchTextField;
    @FXML private TextField AppointmentSearchTextField;


    //Constructor for new Controller object
    public GuiController(String userName){
        this.userName = userName;
        this.nextCustomerId = 1;
        this.schedule = new Schedule();                                             //schedule object passed in from Main

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

        initClocks();

        //initialize radio button flip/flop logic
        weekSelected = false;
        weekRadio.setSelected(false);
        monthRadio.setSelected(true);

        bundle = resourceBundle;


        //Change default table placeholder messages
        CustomersTable.setPlaceholder(new Label("Customers list is empty"));
        AppointmentsTable.setPlaceholder(new Label("Appointments list is empty"));

        //Get customer table data from MYSQL database
        DatabaseQueryHelper.getAllCustomers(schedule);

        //Get appointment table data from MYSQL database
        DatabaseQueryHelper.getAllAppointments(schedule);

        //Get the next customer ID to use when creating a new customer
        nextCustomerId = DatabaseQueryHelper.getNextCustomerId(schedule);

        //Get the next appointment ID to use when creating a new appointment
        nextAppointmentId = DatabaseQueryHelper.getNextAppointmentId(schedule);

        //populate the table with Customers data from the schedule
        populateCustomerTable();

        //populate the table with Appointments data from the schedule
        populateAppointmentsTable();

        //Set up customer search callback function
        setupCustomerTableSearch();

        //Set up appointment search callback function
        setupAppointmentTableSearch();

        //Set up appointment date filter callback function
        setupAppointmentTableDateSearch();

    }

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
     * Handles adding Customer
     * @param event the action event
     */
    @FXML
    public void addCustomerButtonPressed(ActionEvent event){
        AddCustomerController c = new AddCustomerController(nextCustomerId, userName);
        loadScene(c, event, "ScheduleManager/Views/addCustomer.fxml", 900, 475, bundle);

    }




    /**
     * Handles modifying Customers
     * @param event the action event
     */
    @FXML
    public void modifyCustomerButton(ActionEvent event){
        selectedCustomer = CustomersTable.getSelectionModel().getSelectedItem();


        Alert a;
        if(selectedCustomer == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select a Customer to modify");
            a.show();
        } else {
            ModifyCustomerController c = new ModifyCustomerController(userName, selectedCustomer);
            loadScene(c, event, "ScheduleManager/Views/modifyCustomer.fxml", 900, 475, bundle);
        }
    }

    @FXML
    public void weekRadioPressed(ActionEvent event){
        weekSelected = true;
        weekRadio.setSelected(true);
        monthRadio.setSelected(false);

        //make a change to the selected date so that it triggers the date filter to run again
        LocalDate temp = dateFilter.getValue();
        dateFilter.setValue(LocalDate.now());
        dateFilter.setValue(temp);
    }

    @FXML
    public void monthRadioPressed(ActionEvent event){
        weekSelected = false;
        weekRadio.setSelected(false);
        monthRadio.setSelected(true);

        //make a change to the selected date so that it triggers the date filter to run again
        LocalDate temp = dateFilter.getValue();
        dateFilter.setValue(LocalDate.now());
        dateFilter.setValue(temp);
    }

    @FXML
    public void dateFilterPressed(ActionEvent event){
        LocalDate date = dateFilter.getValue(); // input from user
        Locale locale = Locale.getDefault();
        weekNumberSelected = date.get(WeekFields.of(locale).weekOfWeekBasedYear());
        monthNumberSelected = date.getMonthValue();
        //System.out.println(weekNumberSelected);
        //System.out.println(monthNumberSelected);
    }

    /**
     * Handles deleting Customers
     * @param event the action event
     */
    @FXML
    public void deleteCustomerButton(ActionEvent event)
    {
        //Need to add code that deletes all customer appointments
        selectedCustomer = CustomersTable.getSelectionModel().getSelectedItem();
        Alert a;
        Optional<ButtonType> result;
        //Handle the error where no Customer is selected
        if(selectedCustomer == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select a Customer to delete");
            a.show();
        } else {
            a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Customer deletion");
            a.setHeaderText("You are about to delete Customer: " + selectedCustomer.getName() + " and all of their appointments.");
            a.setContentText("Are you sure you want to do this?");
            result = a.showAndWait();
            if(result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    schedule.deleteCustomer(selectedCustomer);
                    DatabaseQueryHelper.deleteCustomer(selectedCustomer);//delete the customer from the database
                }
            }
        }

        //refresh the page to make sure any related appointments are cleared
        Controller c = new GuiController(userName);
        loadScene(c, event, "ScheduleManager/Views/gui.fxml", 900, 475, bundle);
    }


    /**
     * Handles adding Appointments
     * @param event the action event
     */
    @FXML
    public void addAppointmentButtonPressed(ActionEvent event){
        AddAppointmentController c = new AddAppointmentController(nextAppointmentId, userName);
        loadScene(c, event, "ScheduleManager/Views/addAppointment.fxml", 900, 675, bundle );
    }

    /**
     * Handles modifying Appointments
     * @param event the action event
     */
    @FXML
    public void modifyAppointmentButton(ActionEvent event){
        Alert a;
        selectedAppointment = AppointmentsTable.getSelectionModel().getSelectedItem();
        if(selectedAppointment == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select a Appointment to modify");
            a.show();
        } else {
            ModifyAppointmentController c = new ModifyAppointmentController(userName, selectedAppointment);
            loadScene(c, event, "ScheduleManager/Views/modifyAppointment.fxml", 900, 675, bundle);
        }
    }

    /**
     * Handles deleting Appointments
     * @param event the action event
     */
    @FXML
    public void deleteAppointmentButton(ActionEvent event)
    {
        selectedAppointment = AppointmentsTable.getSelectionModel().getSelectedItem();
        Alert a;
        Optional<ButtonType> result;
        //Handle the error where no Appointment is selected
        if(selectedAppointment == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select an Appointment to delete");
            a.show();
            //Handle the error where an Appointment has associate Customers still
        }else{  //everything is checked, just need to confirm deletion
            a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Appointment deletion");
            a.setHeaderText("You are about to delete Appointment: " + selectedAppointment.getTitle());
            a.setContentText("Are you sure you want to do this?");
            result = a.showAndWait();
            if(result.isPresent()){
                if (result.get() == ButtonType.OK) {
                    schedule.deleteAppointment(selectedAppointment);
                    DatabaseQueryHelper.deleteAppointment(selectedAppointment);
                }
            }
        }
    }



    /**
     * Handles the Customer search error messages
     * @param event the action event
     */
    @FXML
    //Handle showing the error messages for no Customer search results and empty Customer data table
    public void CustomerSearchKeyTyped(KeyEvent event){
        if(!CustomerSearchTextField.getText().isEmpty()){   //if there is something typed in the search box
            if(filteredCustomers.size() == 0){              //and there are no results
                CustomersTable.setPlaceholder(new Label("Nothing found in Customers search"));
            }
        } else {  //there is something in the search box but no content
            CustomersTable.setPlaceholder(new Label("Customers list is empty"));
        }
    }

    /**
     * Handles the Appointment search error messages
     * @param event the action event
     */
    @FXML
    //Handle showing the error messages for no Appointment search results and empty Appointment data table
    public void AppointmentSearchKeyTyped(KeyEvent event){
        if(!AppointmentSearchTextField.getText().isEmpty()){
            if(filteredAppointments.size() == 0){
                AppointmentsTable.setPlaceholder(new Label("Nothing found in Appointments search"));
            }
        } else {
            AppointmentsTable.setPlaceholder(new Label("Appointments list is empty"));
        }
    }


    private void populateCustomerTable(){
        CustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        CustomerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        CustomerAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        CustomerDivisionCol.setCellValueFactory(new PropertyValueFactory<>("division"));
        CustomerCountryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        CustomerPostalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        CustomerPhoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        CustomersTable.setItems(schedule.getAllCustomers());
    }

    private void populateAppointmentsTable(){
        AppointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        AppointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        AppointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        AppointmentLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        AppointmentContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        AppointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        AppointmentStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        AppointmentEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        AppointmentCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        AppointmentUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        AppointmentsTable.setItems(schedule.getAllAppointments());
    }


    private void setupCustomerTableSearch(){
        //Set up anonymous callback function for the event listener on the Customers search field
        filteredCustomers = new FilteredList<>(schedule.getAllCustomers(), p -> true);
        CustomerSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredCustomers.setPredicate(Customer -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            //if search text equals Customer ID or name
            return Customer.getName().toLowerCase().contains(lowerCaseFilter) || lowerCaseFilter.equals(String.valueOf(Customer.getId())); // Filter matches name or ID.
        }));
        SortedList<Customer> sortedCustomersData = new SortedList<>(filteredCustomers);
        sortedCustomersData.comparatorProperty().bind(CustomersTable.comparatorProperty());
        CustomersTable.setItems(sortedCustomersData);
    }

    private void setupAppointmentTableSearch(){
        //Set up anonymous callback function for the event listener on the Appointments search field
        filteredAppointments = new FilteredList<>(schedule.getAllAppointments(), p -> true);
        AppointmentSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredAppointments.setPredicate(Appointment -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();

            //if search text equals Appointment ID or name
            return Appointment.getTitle().toLowerCase().contains(lowerCaseFilter) || lowerCaseFilter.equals(String.valueOf(Appointment.getId())); // Filter matches title or ID.
// Filter doesn't match.
        }));

        SortedList<Appointment> sortedAppointmentsData = new SortedList<>(filteredAppointments);
        sortedAppointmentsData.comparatorProperty().bind(AppointmentsTable.comparatorProperty());
        AppointmentsTable.setItems(sortedAppointmentsData);
    }

    private void setupAppointmentTableDateSearch(){
        //Set up anonymous callback function for the event listener on the Appointments date filter
        filteredAppointments = new FilteredList<>(schedule.getAllAppointments(), p -> true);
        dateFilter.valueProperty().addListener((observable, oldValue, newValue) -> filteredAppointments.setPredicate(Appointment -> {
                if (newValue == null) {
                    return true;
                }
                LocalDate date = newValue; // input from user
                Locale locale = Locale.getDefault();
                weekNumberSelected = date.get(WeekFields.of(locale).weekOfWeekBasedYear());
                monthNumberSelected = date.getMonthValue();

                if (weekSelected) {
                    LocalDate appStart = Appointment.getStart().toLocalDateTime().toLocalDate();
                    int appStartWeek = appStart.get(WeekFields.of(locale).weekOfWeekBasedYear());
                    //if selected week matches
                    return weekNumberSelected == appStartWeek;
                } else {
                    LocalDate appStart = Appointment.getStart().toLocalDateTime().toLocalDate();
                    int appStartMonth = appStart.getMonthValue();
                    //if selected month matches
                    return monthNumberSelected == appStartMonth;
                }
            }));
        SortedList<Appointment> sortedAppointmentsData = new SortedList<>(filteredAppointments);
        sortedAppointmentsData.comparatorProperty().bind(AppointmentsTable.comparatorProperty());
        AppointmentsTable.setItems(sortedAppointmentsData);
    }
}
