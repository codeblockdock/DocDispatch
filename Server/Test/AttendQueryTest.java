/*
 * AttendQueryTest.java - Simple Test File to Mark a Query as Attended
 * 
 * PURPOSE:
 * This standalone Java file allows you to:
 * 1. View all pending (unattended) queries
 * 2. Select a query by qid
 * 3. Add doctor/hospital details to the attended table
 * 4. Set attended = 1 in the queries table
 * 
 * HOW TO RUN:
 * 1. Compile: javac -cp mysql-connector-j-8.x.x.jar AttendQueryTest.java
 * 2. Run: java -cp ".;mysql-connector-j-8.x.x.jar" AttendQueryTest
 * 
 * Or simply run from IDE with MySQL connector in classpath.
 */

import java.sql.*;
import java.util.Scanner;

public class AttendQueryTest {
    
    // Database connection settings (same as application.properties)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/docdispatch";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Connected to database successfully!\n");
            
            // Show all pending queries
            showPendingQueries(conn);
            
            // Get qid from user
            System.out.print("\nEnter QID to attend (or 0 to exit): ");
            int qid = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            if (qid == 0) {
                System.out.println("Exiting...");
                return;
            }
            
            // Check if query exists and is not already attended
            if (!isValidPendingQuery(conn, qid)) {
                System.out.println("Error: QID " + qid + " not found or already attended.");
                return;
            }
            
            // Get attendance details from user
            System.out.println("\n--- Enter Attendance Details ---");
            
            System.out.print("Doctor Name: ");
            String doctor = scanner.nextLine();
            
            System.out.print("Hospital Name: ");
            String hospital = scanner.nextLine();
            
            System.out.print("City: ");
            String city = scanner.nextLine();
            
            System.out.print("Diagnosis: ");
            String diagnosis = scanner.nextLine();
            
            System.out.print("Treatment: ");
            String treatment = scanner.nextLine();
            
            System.out.print("Advice (optional, press Enter to skip): ");
            String advice = scanner.nextLine();
            
            System.out.print("Appointment (e.g., '2024-01-15 10:00 AM'): ");
            String appointment = scanner.nextLine();
            
            // Perform the attendance update
            boolean success = attendQuery(conn, qid, doctor, hospital, city, diagnosis, treatment, advice, appointment);
            
            if (success) {
                System.out.println("\n✓ Query #" + qid + " marked as attended successfully!");
                System.out.println("  Doctor: " + doctor);
                System.out.println("  Diagnosis: " + diagnosis);
            } else {
                System.out.println("\n✗ Failed to attend query #" + qid);
            }
            
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        scanner.close();
    }
    
    private static void showPendingQueries(Connection conn) throws SQLException {
        String sql = "SELECT qid, name, contact, age, gender, symptoms, received_at FROM queries WHERE attended = 0 ORDER BY received_at DESC";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("========== PENDING QUERIES (Attended = 0) ==========");
            System.out.printf("%-6s %-20s %-15s %-5s %-8s %-30s %-20s%n", 
                "QID", "Name", "Contact", "Age", "Gender", "Symptoms", "Received At");
            System.out.println("-".repeat(110));
            
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.printf("%-6d %-20s %-15s %-5d %-8s %-30s %-20s%n",
                    rs.getInt("qid"),
                    truncate(rs.getString("name"), 18),
                    rs.getString("contact"),
                    rs.getInt("age"),
                    rs.getString("gender"),
                    truncate(rs.getString("symptoms"), 28),
                    rs.getTimestamp("received_at").toString()
                );
            }
            
            if (!hasRows) {
                System.out.println("No pending queries found.");
            }
            System.out.println("=".repeat(110));
        }
    }
    
    private static boolean isValidPendingQuery(Connection conn, int qid) throws SQLException {
        String sql = "SELECT qid FROM queries WHERE qid = ? AND attended = 0";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, qid);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }
    
    private static boolean attendQuery(Connection conn, int qid, String doctor, String hospital, 
                                        String city, String diagnosis, String treatment, 
                                        String advice, String appointment) throws SQLException {
        // Start transaction
        conn.setAutoCommit(false);
        
        try {
            // 1. Insert into attended table
            String insertSql = "INSERT INTO attended (qid, doctor, hospital, city, diagnosis, treatment, advice, appointment) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, qid);
                pstmt.setString(2, doctor);
                pstmt.setString(3, hospital);
                pstmt.setString(4, city);
                pstmt.setString(5, diagnosis);
                pstmt.setString(6, treatment);
                pstmt.setString(7, advice.isEmpty() ? null : advice);
                pstmt.setString(8, appointment);
                pstmt.executeUpdate();
            }
            
            // 2. Update queries table - set attended = 1
            String updateSql = "UPDATE queries SET attended = 1 WHERE qid = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, qid);
                pstmt.executeUpdate();
            }
            
            // Commit transaction
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            // Rollback on error
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 2) + ".." : str;
    }
}
