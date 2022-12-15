/**
 *
 * @author Aaron Tucker
 */

package ScheduleManager.Models;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Schedule {
    private final ObservableList<Customer> allCustomers;
    private final ObservableList<Appointment> allAppointments;


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
        for (Customer allCustomer : this.allCustomers) {
            if (allCustomer.getId() == CustomerId) {
                return allCustomer;
            }
        }
        return null;    //no Customer found
    }

    /**
     * @param AppointmentId the AppointmentId to search for
     * @return the Appointment that matches AppointmentId
     */
    public Appointment lookupAppointment(int AppointmentId){
        for (Appointment allAppointment : this.allAppointments) {
            if (allAppointment.getId() == AppointmentId) {
                return allAppointment;
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
     */
    public void deleteCustomer(Customer selectedCustomer){
        for(int i = 0; i < allCustomers.size(); i++){
            if(allCustomers.get(i).equals(selectedCustomer)){
                allCustomers.remove(i);
                return;
            }
        }
    }

    /**
     * @param selectedAppointment the Appointment to delete
     */
    public void deleteAppointment(Appointment selectedAppointment){
        for(int i = 0; i < allAppointments.size(); i++){
            if(allAppointments.get(i).equals(selectedAppointment)){
                allAppointments.remove(i);
                return;
            }
        }
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

    public ObservableList<Appointment> getContactsAppointments(String contact){
        ObservableList<Appointment> contactsAppts= FXCollections.observableArrayList();

        for (Appointment allAppointment : this.allAppointments) {
            if (allAppointment.getContact().equals(contact)) {
                contactsAppts.add(allAppointment);
            }
        }
        return contactsAppts;
    }
}

