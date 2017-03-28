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

import static MainFiles.Booking.selectionToCalendar;
import SQLFiles.SQL;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Timer;
import java.sql.*;
import java.util.ArrayList;

public class BookingSystemGUI extends javax.swing.JFrame {
SimpleDateFormat formatDateWithTime = Booking.FORMAT;
int bookingDurationInMinutes = Booking.getBookingDuration();
SQL sql;
Mail mail;

    /**
     * Creates new form JFrameGUI
     */
    
    SimpleDateFormat printDayOnly = new SimpleDateFormat("EEEE"); //for updateDay
    SimpleDateFormat formatDateForUpdate = new SimpleDateFormat("dd MMMM yyyy"); //for updateDay
    SimpleDateFormat dateWithSeconds = new SimpleDateFormat("dd MMMM yyyy hh:mm:ssa");
    
    DefaultComboBoxModel listModel;
    
    
    
   
    public BookingSystemGUI() {
        
        //sqlTimes = new SQL();
        initComponents();
        sql = new SQL("jdbc:mysql://localhost:3306/bookings?zeroDateTimeBehavior=convertToNull&useSSL=false","myuser","xxxx");
        mail = new Mail("windowpane1712@gmail.com", "huzaifa1712");
        nameLabel.setText("none");
        dateLabel.setText("none");
        
        
        timerBySecond.start();
        timerForBooking.start();
        
        listModel = new DefaultComboBoxModel();
        nameLabel.setText(sql.getCurrentBooking()[0]); //set booking message to returned msg
        
        Calendar now = Calendar.getInstance();
        String date = formatDateWithTime.format(now.getTime());
        timeLbl.setText(date);
        
        //dayBox.removeAllItems();
        String monthChosen = mthBox.getSelectedItem().toString().toUpperCase();
        Month monthEnum = Month.valueOf(monthChosen);
        YearMonth yearMonth = YearMonth.of(Year.now().getValue(),monthEnum);
        int numberOfDays = yearMonth.lengthOfMonth();
        
        for(int i = 1; i <= numberOfDays;i++){
             dayBox.addItem(Integer.toString(i));
            
        }
        
        try {
             setElementsToTimes();
       } 
        
        catch (SQLException ex) {
        Logger.getLogger(BookingSystemGUI.class.getName()).log(Level.SEVERE, null, ex);
        
       }
        setAllFieldsToBlank();
       
    }
    
