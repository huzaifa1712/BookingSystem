package TestFiles;

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
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
public class DateTest {

    public static void main(String[] args){
        
        SimpleDateFormat ft = new SimpleDateFormat("EEEE MMMM hh:mm", Locale.ENGLISH);
        try {
            /*Date date = ft.parse("01/29/02");
            Date now = new Date();
            System.out.println(date.toString());
            System.out.println("Now: " + now.toString());
            System.out.println(date.compareTo(now));
            */
            
            Date someDate = ft.parse("Monday April 03:00");
            Date now = new Date();
            System.out.println("Now: " + now.toString());
            System.out.println("Booking: " + someDate.toString());
            System.out.println(someDate.compareTo(now));
            
        } catch (ParseException ex) {
            Logger.getLogger(DateTest.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        
        //use YearMonth to find number of days in a month of a particular year
        System.out.println(Year.now());
        System.out.println(Year.now().getValue());
        
       
        Month someMonth = Month.valueOf("FEBRUARY");
        YearMonth yearMonthObject = YearMonth.of(Year.now().getValue(), someMonth);
        System.out.println(yearMonthObject.lengthOfMonth());
        Calendar now = Calendar.getInstance();
        SimpleDateFormat printDayOnly = new SimpleDateFormat("EEEE");
        System.out.println(printDayOnly.format(now.getTime()));
        
        
        
        
        
        
    }

    private static void Month(String january) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
