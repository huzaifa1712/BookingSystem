/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jeff
 */
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.text.SimpleDateFormat;

public class CalendarTest {
    public static void main(String[]args){
    SimpleDateFormat format = new SimpleDateFormat("EEEE,MMMM,yyyy,HH:mm a");
    Calendar day1 = new GregorianCalendar(2011,7,15);
    System.out.println(day1.get(Calendar.DAY_OF_MONTH));
    System.out.println(day1.get(Calendar.HOUR));
    Calendar day2 = new GregorianCalendar(2015,7,15);
    System.out.println(day2.compareTo(day1));
    Calendar dayNow = new GregorianCalendar();
    
    System.out.println(dayNow.get(Calendar.DAY_OF_MONTH));
    System.out.println(dayNow.get(Calendar.MONTH));
    System.out.println(dayNow.get(Calendar.HOUR_OF_DAY));
    System.out.println(format.format(dayNow.getTime()));
    
    //what happens when you create a Calendar without specifying year
    Calendar someDay = new GregorianCalendar();
    someDay.set(Calendar.DAY_OF_MONTH,Calendar.MONDAY);
    someDay.set(Calendar.MONTH,Calendar.JANUARY);
    someDay.set(Calendar.HOUR_OF_DAY,15);
    someDay.set(Calendar.MINUTE,30);
    System.out.println(format.format(someDay.getTime()));
    
    }
    
}
