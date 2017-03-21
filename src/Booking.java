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
import java.util.Calendar;
import java.util.GregorianCalendar;
public class Booking {
    private String name;
    private Calendar date;
    private SimpleDateFormat format = new SimpleDateFormat("dd/mm/yy");
    
    public Booking(String name, Calendar date){
        this.name = name;
        this.date = date;
    }
    
    public Calendar getDateObject(){
        return date;
    }
    
    public void printDateFormatted(){
        System.out.println(format.format(date.getTime()));
    }
    
    public String getName(){
        return name;
    }
    
    //method to convert current selections into Calendar object
    public Calendar toCalendar(){
        
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
