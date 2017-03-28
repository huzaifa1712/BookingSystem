/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainFiles;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author jeff
 */
public class BookingDisplay {
    private String message;
    private boolean isThereBookingNow;
    private String name;
    private String date;
    
    
     
    public BookingDisplay(String message, boolean isThereBookingNow, String name, String date){
        this.message = message;
        this.isThereBookingNow = isThereBookingNow;
        this.name = name;
        this.date = date;
    }
    
   
    
    public String getMessage(){
        return message;
    }
    
    public void setMessage(String message){
        this.message = message;
    }
    
    public boolean isThereBookingNow(){
        return isThereBookingNow;
    }
    
    public void setBookingNow(boolean isThereBookingNow){
        this.isThereBookingNow = isThereBookingNow;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getDate(){
        return date;
    }
    
    public void setDate(String date){
        this.date = date;
    }
    
    public Calendar dateToCalendar(){
        Calendar dateAsCal = Calendar.getInstance();
       try{
            Date date = Booking.FORMAT.parse(this.date);
            dateAsCal.setTime(date);
       }
       
       catch(ParseException ex){
           ex.printStackTrace();
       }
       
       return dateAsCal;
    }
    
    
}
