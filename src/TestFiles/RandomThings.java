/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestFiles;

import MainFiles.Booking;
import SQLFiles.SQL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jeff
 */
public class RandomThings {
    //check if booking is still ongoing based on booking duration
    
    public static void main(String[]args){
        //SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mma");
        
        /*Calendar now = new GregorianCalendar();
        
        
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy hh:mma");
        String date = "23 March 2017 12:23AM";
        
        Date dateObj = null;
        try {
             dateObj = format.parse(date);
        } catch (ParseException ex) {
            Logger.getLogger(RandomThings.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        //use .add to add time and also change other values e.g am_pm
       // now.add(Calendar.HOUR, 2);
       
       SimpleDateFormat format = Booking.FORMAT;
       int durationInMinutes = 90; 
       String dateToParse = "25 March 2017 03:40PM";
       Calendar now = Calendar.getInstance();
       
       Calendar bookingDate = Calendar.getInstance();
        try {
            bookingDate.setTime(format.parse(dateToParse));
        } catch (ParseException ex) {
            Logger.getLogger(RandomThings.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        System.out.println(format.format(bookingDate.getTime()));
        //System.out.println(RandomThings.hasBookingElapsed(bookingDate, durationInMinutes));
        
        System.out.println("            Jeff Ragha                ");
        System.out.println();
        
        
        
        
        
    }
}
