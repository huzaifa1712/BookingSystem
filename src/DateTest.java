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
import java.util.logging.Level;
import java.util.logging.Logger;
public class DateTest {

    public static void main(String[] args){
        
        SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yy");
        try {
            Date date = ft.parse("01/29/02");
            Date now = new Date();
            System.out.println(date.toString());
            System.out.println("Now: " + now.toString());
            System.out.println(date.compareTo(now));
        } catch (ParseException ex) {
            Logger.getLogger(DateTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("hello");
        }
        
        
        
    }
}
