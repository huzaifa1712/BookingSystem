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
public class SQLTestMyOwn {
    public static void main(String[]args){
        try{
            Connection conn = DriverManager.getConnection(
            "jdbc:derby://localhost:1527/sample");
            
            Statement stmt = conn.createStatement();
            
            String query = "select * from colleagues;";
            System.out.println("Query is " + query);
            
            ResultSet rset = stmt.executeQuery(query);
            ResultSetMetaData meta = rset.getMetaData();
            int columnCount = meta.getColumnCount();
            
             //Code to print column names
            for(int i = 1; i <= columnCount;i++){
                System.out.print(meta.getColumnName(i) + " ");
                
            }
            //
         
            System.out.println("");
            
            // Code to print all the data in the table
            while(rset.next()){
                for(int i = 1; i <= columnCount;i++){
                    
                    System.out.print(rset.getString(i));
                    System.out.print(" ");
                    
                }
                System.out.println("");
            }
            //
            
        }
        
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }
}