    Timer timerBySecond = new Timer(1000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        
        Calendar now = Calendar.getInstance();
        String date = dateWithSeconds.format(now.getTime());
        timeLbl.setText(date);
        timeLabelAdmin.setText(date);
        
    }
});
       
   Timer timerForBooking = new Timer(5000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
           
            String prevName = nameLabel.getText().trim();
            System.out.println("Previous name = " + prevName);
            
            String prevDate = dateLabel.getText().trim();
            System.out.println("Previous date = " + prevDate);
            System.out.println("");
            
            
            
            
            //now text
            BookingDisplay now = new BookingDisplay(sql.getCurrentBooking()[0],Boolean.getBoolean(sql.getCurrentBooking()[1]),sql.getCurrentBooking()[2],sql.getCurrentBooking()[3]);
            
            System.out.println("Now name = " + now.getName());
            System.out.println("Now date = " + now.getDate());
            //System.out.println("Now DC = " + now.isThereBookingNow());
            System.out.println("------------------------------");
            
            //send email
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    if((prevDate.trim().equals("none") && !(now.getDate().trim().equals("none"))) || ((!prevDate.trim().equals("none") && !now.getDate().trim().equals("none")) && (!prevDate.trim().equals(now.getDate().trim()))) ){
                        
                        
                        String recipient = sql.getCurrentBooking()[4];
                        String subject = "Reminder: Your booking on " + now.getDate() + " for the room!";
                        String body = "Dear " + now.getName() + ", " + "\n" + "\n\t" + "You booked the room for " + bookingDurationInMinutes 
                                + " minutes. You may now come use it!";
                        
                        
                        String[] to = { recipient }; // list of recipient email addresses
                        
                        Mail.sendFromGmail(to, subject, body);
                        
                    }
                    
                    
                }
            
            });
        
            thread.start();
            //if no new bookings
            if(!now.getDate().equals("none")){
                //if already set to something
                if(!(prevDate.trim().equals("none"))){
                    //min duration for booking
                    
                    Calendar prevBookingAsCal = Calendar.getInstance();
                    
                    try {
                        prevBookingAsCal.setTime(formatDateWithTime.parse(prevDate));
                    } catch (ParseException ex) {
                        Logger.getLogger(BookingSystemGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    boolean bookingElapsed = Booking.hasBookingElapsed(prevBookingAsCal, bookingDurationInMinutes);
                    System.out.println("Booking elapsed: " + bookingElapsed);
                    
                    if(bookingElapsed){ //true - booking elapsed
                       // set to none - the value from the getcurrentbooking
                       System.out.println("Booking elapsed!!");
                       nameLabel.setText("none");                     /**/
                       dateLabel.setText("none");                    /**/
                        
                    } //else do nothing as its already set to somet WARNING cCHECK LATER
                    
                    else{
                        nameLabel.setText(prevName.trim());
                        dateLabel.setText(prevDate.trim());
                    }
                   
                }
                
                else{ //if set to none  right now this block runs
                    nameLabel.setText(now.getName().trim());
                    dateLabel.setText(now.getDate().trim());
                }
            }
            
            //if there are new bookings
            else{
                if(!prevDate.trim().equals("none")){
                    try {
                    //if set to  bookings right now
                        Date prevDateObj = formatDateWithTime.parse(prevDate);
                        Calendar prevDateAsCal = Calendar.getInstance();
                        prevDateAsCal.setTime(prevDateObj);
                        
                        if(Booking.isBookingNowAfterPrev(prevDateAsCal, now.dateToCalendar(), bookingDurationInMinutes)){
                            nameLabel.setText(now.getName().trim());
                            dateLabel.setText(now.getDate().trim());
                        
                        }
                        
                        else{
                            nameLabel.setText(prevName.trim());
                            dateLabel.setText(prevDate.trim());
                        }
                        
                    } catch (ParseException ex) {
                        Logger.getLogger(BookingSystemGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                }
                
                else{
                    nameLabel.setText(now.getName().trim());
                    dateLabel.setText(now.getDate().trim());
                }
            }
            
            
            
           
            //nameLabel.setText(now.getName());
            //dateLabel.setText(now.getDate());
            
            
            
        }
    });
   
   
   
   public void setAllFieldsToBlank(){
       errorMessage.setText("");
       errAdmin.setText("");
   }
   
   //sets the ComboBox and List to the list of database times
   public void setElementsToTimes() throws SQLException{
       ArrayList<String> timesArrList = sql.returnTimesArray();
       String[] timesArray = timesArrList.toArray(new String[timesArrList.size()]);
       listModel.removeAllElements();
       
       for(int i = 0; i < timesArray.length;i++){
           listModel.addElement(timesArray[i]);
       }
       
       timeBox.setModel(listModel);
       timeList.setModel(listModel);
   }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jSpinner1 = new javax.swing.JSpinner();
        jScrollPane3 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        timeList = new javax.swing.JList<>();
        jLabel3 = new javax.swing.JLabel();
        timeField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        errAdmin = new javax.swing.JLabel();
        addTime = new javax.swing.JButton();
        deleteTime = new javax.swing.JButton();
        deleteAllBookings = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        bookingDuration = new javax.swing.JTextField();
        setDuration = new javax.swing.JButton();
        timeLabelAdmin = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dayBox = new javax.swing.JComboBox<>();
        timeBox = new javax.swing.JComboBox<>();
        mthBox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        emailBox = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        nameBox = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        dayLbl = new javax.swing.JLabel();
        addBtn = new javax.swing.JButton();
        errorMessage = new javax.swing.JLabel();
        timeLbl = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        dateLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jScrollPane3.setViewportView(jEditorPane1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 0, 18))); // NOI18N

        jScrollPane2.setViewportView(timeList);

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel3.setText("Available Times:");

        timeField.setToolTipText("Enter time in 12h format e.g 04:30PM");
        timeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                timeFieldKeyPressed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Source Code Pro", 0, 13)); // NOI18N
        jLabel7.setText("Add new time:");

        errAdmin.setFont(new java.awt.Font("Source Code Pro", 0, 13)); // NOI18N
        errAdmin.setForeground(new java.awt.Color(255, 51, 51));
        errAdmin.setText("err");

        addTime.setFont(new java.awt.Font("Source Code Pro", 0, 13)); // NOI18N
        addTime.setText("Add time");
        addTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTimeActionPerformed(evt);
            }
        });

        deleteTime.setFont(new java.awt.Font("Source Code Pro", 0, 13)); // NOI18N
        deleteTime.setText("Delete time");
        deleteTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTimeActionPerformed(evt);
            }
        });

        deleteAllBookings.setBackground(new java.awt.Color(255, 255, 255));
        deleteAllBookings.setFont(new java.awt.Font("Marlett", 0, 14)); // NOI18N
        deleteAllBookings.setForeground(new java.awt.Color(255, 0, 51));
        deleteAllBookings.setText("Delete all bookings!");
        deleteAllBookings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllBookingsActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Source Code Pro", 0, 13)); // NOI18N
        jLabel2.setText("Set booking duration(minutes):");

        bookingDuration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookingDurationActionPerformed(evt);
            }
        });

        setDuration.setFont(new java.awt.Font("Source Code Pro", 0, 13)); // NOI18N
        setDuration.setText("Set");
        setDuration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setDurationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(errAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(46, 46, 46)
                            .addComponent(bookingDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(setDuration))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(timeField, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(46, 46, 46)
                            .addComponent(addTime)
                            .addGap(18, 18, 18)
                            .addComponent(deleteTime))))
                .addGap(377, 377, 377))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(178, 178, 178)
                .addComponent(deleteAllBookings)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(bookingDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(setDuration))
                .addGap(10, 10, 10)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(deleteTime)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(timeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(addTime, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errAdmin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addComponent(deleteAllBookings, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        timeLabelAdmin.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        timeLabelAdmin.setText("Time");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(timeLabelAdmin)))
                .addContainerGap(272, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(timeLabelAdmin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Admin", jPanel3);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Add a booking", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 0, 18))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Source Code Pro", 0, 13)); // NOI18N
        jLabel1.setText("Date:");

        dayBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dayBoxActionPerformed(evt);
            }
        });

        timeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeBoxActionPerformed(evt);
            }
        });

        mthBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        mthBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mthBoxActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Source Code Pro", 0, 13)); // NOI18N
        jLabel4.setText("Name:");

        emailBox.setToolTipText("Enter your full email address");
        emailBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailBoxActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Source Code Pro", 0, 18)); // NOI18N
        jLabel5.setText("Name:");
        jLabel5.setToolTipText("");

        nameLabel.setFont(new java.awt.Font("Times New Roman", 0, 20)); // NOI18N
        nameLabel.setText("Jeff");

        nameBox.setToolTipText("Enter your name");
        nameBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameBoxActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Source Code Pro", 0, 13)); // NOI18N
        jLabel6.setText("E-mail:");

        dayLbl.setFont(new java.awt.Font("Source Code Pro", 0, 13)); // NOI18N
        dayLbl.setText("Day");

        addBtn.setFont(new java.awt.Font("Source Code Pro", 0, 13)); // NOI18N
        addBtn.setText("Add ");
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        errorMessage.setFont(new java.awt.Font("Source Code Pro", 0, 13)); // NOI18N
        errorMessage.setForeground(new java.awt.Color(255, 0, 0));
        errorMessage.setText("Error");

        timeLbl.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        timeLbl.setText("Time");

        jLabel8.setFont(new java.awt.Font("Source Code Pro", 0, 18)); // NOI18N
        jLabel8.setText("Date:");

        dateLabel.setFont(new java.awt.Font("Times New Roman", 0, 20)); // NOI18N
        dateLabel.setText("date");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(37, 37, 37)
                        .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(47, 47, 47)
                                .addComponent(dateLabel))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(errorMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(addBtn))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel6))
                                    .addGap(24, 24, 24)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(nameBox, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(dayBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(mthBox, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(timeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(39, 39, 39)
                                            .addComponent(dayLbl))
                                        .addComponent(emailBox, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 275, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(318, 318, 318)
                .addComponent(timeLbl)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(timeLbl)
                .addGap(47, 47, 47)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(dayBox, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mthBox, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dayLbl))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(nameBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(emailBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addBtn)
                    .addComponent(errorMessage))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(nameLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(dateLabel))
                .addContainerGap(120, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Make a booking", jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 905, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(464, 464, 464)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
   
    private void mthBoxItemStateChanged(java.awt.event.ItemEvent evt) {                                                     

    if (java.awt.event.ItemEvent.DESELECTED == evt.getStateChange()) {

        String valueBeforeDeselection = evt.getItem().toString();
        // Do something if needed
        System.out.println("If statement");
        
    } else if (java.awt.event.ItemEvent.SELECTED == evt.getStateChange()) {
        System.out.println("Else statement");
        String valueAfterSelection = evt.getItem().toString();
        // Set the values of the ComboBox2
    }
}
    
    private void emailBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailBoxActionPerformed

    private void nameBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameBoxActionPerformed
    public void updatedayLbl(){
        String day = dayBox.getSelectedItem().toString();
        String month = mthBox.getSelectedItem().toString();
        String year = String.valueOf(Year.now().getValue());
        
        String date = day + " " + month + " " + year;
        Date dateObject = null;
    
        try {
         dateObject = formatDateForUpdate.parse(date);
       } 
        catch (ParseException ex) {
        Logger.getLogger(BookingSystemGUI.class.getName()).log(Level.SEVERE, null, ex);
      }
    
    
    dayLbl.setText(printDayOnly.format(dateObject));
    
   }
    private void mthBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mthBoxActionPerformed
        // TODO add your handling code here:
        //Code to set day elements in dayBox according to month selected.
        dayBox.removeAllItems();
        
        String monthChosen = mthBox.getSelectedItem().toString().toUpperCase();
        Month monthEnum = Month.valueOf(monthChosen);
        YearMonth yearMonth = YearMonth.of(Year.now().getValue(),monthEnum);
        int numberOfDays = yearMonth.lengthOfMonth();
        
        for(int i = 1; i <= numberOfDays;i++){
            dayBox.addItem(Integer.toString(i));
            
        }
        updatedayLbl();
        
        
    }//GEN-LAST:event_mthBoxActionPerformed

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        // TODO add your handling code here:
        
        //check if name and email are empty, set error message if yes
        if(nameBox.getText().equals("") || emailBox.getText().equals("")){
            errorMessage.setText("Please ensure name and email are not left empty!");
        }
        
        else{
            if(Mail.isEmailValid(emailBox.getText().trim())){
                try{
                    //Make date from selections, make new booking
                    Calendar dateToAdd = selectionToCalendar(
                            dayBox.getSelectedItem().toString() , mthBox.getSelectedItem().toString() , 
                            timeBox.getSelectedItem().toString());

                    Booking bkngToAdd = new Booking(nameBox.getText(), dateToAdd, emailBox.getText());
                    System.out.println(bkngToAdd.toString());

                    //add to DB
                    int errorCode = sql.insertBooking(bkngToAdd);

                    if(errorCode == 1){
                        errorMessage.setText("That date has already been booked! Please use a different date.");
                    }

                    else if (errorCode == 2){
                        errorMessage.setText("Please choose a date in the present or future!");
                    }
                    else{
                        errorMessage.setText("");
                    }

                    //reset text fields
                    nameBox.setText("");
                    emailBox.setText("");

                }

                catch(ParseException ex){
                    ex.printStackTrace();
                }
            
            //exception for duplicate entry
            
             }
            
            else{
                errorMessage.setText("Please make sure you have entered a valid email address!");
            }
        }
    }//GEN-LAST:event_addBtnActionPerformed

    private void dayBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dayBoxActionPerformed
        // TODO add your handling code here:
        if(dayBox.getItemCount() != 0){
            updatedayLbl();
        }
        //updatedayLbl();
    }//GEN-LAST:event_dayBoxActionPerformed

    private void timeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_timeBoxActionPerformed

    private void addTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTimeActionPerformed
        // TODO add your handling code here:

        String time = timeField.getText().trim();

        if(Booking.isTimeValid(time)){
            try {
                if(sql.insertTime(time)){

                    setElementsToTimes();
                    errAdmin.setText("");
                }

                else{
                    errAdmin.setText("Error: You can't add a time that exists!");

                }
            }

            catch (SQLException ex) {
                Logger.getLogger(BookingSystemGUI.class.getName()).log(Level.SEVERE, null, ex);
                errAdmin.setText("Error: You can't add a time that exists!");
            }
        }

        else{
            errAdmin.setText("Error: Time should be in 12h format e.g 04:30PM");
        }

        timeField.setText("");
    }//GEN-LAST:event_addTimeActionPerformed

    private void timeFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_timeFieldKeyPressed
        // TODO add your handling code here:
        System.out.println(timeField.getText().length());

        if(timeField.getText().length() > 6){
            errAdmin.setText("Error: Make sure entered time is in 12h format!");
            timeField.setText(timeField.getText().substring(0,6));
        }

        if(timeField.getText().length()< 6){
            errAdmin.setText("");
        }

    }//GEN-LAST:event_timeFieldKeyPressed

    private void deleteTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteTimeActionPerformed
        // TODO add your handling code here:
        if(timeList.getSelectedIndex() == -1){
            errAdmin.setText("Please select one or more times to delete!");
        }
        
            else{
                try{
                  ArrayList<String> timesArr = (ArrayList) timeList.getSelectedValuesList();
           
                  for(String time: timesArr){
                    sql.deleteByElement("times", "times", time);
                
                  }
                  
                  setElementsToTimes();
                  errAdmin.setText("");
                }  
                
              catch(SQLException ex){
                Logger.getLogger(BookingSystemGUI.class.getName()).log(Level.SEVERE, null, ex);
             }
        }
        
       
        
        
    }//GEN-LAST:event_deleteTimeActionPerformed

    private void deleteAllBookingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllBookingsActionPerformed
        // TODO add your handling code here:
        sql.deleteAllRows();
    }//GEN-LAST:event_deleteAllBookingsActionPerformed

    private void bookingDurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookingDurationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bookingDurationActionPerformed

    private void setDurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setDurationActionPerformed
        // TODO add your handling code here:
        String durationText = bookingDuration.getText();
        int bookingDurationMinutes = 0;
        try{
             bookingDurationMinutes = Integer.parseInt(durationText);
             
             if(bookingDurationMinutes <= 0 ){
                 errAdmin.setText("Please make sure booking duration is bigger than 0!");
             }
        }
        
        catch(NumberFormatException ex){
            errAdmin.setText("Please enter a valid positive integer for booking duration!");
        }
        
        if(bookingDurationMinutes != 0){
            Booking.setBookingDuration(bookingDurationMinutes);
        }
    }//GEN-LAST:event_setDurationActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BookingSystemGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BookingSystemGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BookingSystemGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BookingSystemGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BookingSystemGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JButton addTime;
    private javax.swing.JTextField bookingDuration;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JComboBox<String> dayBox;
    private javax.swing.JLabel dayLbl;
    private javax.swing.JButton deleteAllBookings;
    private javax.swing.JButton deleteTime;
    private javax.swing.JTextField emailBox;
    private javax.swing.JLabel errAdmin;
    private javax.swing.JLabel errorMessage;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JComboBox<String> mthBox;
    private javax.swing.JTextField nameBox;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton setDuration;
    private javax.swing.JComboBox<String> timeBox;
    private javax.swing.JTextField timeField;
    private javax.swing.JLabel timeLabelAdmin;
    private javax.swing.JLabel timeLbl;
    private javax.swing.JList<String> timeList;
    // End of variables declaration//GEN-END:variables
}
