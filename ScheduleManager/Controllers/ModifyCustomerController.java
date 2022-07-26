/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.DatabaseQueryHelper;
import ScheduleManager.DBHelper.JDBC;
import ScheduleManager.Models.Customer;
import ScheduleManager.Models.Schedule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    public Button modifyCancel;
    public Button modifySave;
    ResourceBundle bundle;


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

    private final String userName;    //name to store in database associating which user added this customer

    private final Customer selectedCustomer;





    //Constructor for new Controller object
    public ModifyCustomerController(String userName, Customer selectedCustomer){
        this.selectedCustomer = selectedCustomer;
        this.userName = userName;
    }


    //LOGIN EVENT HANDLERS







    /**
     * Called every time a screen is loaded
     * @param url the url
     * @param resourceBundle the resource bundle
     */
    @Override
    public void initialize( URL url, ResourceBundle resourceBundle) {
        bundle = resourceBundle;

        CustomerIdField.setText(String.valueOf(selectedCustomer.getId()));
        CustomerNameField.setText(selectedCustomer.getName());
        CustomerAddressField.setText(selectedCustomer.getAddress());
        CustomerPhoneNumberField.setText(selectedCustomer.getPhoneNumber());
        CustomerPostalCodeField.setText(selectedCustomer.getPostalCode());
        selectedDivisionID = selectedCustomer.getDivision_ID();


        //get division name and country ID from database
        selectedDivision = DatabaseQueryHelper.getDivisionName(selectedDivisionID);
        selectedCountryID = DatabaseQueryHelper.getCountryIDFromDivisionID(selectedDivisionID);


        //get country name from database
        selectedCountry = DatabaseQueryHelper.getCountryName(selectedDivisionID);


        countryComboBox.setPromptText(selectedCountry);
        divisionComboBox.setPromptText(selectedDivision);


        CustomerIdField.setEditable(false);
        CustomerIdField.setText(String.valueOf(selectedCustomer.getId()));


        //Get country list from database
        ArrayList<String> countries = DatabaseQueryHelper.getCountries();

        //populate the countries combo box
        for (String country : countries) {
            countryComboBox.getItems().addAll(country);
        }


        //Get divisions for the selected country from the database
        ArrayList<String> divisions = DatabaseQueryHelper.getDivisions(selectedCountryID);

        //populate the division combo box
        for (String division : divisions) {
            divisionComboBox.getItems().addAll(division);
        }
    }



    /**
     * Handles country combobox
     * @param event the action event
     */
    @FXML
    public void countryBoxPressed(ActionEvent event) {
        selectedCountry = countryComboBox.getValue();
        divisionComboBox.getItems().clear();    //empty out any old values

        //once country is changed, division can't be kept valid
        selectedDivision="";
        divisionComboBox.setPromptText("Select a division");

        //Get country ID from selected country name
        selectedCountryID = DatabaseQueryHelper.getCountryID(selectedCountry);

        //Get divisions for the selected country from the database
        ArrayList<String> divisions = DatabaseQueryHelper.getDivisions(selectedCountryID);

        //populate the division combo box
        for (String division : divisions) {
            divisionComboBox.getItems().addAll(division);
        }

    }


    /**
     * Handles division combobox
     * @param event the action event
     */
    @FXML
    public void divisionBoxPressed(ActionEvent event) {
        selectedDivision = divisionComboBox.getValue();

        //Get division ID from selected division name
        selectedDivisionID = DatabaseQueryHelper.getDivisionID(selectedDivision);

    }




    /**
     * Handles saving Customers
     * @param event the action event
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
                    //Input is good

                    DatabaseQueryHelper.modifyCustomer(id, name, address, postalCode, phoneNumber, userName, selectedDivisionID);

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
     */
    @FXML
    public void modifyCustomerCancelPressed(ActionEvent event) {
        Controller c = new GuiController(userName);
        loadScene(c, event, "ScheduleManager/Views/gui.fxml", 900, 475, bundle);
    }


}
