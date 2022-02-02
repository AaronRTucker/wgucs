package InventoryManager.Controllers;

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

    @FXML
    public void pressExitButton(ActionEvent event){
        System.out.println("Application exiting");
        Platform.exit();
    }

    @FXML
    public void addPartButtonPressed(ActionEvent event) throws IOException{
        loadScene(event, "InventoryManager/Views/addPart.fxml", 900, 475);

        //ID will be generated and incremented automatically
        //this code has to be run after the stage has been swapped, if run before it will throw a null error
        partIdField.setEditable(false);
        partIdField.setText(String.valueOf(this.nextPartId));

    }

    public void modifyPartSavePressed(ActionEvent event) throws IOException{
        inventory.deletePart(selectedPart);
        addPartSavePressed(event);
    }
    @FXML
    public void modifyPartCancelPressed(ActionEvent event) throws IOException{
        addPartCancelPressed(event);
    }

    @FXML
    public void addPartSavePressed(ActionEvent event) throws IOException{
        int id = this.nextPartId;
        this.nextPartId++;

        try{
            String name = partNameField.getText();
            double price = Double.parseDouble(partPriceField.getText());
            int stock = Integer.parseInt(partInvField.getText());
            int min = Integer.parseInt(partMinField.getText());
            int max = Integer.parseInt(partMaxField.getText());

            if(stock < min || stock > max){
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Inventory must be between min and max levels");
                a.show();
            }


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

        } catch(Exception e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Inappropriate user input: " + e.getMessage());
            a.show();
        }
    }

    @FXML
    public void saveProductPressed(ActionEvent event) throws IOException{
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
            } else {
                Product newProduct = new Product(id, name, price, stock, min, max);
                for(int i = 0; i< temporaryAssociatedParts.size(); i++){
                    newProduct.addAssociatedPart(temporaryAssociatedParts.get(i));
                }
                this.inventory.addProduct(newProduct);
                addPartCancelPressed(event);        //return to home screen if there are no errors
            }
        } catch (Exception e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Inappropriate user input: " + e.getMessage());
            a.show();
        }
    }

    @FXML
    public void modifyPartButton(ActionEvent event) throws IOException {
        selectedPart = partsTable.getSelectionModel().getSelectedItem();

        Alert a;
        if(selectedPart == null){
            a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Please select a part to modify");
            a.show();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("InventoryManager/Views/modifyPart.fxml"));
            loader.setController(this);
            scene = new Scene((Pane)loader.load(), 900,475);
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

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


    @FXML
    public void addProductButtonPressed(ActionEvent event) throws IOException{
        loadScene(event,"InventoryManager/Views/addProduct.fxml", 900, 675 );

        //ID will be generated and incremented automatically
        //this code has to be run after the stage has been swapped, if run before it will throw a null error
        productIdField.setEditable(false);
        productIdField.setText(String.valueOf(this.nextProductId));
    }

    @FXML
    public void modifyProductButton(ActionEvent event) throws IOException{
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


    @FXML
    public void inHouseSelected(ActionEvent event){
        varField.setText("Machine ID");
        inHouse = true;
    }

    @FXML
    public void outsourcedSelected(ActionEvent event){
        varField.setText("Company Name");
        inHouse = false;
    }

    @FXML
    public void addPartCancelPressed(ActionEvent event) throws IOException{
        loadScene(event, "InventoryManager/Views/gui.fxml", 900, 475);
    }

    @FXML
    public void addPartToProductBtnPressed(ActionEvent event) throws IOException{
        temporaryAssociatedParts.add(partsTable.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void addProductCancelPressed(ActionEvent event) throws IOException{
        addPartCancelPressed(event);
    }
    @FXML
    public void removeAssociatedPartPressed(ActionEvent event) throws IOException{
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

    @FXML
    public void modifyCancelPressed(ActionEvent event){

    }

    @FXML
    public void saveModifyProductPressed(ActionEvent event) throws IOException{
        inventory.deleteProduct(selectedProduct);
        saveProductPressed(event);
    }

    @FXML
    //Handle showing the error messages for no part search results and empty parts data table
    public void partSearchKeyTyped(KeyEvent event) throws IOException{
        if(!partSearchTextField.getText().isEmpty()){   //if there is something typed in the search box
            if(filteredParts.size() == 0){              //and there are no results
                partsTable.setPlaceholder(new Label("Nothing found in parts search"));
            }
        } else {  //there is something in the search box but no content
            partsTable.setPlaceholder(new Label("Parts inventory is empty"));
        }
    }

    @FXML
    //Handle showing the error messages for no product search results and empty product data table
    public void productSearchKeyTyped(KeyEvent event) throws IOException{
        if(!productSearchTextField.getText().isEmpty()){
            if(filteredProducts.size() == 0){
                productsTable.setPlaceholder(new Label("Nothing found in products search"));
            }
        } else {
            productsTable.setPlaceholder(new Label("Products inventory is empty"));
        }
    }


    //Private helper function
    //Handle switching between fxml file scenes
    private void loadScene(ActionEvent event, String location, int width, int height) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(location));      //absolute reference for file path of scene
        loader.setController(this);
        scene = new Scene((Pane)loader.load(), width,height);                                       //set width and height of scene
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
