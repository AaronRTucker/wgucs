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
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.net.URL;
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
            a.setContentText("Please select a Customer to delete");
            a.show();
        } else {
            a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Customer deletion");
            a.setHeaderText("You are about to delete Customer: " + selectedCustomer.getName());
            a.setContentText("Are you sure you want to do this?");
            result = a.showAndWait();
            if(result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    DatabaseQueryHelper.deleteCustomer(schedule, selectedCustomer);//delete the customer from the database
                }
            }
        }
    }


    /**
     * Handles adding Appointments
     * @param event the action event
     */
    @FXML
    public void addAppointmentButtonPressed(ActionEvent event){
        AddAppointmentController c = new AddAppointmentController(nextAppointmentId, userName);
        loadScene(c, event, "ScheduleManager/Views/addAppointment.fxml", 900, 675, bundle );

        //ID will be generated and incremented automatically
        //this code has to be run after the stage has been swapped, if run before it will throw a null error
        //AppointmentIdField.setEditable(false);
        //AppointmentIdField.setText(String.valueOf(this.nextAppointmentId));
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
            //temporaryAssociatedCustomers = selectedAppointment.getAllAssociatedCustomers();
            //loadScene(this.event, "ScheduleManager/Views/modifyAppointment.fxml", 900, 675);

            //ID will be generated and incremented automatically
            //this code has to be run after the stage has been swapped, if run before it will throw a null error
            /*
            AppointmentIdField.setEditable(false);
            AppointmentIdField.setText(String.valueOf(selectedAppointment.getId()));
            AppointmentTitleField.setText(selectedAppointment.getTitle());
            AppointmentDescriptionField.setText(selectedAppointment.getDescription());
            AppointmentLocationField.setText(selectedAppointment.getLocation());
            AppointmentTypeField.setText(selectedAppointment.getType());
            AppointmentStartField.setText(String.valueOf(selectedAppointment.getStart()));
            AppointmentEndField.setText(String.valueOf(selectedAppointment.getEnd()));
            AppointmentCustomerIdField.setText(String.valueOf(selectedAppointment.getCustomerId()));
            AppointmentUserIdField.setText(String.valueOf(selectedAppointment.getUserId()));

            */
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
            a.setContentText("Please select a Appointment to delete");
            a.show();
            //Handle the error where a Appointment has associate Customers still
        } else if( selectedAppointment.getAllAssociatedCustomers().size() > 0) {
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Cannot delete a Appointment with associated Customers");
            a.show();
        }else{  //everything is checked, just need to confirm deletion
            a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Appointment deletion");
            a.setHeaderText("You are about to delete Appointment: " + selectedAppointment.getTitle());
            a.setContentText("Are you sure you want to do this?");
            result = a.showAndWait();
            if(result.isPresent()){
                if (result.get() == ButtonType.OK) {
                    schedule.deleteAppointment(selectedAppointment);
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
}
