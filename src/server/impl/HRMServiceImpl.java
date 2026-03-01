package server.impl;

import shared.dto.ResetRequestDTO;
import java.util.ArrayList;
import java.util.List;
import shared.services.HRMService;
import shared.dto.LoginResultDTO;
import shared.dto.MonthlyReportDTO;
import shared.dto.MonthlySalaryDTO;
import shared.dto.YearlyReportDTO;
import shared.models.Employee;

import server.DatabaseConnection;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class HRMServiceImpl extends UnicastRemoteObject implements HRMService {

    private static class Account {
        String username;
        String password;
        String role; // "HR" or "EMPLOYEE"
        boolean active;
        String fullName;
        String employeeId;

        Account(String username, String password, String role, boolean active, String fullName, String employeeId) {
            this.username = username;
            this.password = password;
            this.role = role;
            this.active = active;
            this.fullName = fullName;
            this.employeeId = employeeId;
        }
    }

    private final Map<String, Account> accounts = new HashMap<>();

    public HRMServiceImpl() throws RemoteException {
        super();
        loadAccountsFromDb();
    }

    private void loadAccountsFromDb() throws RemoteException {
        accounts.clear();

        final String sql = """
                    SELECT username, password, role, active, full_name, employee_id
                    FROM accounts
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");
                boolean active = rs.getBoolean("active");
                String fullName = rs.getString("full_name");
                String employeeId = rs.getString("employee_id");

                accounts.put(username, new Account(username, password, role, active, fullName, employeeId));
            }

        } catch (SQLException e) {
            throw new RemoteException("Failed reading accounts from DB: " + e.getMessage(), e);
        }
    }

    @Override
    public LoginResultDTO login(String username, String password) throws RemoteException {
        if (username == null || password == null) {
            return new LoginResultDTO(false, null, "Username/password cannot be null");
        }

        Account acc = accounts.get(username.trim());
        if (acc == null) {
            // Optional: if you want “always latest”, you can reload here:
            // loadAccountsFromDb();
            // acc = accounts.get(username.trim());
            return new LoginResultDTO(false, null, "User not found");
        }

        if (!acc.active) {
            return new LoginResultDTO(false, null, "Account is deactivated. Contact HR/Admin.");
        }

        if (!acc.password.equals(password)) {
            return new LoginResultDTO(false, null, "Invalid password");
        }

        return new LoginResultDTO(true, acc.role, "Login successful", acc.employeeId,
    acc.fullName);
    
    }
    /*
     * PSEUDOCODE - getEmployeeProfile():
     * 
     * FUNCTION getEmployeeProfile(employeeId)
     *     1. QUERY employees table WHERE employee_id = employeeId
     *     2. IF found THEN
     *        CREATE Employee object
     *        RETURN Employee
     *     3. ELSE
     *        RETURN null
     * END FUNCTION
     * 
     * NOTE: employeeId is String (e.g., "EMP001"), not int!
     */
    @Override
    public Employee getEmployeeProfile(String employeeId) throws RemoteException {
        final String sql = """
            SELECT id, employee_id, first_name, last_name, ic_passport, 
                   email, phone, address, department, position
            FROM employees
            WHERE employee_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getInt("id"));
                emp.setEmployeeId(rs.getString("employee_id"));
                emp.setFirstName(rs.getString("first_name"));
                emp.setLastName(rs.getString("last_name"));
                emp.setIcPassport(rs.getString("ic_passport"));
                emp.setEmail(rs.getString("email"));
                emp.setPhone(rs.getString("phone"));
                emp.setAddress(rs.getString("address"));
                emp.setDepartment(rs.getString("department"));
                emp.setPosition(rs.getString("position"));
                
                return emp;
            }

            return null;

        } catch (SQLException e) {
    System.err.println("DB ERROR in getEmployeeProfile");
    e.printStackTrace();  
    throw new RemoteException("Failed to get employee profile", e);
}
    }
      /*
     * PSEUDOCODE - updateEmployeeProfile():
     * 
     * FUNCTION updateEmployeeProfile(employee)
     *     1. VALIDATE employee object
     *     2. UPDATE employees table
     *     3. RETURN success/failure
     * END FUNCTION
     */
    @Override
    public boolean updateEmployeeProfile(Employee employee) throws RemoteException {
        if (employee == null || employee.getEmployeeId() == null) {
            return false;
        }

        final String sql = """
            UPDATE employees 
            SET first_name = ?, last_name = ?, email = ?, 
                phone = ?, address = ?
            WHERE employee_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getEmail());
            ps.setString(4, employee.getPhone());
            ps.setString(5, employee.getAddress());
            ps.setString(6, employee.getEmployeeId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RemoteException("Failed to update employee profile: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean setAccountActive(String username, boolean active) throws RemoteException {
        if (username == null)
            return false;

        username = username.trim();
        Account acc = accounts.get(username);
        if (acc == null)
            return false;

        final String sql = "UPDATE accounts SET active = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, active);
            ps.setString(2, username);

            int updated = ps.executeUpdate();
            if (updated == 1) {
                acc.active = active; // keep cache in sync
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new RemoteException("Failed updating account active in DB: " + e.getMessage(), e);
        }
    }

    @Override
    public String submitPasswordResetRequest(String fullName, String employeeId) throws RemoteException {
        if (fullName == null || employeeId == null || fullName.trim().isEmpty() || employeeId.trim().isEmpty()) {
            return "Full name and employee ID are required.";
        }

        String fullNameTrim = fullName.trim();
        String employeeIdTrim = employeeId.trim();

        // Verify employee exists (DB check, not CSV)
        final String existsSql = """
                    SELECT 1
                    FROM accounts
                    WHERE LOWER(employee_id) = LOWER(?)
                      AND LOWER(full_name) = LOWER(?)
                    LIMIT 1
                """;

        final String insertSql = """
                    INSERT INTO reset_requests (request_time, full_name, employee_id, status)
                    VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1) check exists
            try (PreparedStatement ps = conn.prepareStatement(existsSql)) {
                ps.setString(1, employeeIdTrim);
                ps.setString(2, fullNameTrim);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return "No matching employee found. Please check your details.";
                    }
                }
            }

            // 2) insert reset request
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(2, fullNameTrim);
                ps.setString(3, employeeIdTrim);
                ps.setString(4, "PENDING");
                ps.executeUpdate();
            }

            return "Reset request submitted. HR/Admin will process it.";

        } catch (SQLException e) {
            throw new RemoteException("Failed saving reset request in DB: " + e.getMessage(), e);
        }
    }

    // ----- stubs for now (so it compiles) -----

    @Override
    public void registerEmployee(Employee employee) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Employee getEmployeeById(String employeeId) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MonthlySalaryDTO getMonthlySalary(String employeeId, int year, int month) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MonthlyReportDTO generateMonthlyReport(int year, int month) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public YearlyReportDTO generateYearlyReport(int year) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ResetRequestDTO> getResetRequests() throws RemoteException {
        final String sql = """
                SELECT request_id, request_time, full_name, employee_id, status
                FROM reset_requests
                ORDER BY request_id DESC
                """;

        List<ResetRequestDTO> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ResetRequestDTO(
                        rs.getInt("request_id"),
                        rs.getTimestamp("request_time"),
                        rs.getString("full_name"),
                        rs.getString("employee_id"),
                        rs.getString("status")));
            }

            return list;

        } catch (SQLException e) {
            throw new RemoteException("Failed reading reset requests from DB: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateResetRequestStatus(int requestId, String newStatus) throws RemoteException {
        if (newStatus == null)
            return false;

        String status = newStatus.trim().toUpperCase();
        if (!(status.equals("PENDING") || status.equals("APPROVED") || status.equals("REJECTED"))) {
            return false;
        }

        final String sql = "UPDATE reset_requests SET status = ? WHERE request_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, requestId);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RemoteException("Failed updating reset request status in DB: " + e.getMessage(), e);
        }
    }

}