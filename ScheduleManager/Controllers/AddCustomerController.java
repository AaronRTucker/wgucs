/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.JDBC;
import ScheduleManager.Models.Appointment;
import ScheduleManager.Models.Country;
import ScheduleManager.Models.Customer;
import ScheduleManager.Models.Schedule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddCustomerController extends Controller {

    private Stage stage;

    private ResourceBundle bundle;
    private Scene scene;
    private Parent root;
    private final Schedule schedule;

    private int nextCustomerId;

    private ArrayList<String> countries;
    private ArrayList<String> divisions;

    //add Customer data fields
    @FXML private Label varField;
    @FXML private TextField CustomerIdField;
    @FXML private TextField CustomerNameField;
    @FXML private TextField CustomerAddressField;
    @FXML private TextField CustomerPostalCodeField;
    @FXML private TextField CustomerPhoneNumberField;

    @FXML private ComboBox<String> countryComboBox;

    @FXML private ComboBox<String> divisionComboBox;

    private String selectedCountry;
    private int selectedCountryID;
    private String selectedDivision;
    private int selectedDivisionID;

    private String userName;    //name to store in database associating which user added this customer





    //Constructor for new Controller object
    public AddCustomerController(int nextCustomerId, Schedule schedule, Controller returnController, String userName){
        this.nextCustomerId = nextCustomerId;                                                    //set the index of the first Customer and Appointment IDs to be 1
        this.schedule = schedule;                                             //schedule object passed in from Main
        this.userName = userName;
        this.returnController = returnController;

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
    public void initialize( URL url, ResourceBundle resourceBundle) {

        selectedDivisionID = -1;        //used to check if division has been selected before creating customer

        bundle = resourceBundle;
        CustomerIdField.setEditable(false);
        CustomerIdField.setText(String.valueOf(this.nextCustomerId));

        countryComboBox.setPromptText("Select a country");



        countries = new ArrayList<>();



        //Get country list from database
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`countries`");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                countryComboBox.getItems().addAll(result.getString("Country"));
                countries.add(result.getString("Country"));
            }

            divisionComboBox.getItems().addAll("Select a Country first");

        } catch(SQLException e){
            System.out.println(e);
        }
    }


    /**
     * Handles country combobox
     * @param event the action event
     * @return void
     */
    @FXML
    public void countryBoxPressed(ActionEvent event) {
        //System.out.println(countryComboBox.getValue());
        selectedCountry = countryComboBox.getValue();
        divisions = new ArrayList<>();  //empty out any old values
        divisionComboBox.getItems().clear();    //empty out any old values

        //Get country ID from selected country name
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`countries` WHERE Country = '" + selectedCountry + "';");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                selectedCountryID = result.getInt("Country_ID");
            }

        } catch(SQLException e){
            System.out.println(e);
        }

        //Get division list from database
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`first_level_divisions` WHERE Country_ID = " + selectedCountryID + ";");
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                divisionComboBox.getItems().addAll(result.getString("Division"));
                divisions.add(result.getString("Division"));
            }

        } catch(SQLException e){
            System.out.println(e);
        }

    }


    /**
     * Handles division combobox
     * @param event the action event
     * @return void
     */
    @FXML
    public void divisionBoxPressed(ActionEvent event) {
        selectedDivision = divisionComboBox.getValue();

        //Get division ID from selected division name
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`first_level_divisions` WHERE Division = '" + selectedDivision + "';");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                selectedDivisionID = result.getInt("Division_ID");
            }

        } catch(SQLException e){
            System.out.println(e);
        }

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
                } else if(selectedDivisionID == -1){
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Please select a state");
                    a.show();
                } else {

                    //Not needed, we're going to just refresh from the database rather than store this persistently in the program
                    //Customer newCustomer = new Customer(id, name, address, postalCode, phoneNumber, selectedDivisionID);
                    //this.schedule.addCustomer(newCustomer);


                    Instant instant = Instant.now() ;                           //get the current moment
                    OffsetDateTime odt = instant.atOffset( ZoneOffset.UTC ) ;   //get the current moment translated to UTC
                    Timestamp timestamp = Timestamp.valueOf(odt.toLocalDateTime());  //convert the current moment into a legacy format due to database datatype restrictions

                    String sql = (          //create_date uses datetime, last_update uses timestamp.  Only timestamp is aware of timezone information
                            "INSERT INTO `client_schedule`.`customers` " +
                            "(Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By,  Division_ID) " +
                            "VALUES ("+id+",'"+name+"','"+address+"','"+postalCode+"','"+phoneNumber+"','"+timestamp+"','"+userName+"','"+timestamp+"','"+userName+"','"+selectedDivisionID+"')" );
                    System.out.println(sql);

                    try {
                        Connection conn = JDBC.connection;
                        Statement stmt = conn.createStatement();
                        stmt.executeUpdate(sql);
                        System.out.println("Inserted records into the table...");

                    } catch(SQLException e){
                        System.out.println(e);
                    }

                    addCustomerCancelPressed(event);        //return to home screen if there are no errors
                }
            } catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Inappropriate user input: " + e.getMessage());
                a.show();
            }
        }








    /**
     * Handles cancelling out of the add Customer screen
     * @param event the action event
     * @return void
     */
    @FXML
    public void addCustomerCancelPressed(ActionEvent event){
        this.loadScene(event, "ScheduleManager/Views/gui.fxml", 900, 475);
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
            loader.setController(returnController);
            scene = new Scene((Pane) loader.load(), width, height);                                       //set width and height of scene
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception){
            System.out.println(exception);
        }
    }
}
