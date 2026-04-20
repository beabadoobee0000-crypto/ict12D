/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.disciplinaryactionms;

import static com.mycompany.disciplinaryactionms.ConnectXamppSQL.conn;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.JFrame;

  
public class frmDashboardAdmin extends javax.swing.JFrame {
     String selectedStatus = "All";
    String selectedImagePath = "";
    private String imagePath;
    private DefaultTableModel model;
    private int rowIndex;
    
    
    private void searchRecords() {
    try {
    //Get user input from search bar #ccstdams
    String keyword = txtSearch.getText();

    //If no filter is applied, reload all data #ccstdams
    if (keyword.isEmpty() && selectedStatus.equals("All")) {
    loadTable();
    return;
    }
    //Get selected column from dropdown #ccstdams
    String column = cmbFilter.getSelectedItem().toString();
        
    System.out.println("Column: " + column);
    System.out.println("Keyword: " + keyword);

    Connection con = ConnectXamppSQL.conn();
    String sql = "SELECT * FROM addrecord WHERE " + column + " LIKE ?";
    PreparedStatement pst = con.prepareStatement(sql);
    pst.setString(1, "%" + keyword + "%");
    
    ResultSet ccst = pst.executeQuery();
    
    //Clear table before inserting new results #ccstdams
    DefaultTableModel model = (DefaultTableModel) tblRecords.getModel();
    model.setRowCount(0);
    boolean found = true;
    
    //Loop through database results #ccstdams
    while (ccst.next()) {
        
    //Compute remaining suspension days #ccstdams
    int suspensionDays = ccst.getInt("Days_of_Suspension");
    
    java.time.LocalDate incidentDate = ccst.getTimestamp("Date_of_Incident")
    .toLocalDateTime().toLocalDate();
    java.time.LocalDate today = java.time.LocalDate.now();

    long daysPassed = java.time.temporal.ChronoUnit.DAYS.between(incidentDate, today);

    int remainingDays = (int) (suspensionDays - daysPassed);
    
    //Prevent negative values #ccstdams
    if (remainingDays < 0) remainingDays = 0;
    
    //Determine status (Active / Inactive) #ccstdams
    String status; 
    if (remainingDays == 0) {
    status = "Inactive";
    } 
    else {
    status = "Active";
    }
    if (!selectedStatus.equals("All") && !status.equals(selectedStatus)) {
    continue;
    }

    //Format remaining days text #ccstdams
    String remainingText = (remainingDays == 0)
    ? "Completed"
    : remainingDays + " day(s) left";
 
    //Add row to table #ccstdams
    model.addRow(new Object[]{
        ccst.getInt("id"),
        ccst.getString("Student_ID"),
        ccst.getString("Student_Name"),
        ccst.getString("Grade_Section"),
        ccst.getString("Offense_Type"),
        remainingText,
        ccst.getString("Offense_Classification"),
        ccst.getTimestamp("Date_of_Incident"),
        ccst.getString("image_path"),
        status
    });
    }

    if (!found) {
    System.out.println("No results found!");
    }
    } 
    catch (Exception e) {
    e.printStackTrace();
    }
    }

