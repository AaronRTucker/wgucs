/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package ScheduleManager.Controllers;

import ScheduleManager.DBHelper.DatabaseQueryHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddCustomerController extends Controller {

    private ResourceBundle bundle;

    private int nextCustomerId;

    //add Customer data fields
    @FXML private Label varField;
    @FXML private TextField CustomerIdField;
    @FXML private TextField CustomerNameField;
    @FXML private TextField CustomerAddressField;
    @FXML private TextField CustomerPostalCodeField;
    @FXML private TextField CustomerPhoneNumberField;

    @FXML private ComboBox<String> countryComboBox;

    @FXML private ComboBox<String> divisionComboBox;

    private int selectedDivisionID;

    private final String userName;    //name to store in database associating which user added this customer





    //Constructor for new Controller object
    public AddCustomerController(int nextCustomerId, String userName){
        this.nextCustomerId = nextCustomerId;                                                    //set the index of the first Customer and Appointment IDs to be 1
        this.userName = userName;

    }




    /**
     * Called every time a screen is loaded
     * @param url the url
     * @param resourceBundle the resource bundle
     */
    @Override
    public void initialize( URL url, ResourceBundle resourceBundle) {

        selectedDivisionID = -1;        //used to check if division has been selected before creating customer

        bundle = resourceBundle;
        CustomerIdField.setEditable(false);
        CustomerIdField.setText(String.valueOf(this.nextCustomerId));

        countryComboBox.setPromptText("Select a country");


        //Get countries list from database
        ArrayList<String> countries = DatabaseQueryHelper.getCountries();


        //populate the countries combo box
        for (String country : countries) {
            countryComboBox.getItems().addAll(country);
        }

        //Set the default message for division box where country hasn't been selected yet
        divisionComboBox.getItems().addAll("Select a Country first");

    }


    //EVENT HANDLERS

    /**
     * Handles country combobox
     * @param event the action event
     */
    @FXML
    public void countryBoxPressed(ActionEvent event) {
        String selectedCountry = countryComboBox.getValue();
        divisionComboBox.getItems().clear();    //empty out any old values

        //Get country ID from selected country name
        int selectedCountryID = DatabaseQueryHelper.getCountryID(selectedCountry);

        //get divisions list from database
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
        String selectedDivision = divisionComboBox.getValue();

        selectedDivisionID = DatabaseQueryHelper.getDivisionID(selectedDivision);

    }




    /**
     * Handles saving Customers
     * @param event the action event
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


                //Handle any input errors

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

                    DatabaseQueryHelper.addCustomer(id, name, address, postalCode, phoneNumber, userName, selectedDivisionID);

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
     */
    @FXML
    public void addCustomerCancelPressed(ActionEvent event){
        Controller c = new GuiController(userName);
        loadScene(c, event, "ScheduleManager/Views/gui.fxml", 900, 475, bundle);
    }

}
