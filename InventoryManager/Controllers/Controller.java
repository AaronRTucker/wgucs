/**
 *
 * @author Aaron Tucker
 * @version 7.0
 */

package InventoryManager.Controllers;

import InventoryManager.DBHelper.JDBC;
import InventoryManager.Models.*;
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
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private final Inventory inventory;

    private int nextPartId;
    private int nextProductId;
    private Part selectedPart;
    private Product selectedProduct;

    //maintain list of temporary associated Parts
    private ObservableList<Part> temporaryAssociatedParts;

    //filtered parts and products list used in searches
    FilteredList<Part> filteredParts;
    FilteredList<Product> filteredProducts;

    /* FXML definitions to link variables from fxml files to the controller*/
    //
    //
    //main page tables
    @FXML private TableView<Part> partsTable;
    @FXML private TableView<Product> productsTable;
    @FXML private TableView<Part> associatedPartsTable;

    //part table columns
    @FXML private TableColumn<Part, Integer> partIdCol;
    @FXML private TableColumn<Part, String> partNameCol;
    @FXML private TableColumn<Part, Integer> partInvCol;
    @FXML private TableColumn<Part, Double> partPriceCol;

    //product table columns
    @FXML private TableColumn<Part, Integer> productIdCol;
    @FXML private TableColumn<Part, String> productNameCol;
    @FXML private TableColumn<Part, Integer> productInvCol;
    @FXML private TableColumn<Part, Double> productPriceCol;

    //associated Parts table columns
    @FXML private TableColumn<Part, Integer> associatedPartIdCol;
    @FXML private TableColumn<Part, String> associatedPartNameCol;
    @FXML private TableColumn<Part, Integer> associatedPartInvCol;
    @FXML private TableColumn<Part, Double> associatedPartPriceCol;

    //add Part data fields
    @FXML private Label varField;
    @FXML private TextField partIdField;
    @FXML private TextField partNameField;
    @FXML private TextField partInvField;
    @FXML private TextField partPriceField;
    @FXML private TextField partMinField;
    @FXML private TextField partMaxField;
    @FXML private TextField partSourcedField;
    private boolean inHouse;

    //add Product data fields
    @FXML private TextField productIdField;
    @FXML private TextField productNameField;
    @FXML private TextField productInvField;
    @FXML private TextField productPriceField;
    @FXML private TextField productMinField;
    @FXML private TextField productMaxField;

    //modify part radio buttons
    @FXML private RadioButton inHouseBtn;
    @FXML private RadioButton outsourcedBtn;

    //Search fields
    @FXML private TextField partSearchTextField;
    @FXML private TextField productSearchTextField;


    //Constructor for new Controller object
    public Controller(Inventory inventory){
        this.nextPartId = 1;                                                    //set the index of the first part and product IDs to be 1
        this.nextProductId = 1;
        this.inventory = inventory;                                             //inventory object passed in from Main
        this.temporaryAssociatedParts = FXCollections.observableArrayList();    //initialize the temporary parts arraylist

    }





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
        inHouse = true;             //set the default part type radio button to be inHouse

        //Change default table placeholder messages
        partsTable.setPlaceholder(new Label("Parts inventory is empty"));
        productsTable.setPlaceholder(new Label("Products inventory is empty"));

        if (partIdCol != null) {     //check to see if this scene has the parts data table in it
            //populate the table with parts data from the inventory
            partIdCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
            partNameCol.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
            partInvCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
            partPriceCol.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));
            partsTable.setItems(inventory.getAllParts());
        }

        if (productIdCol != null) {     //check to see if this scene has the parts data table in it
            //populate the table with products data from the inventory
            productIdCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
            productNameCol.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
            productInvCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
            productPriceCol.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));
            productsTable.setItems(inventory.getAllProducts());
        }

        if (associatedPartIdCol != null) {     //check to see if this scene has the parts data table in it
            //populate the table with data from the temporary associated parts list
            associatedPartIdCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
            associatedPartNameCol.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
            associatedPartInvCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
            associatedPartPriceCol.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));
            associatedPartsTable.setItems(temporaryAssociatedParts);
        }

        //Set up anonymous callback function for the event listener on the parts search field
        filteredParts = new FilteredList<>(inventory.getAllParts(), p -> true);
        partSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredParts.setPredicate(Part -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (Part.getName().toLowerCase().contains(lowerCaseFilter) || lowerCaseFilter.equals(String.valueOf(Part.getId()))) {    //if search text equals part ID or name
                    return true; // Filter matches name or ID.
                }
                return false; // Doesn't match
            });
        });
        SortedList<Part> sortedPartsData = new SortedList<>(filteredParts);
        sortedPartsData.comparatorProperty().bind(partsTable.comparatorProperty());
        partsTable.setItems(sortedPartsData);


        //Set up anonymous callback function for the event listener on the products search field
        filteredProducts = new FilteredList<>(inventory.getAllProducts(), p -> true);
        productSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProducts.setPredicate(Product -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (Product.getName().toLowerCase().contains(lowerCaseFilter) || lowerCaseFilter.equals(String.valueOf(Product.getId()))) {//if search text equals product ID or name
                    return true; // Filter matches name or ID.
                }
                return false; // Filter doesn't match.
            });
        });
        SortedList<Product> sortedProductsData = new SortedList<>(filteredProducts);
        sortedProductsData.comparatorProperty().bind(productsTable.comparatorProperty());
        productsTable.setItems(sortedProductsData);
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
     * Handles adding part
     * @param event the action event
     * @return void
     */
    @FXML
    public void addPartButtonPressed(ActionEvent event){
        loadScene(event, "InventoryManager/Views/addPart.fxml", 900, 475);

        //ID will be generated and incremented automatically
        //this code has to be run after the stage has been swapped, if run before it will throw a null error
        partIdField.setEditable(false);
        partIdField.setText(String.valueOf(this.nextPartId));

    }

    /**
     * Handles saving modified part
     * @param event the action event
     * @return void
     */
    @FXML
    public void modifyPartSavePressed(ActionEvent event){
        inventory.deletePart(selectedPart);
        addPartSavePressed(event);
    }

    /**
     * Handles cancelling out of modify part screen
     * @param event the action event
     * @throws IOException
     * @return void
     */
    @FXML
    public void modifyPartCancelPressed(ActionEvent event) throws IOException{
        addPartCancelPressed(event);
    }

    /**
     * Handles saving parts
     * @param event the action event
     * @return void
     */
    @FXML
    public void addPartSavePressed(ActionEvent event){
        int id = this.nextPartId;
        this.nextPartId++;
            try {
                String name = partNameField.getText();
                double price = Double.parseDouble(partPriceField.getText());
                int stock = Integer.parseInt(partInvField.getText());
                int min = Integer.parseInt(partMinField.getText());
                int max = Integer.parseInt(partMaxField.getText());

                if (stock < min || stock > max) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Inventory must be between min and max levels");
                    a.show();
                } else {
                    if (inHouse) {
                        int machineId = Integer.parseInt(partSourcedField.getText());
                        InHouse newInPart = new InHouse(id, name, price, stock, min, max, machineId);
                        this.inventory.addPart(newInPart);

                    } else {
                        String companyName = partSourcedField.getText();
                        Outsourced newOutPart = new Outsourced(id, name, price, stock, min, max, companyName);
                        this.inventory.addPart(newOutPart);
                    }
                    addPartCancelPressed(event);        //return to home screen if there are no errors
                }
            } catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Inappropriate user input: " + e.getMessage());
                a.show();
            }
        }

    /**
     * Handles saving products
     * @param event the action event
     * @return void
     */
    @FXML
    public void saveProductPressed(ActionEvent event){
        int id = this.nextProductId;
        this.nextProductId++;

        String name;
        double price;
        int stock;
        int min;
        int max;
        try {
            name = productNameField.getText();
            price = Double.parseDouble(productPriceField.getText());
            stock = Integer.parseInt(productInvField.getText());
            min = Integer.parseInt(productMinField.getText());
            max = Integer.parseInt(productMaxField.getText());

            if (stock < min || stock > max) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Inventory must be between min and max levels");
                a.show();
            }else{
                Product newProduct = new Product(id, name, price, stock, min, max);
                for (int i = 0; i < temporaryAssociatedParts.size(); i++) {
                    newProduct.addAssociatedPart(temporaryAssociatedParts.get(i));
                }
                this.inventory.addProduct(newProduct);
                addPartCancelPressed(event);        //return to home screen if there are no errors
            }
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Inappropriate user input: " + e.getMessage());
            a.show();
        }
    }

    /**
     * Handles modifying parts
     * @param event the action event
     * @return void
     */
    @FXML
    public void modifyPartButton(ActionEvent event){
        selectedPart = partsTable.getSelectionModel().getSelectedItem();

        Alert a;
        if(selectedPart == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select a part to modify");
            a.show();
        } else {
            loadScene(event, "InventoryManager/Views/modifyPart.fxml", 900, 475);

            partIdField.setEditable(false);
            partIdField.setText(String.valueOf(selectedPart.getId()));
            partNameField.setText(selectedPart.getName());
            partInvField.setText(String.valueOf(selectedPart.getStock()));
            partMaxField.setText(String.valueOf(selectedPart.getMax()));
            partMinField.setText(String.valueOf(selectedPart.getMin()));
            partPriceField.setText(String.valueOf(selectedPart.getPrice()));
            if(selectedPart.getClass().getName() == "InventoryManager.Models.Outsourced"){
                Outsourced outsourcedPart = (Outsourced)selectedPart;
                partSourcedField.setText(outsourcedPart.getCompanyName());
            } else {
                InHouse inHousePart = (InHouse)selectedPart;
                partSourcedField.setText(String.valueOf(inHousePart.getMachineId()));
            }

            //set radio button based on class type
            if(selectedPart.getClass().getName() == "InventoryManager.Models.InHouse"){
                varField.setText("Machine ID");
                inHouse = true;


            } else {
                varField.setText("Company Name");
                inHouse = false;
                inHouseBtn.setSelected(false);
                outsourcedBtn.setSelected(true);
            }
        }
    }

    /**
     * Handles deleting parts
     * @param event the action event
     * @return void
     */
    @FXML
    public void deletePartButton(ActionEvent event)
    {
        selectedPart = partsTable.getSelectionModel().getSelectedItem();
        Alert a;
        Optional<ButtonType> result;
        //Handle the error where no part is selected
        if(selectedPart == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select a part to delete");
            a.show();
        } else {
            a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Part deletion");
            a.setHeaderText("You are about to delete Part: " + selectedPart.getName());
            a.setContentText("Are you sure you want to do this?");
            result = a.showAndWait();
            if (result.get() == ButtonType.OK) {
                inventory.deletePart(selectedPart);
                //partsTable.getItems().remove( partsTable.getSelectionModel().getSelectedItem() );  //unnecessary, since the view is linked directly to the inventory
            }
        }
    }


    /**
     * Handles adding products
     * @param event the action event
     * @return void
     */
    @FXML
    public void addProductButtonPressed(ActionEvent event){
        loadScene(event,"InventoryManager/Views/addProduct.fxml", 900, 675 );

        //ID will be generated and incremented automatically
        //this code has to be run after the stage has been swapped, if run before it will throw a null error
        productIdField.setEditable(false);
        productIdField.setText(String.valueOf(this.nextProductId));
    }

    /**
     * Handles modifying products
     * @param event the action event
     * @return void
     */
    @FXML
    public void modifyProductButton(ActionEvent event){
        Alert a;
        selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if(selectedProduct == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select a product to modify");
            a.show();
        } else {
            temporaryAssociatedParts = selectedProduct.getAllAssociatedParts();
            loadScene(event, "InventoryManager/Views/modifyProduct.fxml", 900, 675);

            //ID will be generated and incremented automatically
            //this code has to be run after the stage has been swapped, if run before it will throw a null error

            productIdField.setEditable(false);
            productIdField.setText(String.valueOf(selectedProduct.getId()));
            productNameField.setText(selectedProduct.getName());
            productInvField.setText(String.valueOf(selectedProduct.getStock()));
            productMaxField.setText(String.valueOf(selectedProduct.getMax()));
            productMinField.setText(String.valueOf(selectedProduct.getMin()));
            productPriceField.setText(String.valueOf(selectedProduct.getPrice()));
        }

    }

    /**
     * Handles deleting products
     * @param event the action event
     * @return void
     */
    @FXML
    public void deleteProductButton(ActionEvent event)
    {
        selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        Alert a;
        Optional<ButtonType> result;
        //Handle the error where no product is selected
        if(selectedProduct == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select a product to delete");
            a.show();
            //Handle the error where a product has associate parts still
        } else if( selectedProduct.getAllAssociatedParts().size() > 0) {
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Cannot delete a product with associated parts");
            a.show();
        }else{  //everything is checked, just need to confirm deletion
            a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Product deletion");
            a.setHeaderText("You are about to delete Product: " + selectedProduct.getName());
            a.setContentText("Are you sure you want to do this?");
            result = a.showAndWait();
            if (result.get() == ButtonType.OK) {
                inventory.deleteProduct(selectedProduct);
            }
        }
    }


    /**
     * Handles switching to inhouse
     * @param event the action event
     * @return void
     */
    @FXML
    public void inHouseSelected(ActionEvent event){
        varField.setText("Machine ID");
        inHouse = true;
    }

    /**
     * Handles switching to outsourced
     * @param event the action event
     * @return void
     */
    @FXML
    public void outsourcedSelected(ActionEvent event){
        varField.setText("Company Name");
        inHouse = false;
    }

    /**
     * Handles cancelling out of the add part screen
     * @param event the action event
     * @return void
     */
    @FXML
    public void addPartCancelPressed(ActionEvent event){
        loadScene(event, "InventoryManager/Views/gui.fxml", 900, 475);
    }

    /**
     * Handles adding a part to a product
     * @param event the action event
     * @return void
     */
    @FXML
    public void addPartToProductBtnPressed(ActionEvent event){
        Part associatedSelectedPart = partsTable.getSelectionModel().getSelectedItem();
        Alert a;
        if(associatedSelectedPart == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select an associated part to add");
            a.show();
        } else {
            temporaryAssociatedParts.add(associatedSelectedPart);
        }
    }

    /**
     * Handles cancelling out of the add product screen
     * @param event the action event
     * @return void
     */
    @FXML
    public void addProductCancelPressed(ActionEvent event){
        addPartCancelPressed(event);
    }

    /**
     * Handles removing an associated part
     * @param event the action event
     * @return void
     */
    @FXML
    public void removeAssociatedPartPressed(ActionEvent event){
        Part associatedSelectedPart = associatedPartsTable.getSelectionModel().getSelectedItem();
        Alert a;
        Optional<ButtonType> result;
        if(associatedSelectedPart == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select an associated part to remove");
            a.show();
        } else {
            a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Associated part deletion");
            a.setHeaderText("You are about to remove Part: " + associatedSelectedPart.getName());
            a.setContentText("Are you sure you want to do this?");
            result = a.showAndWait();
            if (result.get() == ButtonType.OK) {
                temporaryAssociatedParts.remove( associatedSelectedPart );
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
     * Handles saving product modifications
     * @param event the action event
     * @return void
     */
    @FXML
    public void saveModifyProductPressed(ActionEvent event){
        inventory.deleteProduct(selectedProduct);
        saveProductPressed(event);
    }

    /**
     * Handles the part search error messages
     * @param event the action event
     * @return void
     */
    @FXML
    //Handle showing the error messages for no part search results and empty parts data table
    public void partSearchKeyTyped(KeyEvent event){
        if(!partSearchTextField.getText().isEmpty()){   //if there is something typed in the search box
            if(filteredParts.size() == 0){              //and there are no results
                partsTable.setPlaceholder(new Label("Nothing found in parts search"));
            }
        } else {  //there is something in the search box but no content
            partsTable.setPlaceholder(new Label("Parts inventory is empty"));
        }
    }

    /**
     * Handles the product search error messages
     * @param event the action event
     * @return void
     */
    @FXML
    //Handle showing the error messages for no product search results and empty product data table
    public void productSearchKeyTyped(KeyEvent event){
        if(!productSearchTextField.getText().isEmpty()){
            if(filteredProducts.size() == 0){
                productsTable.setPlaceholder(new Label("Nothing found in products search"));
            }
        } else {
            productsTable.setPlaceholder(new Label("Products inventory is empty"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(location));      //absolute reference for file path of scene
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
