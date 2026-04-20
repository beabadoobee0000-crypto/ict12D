/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.disciplinaryactionms;

import java.sql.*;
import java.util.*;

public class ChartData {
    public static Map<String, Integer> getMonthlyData(int year) {
    Map<String, Integer> data = new LinkedHashMap<>();

    try {
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/ict12d", "root", "");

        String sql = "SELECT MONTH(Date_of_Incident) AS month, COUNT(*) AS total " +
                     "FROM addrecord WHERE YEAR(Date_of_Incident) = ? GROUP BY month";

        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, year);
        ResultSet rs = pst.executeQuery();

        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        for (String m : months) {
            data.put(m, 0);
        }

        while (rs.next()) {
            int monthNum = rs.getInt("month");

            String monthName = switch (monthNum) {
                case 1 -> "Jan"; 
                case 2 -> "Feb"; 
                case 3 -> "Mar";
                case 4 -> "Apr"; 
                case 5 -> "May"; 
                case 6 -> "Jun";
                case 7 -> "Jul"; 
                case 8 -> "Aug"; 
                case 9 -> "Sep";
                case 10 -> "Oct"; 
                case 11 -> "Nov"; 
                case 12 -> "Dec";
                default -> "Unknown";
            };

            data.put(monthName, rs.getInt("total"));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    
     return data;
    }
}