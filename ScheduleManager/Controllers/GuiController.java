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
import java.time.*;
import java.time.temporal.WeekFields;
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
    @FXML private TableColumn<Appointment, String> AppointmentStartCol;
    @FXML private TableColumn<Appointment, String> AppointmentEndCol;
    @FXML private TableColumn<Schedule, Integer> AppointmentCustomerIdCol;
    @FXML private TableColumn<Schedule, Integer> AppointmentUserIdCol;

    @FXML private Button exitBtn;
    @FXML private Button addCustomerBtn;
    @FXML private Button addAppointmentBtn;
    @FXML private Button modCustomerBtn;
    @FXML private Button delCustomerBtn;
    @FXML private Button delAppointmentBtn;
    @FXML private Button modAppointmentBtn;

    @FXML private Button reportsButton;

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
        this.schedule = new Schedule();     //schedule object passed in from Main

    }


    //LOGIN EVENT HANDLERS


    /**
     * Called every time a screen is loaded
     * @param url the url
     * @param resourceBundle the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //initialize week/month search with today's date to avoid null error
        Locale locale = Locale.getDefault();
        LocalDate date = LocalDate.now();
        monthNumberSelected = date.getMonthValue();

        initClocks();

        //initialize radio button flip/flop logic
        weekSelected = false;
        weekRadio.setSelected(false);
        monthRadio.setSelected(true);

        bundle = resourceBundle;


        //Change default table placeholder messages
        CustomersTable.setPlaceholder(new Label(bundle.getString("CustomersListIsEmpty")));
        AppointmentsTable.setPlaceholder(new Label(bundle.getString("AppointmentsListIsEmpty")));


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

        dateFilter.valueProperty().setValue(date);      //trigger event listener to filter appointments to current month

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
        loadScene(c, event, "ScheduleManager/Views/addCustomer.fxml", 1000, 475, bundle);

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
            a.setContentText(bundle.getString("PleaseSelectACustomerToModify"));
            a.show();
        } else {
            ModifyCustomerController c = new ModifyCustomerController(userName, selectedCustomer);
            loadScene(c, event, "ScheduleManager/Views/modifyCustomer.fxml", 900, 475, bundle);
        }
    }

    /**
     * Week radio pressed event handler
     *
     * @param event the event
     */
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

    /**
     * Month radio pressed event handler
     *
     * @param event the event
     */
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

    /**
     * Date filter pressed event handler
     *
     * @param event the event
     */
    @FXML
    public void dateFilterPressed(ActionEvent event){
        LocalDate date = dateFilter.getValue(); // input from user
        Locale locale = Locale.getDefault();
        weekNumberSelected = date.get(WeekFields.of(locale).weekOfWeekBasedYear());
        monthNumberSelected = date.getMonthValue();
    }


    /**
     * Reports button pressed event handler.
     *
     * @param event the event
     */
    @FXML
    public void reportsButtonPressed(ActionEvent event){
        Controller c = new ReportController(userName);
        loadScene(c, event, "ScheduleManager/Views/reports.fxml", 900, 475, bundle);
    }

    /**
     * Handles deleting Customers
     * @param event the action event
     */
    @FXML
    public void deleteCustomerButton(ActionEvent event)
    {
        selectedCustomer = CustomersTable.getSelectionModel().getSelectedItem();
        Alert a;
        Optional<ButtonType> result;
        //Handle the error where no Customer is selected
        if(selectedCustomer == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(bundle.getString("PleaseSelectACustomerToDelete"));
            a.show();
        } else {
            a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle(bundle.getString("CustomerDeletion"));
            a.setHeaderText(bundle.getString("YouAreAboutToDelete") +": " + selectedCustomer.getName() + " " + bundle.getString("AndAllOfTheirAppointments"));
            a.setContentText(bundle.getString("AreYouSureYouWantToDoThis"));
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
            a.setContentText(bundle.getString("PleaseSelectAnAppointmentToModify"));
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
            a.setContentText(bundle.getString("PleaseSelectAnAppointmentToDelete"));
            a.show();
            //Handle the error where an Appointment has associate Customers still
        }else{  //everything is checked, just need to confirm deletion
            a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle(bundle.getString("AppointmentDeletion"));
            a.setHeaderText(bundle.getString("YouAreAboutToDelete")+ " \nAppointment ID:" + selectedAppointment.getId() + "\nAppointment Title: " + selectedAppointment.getTitle() + "\nAppointment Type: " + selectedAppointment.getType());
            a.setContentText(bundle.getString("AreYouSureYouWantToDoThis"));
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
                CustomersTable.setPlaceholder(new Label(bundle.getString("NothingFoundInCustomersSearch")));
            }
        } else {  //there is something in the search box but no content
            CustomersTable.setPlaceholder(new Label("CustomersListIsEmpty"));
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
                AppointmentsTable.setPlaceholder(new Label(bundle.getString("NothingFoundInAppointmentsSearch")));
            }
        } else {
            AppointmentsTable.setPlaceholder(new Label(bundle.getString("AppointmentsListIsEmpty")));
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


    /**
     * Populates the appointment table.
     * Lambda method is used as an anonymous callback function, taking in the appt object and returning an object with the correct local timezone offset
     */
    public void populateAppointmentsTable(){
        AppointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        AppointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        AppointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        AppointmentLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        AppointmentContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        AppointmentTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        //AppointmentStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));     //replaced with timezone offset version below
        AppointmentStartCol.setCellValueFactory(appt -> {       //lambda expression used to functionally generate the correct object to return inline
            //Convert from UTC stored in the database to local timezone in the app
            Timestamp startTime = appt.getValue().getStart();
            ZoneId zone = ZoneId.systemDefault();
            ZonedDateTime zdt = startTime.toLocalDateTime().atZone(zone);
            ZoneOffset offset = zdt.getOffset();
            return Bindings.createStringBinding(() -> "" + startTime.toLocalDateTime() + " " + ZoneId.systemDefault());
        });


        //AppointmentEndCol.setCellValueFactory(new PropertyValueFactory<>("end")); //replaced with timezone offset version below
        AppointmentEndCol.setCellValueFactory(appt -> {   //lambda expression used to functionally generate the correct object to return inline
            //Convert from UTC stored in the database to local timezone in the app
            Timestamp endTime = appt.getValue().getEnd();
            ZoneId zone = ZoneId.systemDefault();
            ZonedDateTime zdt = endTime.toLocalDateTime().atZone(zone);
            ZoneOffset offset = zdt.getOffset();
            return Bindings.createStringBinding(() -> "" + endTime.toLocalDateTime() + " " + ZoneId.systemDefault());
        });
        AppointmentCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        AppointmentUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        AppointmentsTable.setItems(schedule.getAllAppointments());
    }


    /**
     * Sets up the customer table to be searched.
     * Lambda method creates an anonymous listener function to handle the search filtering
     */
    public void setupCustomerTableSearch(){
        //Set up anonymous callback function for the event listener on the Customers search field
        //Lambda expressions used to set up the search function inline
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

    /**
     * Sets up the appointment table to be searched.
     * Lambda method creates an anonymous listener function to handle the search filtering
     */
    public void setupAppointmentTableSearch(){
        //Set up anonymous callback function for the event listener on the Appointments search field
        //Lambda expressions used to set up the search function inline
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

    /**
     * Sets up the appointment table to be searched by date.
     * Lambda method creates an anonymous listener function to handle the search filtering
     */
    public void setupAppointmentTableDateSearch(){
        //Set up anonymous callback function for the event listener on the Appointments date filter
        //Lambda expressions used to set up the search function inline
        filteredAppointments = new FilteredList<>(schedule.getAllAppointments(), p -> true);
        dateFilter.valueProperty().addListener((observable, oldValue, newValue) -> filteredAppointments.setPredicate(Appointment -> {
                if (newValue == null) {
                    return true;
                }
                //user has passed in a date to newValue
                Locale locale = Locale.getDefault();
                weekNumberSelected = newValue.get(WeekFields.of(locale).weekOfWeekBasedYear());
                monthNumberSelected = newValue.getMonthValue();

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
