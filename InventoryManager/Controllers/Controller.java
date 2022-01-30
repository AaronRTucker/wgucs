package InventoryManager.Controllers;

import InventoryManager.Models.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;



import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

    //maintain list of temporary associated Parts
    private ObservableList<Part> temporaryAssociatedParts;

    public Controller(Inventory inventory){
        this.nextPartId = 1;
        this.nextProductId = 1;
        this.inventory = inventory;
        this.temporaryAssociatedParts = FXCollections.observableArrayList();
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inHouse = true;

        if(partIdCol != null) {     //check to see if this scene has the parts data table in it

            partIdCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
            partNameCol.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
            partInvCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
            partPriceCol.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));
            partsTable.setItems(inventory.getAllParts());
        }

        if(productIdCol != null) {     //check to see if this scene has the parts data table in it

            productIdCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
            productNameCol.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
            productInvCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
            productPriceCol.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));
            productsTable.setItems(inventory.getAllProducts());
        }

        if(associatedPartIdCol != null) {     //check to see if this scene has the parts data table in it

            associatedPartIdCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
            associatedPartNameCol.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
            associatedPartInvCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
            associatedPartPriceCol.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));
            associatedPartsTable.setItems(temporaryAssociatedParts);
        }
    }

    @FXML
    public void pressExitButton(ActionEvent event){
        System.out.println("Application exiting");
        Platform.exit();
    }

    @FXML
    public void addPartButtonPressed(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("InventoryManager/Views/addPart.fxml"));
        loader.setController(this);
        scene = new Scene((Pane)loader.load(), 900,475);
        System.out.println(stage);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

        //ID will be generated and incremented automatically
        //this code has to be run after the stage has been swapped, if run before it will throw a null error
        partIdField.setEditable(false);
        partIdField.setText(String.valueOf(this.nextPartId));

    }

    public void modifyPartSavePressed(ActionEvent event) throws IOException{
        partsTable.getItems().remove( selectedPart );
        addPartSavePressed(event);
    }

    public void modifyPartCancelPressed(ActionEvent event) throws IOException{
        addPartCancelPressed(event);
    }

    @FXML
    public void addPartSavePressed(ActionEvent event) throws IOException{
        boolean error = false;
        System.out.println(partIdField.getText());
        int id = this.nextPartId;
        this.nextPartId++;
        String name = partNameField.getText();
        double price = Double.parseDouble(partPriceField.getText());
        int stock = Integer.parseInt(partInvField.getText());
        int min = Integer.parseInt(partMinField.getText());
        int max = Integer.parseInt(partMaxField.getText());

        if(stock < min || stock > max){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Inventory must be between min and max levels");
            a.show();
            error = true;
        }

        if(!error) {
            if (inHouse) {
                int machineId = Integer.parseInt(partSourcedField.getText());
                InHouse newInPart = new InHouse(id, name, price, stock, min, max, machineId);
                this.inventory.addPart(newInPart);

            } else {
                String companyName = partSourcedField.getText();
                Outsourced newOutPart = new Outsourced(id, name, price, stock, min, max, companyName);
                this.inventory.addPart(newOutPart);
            }
            //System.out.println("Inventory size: " + inventory.getAllParts().size());
            addPartCancelPressed(event);        //return to home screen if there are no errors
        }
    }

    @FXML
    public void modifyPartButton(ActionEvent event) throws IOException {
        selectedPart = partsTable.getSelectionModel().getSelectedItem();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("InventoryManager/Views/modifyPart.fxml"));
        loader.setController(this);
        scene = new Scene((Pane)loader.load(), 900,475);
        System.out.println(stage);
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

        System.out.println(selectedPart.getClass().getName());
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

    @FXML
    public void deletePartButton(ActionEvent event)
    {
        inventory.deletePart(selectedPart);
        partsTable.getItems().remove( partsTable.getSelectionModel().getSelectedItem() );
    }


    @FXML
    public void addProductButtonPressed(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("InventoryManager/Views/addProduct.fxml"));
        loader.setController(this);
        scene = new Scene((Pane)loader.load(), 900,675);
        System.out.println(stage);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

        //ID will be generated and incremented automatically
        //this code has to be run after the stage has been swapped, if run before it will throw a null error
        productIdField.setEditable(false);
        productIdField.setText(String.valueOf(this.nextProductId));
    }

    @FXML
    public void modifyProductButton(ActionEvent event) throws IOException{
        selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        temporaryAssociatedParts = selectedProduct.getAllAssociatedParts();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("InventoryManager/Views/modifyProduct.fxml"));
        loader.setController(this);
        scene = new Scene((Pane)loader.load(), 900,675);
        System.out.println(stage);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

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

    @FXML
    public void deleteProductButton(ActionEvent event)
    {
        selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        inventory.deleteProduct(selectedProduct);
        productsTable.getItems().remove( selectedProduct );
    }

    @FXML
    public void saveProductPressed(ActionEvent event) throws IOException{
        boolean error = false;
        int id = this.nextProductId;
        this.nextProductId++;
        String name = productNameField.getText();
        double price = Double.parseDouble(productPriceField.getText());
        int stock = Integer.parseInt(productInvField.getText());
        int min = Integer.parseInt(productMinField.getText());
        int max = Integer.parseInt(productMaxField.getText());

        if(stock < min || stock > max){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Inventory must be between min and max levels");
            a.show();
            error = true;
        }

        if(!error) {
            Product newProduct = new Product(id, name, price, stock, min, max);
            for(int i = 0; i< temporaryAssociatedParts.size(); i++){
                newProduct.addAssociatedPart(temporaryAssociatedParts.get(i));
            }
            this.inventory.addProduct(newProduct);
            addPartCancelPressed(event);        //return to home screen if there are no errors
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
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("InventoryManager/Views/gui.fxml"));     //using absolute file references instead of relative for jar export to work
        //Controller c = new Controller(inventory);
        loader.setController(this);
        scene = new Scene((Pane)loader.load(), 900,475);
        System.out.println(stage);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void addPartToProductBtnPressed(ActionEvent event) throws IOException{
        temporaryAssociatedParts.add(partsTable.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void addProductCancelPressed(ActionEvent event) throws IOException{
        addPartCancelPressed(event);
    }

    @FXML public void removeAssociatedPartPressed(ActionEvent event) throws IOException{
        temporaryAssociatedParts.remove( associatedPartsTable.getSelectionModel().getSelectedItem() );
    }

    @FXML
    public void modifyCancelPressed(ActionEvent event){

    }

    @FXML
    public void saveModifyProductPressed(ActionEvent event) throws IOException{
        inventory.deleteProduct(selectedProduct);
        productsTable.getItems().remove( selectedProduct );
        saveProductPressed(event);
    }
}
