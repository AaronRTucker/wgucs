package ScheduleManager.Models;

/**
 *
 * @author Aaron Tucker
 */
public class Customer {
    private int id;
    private String name;
    private String address;



    private String division;

    private String country;
    private String postalCode;
    private String phoneNumber;

    private int division_ID;



    public Customer(int id, String name, String address, String postalCode, String division, String country, String phoneNumber, int division_ID) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.division = division;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.postalCode = postalCode;
        this.division_ID = division_ID;


    }


    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode the postal code to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return the division ID
     */
    public int getDivision_ID() {
        return division_ID;
    }

    /**
     * @param division_ID the postal code to set
     */
    public void setDivision_ID(int division_ID) {
        this.division_ID = division_ID;
    }

}