   // private void displayImage(String path) {
        //try {
           //ImageIcon icon = new ImageIcon(path);
            //Image img = icon.getImage().getScaledInstance(
           //picture.getWidth(),
            //picture.getHeight(),
           // Image.SCALE_SMOOTH);
            //picture.setIcon(new ImageIcon(img));
          // } 
      // catch (Exception e) {
            //picture.setIcon(null);
           // System.out.println("Image error: " + e.getMessage());
       // }
       // }
        public frmDashboardAdmin() {
        initComponents();
        
        tblRecords.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblRecords.getTableHeader().setBackground(new Color(102,204,255));
        tblRecords.getTableHeader().setForeground(Color.BLACK);
        tblRecords.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        cmbMonth.addActionListener(e -> filterByMonthYear());
        cmbYear.addActionListener(e -> filterByMonthYear());
        loadSummary();
        RoundedPanel panel = new RoundedPanel(30);
        panel.setBackground(Color.WHITE);
        //picture.setPreferredSize(new java.awt.Dimension(200, 200));
        
        loadTable();
        tblRecords.getColumnModel().getColumn(8).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
    @Override
    public java.awt.Component getTableCellRendererComponent(
    javax.swing.JTable table, Object value,
    boolean isSelected, boolean hasFocus,
    int row, int column) {

        java.awt.Component ccst = super.getTableCellRendererComponent(
        table, value, isSelected, hasFocus, row, column);
        String status = value.toString();
        setHorizontalAlignment(javax.swing.JLabel.CENTER);

        if (status.equals("Active")) {
            ccst.setBackground(new java.awt.Color(46, 204, 113));
            ccst.setForeground(java.awt.Color.WHITE);
            } 
        else {
            ccst.setBackground(new java.awt.Color(231, 76, 60));
            ccst.setForeground(java.awt.Color.WHITE);
            }
        if  (isSelected) {
            ccst.setBackground(table.getSelectionBackground());
            ccst.setForeground(table.getSelectionForeground());
            }
        return ccst;
        }
        });
        
        scaleImage("C:\\Users\\CL2~PC20\\Downloads\\damsim\\back.png", lblbackg);
        }
    private void scaleImage (String location, JLabel label) {
    ImageIcon icon = new ImageIcon(location);
    Image img = icon.getImage();
    Image imgScale = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
    ImageIcon scaledIcon = new ImageIcon(imgScale);
    label.setIcon(scaledIcon);
    }
    
    
    private void loadTable() {
        try {
            
        Connection con = ConnectXamppSQL.conn();
        
        //Get all records
        String sql = "SELECT * FROM addrecord";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet ccst = pst.executeQuery();

        //Clear table
        DefaultTableModel model = (DefaultTableModel) tblRecords.getModel();
        model.setRowCount(0);

        //Loop through records
        while (ccst.next()) {
        int suspensionDays = ccst.getInt("Days_of_Suspension");

        java.time.LocalDate incidentDate = ccst.getTimestamp("Date_of_Incident")
        .toLocalDateTime().toLocalDate();
        java.time.LocalDate today = java.time.LocalDate.now();
        long daysPassed = java.time.temporal.ChronoUnit.DAYS.between(incidentDate, today);
        int remainingDays = (int)(suspensionDays - daysPassed);
        
        if (remainingDays < 0) remainingDays = 0;
        
        String status;
        if (remainingDays == 0) {
          status = "Inactive";
        } 
        else {
          status = "Active";
        }
        
        String remainingText = (remainingDays == 0)
        ? "Completed" 
        : remainingDays + " day(s) left";
        
        //Add data to table
        model.addRow(new Object[]{
        ccst.getInt("id"),
        ccst.getString("Student_ID"),
        ccst.getString("Student_Name"),
        ccst.getString("Grade_Section"),
        ccst.getString("Offense_Type"),
        remainingText,
        ccst.getString("Offense_Classification"),
        ccst.getTimestamp("Date_of_Incident"),
        ccst.getString("image_path"),
        status 
        });
        }

    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this, e.getMessage());
    }
    }
    public void loadSummary() {
    try {
        Connection con = ConnectXamppSQL.conn();
        String sql = "SELECT * FROM addrecord";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        int active = 0;
        int inactive = 0;

        while (rs.next()) {
        int suspensionDays = rs.getInt("Days_of_Suspension");

    LocalDate incidentDate = rs.getTimestamp("Date_of_Incident")
    .toLocalDateTime().toLocalDate();
    LocalDate today = LocalDate.now();

    long daysPassed = ChronoUnit.DAYS.between(incidentDate, today);
    int remainingDays = (int)(suspensionDays - daysPassed);

    if (remainingDays <= 0) {
        inactive++;
    } else {
        active++;
    }
    }

lblActiveNum.setText(String.valueOf(active));
lblActiveNum.setForeground(new Color(46, 204, 113));
lblActiveNum.setFont(new Font("Arial", Font.BOLD, 24));
lblInactiveNum.setText(String.valueOf(inactive));
lblInactiveNum.setForeground(new Color(231, 76, 60));
lblInactiveNum.setFont(new Font("Arial", Font.BOLD, 24));

    String mostSql = "SELECT Offense_Type, COUNT(*) AS total " +
                     "FROM addrecord " +
                     "GROUP BY Offense_Type " +
                     "ORDER BY total DESC LIMIT 1";

    PreparedStatement pstMost = con.prepareStatement(mostSql);
    ResultSet rsMost = pstMost.executeQuery();

    if (rsMost.next()) {
    lblMostViolation.setText(rsMost.getString("Offense_Type"));
    } 
    else {
    lblMostViolation.setText("None");
    }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
    
    private int getMonthNumber(String month) {
    switch (month) {
        case "January": return 1;
        case "February": return 2;
        case "March": return 3;
        case "April": return 4;
        case "May": return 5;
        case "June": return 6;
        case "July": return 7;
        case "August": return 8;
        case "September": return 9;
        case "October": return 10;
        case "November": return 11;
        case "December": return 12;
        default: return 0;
    }
}
    private void filterByMonthYear() {
    try {
        Connection con = ConnectXamppSQL.conn();

        String month = cmbMonth.getSelectedItem().toString();
        String year = cmbYear.getSelectedItem().toString();

        StringBuilder sql = new StringBuilder("SELECT * FROM addrecord WHERE 1=1");

        if (!month.equals("All")) {
            sql.append(" AND MONTH(Date_of_Incident) = ?");
        }

        if (!year.equals("All")) {
            sql.append(" AND YEAR(Date_of_Incident) = ?");
        }

        PreparedStatement pst = con.prepareStatement(sql.toString());

        int paramIndex = 1;

        if (!month.equals("All")) {
            pst.setInt(paramIndex++, getMonthNumber(month));
        }

        if (!year.equals("All")) {
            pst.setInt(paramIndex++, Integer.parseInt(year));
        }

        ResultSet ccst = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) tblRecords.getModel();
        model.setRowCount(0);

        while (ccst.next()) {

            int suspensionDays = ccst.getInt("Days_of_Suspension");

            LocalDate incidentDate = ccst.getTimestamp("Date_of_Incident")
                    .toLocalDateTime().toLocalDate();
            LocalDate today = LocalDate.now();

            long daysPassed = ChronoUnit.DAYS.between(incidentDate, today);
            int remainingDays = (int)(suspensionDays - daysPassed);
            if (remainingDays < 0) remainingDays = 0;

            String status = (remainingDays == 0) ? "Inactive" : "Active";

            String remainingText = (remainingDays == 0)
                    ? "Completed"
                    : remainingDays + " day(s) left";

            model.addRow(new Object[]{
                ccst.getInt("id"),
                ccst.getString("Student_ID"),
                ccst.getString("Student_Name"),
                ccst.getString("Grade_Section"),
                ccst.getString("Offense_Type"),
                remainingText,
                ccst.getString("Offense_Classification"),
                ccst.getTimestamp("Date_of_Incident"),
                ccst.getString("image_path"),
                status
        });
        }
        } 
        catch (Exception e) {
        e.printStackTrace();
        }
        }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new RoundedPanel(30);
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        StudentId = new javax.swing.JTextField();
        StudentName = new javax.swing.JTextField();
        GradeAndSection = new javax.swing.JComboBox<>();
        jPanel5 = new RoundedPanel(30);
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        DateofIncident = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        Offense = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        Classification = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        btnUpdate = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new RoundedPanel(30);
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRecords = new javax.swing.JTable();
        jPanel6 = new RoundedPanel(30);
        jButton4 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        cmbMonth = new javax.swing.JComboBox<>();
        cmbYear = new javax.swing.JComboBox<>();
        cmbFilter = new javax.swing.JComboBox<>();
        jPanel8 = new RoundedPanel(30);
        jLabel15 = new javax.swing.JLabel();
        lblMostViolation = new javax.swing.JLabel();
        jPanel9 = new RoundedPanel(30);
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblActiveNum = new javax.swing.JLabel();
        lblInactiveNum = new javax.swing.JLabel();
        lblbackg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Student Info");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Students ID");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Grade & Section");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Students Name");

        StudentId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StudentIdActionPerformed(evt);
            }
        });

        StudentName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StudentNameActionPerformed(evt);
            }
        });

        GradeAndSection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ICT 12 - A  ", "ICT 12 - B  ", "ICT 12 - C  ", "ICT 12 - D  ", "ICT 12 - E  ", " ", "ICT 11 - A  ", "ICT 11 - B  ", "ICT 11 - C  ", "ICT 11 - D  ", "ICT 11 - E  ", " ", "GAS 12 - A  ", "GAS 12 - B  ", "GAS 12 - C  ", "GAS 12 - D  ", "GAS 12 - E  ", " ", "GAS 11 - A  ", "GAS 11 - B  ", "GAS 11 - C  ", "GAS 11 - D  ", "GAS 11 - E  ", " ", "STEM 12 - A  ", "STEM 12 - B  ", "STEM 12 - C  ", "STEM 12 - D  ", "STEM 12 - E  ", " ", "STEM 11 - A  ", "STEM 11 - B  ", "STEM 11 - C  ", "STEM 11 - D  ", "STEM 11 - E  ", " ", "HUMSS 12 - A  ", "HUMSS 12 - B  ", "HUMSS 12 - C  ", "HUMSS 12 - D  ", "HUMSS 12 - E  ", " ", "HUMSS 11 - A  ", "HUMSS 11 - B  ", "HUMSS 11 - C  ", "HUMSS 11 - D  ", "HUMSS 11 - E  ", " ", "HE 12 - A  ", "HE 12 - B  ", "HE 12 - C  ", "HE 12 - D  ", "HE 12 - E  ", " ", "HE 11 - A  ", "HE 11 - B  ", "HE 11 - C  ", "HE 11 - D  ", "HE 11 - E  ", " ", "ABM 12 - A  ", "ABM 12 - B  ", "ABM 12 - C  ", "ABM 12 - D  ", "ABM 12 - E  ", " ", "ABM 11 - A  ", "ABM 11 - B  ", "ABM 11 - C  ", "ABM 11 - D  ", "ABM 11 - E  " }));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(StudentId)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(StudentName)
                            .addComponent(GradeAndSection, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel5))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(StudentId, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(StudentName, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(GradeAndSection, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 150, 190, 260));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("OFFENSE DETAILS");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Date of Incident");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Offense Type");

        Offense.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Late", "Improper Uniform", "No ID", "Loitering", "Disruptive Behavior", "Using Phone During Class", "Eating Inside Classroom", "Sleeping in Class", "Cutting Classes", "Disrespect to Teacher", "Bullying", "Fighting (Minor)", "Cheating", "Vandalism", "Using Profanity", "Unauthorized Absence", "Physical Assault", "Cyberbullying", "Theft", "Drug Possession", "Smoking Inside Campus", "Alcohol Possession", "Weapon Possession", "Serious Threats" }));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("Offense Classification");

        Classification.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minor", "Major" }));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("Days of Classification");

        btnUpdate.setBackground(new java.awt.Color(102, 255, 102));
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("UPDATE");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(255, 0, 0));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("DELETE");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(78, 78, 78)
                                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(61, 61, 61))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Offense, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(DateofIncident, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(33, 33, 33)
                                .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(Classification, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(42, 42, 42))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(DateofIncident, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Offense, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12))
                    .addComponent(btnUpdate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Classification, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 150, 350, 260));

        jPanel2.setBackground(new java.awt.Color(102, 204, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("C L A R K  C O L L E G E");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("OF SCIENCE AND TECHNOLOGY");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 40, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("___________________________________________________________");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 50, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("DISCIPLINARY ACTION MONITORING SYSTEM");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 70, -1, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1370, 110));

        jPanel3.setBackground(new java.awt.Color(102, 204, 255));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Dashboard");
        jButton1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Monthly Record");
        jButton2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Add Record");
        jButton3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(255, 0, 0));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("LOGOUT");
        jButton7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 382, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 230, 670));

        tblRecords.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Student Id", "Student Name", "Grade & Section", "Offense", "Offense Classification", "Remaining Days", "Date of Incident", "ImagePath", "Status"
            }
        ));
        tblRecords.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRecordsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblRecords);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 460, 1050, 330));

        jButton4.setBackground(new java.awt.Color(102, 204, 255));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Browse");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel18.setText("Image:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4)
                            .addComponent(jLabel18)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addGap(4, 4, 4)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 150, 240, 260));

        jPanel7.setBackground(new java.awt.Color(102, 204, 255));

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Search:");

        cmbMonth.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        cmbMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));

        cmbYear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        cmbYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030" }));

        cmbFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbYear, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(519, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch)
                    .addComponent(jLabel14)
                    .addComponent(cmbMonth, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(cmbYear, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(cmbFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 420, 1050, 40));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Most Violation:");

        lblMostViolation.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel15))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(lblMostViolation, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblMostViolation, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 160, 220, 100));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("Active:");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("Inactive:");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblActiveNum, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblInactiveNum, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel17)
                .addGap(26, 26, 26))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblActiveNum, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInactiveNum, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        jPanel1.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 290, 220, 100));

        lblbackg.setBackground(new java.awt.Color(102, 204, 255));
        jPanel1.add(lblbackg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1370, 810));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void StudentIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StudentIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_StudentIdActionPerformed

    private void StudentNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StudentNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_StudentNameActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        try {
        int id = Integer.parseInt(tblRecords.getValueAt(rowIndex, 0).toString());
        int confirm = javax.swing.JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to delete this record?",
        "Confirm",
        javax.swing.JOptionPane.YES_NO_OPTION
        );

        if (confirm == 0) {
        ConnectXamppSQL.Delete("addrecord", "id", "=", String.valueOf(id));
        javax.swing.JOptionPane.showMessageDialog(this, "Record Deleted!");
        loadTable();
        loadSummary(); 
        }
        } 
        catch (Exception e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(this, e.getMessage());
    }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
