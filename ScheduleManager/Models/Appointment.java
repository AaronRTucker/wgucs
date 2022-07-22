/**
 * @author Aaron Tucker
 */
package ScheduleManager.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Timestamp;

public class Appointment {


    ObservableList<Customer> associatedCustomers;
    private int id;
    private String title;
    private String description;
    private String location;
    private String type;
    private java.sql.Timestamp start;

    private java.sql.Timestamp end;

    private int customerId;


    private int userId;

    public Appointment(int id, String title, String description, String location, String type, java.sql.Timestamp start, java.sql.Timestamp end, int customerId, int userId){
        this.associatedCustomers = FXCollections.observableArrayList();
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerId = customerId;
        this.userId = userId;

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }




    /**
     * @param customer the customer to add
     */
    public void addAssociatedCustomer(Customer customer){
        associatedCustomers.add(customer);
    }

    /**
     * @param selectedAssociatedCustomer the customer from the list to delete
     * @return boolean true if delete was successful, false if not
     */
    public boolean deleteAssociatedCustomer(Customer selectedAssociatedCustomer){
       // boolean customerDeleted = false;  //reassigned local variable??
        for(int i = 0; i < associatedCustomers.size(); i++){
            if(associatedCustomers.get(i).equals(selectedAssociatedCustomer)){
                associatedCustomers.remove(i);
                return true;
                //customerDeleted = true;
                //break;      //don't delete multiple customers that match?
            }
        }
        //return customerDeleted;  //return true if customer deleted?
        return false;       //no customer deleted from list b/c it doesn't exist
    }

    /**
     * @return ObservableList<Customer> the list of associated customers
     */
    public ObservableList<Customer> getAllAssociatedCustomers(){
        return this.associatedCustomers;
    }
}
