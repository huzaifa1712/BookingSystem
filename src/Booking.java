/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jeff
 */
import java.util.Date;
import java.text.SimpleDateFormat;
public class Booking {
    private String name;
    private Date date;
    private String test;
    
    public Booking(String name, Date date){
        this.name = name;
        this.date = date;
    }
    
    public Date getDate(){
        return date;
    }
    
    public String getName(){
        return name;
    }
    
    
    //returns true if bookings are same, false if bookings are not same
    
    public boolean equals(Booking booking){
        if(booking.getDate().equals(this.date)&& booking.getName().equals(this.name)){
            return true;
        }
        
        else{
            return false;
        }
    }
   
}
