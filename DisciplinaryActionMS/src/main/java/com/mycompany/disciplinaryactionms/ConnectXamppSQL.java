/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.disciplinaryactionms;

import java.sql.*;
import java.util.*;
public class ConnectXamppSQL {

public static Connection conn() throws Exception {
String databaseName = "ict12d";
return DriverManager.getConnection("jdbc:mysql://localhost:3306/"+databaseName, "root", "");
}

public static InsertBuilder Insert(String table) {
return new InsertBuilder(table);
}
public static class InsertBuilder {
private String table;
private List<String> columns = new ArrayList<>();
private List<String> values = new ArrayList<>();
public InsertBuilder(String table) {
this.table = table;
}
public InsertBuilder set(String column, String value) {
columns.add(column);
values.add("'" + value + "'");
return this;
}
public InsertBuilder set(String column, java.sql.Timestamp value) {
        columns.add(column);
        values.add("'" + value.toString() + "'");
        return this;
}
public void execute() throws Exception {
String sql = "INSERT INTO " + table + " (" + String.join(", ", columns) + ") VALUES (" + String.join(", ", values) + ")";
conn().createStatement().execute(sql);
System.out.println("Inserted: " + sql);
}
}
public static UpdateBuilder Update(String table) {
return new UpdateBuilder(table);
}
public static class UpdateBuilder {
private String table;
private List<String> setClauses = new ArrayList<>();
private List<String> whereClauses = new ArrayList<>();
public UpdateBuilder(String table) {
this.table = table;
}
public UpdateBuilder set(String column, String value) {
setClauses.add(column + "='" + value + "'");
return this;
}
public UpdateBuilder where(String column, String op, String value) {
if (op.equals("%")) op = "LIKE";
if (op.equalsIgnoreCase("LIKE") && !value.contains("%")) value = "%" + value + "%";
whereClauses.add(column + " " + op + " '" + value + "'");
return this;
}
public void execute() throws Exception {
String sql = "UPDATE " + table + " SET " + String.join(", ", setClauses);
if (!whereClauses.isEmpty()) {
sql += " WHERE " + String.join(" AND ", whereClauses);
}
conn().createStatement().execute(sql);
System.out.println("Updated: " + sql);
}
}

public static void Delete(String table, String column, String op, String value) throws Exception {
if (op.equals("%")) op = "LIKE";
if (op.equalsIgnoreCase("LIKE") && !value.contains("%")) value = "%" + value + "%";
String sql = "DELETE FROM " + table + " WHERE " + column + " " + op + " '" + value + "'";
conn().createStatement().execute(sql);
System.out.println("Deleted: " + sql);
}

private String table;
private List<String> conditions = new ArrayList<>();
public static ConnectXamppSQL Read(String table) {
ConnectXamppSQL r = new ConnectXamppSQL();
r.table = table;
return r;
}
public ConnectXamppSQL where(String column, String op, String value) {
if (op.equals("%")) op = "LIKE"; // shorthand for LIKE
if (op.equalsIgnoreCase("LIKE") && !value.contains("%")) value = "%" + value + "%";
conditions.add(column + " " + op + " '" + value + "'");
return this;
}
public List<Map<String,String>> get() throws Exception {
List<Map<String,String>> rows = new ArrayList<>();
String whereClause = "";
if (!conditions.isEmpty()) {
whereClause = " WHERE " + String.join(" AND ", conditions);
}
ResultSet rs = conn().createStatement().executeQuery(
"SELECT * FROM " + table + whereClause
);
ResultSetMetaData md = rs.getMetaData();
int colCount = md.getColumnCount();
while (rs.next()) {
Map<String,String> row = new HashMap<>();
for (int i = 1; i <= colCount; i++) {
String colName = md.getColumnName(i);
String value = rs.getString(i);
row.put(colName, value);
System.out.print(colName + ": " + value + " | ");
}
System.out.println();
rows.add(row);
}
return rows;
}
public Map<String,String> getOne() throws Exception {
List<Map<String,String>> rows = get();
if (rows.isEmpty()) return null;
return rows.get(0);
}
}