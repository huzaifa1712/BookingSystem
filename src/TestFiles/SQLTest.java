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
import java.sql.*;

import java.util.ArrayList;
public class SQLTest {
    private static Connection conn;
    public static void main(String[]args){
        try{
            //Step 1: Allocate a database, 'Connection' object                                                                                                                                                              
             conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/bookings?zeroDateTimeBehavior=convertToNull&useSSL=false","myuser","xxxx");
            //MySQL: "jdbc:mysql://hostname:port/databaseName","username","password"
            
            //Step 2: Allocate a 'Statement' object in the Connection
            Statement stmt = conn.createStatement();
            
            //Step 3: Execute a SQL SELECT query , the query result is returned
            //in a 'ResultSet' object
            
            String strSelect = "select max(id) from bookings;";
            System.out.println("The SQL query is: " + strSelect);
            
            ResultSet rset = stmt.executeQuery(strSelect);
            ResultSetMetaData meta = rset.getMetaData();
            int columnCount = meta.getColumnCount();
            //Step 4: Process the ResultSet by scrolling the cursor forward via next()
            //For each row, retrieve contents of cells with getXxx(columnName)
            
            //arraylist to store columnNames to iterate through later
            /*ArrayList<String> columnNames = new ArrayList<>();
            
            for(int i = 1; i <= columnCount;i++){
                String name = meta.getColumnName(i);
                columnNames.add(name);
            }*/
            
            //Code to print column names
            /*for(int i = 1; i <= columnCount;i++){
                System.out.print(meta.getColumnName(i) + " ");
                
            }*/
            //
         
            System.out.println("");
            
            // Code to print all the data in the table
            String result = "";
            while(rset.next()){
                for(int i = 1; i <= columnCount;i++){
                    result += rset.getString(i);
                    System.out.print(rset.getString(i));
                    System.out.print(" ");
                    
                }
                System.out.println("");
            }
            
            System.out.println(result.toLowerCase().equals("null"));
            //System.out.println(max);
            
            /*int newAutoIncrement = max + 1;
            
            String strUpdate = "alter table bookings auto_increment = " + newAutoIncrement +";";
            stmt.executeUpdate(strUpdate);
            strUpdate = "insert into bookings (name,date,email) values ('Colin','r3r34','2r3r');";
            stmt.executeUpdate(strUpdate);*/
           
            //
           
            //System.out.println("The records selected are:");
            /*int rowCount = 0;
            while(rset.next()){ //Move cursor to next row, return false if no more row
                int id = rset.getString("id");
                String title = rset.getString("title");
                double price = rset.getDouble("price");
                int qty = rset.getInt("qty");
                System.out.println(title + ", " + price + ", " + qty);
                rowCount++;
            }
            rset.
*/
            
            //System.out.println("Total number of records = " + rowCount);
            System.out.println();
            
        }
        
        catch(SQLException ex){
            ex.printStackTrace();
        }
        
        //Step 5: Close the resources - done automatically by try-with-resources
    }
}
