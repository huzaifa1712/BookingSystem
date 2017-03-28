package MainFiles;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jeff
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;


public class Booking {
    private String name;
    private Calendar date;
    private String dateString;
    private String email;
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd MMMM yyyy hh:mma");
    public static final SimpleDateFormat TIMEFORMAT = new SimpleDateFormat("hh:mma");
    private static int bookingDuration = 2; //default
  
    
    
    
    //constructor
    public Booking(){
        this.name = "none";
        this.date = null;
        this.dateString = "none";
        this.email = "none";
        try {
            setDateFromString("none");
        } catch (ParseException ex) {
            Logger.getLogger(Booking.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    public Booking(String name, Calendar date, String email){
        this.name = name;
        this.date = date;
        this.email = email;
        this.dateString = FORMAT.format(date.getTime());
       // counter++;
       // id = counter;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDateString(){
        return this.dateString;
    }
    
    public void setDateFromString(String date) throws ParseException{
        this.date.setTime(FORMAT.parse(date));
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return email;
    }
    
    public static int getBookingDuration(){
        return bookingDuration;
    }
    
    public static void setBookingDuration(int minutes){
        bookingDuration = minutes;
    }
    
    public Calendar getDateObject(){
        return date;
    }
    
    
    public void printDateFormatted(){
        System.out.println(FORMAT.format(date.getTime()));
    }
    
    public static boolean hasBookingElapsed(Calendar bookingDate, int bookingDurationMinutes){
        Calendar now = Calendar.getInstance();
        
        bookingDate.add(Calendar.MINUTE, bookingDurationMinutes);
        //if date is not before now + duration (after or equal
        if(now.compareTo(bookingDate) >= 0){
            return true;
        }
        
        else{
            return false;
        }
            
        
    }
    
    public static boolean isBookingNowAfterPrev(Calendar bookingDatePrev, Calendar bookingDateNow, int bookingDurationMinutes){
        
        
        bookingDatePrev.add(Calendar.MINUTE, bookingDurationMinutes);
        //if date is not before now + duration (after or equal
        
        if(bookingDateNow.compareTo(bookingDatePrev) >= 0){
            return true;
        }
        
        else{
            return false;
        }
            
        
    }
    
    //checks if time from addTime is valid or not
    
    public static boolean isTimeValid(String date){
        boolean isValid = false;
        
        try{
            TIMEFORMAT.setLenient(true);
            TIMEFORMAT.parse(date);
            isValid = true;
        }
        
        catch(ParseException ex){
            
        }
        
        return isValid;
    }
    public Calendar bookingToCalendar(Booking booking){
        Calendar bookingAsCal = Calendar.getInstance();
        bookingAsCal.setTime(booking.getDateObject().getTime());
        return bookingAsCal;
    }
    
    //method to convert current selections into Calendar object
    public static Calendar selectionToCalendar(String day, String month, String time) throws ParseException{
        
        String fullDate = day + " " + month + " " + Year.now().getValue() + " " + time;
        Calendar dateAsCal = Calendar.getInstance();
        dateAsCal.setTime(FORMAT.parse(fullDate));
        return dateAsCal;
    }
    
    //returns true if bookings are same, false if bookings are not same
    public boolean equals(Booking booking){
        return booking.getDateObject().equals(this.date) && booking.getName().equals(this.name);
    }
    
    @Override
    public String toString(){
        return name + " " +  getDateString() + " " + email;
        
    }
    
    public static void main(String[]args){
   
    }
   
}
