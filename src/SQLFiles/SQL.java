package SQLFiles;
import MainFiles.Booking;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jeff
 */

public class SQL {
    //private static Connection conn;
    private static Calendar now;
    private static SimpleDateFormat format = Booking.FORMAT;
    private static String url;
    private static String user;
    private static String pass;
    
    public SQL(String url, String user, String pass) {
        //conn = DriverManager.getConnection(
            //"jdbc:mysql://localhost:3306/bookings?zeroDateTimeBehavior=convertToNull&useSSL=false","myuser","xxxx");
        this.url = url;
        this.user = user;
        this.pass = pass;
        
        
    }
    
    public static String readResultSet(ResultSet rs){
        String result = "";
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            
            
            
            while(rs.next()){
                for(int i = 1; i <= colCount;i++){
                    result += rs.getString(i);
                    
                    //System.out.print(rs.getString(i));
                    //System.out.print(" ");
                    
                }
                //System.out.println("");
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
        
    }
    
    public static ArrayList<String> returnResultArray(ResultSet rs){
        ArrayList<String> resultArr = new ArrayList<>();
        
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            
            
            
            while(rs.next()){
                for(int i = 1; i <= colCount;i++){
                    resultArr.add(rs.getString(i));
                    
                    //System.out.print(rs.getString(i));
                    //System.out.print(" ");
                    
                }
                //System.out.println("");
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return resultArr;
    }
    
    public ArrayList<String> returnTimesArray() throws SQLException{
        try (Connection conn = DriverManager.getConnection(url,user,pass)) {
            Statement stmt = conn.createStatement();
            String timesQuery = "select * from times;";
            
            ResultSet rsTimes = stmt.executeQuery(timesQuery);
            
            ArrayList<String> timesArr = returnResultArray(rsTimes);
            
            return timesArr;
        }
        
        
    }
    
    public int insertBooking(Booking booking){
       Connection conn = null;
        
        try {
          conn = DriverManager.getConnection(url,user,pass);
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int errorCode = 0; //errorCode 0 if ok 
        
        //check if date is before now, if yes set errorcode
        
        Calendar dateAsCal = booking.getDateObject();
        
        now = Calendar.getInstance();
        
        if(dateAsCal.compareTo(now) < 0){
            errorCode = 2;
        }
        
        else{
       
             try{
            
            
            //PreparedStatement insertStmt = null; 
            Statement stmt = conn.createStatement();
            
            //first, check if table empty, if yes id = 1, if no get max id and id = max + 1
            int id = 0;
            int max = 0;
            
            String tableQuery = "select max(id) from bookings;";
            ResultSet rs = stmt.executeQuery(tableQuery);
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            
            //to get rs as string
            String result = "";
            while(rs.next()){
                for(int i = 1; i <= columnCount;i++){
                    result += rs.getString(i);
                    //System.out.print(rs.getString(i));
                    //System.out.print(" ");
                    
                }
                //System.out.println("");
            }
            
            //check if max id = null (usually means table is empty)
            if(result.toLowerCase().equals("null")){
                id = 1;
            }
            
            //not null - get int value, add by 1, set to id
            
            else{
                 max = Integer.parseInt(result);
                 id = max + 1; //set id to max + 1, use it to set id
                
            }
            
            //continue with inserting
            
            String name = booking.getName();
            String date = booking.getDateString();
            String email = booking.getEmail();
            
            String insertString = "insert into bookings (id,name,date,email) values " + 
                    "(" + String.valueOf(id) + "," + "'" + name + "'" + "," + "'"+ date + "'" + "," + "'" + 
                    email + "'" + ")" + ";";
            
            /*insertStmt = conn.prepareStatement(insertString);
            
            insertStmt.setString(1, name);
            insertStmt.setString(2, date);
            insertStmt.setString(3, email);
            
            int rows = insertStmt.executeUpdate();
            System.out.println(rows + "rows affected");*/
            
            stmt.executeUpdate(insertString);
            
            conn.close();
        }
        
        catch(SQLException ex){
            ex.printStackTrace();
            
            String string = ex.toString();
            //String string = "com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry '05 July 2017 16:30PM' for key 'date'";
            //errorCode = 1 if exception is duplicate entry
            if(string.contains("MySQLIntegrityConstraintViolationException")){
                errorCode = 1;
            }
        }
        
        
     }
        return errorCode;
        
    } 
    
    public boolean insertTime(String time) throws SQLException{
        boolean insertSuccessful = true;
        Connection conn = null;
        conn = DriverManager.getConnection(url,user,pass);
        Statement insert = conn.createStatement();
        String insertQuery = "insert into times values('" + time + "');";
        
        try{
            insert.executeUpdate(insertQuery);
            conn.close();
        }
        
        catch(MySQLIntegrityConstraintViolationException ex){
            insertSuccessful = false;
        }
        
        return insertSuccessful;
    }
    
    //deletes all rows in that table with that element and columnName(not for id)
    public void deleteByElement(String tableName, String columnName, String element) throws SQLException{
        Connection conn = null;
        conn = DriverManager.getConnection(url,user,pass);
        Statement delete = conn.createStatement();
        String deleteQuery = "delete from " + tableName +  " where " + columnName +  " = '" + element + "';";
        
        delete.executeUpdate(deleteQuery);
        conn.close();
    }
    
    public String[] getCurrentBooking(){
       
        String currentBooking = "";
        boolean isThereBookingNow = false;
        String nameBkng = "none";
        String dateBkng = "none";
        String emailBkng = "none";
        String[] returnArray = new String[5];
        
        //System.out.println("start");

        try {
            // TODO add your handling code here:
            //System.out.println("Connection");
            Connection conn = conn = DriverManager.getConnection(url,user,pass);
            Statement stmt = conn.createStatement();
            
            Calendar now = Calendar.getInstance();
            String date = format.format(now.getTime());
            
            String getData = "select * from bookings where date = '" + date + "';";
            
            ResultSet rs = stmt.executeQuery(getData);
            //System.out.println("get data");
            
            
            
            
            if(!rs.isBeforeFirst()){
                currentBooking = "none";
                //System.out.println("check empty");
            }
            
            else{
                isThereBookingNow = true;
                String queryForName = "select name from bookings where date = '" + date + "';";
                
                ResultSet rsName = stmt.executeQuery(queryForName);
                nameBkng = readResultSet(rsName);
                //System.out.println(nameBkng);
                
                String queryForDate = "select date from bookings where date = '" + date + "';";
                ResultSet rsDate = stmt.executeQuery(queryForDate);
                dateBkng = readResultSet(rsDate);
                //System.out.println(dateBkng);
                
                //String queryForEmail = "select"
                String queryForEmail = "select email from bookings where date = '" + date + "';";
                ResultSet rsEmail = stmt.executeQuery(queryForEmail);
                emailBkng = readResultSet(rsEmail);
               
                
                currentBooking = nameBkng + " " + dateBkng;
               // System.out.println(currentBooking);
                
                
                
                
                
            }
            
            conn.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        returnArray[0] = currentBooking; //first index has message to send - either no bookings or name + date
        returnArray[1] = String.valueOf(isThereBookingNow);   //second index has displayCode  to indicate whether no bookings or name + date
        returnArray[2] = nameBkng;                      // third index has just name
        returnArray[3] = dateBkng;                      //fourth has just date
        returnArray[4] = emailBkng;                     //fifth has email
        
        return returnArray;
        
        
    }
        
    public void deleteAllRows(){
        
        Connection conn = null;
        
        try {
          conn = DriverManager.getConnection(url,user,pass);
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            Statement stmt = conn.createStatement();
            
            //query
            String deleteQuery = "delete from bookings;";
            String resetAutoIncrement = "alter table bookings auto_increment = 1;";
            
            stmt.executeUpdate(deleteQuery);
            stmt.executeUpdate(resetAutoIncrement);
            
            conn.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void main(String[]args){
         
    }
   
}
