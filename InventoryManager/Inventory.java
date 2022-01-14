package InventoryManager; /**
 * Inventory.java
 */

/**
 *
 * @author Aaron Tucker
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class Inventory {
    private ObservableList<Part> allParts;
    private ObservableList<Product> allProducts;


    public Inventory(){
        allParts = FXCollections.observableArrayList();
        allProducts = FXCollections.observableArrayList();
    }

    /**
     * @param newPart the part to add
     */
    public void addPart(Part newPart){
        this.allParts.add(newPart);
    }

    /**
     * @param newProduct the product to add
     */
    public void addProduct(Product newProduct){
        this.allProducts.add(newProduct);
    }

    /**
     * @param partId the partId to search for
     * @return the part that matches partId
     */
    public Part lookupPart(int partId){
        for(int i = 0; i < this.allParts.size(); i++){
            if(this.allParts.get(i).getId() == partId){
                return this.allParts.get(i);
            }
        }
        return null;    //no part found
    }

    /**
     * @param productId the productId to search for
     * @return the product that matches productId
     */
    public Product lookupProduct(int productId){
        for(int i = 0; i < this.allProducts.size(); i++){
            if(this.allProducts.get(i).getId() == productId){
                return this.allProducts.get(i);
            }
        }
        return null;    //no product found
    }

    /**
     * @param index the index of the part to update
     * @param selectedPart the selected part
     */
    public void updatePart(int index, Part selectedPart){
        allParts.set(index, selectedPart);
    }

    /**
     * @param index the index of the product to change
     * @param newProduct the new product
     */
    public void updateProduct(int index, Product newProduct){
        allProducts.set(index, newProduct);
    }

    /**
     * @param selectedPart the part to delete
     * @return true if delete was successful, false otherwise
     */
    public boolean deletePart(Part selectedPart){
        for(int i = 0; i < allParts.size(); i++){
            if(allParts.get(i).equals(selectedPart)){
                allParts.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * @param selectedProduct the product to delete
     * @return true if delete was successful, false otherwise
     */
    public boolean deleteProduct(Product selectedProduct){
        for(int i = 0; i < allProducts.size(); i++){
            if(allProducts.get(i).equals(selectedProduct)){
                allProducts.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * @return the list of all parts
     */
    public ObservableList<Part> getAllParts(){
        return allParts;
    }

    /**
     * @return the list of all products
     */
    public ObservableList<Product> getAllProducts(){
        return allProducts;
    }
}

