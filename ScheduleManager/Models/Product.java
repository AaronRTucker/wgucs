/**
 * @author Aaron Tucker
 */
package ScheduleManager.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Product {
    ObservableList<Part> associatedParts;
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;

    public Product(int id, String name, double price, int stock, int min, int max){
        this.associatedParts = FXCollections.observableArrayList();
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.min = min;
        this.max = max;

    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @param stock the stock to set
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * @param min the min to set
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * @param max the max to set
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * @return the ID
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @return the stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * @return the min
     */
    public int getMin() {
        return min;
    }

    /**
     * @return the max
     */
    public int getMax() {
        return max;
    }

    /**
     * @param part the part to add
     */
    public void addAssociatedPart(Part part){
        associatedParts.add(part);
    }

    /**
     * @param selectedAssociatedPart the part from the list to delete
     * @return boolean true if delete was successful, false if not
     */
    public boolean deleteAssociatedPart(Part selectedAssociatedPart){
       // boolean partDeleted = false;  //reassigned local variable??
        for(int i = 0; i < associatedParts.size(); i++){
            if(associatedParts.get(i).equals(selectedAssociatedPart)){
                associatedParts.remove(i);
                return true;
                //partDeleted = true;
                //break;      //don't delete multiple parts that match?
            }
        }
        //return partDeleted;  //return true if part deleted?
        return false;       //no part deleted from list b/c it doesn't exist
    }

    /**
     * @return ObservableList<Part> the list of associated parts
     */
    public ObservableList<Part> getAllAssociatedParts(){
        return this.associatedParts;
    }
}
