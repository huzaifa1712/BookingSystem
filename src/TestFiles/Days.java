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
public enum Days {
    SUNDAY(1, "SUNDAY"),
    MONDAY(2,"MONDAY"),
    TUESDAY(3,"TUESDAY"),
    WEDNESDAY(4,"WEDNESDAY"),
    THURSDAY(5,"THURSDAY"),
    FRIDAY(6,"FRIDAY"),
    SATURDAY(7,"SATURDAY");
    
    private int value;
    private String day;
    
    Days(int value, String day){
        this.value = value;
        this.day = day;
        
    }
    
    public int getValue(){
        return value;
    }
    
   
    /*
     Days(){
        switch(this){
            case MONDAY:
                name =  "MONDAY";
                value = 2;
                break;
            case TUESDAY:
                name =  "TUESDAY";
                value = 3;
                break;
            case WEDNESDAY:
                name =  "WEDNESDAY";
                value = 4;
                break;
            case THURSDAY:
                name =  "THURSDAY";
                value = 5;
                break;
            case FRIDAY:
                name =  "FRIDAY";
                value = 6;
                break;
            case SATURDAY:
                name =  "SATURDAY";
                value = 7;
                break;
            case SUNDAY:
                name =  "SUNDAY";
                value = 1;
                break;
                
        }
*/
       
    
    
    
}