try {
    int selectedRow = tblRecords.getSelectedRow();

    if (selectedRow == -1) {
        javax.swing.JOptionPane.showMessageDialog(this, "Please select a record to update!");
        return;
    }

    
    String id = tblRecords.getValueAt(selectedRow, 0).toString();
    String studentId = StudentId.getText();
    String studentName = StudentName.getText();
    String gradeSection = GradeAndSection.getSelectedItem().toString();
    String offense = Offense.getSelectedItem().toString();
    String classification = Classification.getSelectedItem().toString();
    String suspension = jSpinner2.getValue().toString();

    if (selectedImagePath == null || selectedImagePath.isEmpty()) {
    selectedImagePath = tblRecords.getValueAt(selectedRow, 8).toString();
    }
    Date date = (Date) DateofIncident.getValue();
    java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
    String sql = "UPDATE addrecord SET "
            + "Student_Id=?, "
            + "Student_Name=?, "
            + "Grade_Section=?, "
            + "Offense_Type=?, "
            + "Offense_Classification=?, "
            + "Days_of_Suspension=?, "
            + "image_path=?,"
            + "Date_of_Incident=? "
            + "WHERE id=?";

    java.sql.PreparedStatement pst = conn().prepareStatement(sql);

    pst.setString(1, studentId);
    pst.setString(2, studentName);
    pst.setString(3, gradeSection);
    pst.setString(4, offense);
    pst.setString(5, classification);
    pst.setString(6, suspension);
    pst.setString(7, selectedImagePath);
    pst.setTimestamp(8, sqlDate);
    pst.setString(9, id);
     pst.executeUpdate();

    javax.swing.JOptionPane.showMessageDialog(this, "Record Updated Successfully!");
    loadTable();
    loadSummary(); 
    } 
    catch (Exception e) {
    e.printStackTrace();
    javax.swing.JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }     
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       frmDashboardAdmin DA = new frmDashboardAdmin();
       DA.setVisible(true);
       this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tblRecordsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRecordsMouseClicked
     model = (DefaultTableModel) tblRecords.getModel();
        rowIndex = tblRecords.getSelectedRow();
        StudentId.setText(model.getValueAt(rowIndex, 1).toString());
        StudentName.setText(model.getValueAt(rowIndex, 2).toString());
        Offense.setSelectedItem(model.getValueAt(rowIndex, 4).toString());
        GradeAndSection.setSelectedItem(model.getValueAt(rowIndex, 3).toString());
        Classification.setSelectedItem(model.getValueAt(rowIndex, 6).toString());
        Object dateObj = model.getValueAt(rowIndex, 7);
        if (dateObj instanceof Timestamp) {
        DateofIncident.setValue((Timestamp) dateObj);
        }
        
    try {
        int id = Integer.parseInt(model.getValueAt(rowIndex, 0).toString());

        Connection con = ConnectXamppSQL.conn();
        String sql = "SELECT Days_of_Suspension FROM addrecord WHERE id=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            jSpinner2.setValue(rs.getInt("Days_of_Suspension"));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    
    String imgPath = model.getValueAt(rowIndex, 8).toString();
    selectedImagePath = imgPath;
    File imgFile = new File(imgPath);

    if (imgFile.exists()) {
   //displayImage(imgFile.getAbsolutePath());
    }
    else {
    System.out.println("Image not found: " + imgPath);
    //picture.setIcon(null);
    }

  
     
    }//GEN-LAST:event_tblRecordsMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       frmMonthlyRecord month = new frmMonthlyRecord();
       month.setVisible(true);
       this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        frmAddRecord add = new frmAddRecord();
       add.setVisible(true);
       this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        JFileChooser file = new JFileChooser();
        file.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.image","jpg","gif","png","*.webp");
        file.addChoosableFileFilter(filter);
        int output = file.showSaveDialog(file);
        if(output==JFileChooser.APPROVE_OPTION){
        File  selectFile = file.getSelectedFile();
        String path = selectFile.getAbsolutePath();
        //picture.setIcon(imageAdjust(path,null));
        selectedImagePath = path;
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchKeyReleased

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
      logins d = new logins();
       d.setVisible(true);
       this.dispose();
    }//GEN-LAST:event_jButton7ActionPerformed

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
            java.util.logging.Logger.getLogger(frmDashboardAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmDashboardAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmDashboardAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmDashboardAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmDashboardAdmin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> Classification;
    private javax.swing.JSpinner DateofIncident;
    private javax.swing.JComboBox<String> GradeAndSection;
    private javax.swing.JComboBox<String> Offense;
    private javax.swing.JTextField StudentId;
    private javax.swing.JTextField StudentName;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cmbFilter;
    private javax.swing.JComboBox<String> cmbMonth;
    private javax.swing.JComboBox<String> cmbYear;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JLabel lblActiveNum;
    private javax.swing.JLabel lblInactiveNum;
    private javax.swing.JLabel lblMostViolation;
    private javax.swing.JLabel lblbackg;
    private javax.swing.JTable tblRecords;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
