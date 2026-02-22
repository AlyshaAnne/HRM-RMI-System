package server;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class TestDB {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM reset_requests");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("request_id") + " | " +
                                rs.getString("employee_id") + " | " +
                                rs.getString("status"));
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}