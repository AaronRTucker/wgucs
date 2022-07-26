package ScheduleManager.DBHelper;

import ScheduleManager.Models.Appointment;
import ScheduleManager.Models.Customer;
import ScheduleManager.Models.Schedule;

import java.sql.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;


public abstract class DatabaseQueryHelper {




    //Add the current customers from the database into a schedule object
    public static void getAllCustomers(Schedule schedule){

        //Get customer table data from MYSQL database

        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`customers`");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                int divisionId = result.getInt("Division_ID");
                String countryName = getCountryName(divisionId);
                String divisionName = getDivisionName(divisionId);
                Customer c = new Customer(

                        //change these to column names instead of indexes
                        result.getInt("Customer_ID"),
                        result.getString("Customer_Name"),
                        result.getString("Address"),
                        result.getString("Postal_Code"),
                        divisionName,
                        countryName,
                        result.getString("Phone"),
                        divisionId

                );
                schedule.addCustomer(c);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
    }


    //Get the next customer ID by looking for the highest current ID in the database
    public static int getNextCustomerId(Schedule schedule){
        int nextCustomerId = -1;

        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`customers`");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                int temp = result.getInt("Customer_ID");
                if (temp >= nextCustomerId) {
                    System.out.println("yes");
                    nextCustomerId = temp + 1;
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return nextCustomerId;
    }


    public static void getAllAppointments(Schedule schedule){
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`appointments`");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Appointment a = new Appointment(
                        result.getInt("Appointment_ID"),
                        result.getString("Title"),
                        result.getString("Description"),
                        result.getString("Location"),
                        result.getString("Type"),
                        result.getTimestamp("Start"),
                        result.getTimestamp("End"),
                        result.getInt("Customer_ID"),
                        result.getInt("User_ID")
                );
                schedule.addAppointment(a);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void deleteCustomer(Schedule schedule, Customer customer){
        schedule.deleteCustomer(customer);

        String sql = ("DELETE FROM client_schedule.customers WHERE Customer_ID = " + customer.getId() + " ");
        System.out.println(sql);

        try {
            Connection conn = JDBC.connection;
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("Deleted records in the table...");

        } catch(SQLException e){
            e.printStackTrace();
        }
    }


    public static int getDivisionID(String divisionName){
        //Get division ID from selected division name
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`first_level_divisions` WHERE Division = '" + divisionName + "';");
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return result.getInt("Division_ID");
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
        return -1;      //something went wrong
    }


    public static String getDivisionName (int divisionID){
        //Get division list from database
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`first_level_divisions` WHERE Division_ID = " + divisionID + ";");
            ResultSet result = ps.executeQuery();

            if (result.next() ) {
                return result.getString("Division");
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        return "Not found";
    }

    public static String getCountryName (int divisionID){
         int countryID = getCountryIDFromDivisionID(divisionID);

        //Get country name from database
        try {
            //if(countryID) {
                PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`countries` WHERE Country_ID = " + countryID + ";");
            //}
            ResultSet result = ps.executeQuery();

            if (result.next() ) {
                return result.getString("Country");
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        return "Not found";
    }

    public static int getCountryIDFromDivisionID(int divisionID){
        //Get country ID from database
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`first_level_divisions` WHERE Division_ID = " + divisionID + ";");
            ResultSet result = ps.executeQuery();

            if (result.next() ) {
                return result.getInt("Country_ID");
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        return -1;
    }


    public static int getCountryID(String countryName){
        //Get country ID from selected country name
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`countries` WHERE Country = '" + countryName + "';");
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return result.getInt("Country_ID");
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        return -1;
    }


    public static ArrayList<String> getDivisions(int countryID){
        ArrayList<String> divisions = new ArrayList<>();
        //Get division list from database
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`first_level_divisions` WHERE Country_ID = " + countryID + ";");
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                divisions.add(result.getString("Division"));
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
        return divisions;
    }

    public static ArrayList<String> getCountries(){
        ArrayList<String> countries = new ArrayList<>();
        //Get country list from database
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM `client_schedule`.`countries`");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                countries.add(result.getString("Country"));
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        return countries;
    }

    public static void addCustomer(int id,String name,String address,String postalCode,String phoneNumber,String userName,int selectedDivisionID){
        Instant instant = Instant.now() ;                           //get the current moment
        OffsetDateTime odt = instant.atOffset( ZoneOffset.UTC ) ;   //get the current moment translated to UTC
        Timestamp timestamp = Timestamp.valueOf(odt.toLocalDateTime());  //convert the current moment into a legacy format due to database datatype restrictions

        String sql = (          //create_date uses datetime, last_update uses timestamp.  Only timestamp is aware of timezone information
                "INSERT INTO `client_schedule`.`customers` " +
                        "(Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By,  Division_ID) " +
                        "VALUES ("+id+",'"+name+"','"+address+"','"+postalCode+"','"+phoneNumber+"','"+timestamp+"','"+userName+"','"+timestamp+"','"+userName+"','"+selectedDivisionID+"')" );
        System.out.println(sql);

        try {
            Connection conn = JDBC.connection;
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("Inserted records into the table...");

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void modifyCustomer(int id,String name,String address,String postalCode,String phoneNumber,String userName,int selectedDivisionID){
        //Input is good

        Instant instant = Instant.now() ;                           //get the current moment
        OffsetDateTime odt = instant.atOffset( ZoneOffset.UTC ) ;   //get the current moment translated to UTC
        Timestamp timestamp = Timestamp.valueOf(odt.toLocalDateTime());  //convert the current moment into a legacy format due to database datatype restrictions


        String sql = ("UPDATE client_schedule.customers SET " +
                "Customer_Name='"+name+"', " +
                "Address='"+address+"', " +
                "Postal_Code='"+postalCode+"', " +
                "Phone = '"+phoneNumber+"', " +
                "Created_By = '"+userName+"', " +
                "Last_Update = '"+timestamp+"', " +
                "Last_Updated_By = '"+userName+"'," +
                "Division_ID = "+selectedDivisionID+" " +
                "WHERE Customer_ID = " + id + " ");
        System.out.println(sql);

        try {
            Connection conn = JDBC.connection;
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("Modified records in the table...");

        } catch(SQLException e){
            e.printStackTrace();
        }
    }
}
