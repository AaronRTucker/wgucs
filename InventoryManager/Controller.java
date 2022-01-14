package InventoryManager;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private final Inventory inventory;

    //main page tables
    @FXML private TableView<Part> partsTable;
    @FXML private TableView<Product> productsTable;

    //part table columns
    @FXML private TableColumn<Part, Integer> partIdCol;
    @FXML private TableColumn<Part, String> partNameCol;
    @FXML private TableColumn<Part, Integer> partInvCol;
    @FXML private TableColumn<Part, Double> partPriceCol;

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

    public Controller(Inventory inventory){
        this.inventory = inventory;
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Inventory ID: " + inventory);
        inHouse = true;
        System.out.println("Inventory size: " + inventory.getAllParts().size());
        for(int i = 0; i < inventory.getAllParts().size(); i++) {
            System.out.println(inventory.getAllParts().get(i).getName());
        }


        if(partIdCol != null) {     //check to see if this scene has the data table in it

            partIdCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
            partNameCol.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
            partInvCol.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
            partPriceCol.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));
            partsTable.setItems(inventory.getAllParts());
        }
    }

    @FXML
    public void pressExitButton(ActionEvent event){
        System.out.println("Application exiting");
        Platform.exit();
    }

    @FXML
    public void addPartButtonPressed(ActionEvent event) throws IOException{
        System.out.println("Open add part screen");
        /*
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("addPart.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        */

        FXMLLoader loader = new FXMLLoader(getClass().getResource("addPart.fxml"));
        //Controller c = new Controller(inventory);
        loader.setController(this);
        scene = new Scene((Pane)loader.load(), 900,475);
        System.out.println(stage);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void addPartSavePressed(ActionEvent event) throws IOException{
        boolean error = false;
        System.out.println(partIdField.getText());
        int id = Integer.parseInt(partIdField.getText());
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

        if(inHouse){
            System.out.println("Adding in-house");
            int machineId = Integer.parseInt(partSourcedField.getText());
            InHouse newPart = new InHouse(id, name, price, stock, min, max, machineId);
            this.inventory.addPart(newPart);

        } else {
            System.out.println("Adding out-house");
            String companyName = partSourcedField.getText();
            Outsourced newPart = new Outsourced(id, name, price, stock, min, max, companyName);
            this.inventory.addPart(newPart);
        }
        System.out.println("Inventory size: " + inventory.getAllParts().size());


        if(!error){
            addPartCancelPressed(event);        //return to home screen if there are no errors
        }
    }

    @FXML
    public void modifyPartButton(ActionEvent event){
        System.out.println("Modify part screen");
    }

    @FXML
    public void deletePartButton(ActionEvent event){
        System.out.println("delete part");
    }


    @FXML
    public void addProductButtonPressed(ActionEvent event) throws IOException{
        System.out.println("Open add product screen");
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("addProduct.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void modifyProductButton(ActionEvent event){
        System.out.println("Modify product screen");
    }

    @FXML
    public void deleteProductButton(ActionEvent event){
        System.out.println("delete product");
    }

    @FXML
    public void saveProductPressed(ActionEvent event){
        System.out.println("delete product");
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
    public void removePartPressed(ActionEvent event){
        System.out.println("delete product");
    }

    @FXML
    public void addPartCancelPressed(ActionEvent event) throws IOException{
        System.out.println("Part Cancel Pressed");
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("gui.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void addProductCancelPressed(ActionEvent event) throws IOException{
        System.out.println("Product Cancel Pressed");
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("gui.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}
