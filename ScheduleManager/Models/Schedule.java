
/**
 * Inventory.java
 */

/**
 *
 * @author Aaron Tucker
 */

package ScheduleManager.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Schedule {
    private ObservableList<Customer> allCustomers;
    private ObservableList<Appointment> allAppointments;


    public Schedule(){
        allCustomers = FXCollections.observableArrayList();
        allAppointments = FXCollections.observableArrayList();
    }

    /**
     * @param newCustomer the Customer to add
     */
    public void addCustomer(Customer newCustomer){
        this.allCustomers.add(newCustomer);
    }

    /**
     * @param newAppointment the Appointment to add
     */
    public void addAppointment(Appointment newAppointment){
        this.allAppointments.add(newAppointment);
    }

    /**
     * @param CustomerId the CustomerId to search for
     * @return the Customer that matches CustomerId
     */
    public Customer lookupCustomer(int CustomerId){
        for(int i = 0; i < this.allCustomers.size(); i++){
            if(this.allCustomers.get(i).getId() == CustomerId){
                return this.allCustomers.get(i);
            }
        }
        return null;    //no Customer found
    }

    /**
     * @param AppointmentId the AppointmentId to search for
     * @return the Appointment that matches AppointmentId
     */
    public Appointment lookupAppointment(int AppointmentId){
        for(int i = 0; i < this.allAppointments.size(); i++){
            if(this.allAppointments.get(i).getId() == AppointmentId){
                return this.allAppointments.get(i);
            }
        }
        return null;    //no Appointment found
    }

    /**
     * @param index the index of the Customer to update
     * @param selectedCustomer the selected Customer
     */
    public void updateCustomer(int index, Customer selectedCustomer){
        allCustomers.set(index, selectedCustomer);
    }

    /**
     * @param index the index of the Appointment to change
     * @param newAppointment the new Appointment
     */
    public void updateAppointment(int index, Appointment newAppointment){
        allAppointments.set(index, newAppointment);
    }

    /**
     * @param selectedCustomer the Customer to delete
     * @return true if delete was successful, false otherwise
     */
    public boolean deleteCustomer(Customer selectedCustomer){
        for(int i = 0; i < allCustomers.size(); i++){
            if(allCustomers.get(i).equals(selectedCustomer)){
                allCustomers.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * @param selectedAppointment the Appointment to delete
     * @return true if delete was successful, false otherwise
     */
    public boolean deleteAppointment(Appointment selectedAppointment){
        for(int i = 0; i < allAppointments.size(); i++){
            if(allAppointments.get(i).equals(selectedAppointment)){
                allAppointments.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * @return the list of all Customers
     */
    public ObservableList<Customer> getAllCustomers(){
        return allCustomers;
    }

    /**
     * @return the list of all Appointments
     */
    public ObservableList<Appointment> getAllAppointments(){
        return allAppointments;
    }
}

