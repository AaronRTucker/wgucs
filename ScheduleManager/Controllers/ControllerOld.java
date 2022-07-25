/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.JDBC;
import ScheduleManager.Models.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;



import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ControllerOld implements Initializable {

    private Stage stage;

    private ResourceBundle bundle;
    private Scene scene;
    private Parent root;
    private final Schedule schedule;

    private int nextCustomerId;
    private int nextAppointmentId;
    private Customer selectedCustomer;
    private Appointment selectedAppointment;

    //maintain list of temporary associated Customers
    private ObservableList<Customer> temporaryAssociatedCustomers;

    //filtered Customers and Appointments list used in searches
    FilteredList<Customer> filteredCustomers;
    FilteredList<Appointment> filteredAppointments;

    /* FXML definitions to link variables from fxml files to the controller*/
    //
    //
    //main page tables
    @FXML private TableView<Customer> CustomersTable;
    @FXML private TableView<Appointment> AppointmentsTable;
    @FXML private TableView<Customer> associatedCustomersTable;

    //Customer table columns
    @FXML private TableColumn<Customer, Integer> CustomerIdCol;
    @FXML private TableColumn<Customer, String> CustomerNameCol;
    @FXML private TableColumn<Customer, String> CustomerAddressCol;
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

    //associated Customers table columns

    @FXML private TableColumn<Customer, Integer> AssociatedCustomerIdCol;
    @FXML private TableColumn<Customer, String> AssociatedCustomerNameCol;
    @FXML private TableColumn<Customer, String> AssociatedCustomerAddressCol;
    @FXML private TableColumn<Customer, String> AssociatedCustomerPostalCodeCol;
    @FXML private TableColumn<Customer, String> AssociatedCustomerPhoneNumberCol;

    //add Customer data fields
    @FXML private Label varField;
    @FXML private TextField CustomerIdField;
    @FXML private TextField CustomerNameField;
    @FXML private TextField CustomerAddressField;
    @FXML private TextField CustomerPostalCodeField;
    @FXML private TextField CustomerPhoneNumberField;


    //add Appointment data fields
    @FXML private TextField AppointmentIdField;
    @FXML private TextField AppointmentTitleField;
    @FXML private TextField AppointmentDescriptionField;
    @FXML private TextField AppointmentLocationField;
    @FXML private TextField AppointmentTypeField;
    @FXML private TextField AppointmentStartField;
    @FXML private TextField AppointmentEndField;
    @FXML private TextField AppointmentCustomerIdField;
    @FXML private TextField AppointmentUserIdField;




    //Search fields
    @FXML private TextField CustomerSearchTextField;
    @FXML private TextField AppointmentSearchTextField;


    //Constructor for new Controller object
    public ControllerOld(Schedule schedule){
        this.nextCustomerId = 1;                                                    //set the index of the first Customer and Appointment IDs to be 1
        this.nextAppointmentId = 1;
        this.schedule = schedule;                                             //schedule object passed in from Main
        this.temporaryAssociatedCustomers = FXCollections.observableArrayList();    //initialize the temporary Customers arraylist

    }


    //LOGIN EVENT HANDLERS







    /**
     * Called every time a screen is loaded
     * @futureenhancement add more search fields to find specific quantities
     * @runtimeerror discovered that file paths to fxml scenes need to be absolute instead of relative to work
     * @param url the url
     * @param resourceBundle the resource bundle
     * @return void
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        bundle = resourceBundle;


        //Change default table placeholder messages
        CustomersTable.setPlaceholder(new Label("Customers list is empty"));
        AppointmentsTable.setPlaceholder(new Label("Appointments list is empty"));

        if (CustomerIdCol != null) {     //check to see if this scene has the Customers data table in it
            //populate the table with Customers data from the schedule
            CustomerIdCol.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("id"));
            CustomerNameCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
            CustomerAddressCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("address"));
            CustomerPostalCodeCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("postalCode"));
            CustomerPhoneNumberCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("phoneNumber"));
            CustomersTable.setItems(schedule.getAllCustomers());
        }

        if (AppointmentIdCol != null) {     //check to see if this scene has the Customers data table in it
            //populate the table with Appointments data from the schedule
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

        if (AssociatedCustomerIdCol != null) {     //check to see if this scene has the Customers data table in it
            //populate the table with data from the temporary associated Customers list
            AssociatedCustomerIdCol.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("id"));
            AssociatedCustomerNameCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
            AssociatedCustomerAddressCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("address"));
            AssociatedCustomerPostalCodeCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("postalCode"));
            AssociatedCustomerPhoneNumberCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("phoneNumber"));
            associatedCustomersTable.setItems(temporaryAssociatedCustomers);
        }

        //Set up anonymous callback function for the event listener on the Customers search field
        filteredCustomers = new FilteredList<>(schedule.getAllCustomers(), p -> true);
        CustomerSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredCustomers.setPredicate(Customer -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (Customer.getName().toLowerCase().contains(lowerCaseFilter) || lowerCaseFilter.equals(String.valueOf(Customer.getId()))) {    //if search text equals Customer ID or name
                    return true; // Filter matches name or ID.
                }
                return false; // Doesn't match
            });
        });
        SortedList<Customer> sortedCustomersData = new SortedList<>(filteredCustomers);
        sortedCustomersData.comparatorProperty().bind(CustomersTable.comparatorProperty());
        CustomersTable.setItems(sortedCustomersData);


        //Set up anonymous callback function for the event listener on the Appointments search field
        filteredAppointments = new FilteredList<>(schedule.getAllAppointments(), p -> true);
        AppointmentSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredAppointments.setPredicate(Appointment -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (Appointment.getTitle().toLowerCase().contains(lowerCaseFilter) || lowerCaseFilter.equals(String.valueOf(Appointment.getId()))) {//if search text equals Appointment ID or name
                    return true; // Filter matches title or ID.
                }
                return false; // Filter doesn't match.
            });
        });
        SortedList<Appointment> sortedAppointmentsData = new SortedList<>(filteredAppointments);
        sortedAppointmentsData.comparatorProperty().bind(AppointmentsTable.comparatorProperty());
        AppointmentsTable.setItems(sortedAppointmentsData);


    }

    /**
     * Handle exiting the application
     * @param event the action event
     * @return void
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
     * @return void
     */
    @FXML
    public void loginSubmitPressed(ActionEvent event){
        System.out.println("Logging in");
    }

    /**
     * Handles adding Customer
     * @param event the action event
     * @return void
     */
    @FXML
    public void addCustomerButtonPressed(ActionEvent event){
        loadScene(event, "ScheduleManager/Views/addCustomer.fxml", 900, 475);

        //ID will be generated and incremented automatically
        //this code has to be run after the stage has been swapped, if run before it will throw a null error
        CustomerIdField.setEditable(false);
        CustomerIdField.setText(String.valueOf(this.nextCustomerId));

    }

    /**
     * Handles saving modified Customer
     * @param event the action event
     * @return void
     */
    @FXML
    public void modifyCustomerSavePressed(ActionEvent event){
        schedule.deleteCustomer(selectedCustomer);
        addCustomerSavePressed(event);
    }

    /**
     * Handles cancelling out of modify Customer screen
     * @param event the action event
     * @throws IOException
     * @return void
     */
    @FXML
    public void modifyCustomerCancelPressed(ActionEvent event) throws IOException{
        addCustomerCancelPressed(event);
    }

    /**
     * Handles saving Customers
     * @param event the action event
     * @return void
     */
    @FXML
    public void addCustomerSavePressed(ActionEvent event){
        int id = this.nextCustomerId;
        this.nextCustomerId++;
            try {
                String name = CustomerNameField.getText();
                String address = CustomerAddressField.getText();
                String postalCode = CustomerPostalCodeField.getText();
                String phoneNumber = CustomerPhoneNumberField.getText();




                if (name.equals("") || address.equals("") || postalCode.equals("") || phoneNumber.equals("")) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Text Fields must not be empty");
                    a.show();
                } else {
                    Customer newCustomer = new Customer(id, name, address, postalCode, phoneNumber, -1);    //don't use this!
                    this.schedule.addCustomer(newCustomer);
                    addCustomerCancelPressed(event);        //return to home screen if there are no errors
                }
            } catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Inappropriate user input: " + e.getMessage());
                a.show();
            }
        }

    /**
     * Handles saving Appointments
     * @param event the action event
     * @return void
     */
    @FXML
    public void saveAppointmentPressed(ActionEvent event){

        /*
        int id = this.nextAppointmentId;
        this.nextAppointmentId++;

        String name;
        double price;
        int stock;
        int min;
        int max;
        try {
            name = AppointmentNameField.getText();
            price = Double.parseDouble(AppointmentPriceField.getText());
            stock = Integer.parseInt(AppointmentInvField.getText());
            min = Integer.parseInt(AppointmentMinField.getText());
            max = Integer.parseInt(AppointmentMaxField.getText());

            if (stock < min || stock > max) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("schedule must be between min and max levels");
                a.show();
            }else{
                Appointment newAppointment = new Appointment(id, name, price, stock, min, max);
                for (int i = 0; i < temporaryAssociatedCustomers.size(); i++) {
                    newAppointment.addAssociatedCustomer(temporaryAssociatedCustomers.get(i));
                }
                this.schedule.addAppointment(newAppointment);
                addCustomerCancelPressed(event);        //return to home screen if there are no errors
            }
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Inappropriate user input: " + e.getMessage());
            a.show();
        }


         */
    }

    /**
     * Handles modifying Customers
     * @param event the action event
     * @return void
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
            loadScene(event, "ScheduleManager/Views/modifyCustomer.fxml", 900, 475);

            CustomerIdField.setEditable(false);
            CustomerIdField.setText(String.valueOf(selectedCustomer.getId()));
            CustomerNameField.setText(selectedCustomer.getName());
            CustomerAddressField.setText(selectedCustomer.getAddress());
            CustomerPhoneNumberField.setText(selectedCustomer.getPhoneNumber());
            CustomerPostalCodeField.setText(selectedCustomer.getPostalCode());

        }
    }

    /**
     * Handles deleting Customers
     * @param event the action event
     * @return void
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
            if (result.get() == ButtonType.OK) {
                schedule.deleteCustomer(selectedCustomer);
                //CustomersTable.getItems().remove( CustomersTable.getSelectionModel().getSelectedItem() );  //unnecessary, since the view is linked directly to the schedule
            }
        }
    }


    /**
     * Handles adding Appointments
     * @param event the action event
     * @return void
     */
    @FXML
    public void addAppointmentButtonPressed(ActionEvent event){
        loadScene(event, "ScheduleManager/Views/addAppointment.fxml", 900, 675 );

        //ID will be generated and incremented automatically
        //this code has to be run after the stage has been swapped, if run before it will throw a null error
        AppointmentIdField.setEditable(false);
        AppointmentIdField.setText(String.valueOf(this.nextAppointmentId));
    }

    /**
     * Handles modifying Appointments
     * @param event the action event
     * @return void
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
            temporaryAssociatedCustomers = selectedAppointment.getAllAssociatedCustomers();
            loadScene(event, "ScheduleManager/Views/modifyAppointment.fxml", 900, 675);

            //ID will be generated and incremented automatically
            //this code has to be run after the stage has been swapped, if run before it will throw a null error

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
        }

    }

    /**
     * Handles deleting Appointments
     * @param event the action event
     * @return void
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
            if (result.get() == ButtonType.OK) {
                schedule.deleteAppointment(selectedAppointment);
            }
        }
    }


    /**
     * Handles cancelling out of the add Customer screen
     * @param event the action event
     * @return void
     */
    @FXML
    public void addCustomerCancelPressed(ActionEvent event){
        loadScene(event, "ScheduleManager/Views/gui.fxml", 900, 475);
    }

    /**
     * Handles adding a Customer to a Appointment
     * @param event the action event
     * @return void
     */
    @FXML
    public void addCustomerToAppointmentBtnPressed(ActionEvent event){
        Customer associatedSelectedCustomer = CustomersTable.getSelectionModel().getSelectedItem();
        Alert a;
        if(associatedSelectedCustomer == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select an associated Customer to add");
            a.show();
        } else {
            temporaryAssociatedCustomers.add(associatedSelectedCustomer);
        }
    }

    /**
     * Handles cancelling out of the add Appointment screen
     * @param event the action event
     * @return void
     */
    @FXML
    public void addAppointmentCancelPressed(ActionEvent event){
        addCustomerCancelPressed(event);
    }

    /**
     * Handles removing an associated Customer
     * @param event the action event
     * @return void
     */
    @FXML
    public void removeAssociatedCustomerPressed(ActionEvent event){
        Customer associatedSelectedCustomer = associatedCustomersTable.getSelectionModel().getSelectedItem();
        Alert a;
        Optional<ButtonType> result;
        if(associatedSelectedCustomer == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select an associated Customer to remove");
            a.show();
        } else {
            a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Associated Customer deletion");
            a.setHeaderText("You are about to remove Customer: " + associatedSelectedCustomer.getName());
            a.setContentText("Are you sure you want to do this?");
            result = a.showAndWait();
            if (result.get() == ButtonType.OK) {
                temporaryAssociatedCustomers.remove( associatedSelectedCustomer );
            }
        }
    }

    /**
     * Handles cancelling out of modify scenes
     * @param event the action event
     * @return void
     */
    @FXML
    public void modifyCancelPressed(ActionEvent event){

    }

    /**
     * Handles saving Appointment modifications
     * @param event the action event
     * @return void
     */
    @FXML
    public void saveModifyAppointmentPressed(ActionEvent event){
        schedule.deleteAppointment(selectedAppointment);
        saveAppointmentPressed(event);
    }

    /**
     * Handles the Customer search error messages
     * @param event the action event
     * @return void
     */
    @FXML
    //Handle showing the error messages for no Customer search results and empty Customers data table
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
     * @return void
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


    /**
     * Loads a new scene with a given file, width, and height
     * @param event the action event
     * @param location the location of the scene file
     * @param width the width of the scene
     * @param height the height of the scene
     * @return void
     * @RUNTIME ERROR
     */
    //Private helper function
    //Handle switching between fxml file scenes
    private void loadScene(ActionEvent event, String location, int width, int height){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(location), bundle);      //absolute reference for file path of scene
            loader.setController(this);
            scene = new Scene((Pane) loader.load(), width, height);                                       //set width and height of scene
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception){
            System.out.println(exception);
        }
    }
}
