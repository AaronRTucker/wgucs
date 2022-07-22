/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.JDBC;
import ScheduleManager.Models.Appointment;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class AddCustomerController extends Controller {

    private Stage stage;

    private ResourceBundle bundle;
    private Scene scene;
    private Parent root;
    private final Schedule schedule;

    private int nextCustomerId;

    //add Customer data fields
    @FXML private Label varField;
    @FXML private TextField CustomerIdField;
    @FXML private TextField CustomerNameField;
    @FXML private TextField CustomerAddressField;
    @FXML private TextField CustomerPostalCodeField;
    @FXML private TextField CustomerPhoneNumberField;





    //Constructor for new Controller object
    public AddCustomerController(int nextCustomerId, Schedule schedule, Controller returnController){
        this.nextCustomerId = nextCustomerId;                                                    //set the index of the first Customer and Appointment IDs to be 1
        this.schedule = schedule;                                             //schedule object passed in from Main
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

        bundle = resourceBundle;
        CustomerIdField.setEditable(false);
        CustomerIdField.setText(String.valueOf(this.nextCustomerId));


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
                    Customer newCustomer = new Customer(id, name, address, postalCode, phoneNumber);
                    //this.schedule.addCustomer(newCustomer);

                    String sql = (
                            "INSERT INTO `client_schedule`.`customers` " +
                            "(Customer_ID, Customer_Name, Address, Postal_Code, Phone) " +
                            "VALUES ("+id+",'"+name+"','"+address+"','"+postalCode+"','"+phoneNumber+"')" );
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
