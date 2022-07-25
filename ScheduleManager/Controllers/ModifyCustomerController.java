/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.JDBC;
import ScheduleManager.Models.Customer;
import ScheduleManager.Models.Schedule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ModifyCustomerController extends Controller {

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

    private Customer selectedCustomer;





    //Constructor for new Controller object
    public ModifyCustomerController(Schedule schedule, Controller returnController, String userName, Customer selectedCustomer){
        this.selectedCustomer = selectedCustomer;
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

        CustomerIdField.setText(String.valueOf(selectedCustomer.getId()));
        CustomerNameField.setText(selectedCustomer.getName());
        CustomerAddressField.setText(selectedCustomer.getAddress());
        CustomerPhoneNumberField.setText(selectedCustomer.getPhoneNumber());
        CustomerPostalCodeField.setText(selectedCustomer.getPostalCode());
        selectedDivisionID = selectedCustomer.getDivision_ID();

        //get division name and country ID from database
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`first_level_divisions` WHERE Division_ID = " + selectedDivisionID + ";");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                selectedDivision = result.getString("Division");
                selectedCountryID = result.getInt("Country_ID");
            }

        } catch(SQLException e){
            System.out.println(e);
        }

        //get country name from database
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`countries` WHERE Country_ID = " + selectedCountryID + ";");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                selectedCountry = result.getString("Country");
            }

        } catch(SQLException e){
            System.out.println(e);
        }


        countryComboBox.setPromptText(selectedCountry);
        divisionComboBox.setPromptText(selectedDivision);


        bundle = resourceBundle;
        CustomerIdField.setEditable(false);
        CustomerIdField.setText(String.valueOf(selectedCustomer.getId()));


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


        divisions = new ArrayList<>();  //empty out any old values
        divisionComboBox.getItems().clear();    //empty out any old values
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

        //once country is changed, division can't be kept valid
        selectedDivision="";
        divisionComboBox.setPromptText("Select a division");

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
    public void modifyCustomerSavePressed(ActionEvent event){
        int id = selectedCustomer.getId();
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


                    String sql = ("UPDATE client_schedule.customers SET " +
                            "Customer_Name='"+name+"', " +
                            "Address='"+address+"', " +
                            "Postal_Code='"+postalCode+"', " +
                            "Phone = '"+phoneNumber+"', " +
                            "Created_By = '"+userName+"', " +
                            "Last_Update = '"+timestamp+"', " +
                            "Last_Updated_By = '"+userName+"'," +
                            "Division_ID = "+selectedDivisionID+" " +
                            "WHERE Customer_ID = " + id + " ");
                    System.out.println(sql);

                    try {
                        Connection conn = JDBC.connection;
                        Statement stmt = conn.createStatement();
                        stmt.executeUpdate(sql);
                        System.out.println("Modified records in the table...");

                    } catch(SQLException e){
                        System.out.println(e);
                    }

                    modifyCustomerCancelPressed(event);        //return to home screen if there are no errors
                }
            } catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Inappropriate user input: " + e.getMessage());
                a.show();
            }
        }


    /**
     * Handles cancelling out of modify Customer screen
     * @param event the action event
     * @throws IOException
     * @return void
     */
    @FXML
    public void modifyCustomerCancelPressed(ActionEvent event) throws IOException{
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
