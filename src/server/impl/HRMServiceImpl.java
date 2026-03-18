package server.impl;

import server.repository.EmployeeRepository;
import shared.dto.YearlyEmployeeReportDTO;
import shared.dto.ResetRequestDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import shared.services.HRMService;
import shared.dto.LoginResultDTO;
import shared.dto.MonthlyReportDTO;
import shared.dto.MonthlySalaryDTO;
import shared.dto.YearlyReportDTO;
import shared.models.Employee;
import shared.models.FamilyMember;
import shared.models.LeaveApplication;
import shared.models.LeaveBalance;
import shared.models.LeaveType;
import server.DatabaseConnection;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/*
 * PSEUDOCODE for HRMServiceImpl:
 * 
 * PURPOSE: Server-side implementation of all HRM system operations
 * 
 * ARCHITECTURE:
 * - Extends UnicastRemoteObject for RMI support
 * - Implements HRMService interface
 * - Manages database connections via DatabaseConnection class
 * - Handles all business logic and data validation
 * 
 * MAIN COMPONENTS:
 * 1. Account Management (login, activation)
 * 2. Employee Profile Management (view, update)
 * 3. Family Details Management (CRUD operations)
 * 4. Password Reset Management
 * 5. Reports & Salary (stub methods)
 */
public class HRMServiceImpl extends UnicastRemoteObject implements HRMService {

    // ==========================================
    // INNER CLASS: Account Cache
    // ==========================================

    /*
     * PSEUDOCODE - Account Inner Class:
     * 
     * PURPOSE: In-memory cache of account data for fast login validation
     * 
     * FIELDS:
     * - username: Login username
     * - password: Plain text password (should be hashed in production)
     * - role: HR, ADMIN, or EMPLOYEE
     * - active: Whether account is enabled
     * - fullName: Employee's full name
     * - employeeId: Business ID like "EMP001"
     */
    private static class Account {
        String username;
        String password;
        String role;
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

    // ==========================================
    // INSTANCE VARIABLES
    // ==========================================

    private final Map<String, Account> accounts = new HashMap<>();
    private final EmployeeRepository employeeRepository = new EmployeeRepository();

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    /*
     * PSEUDOCODE - Constructor:
     * 
     * FUNCTION HRMServiceImpl()
     * 1. CALL super() to initialize UnicastRemoteObject
     * 2. CALL loadAccountsFromDb() to populate account cache
     * END FUNCTION
     */
    public HRMServiceImpl() throws RemoteException {
        super();
        loadAccountsFromDb();
    }

    // ==========================================
    // ACCOUNT CACHE MANAGEMENT
    // ==========================================

    /*
     * PSEUDOCODE - loadAccountsFromDb():
     * 
     * PURPOSE: Load all accounts from database into memory cache
     * 
     * FUNCTION loadAccountsFromDb()
     * 1. CLEAR existing accounts map
     * 2. PREPARE SQL query: SELECT all from accounts table
     * 3. CONNECT to database
     * 4. EXECUTE query
     * 5. FOR each row in ResultSet:
     * a. CREATE Account object with row data
     * b. PUT into accounts map with username as key
     * 6. CLOSE resources
     * 
     * CATCH SQLException:
     * THROW RemoteException
     * END FUNCTION
     */
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

    // ==========================================
    // AUTHENTICATION METHODS
    // ==========================================

    /*
     * PSEUDOCODE - login():
     * 
     * PURPOSE: Authenticate user and return login result
     * 
     * FUNCTION login(username, password)
     * 1. VALIDATE inputs (not null)
     * 2. GET account from cache using username
     * 3. IF account not found THEN
     * - RETURN failure DTO with "User not found" message
     * 4. IF account is not active THEN
     * - RETURN failure DTO with "Account deactivated" message
     * 5. IF password doesn't match THEN
     * - RETURN failure DTO with "Invalid password" message
     * 6. ELSE
     * - RETURN success DTO with employee details (id, name, role)
     * END FUNCTION
     */
    @Override
    public LoginResultDTO login(String username, String password) throws RemoteException {
        // Validation
        if (username == null || password == null) {
            return new LoginResultDTO(false, null, "Username/password cannot be null");
        }

        // Get account from cache
        Account acc = accounts.get(username.trim());

        // Check if account exists
        if (acc == null) {
            // Optional: Reload from database for latest data
            // loadAccountsFromDb();
            // acc = accounts.get(username.trim());
            return new LoginResultDTO(false, null, "User not found");
        }

        // Check if account is active
        if (!acc.active) {
            return new LoginResultDTO(false, null, "Account is deactivated. Contact HR/Admin.");
        }

        // Check password
        if (!acc.password.equals(password)) {
            return new LoginResultDTO(false, null, "Invalid password");
        }

        // Success - return employee details
        return new LoginResultDTO(true, acc.role, "Login successful", acc.employeeId, acc.fullName);
    }

    // ==========================================
    // EMPLOYEE PROFILE METHODS
    // ==========================================

    /*
     * PSEUDOCODE - getEmployeeProfile():
     * 
     * PURPOSE: Retrieve employee profile data from database
     * 
     * FUNCTION getEmployeeProfile(employeeId)
     * 1. PREPARE SQL query: SELECT from employees WHERE employee_id = ?
     * 2. CONNECT to database
     * 3. SET parameter: employeeId
     * 4. EXECUTE query
     * 5. IF result found THEN
     * a. CREATE Employee object
     * b. POPULATE all fields from ResultSet
     * c. RETURN Employee object
     * 6. ELSE
     * - RETURN null (employee not found)
     * 
     * CATCH SQLException:
     * - LOG error
     * - THROW RemoteException
     * END FUNCTION
     */
    @Override
    public Employee getEmployeeProfile(String employeeId) throws RemoteException {
        final String sql = """
                    SELECT id, employee_id, first_name, last_name, ic_passport,
                       email, phone, address, department, "position"
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
     * PSEUDOCODE - updateEmployeeProfile() with Account Sync:
     * 
     * FUNCTION updateEmployeeProfile(employee)
     * 1. UPDATE employees table (first_name, last_name, etc.)
     * 2. ALSO UPDATE accounts.full_name to keep in sync
     * 3. RETURN success/failure
     * 
     * WHY THIS IS NEEDED:
     * - Password reset checks accounts.full_name
     * - If employee changes name in profile, it must update accounts too
     * - This keeps both tables synchronized
     * END FUNCTION
     */
    @Override
    public boolean updateEmployeeProfile(Employee employee) throws RemoteException {
        if (employee == null || employee.getEmployeeId() == null) {
            return false;
        }

        // SQL to update BOTH tables
        final String updateEmployeeSql = """
                    UPDATE employees
                    SET first_name = ?, last_name = ?, email = ?,
                        phone = ?, address = ?
                    WHERE employee_id = ?
                """;

        final String updateAccountSql = """
                    UPDATE accounts
                    SET full_name = ?
                    WHERE employee_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Use transaction to update both tables atomically
            conn.setAutoCommit(false);

            try {
                // STEP 1: Update employees table
                try (PreparedStatement ps = conn.prepareStatement(updateEmployeeSql)) {
                    ps.setString(1, employee.getFirstName());
                    ps.setString(2, employee.getLastName());
                    ps.setString(3, employee.getEmail());
                    ps.setString(4, employee.getPhone());
                    ps.setString(5, employee.getAddress());
                    ps.setString(6, employee.getEmployeeId());

                    int employeeUpdated = ps.executeUpdate();

                    if (employeeUpdated == 0) {
                        conn.rollback();
                        return false;
                    }
                }

                // STEP 2: Update accounts table with synchronized full name
                try (PreparedStatement ps = conn.prepareStatement(updateAccountSql)) {
                    String fullName = employee.getFirstName() + " " + employee.getLastName();
                    ps.setString(1, fullName);
                    ps.setString(2, employee.getEmployeeId());
                    ps.executeUpdate();
                }

                // Commit both updates
                conn.commit();
                System.out.println("Profile updated and account name synchronized for: " + employee.getEmployeeId());

                // Reload account cache to reflect changes
                loadAccountsFromDb();

                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("DB ERROR in updateEmployeeProfile");
            e.printStackTrace();
            throw new RemoteException("Failed to update employee profile: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // FAMILY DETAILS METHODS
    // ==========================================

    /*
     * PSEUDOCODE - getFamilyDetails():
     * 
     * PURPOSE: Retrieve all family members for a specific employee
     * 
     * FUNCTION getFamilyDetails(employeeId)
     * 1. PREPARE SQL query: SELECT from family_members WHERE employee_id = ?
     * 2. CREATE empty list for results
     * 3. CONNECT to database
     * 4. SET parameter: employeeId
     * 5. EXECUTE query
     * 6. FOR each row in ResultSet:
     * a. CREATE FamilyMember object
     * b. POPULATE all fields from database
     * c. HANDLE date conversion (SQL Date → String)
     * d. ADD to list
     * 7. RETURN list (empty if no family members)
     * 
     * CATCH SQLException:
     * - LOG error
     * - THROW RemoteException
     * END FUNCTION
     */
    @Override
    public List<FamilyMember> getFamilyDetails(String employeeId) throws RemoteException {
        final String sql = """
                    SELECT id, employee_id, name, relationship, ic_passport,
                           date_of_birth, contact_number
                    FROM family_members
                    WHERE employee_id = ?
                    ORDER BY id
                """;

        List<FamilyMember> familyMembers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                FamilyMember member = new FamilyMember();
                member.setId(rs.getInt("id"));
                member.setEmployeeId(rs.getString("employee_id"));
                member.setName(rs.getString("name"));
                member.setRelationship(rs.getString("relationship"));
                member.setIcPassport(rs.getString("ic_passport"));

                // Handle date - convert SQL Date to String (YYYY-MM-DD format)
                Date dob = rs.getDate("date_of_birth");
                member.setDateOfBirth(dob != null ? dob.toString() : "");

                member.setContactNumber(rs.getString("contact_number"));
                familyMembers.add(member);
            }

            return familyMembers;

        } catch (SQLException e) {
            System.err.println("DB ERROR in getFamilyDetails");
            e.printStackTrace();
            throw new RemoteException("Failed to get family details: " + e.getMessage(), e);
        }
    }

    /*
     * PSEUDOCODE - addFamilyMember():
     * 
     * PURPOSE: Insert a new family member record into database
     * 
     * FUNCTION addFamilyMember(member)
     * 1. VALIDATE input:
     * - member not null
     * - name not null
     * - relationship not null
     * IF validation fails THEN RETURN false
     * 
     * 2. PREPARE SQL INSERT statement
     * 3. CONNECT to database
     * 4. SET parameters:
     * - employeeId, name, relationship (required)
     * - icPassport (optional)
     * - dateOfBirth (optional, convert String to SQL Date)
     * - contactNumber (optional)
     * 5. EXECUTE insert
     * 6. IF rows affected > 0 THEN
     * - RETURN true (success)
     * ELSE
     * - RETURN false (failed)
     * 
     * CATCH SQLException:
     * - LOG error
     * - THROW RemoteException
     * END FUNCTION
     */
    @Override
    public boolean addFamilyMember(FamilyMember member) throws RemoteException {
        // Validation
        if (member == null || member.getName() == null || member.getRelationship() == null) {
            return false;
        }

        final String sql = """
                    INSERT INTO family_members (employee_id, name, relationship,
                                               ic_passport, date_of_birth, contact_number)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getEmployeeId());
            ps.setString(2, member.getName());
            ps.setString(3, member.getRelationship());
            ps.setString(4, member.getIcPassport());

            // Handle date - convert String to SQL Date
            if (member.getDateOfBirth() != null && !member.getDateOfBirth().isEmpty()) {
                ps.setDate(5, Date.valueOf(member.getDateOfBirth()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setString(6, member.getContactNumber());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("DB ERROR in addFamilyMember");
            e.printStackTrace();
            throw new RemoteException("Failed to add family member: " + e.getMessage(), e);
        }
    }

    /*
     * PSEUDOCODE - updateFamilyMember():
     * 
     * PURPOSE: Update an existing family member record
     * 
     * FUNCTION updateFamilyMember(member)
     * 1. VALIDATE input:
     * - member not null
     * - member.id > 0 (valid ID)
     * IF validation fails THEN RETURN false
     * 
     * 2. PREPARE SQL UPDATE statement
     * 3. CONNECT to database
     * 4. SET parameters:
     * - name, relationship, icPassport
     * - dateOfBirth (convert String to SQL Date)
     * - contactNumber
     * - WHERE id = member.id
     * 5. EXECUTE update
     * 6. IF rows affected > 0 THEN
     * - RETURN true (success)
     * ELSE
     * - RETURN false (no record found or not updated)
     * 
     * CATCH SQLException:
     * - LOG error
     * - THROW RemoteException
     * END FUNCTION
     */
    @Override
    public boolean updateFamilyMember(FamilyMember member) throws RemoteException {
        // Validation
        if (member == null || member.getId() == 0) {
            return false;
        }

        final String sql = """
                    UPDATE family_members
                    SET name = ?, relationship = ?, ic_passport = ?,
                        date_of_birth = ?, contact_number = ?
                    WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getRelationship());
            ps.setString(3, member.getIcPassport());

            // Handle date - convert String to SQL Date
            if (member.getDateOfBirth() != null && !member.getDateOfBirth().isEmpty()) {
                ps.setDate(4, Date.valueOf(member.getDateOfBirth()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setString(5, member.getContactNumber());
            ps.setInt(6, member.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("DB ERROR in updateFamilyMember");
            e.printStackTrace();
            throw new RemoteException("Failed to update family member: " + e.getMessage(), e);
        }
    }

    /*
     * PSEUDOCODE - deleteFamilyMember():
     * 
     * PURPOSE: Delete a family member record from database
     * 
     * FUNCTION deleteFamilyMember(memberId)
     * 1. PREPARE SQL DELETE statement
     * 2. CONNECT to database
     * 3. SET parameter: memberId
     * 4. EXECUTE delete
     * 5. IF rows affected > 0 THEN
     * - RETURN true (successfully deleted)
     * ELSE
     * - RETURN false (record not found)
     * 
     * CATCH SQLException:
     * - LOG error
     * - THROW RemoteException
     * END FUNCTION
     */
    @Override
    public boolean deleteFamilyMember(int memberId) throws RemoteException {
        final String sql = "DELETE FROM family_members WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("DB ERROR in deleteFamilyMember");
            e.printStackTrace();
            throw new RemoteException("Failed to delete family member: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // ACCOUNT MANAGEMENT METHODS
    // ==========================================

    /*
     * PSEUDOCODE - setAccountActive():
     * 
     * PURPOSE: Enable or disable a user account (HR/Admin only)
     * 
     * FUNCTION setAccountActive(username, active)
     * 1. VALIDATE username not null
     * 2. GET account from cache
     * 3. IF account not found THEN RETURN false
     * 4. UPDATE accounts table SET active = ? WHERE username = ?
     * 5. IF update successful THEN
     * - UPDATE cache with new active status
     * - RETURN true
     * ELSE
     * - RETURN false
     * 
     * CATCH SQLException:
     * THROW RemoteException
     * END FUNCTION
     */
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
                acc.active = active; // Keep cache in sync
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new RemoteException("Failed updating account active in DB: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // PASSWORD RESET METHODS
    // ==========================================

    /*
     * PSEUDOCODE - submitPasswordResetRequest():
     * 
     * PURPOSE: Submit a password reset request from employee
     * 
     * FUNCTION submitPasswordResetRequest(fullName, employeeId)
     * 1. VALIDATE inputs (not null, not empty)
     * 2. CHECK if employee exists in accounts table
     * 3. IF employee not found THEN
     * - RETURN "No matching employee found"
     * 4. INSERT request into reset_requests table with:
     * - request_time (current timestamp)
     * - full_name
     * - employee_id
     * - status = "PENDING"
     * 5. RETURN "Reset request submitted"
     * 
     * CATCH SQLException:
     * THROW RemoteException
     * END FUNCTION
     */
    @Override
    public String submitPasswordResetRequest(String fullName, String employeeId) throws RemoteException {
        if (fullName == null || employeeId == null || fullName.trim().isEmpty() || employeeId.trim().isEmpty()) {
            return "Full name and employee ID are required.";
        }

        String fullNameTrim = fullName.trim();
        String employeeIdTrim = employeeId.trim();

        // Verify employee exists
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

            // 1) Check if employee exists
            try (PreparedStatement ps = conn.prepareStatement(existsSql)) {
                ps.setString(1, employeeIdTrim);
                ps.setString(2, fullNameTrim);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return "No matching employee found. Please check your details.";
                    }
                }
            }

            // 2) Insert reset request
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

    /*
     * PSEUDOCODE - getResetRequests():
     * 
     * PURPOSE: Retrieve all password reset requests (HR/Admin view)
     * 
     * FUNCTION getResetRequests()
     * 1. PREPARE SQL query: SELECT all from reset_requests
     * 2. CREATE empty list
     * 3. CONNECT to database
     * 4. EXECUTE query
     * 5. FOR each row:
     * - CREATE ResetRequestDTO object
     * - ADD to list
     * 6. RETURN list (ordered by request_id DESC)
     * 
     * CATCH SQLException:
     * THROW RemoteException
     * END FUNCTION
     */
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

    /*
     * PSEUDOCODE - updateResetRequestStatus():
     * 
     * PURPOSE: Update status of a password reset request (HR/Admin action)
     * 
     * FUNCTION updateResetRequestStatus(requestId, newStatus)
     * 1. VALIDATE newStatus (must be PENDING, APPROVED, or REJECTED)
     * 2. UPDATE reset_requests table SET status = ? WHERE request_id = ?
     * 3. IF rows affected = 1 THEN
     * - RETURN true
     * ELSE
     * - RETURN false
     * 
     * CATCH SQLException:
     * THROW RemoteException
     * END FUNCTION
     */
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

    // ==========================================
    // STUB METHODS (Not implemented yet)
    // ==========================================

    /*
     * PSEUDOCODE - registerEmployee():
     * 
     * PURPOSE: Register a new employee and create linked login account
     */
    @Override
    public String registerEmployee(Employee employee, String username, String password) throws RemoteException {
        if (employee == null) {
            return "Employee data is required.";
        }

        if (username == null || username.trim().isEmpty()) {
            return "Username is required.";
        }

        if (password == null || password.trim().isEmpty()) {
            return "Password is required.";
        }

        if (employee.getEmployeeId() == null || employee.getEmployeeId().trim().isEmpty()) {
            return "Employee ID is required.";
        }

        if (employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
            return "First name is required.";
        }

        if (employee.getLastName() == null || employee.getLastName().trim().isEmpty()) {
            return "Last name is required.";
        }

        if (employee.getIcPassport() == null || employee.getIcPassport().trim().isEmpty()) {
            return "IC/Passport is required.";
        }

        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            return "Email is required.";
        }

        try {
            if (employeeRepository.employeeIdExists(employee.getEmployeeId().trim())) {
                return "Employee ID already exists.";
            }

            if (employeeRepository.icPassportExists(employee.getIcPassport().trim())) {
                return "IC/Passport already exists.";
            }

            if (employeeRepository.emailExists(employee.getEmail().trim())) {
                return "Email already exists.";
            }

            if (employeeRepository.usernameExists(username.trim())) {
                return "Username already exists.";
            }

            boolean success = employeeRepository.registerEmployee(
                    employee,
                    username.trim(),
                    password.trim());

            if (success) {
                loadAccountsFromDb();
                return "Employee registered successfully.";
            }

            return "Failed to register employee.";

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Database error while registering employee: " + e.getMessage(), e);
        }
    }

    /*
     * PSEUDOCODE - getEmployeeById():
     * 
     * PURPOSE: Get employee details by ID (HR function)
     * STATUS: Not implemented yet - HR team responsibility
     */
    @Override
    public Employee getEmployeeById(String employeeId) throws RemoteException {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return null;
        }

        final String sql = """
                SELECT id, employee_id, first_name, last_name, ic_passport,
                       email, phone, address, department, "position"
                FROM employees
                WHERE employee_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId.trim());

            try (ResultSet rs = ps.executeQuery()) {
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
            }

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to get employee by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public MonthlySalaryDTO getMonthlySalary(String employeeId, int year, int month) throws RemoteException {

        final String sql = """
                    SELECT employee_id, year, month, base_salary, allowance, deduction, tax, net_salary
                    FROM payroll_records
                    WHERE employee_id = ? AND year = ? AND month = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ps.setInt(2, year);
            ps.setInt(3, month);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    MonthlySalaryDTO dto = new MonthlySalaryDTO();

                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setYear(rs.getInt("year"));
                    dto.setMonth(rs.getInt("month"));
                    dto.setBaseSalary(rs.getBigDecimal("base_salary"));
                    dto.setAllowance(rs.getBigDecimal("allowance"));
                    dto.setDeduction(rs.getBigDecimal("deduction"));
                    dto.setTax(rs.getBigDecimal("tax"));
                    dto.setNetSalary(rs.getBigDecimal("net_salary"));

                    return dto;
                }
            }

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to get monthly salary: " + e.getMessage(), e);
        }
    }

    /*
     * PSEUDOCODE - generateMonthlyReport():
     * 
     * PURPOSE: Generate monthly report for all employees (HR function)
     */
    @Override
    public MonthlyReportDTO generateMonthlyReport(int year, int month) throws RemoteException {
        final String sql = """
                SELECT
                    e.employee_id,
                    e.first_name,
                    e.last_name,
                    p.year,
                    p.month,
                    p.base_salary,
                    p.allowance,
                    p.deduction,
                    p.tax,
                    p.net_salary,
                    COALESCE(SUM(la.num_days), 0) AS leave_taken
                FROM employees e
                JOIN payroll_records p
                    ON e.employee_id = p.employee_id
                LEFT JOIN leave_applications la
                    ON e.employee_id = la.employee_id
                    AND la.status = 'Approved'
                    AND EXTRACT(YEAR FROM la.start_date) = p.year
                    AND EXTRACT(MONTH FROM la.start_date) = p.month
                WHERE p.year = ? AND p.month = ?
                GROUP BY
                    e.employee_id, e.first_name, e.last_name,
                    p.year, p.month, p.base_salary, p.allowance,
                    p.deduction, p.tax, p.net_salary
                ORDER BY e.employee_id
                """;

        List<MonthlySalaryDTO> employeeReports = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MonthlySalaryDTO dto = new MonthlySalaryDTO();

                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setFirstName(rs.getString("first_name"));
                    dto.setLastName(rs.getString("last_name"));
                    dto.setYear(rs.getInt("year"));
                    dto.setMonth(rs.getInt("month"));
                    dto.setBaseSalary(rs.getBigDecimal("base_salary"));
                    dto.setAllowance(rs.getBigDecimal("allowance"));
                    dto.setDeduction(rs.getBigDecimal("deduction"));
                    dto.setTax(rs.getBigDecimal("tax"));
                    dto.setNetSalary(rs.getBigDecimal("net_salary"));
                    dto.setLeaveTaken(rs.getInt("leave_taken"));

                    employeeReports.add(dto);
                }
            }

            MonthlyReportDTO report = new MonthlyReportDTO();
            report.setYear(year);
            report.setMonth(month);
            report.setEmployeeReports(employeeReports);

            return report;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to generate monthly report: " + e.getMessage(), e);
        }
    }

    // LEAVE MANAGEMENT METHODS

    /*
     * PSEUDOCODE - getLeaveBalance():
     * 
     * PURPOSE: Get leave balance for an employee for a specific year
     * 
     * FUNCTION getLeaveBalance(employeeId, year)
     * 1. PREPARE SQL query with JOIN:
     * - SELECT from leave_balances
     * - JOIN with leave_types to get leave type name
     * - WHERE employee_id = ? AND year = ?
     * 2. CONNECT to database
     * 3. EXECUTE query
     * 4. CREATE empty list
     * 5. FOR each row:
     * a. CREATE LeaveBalance object
     * b. POPULATE all fields including leaveTypeName from join
     * c. ADD to list
     * 6. RETURN list (empty if no balances found)
     * 
     * CATCH SQLException:
     * - LOG error
     * - THROW RemoteException
     * END FUNCTION
     */
    @Override
    public List<LeaveBalance> getLeaveBalance(String employeeId, int year) throws RemoteException {
        final String sql = """
                    SELECT lb.id, lb.employee_id, lb.leave_type_id, lt.leave_type_name,
                           lb.year, lb.total_days, lb.used_days, lb.remaining_days
                    FROM leave_balances lb
                    JOIN leave_types lt ON lb.leave_type_id = lt.id
                    WHERE lb.employee_id = ? AND lb.year = ?
                    ORDER BY lt.leave_type_name
                """;

        List<LeaveBalance> balances = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LeaveBalance balance = new LeaveBalance();
                balance.setId(rs.getInt("id"));
                balance.setEmployeeId(rs.getString("employee_id"));
                balance.setLeaveTypeId(rs.getInt("leave_type_id"));
                balance.setLeaveTypeName(rs.getString("leave_type_name"));
                balance.setYear(rs.getInt("year"));
                balance.setTotalDays(rs.getInt("total_days"));
                balance.setUsedDays(rs.getInt("used_days"));
                balance.setRemainingDays(rs.getInt("remaining_days"));
                balances.add(balance);
            }

            return balances;

        } catch (SQLException e) {
            System.err.println("DB ERROR in getLeaveBalance");
            e.printStackTrace();
            throw new RemoteException("Failed to get leave balance: " + e.getMessage(), e);
        }
    }

    /*
     * PSEUDOCODE - getAvailableLeaveTypes():
     * 
     * PURPOSE: Get all active leave types for employee to choose from
     * 
     * FUNCTION getAvailableLeaveTypes()
     * 1. PREPARE SQL query: SELECT from leave_types WHERE is_active = true
     * 2. CONNECT to database
     * 3. EXECUTE query
     * 4. CREATE empty list
     * 5. FOR each row:
     * a. CREATE LeaveType object
     * b. POPULATE all fields
     * c. ADD to list
     * 6. RETURN list
     * 
     * CATCH SQLException:
     * - LOG error
     * - THROW RemoteException
     * END FUNCTION
     */
    @Override
    public List<LeaveType> getAvailableLeaveTypes() throws RemoteException {
        final String sql = """
                    SELECT id, leave_type_name, default_days, description, is_active
                    FROM leave_types
                    WHERE is_active = true
                    ORDER BY leave_type_name
                """;

        List<LeaveType> leaveTypes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LeaveType type = new LeaveType();
                type.setId(rs.getInt("id"));
                type.setLeaveTypeName(rs.getString("leave_type_name"));
                type.setDefaultDays(rs.getInt("default_days"));
                type.setDescription(rs.getString("description"));
                type.setActive(rs.getBoolean("is_active"));
                leaveTypes.add(type);
            }

            return leaveTypes;

        } catch (SQLException e) {
            System.err.println("DB ERROR in getAvailableLeaveTypes");
            e.printStackTrace();
            throw new RemoteException("Failed to get leave types: " + e.getMessage(), e);
        }
    }

    /*
     * PSEUDOCODE - submitLeaveApplication():
     * 
     * PURPOSE: Submit a new leave application
     * 
     * FUNCTION submitLeaveApplication(application)
     * 1. VALIDATE input:
     * - application not null
     * - employeeId not null
     * - leaveTypeId valid
     * - dates not null
     * - numDays > 0
     * IF validation fails THEN RETURN false
     * 
     * 2. PREPARE SQL INSERT statement
     * 3. CONNECT to database
     * 4. SET parameters:
     * - employee_id, leave_type_id
     * - start_date, end_date, num_days
     * - reason
     * - status = 'Pending'
     * - applied_date = current timestamp
     * 5. EXECUTE insert
     * 6. IF rows affected > 0 THEN
     * - RETURN true (success)
     * ELSE
     * - RETURN false (failed)
     * 
     * CATCH SQLException:
     * - LOG error
     * - THROW RemoteException
     * END FUNCTION
     */
    @Override
    public boolean submitLeaveApplication(LeaveApplication application) throws RemoteException {
        // Validation
        if (application == null || application.getEmployeeId() == null ||
                application.getLeaveTypeId() == 0 || application.getNumDays() <= 0) {
            return false;
        }

        final String sql = """
                    INSERT INTO leave_applications (employee_id, leave_type_id, start_date,
                                                   end_date, num_days, reason, status, applied_date)
                    VALUES (?, ?, ?, ?, ?, ?, 'Pending', CURRENT_TIMESTAMP)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, application.getEmployeeId());
            ps.setInt(2, application.getLeaveTypeId());
            ps.setDate(3, Date.valueOf(application.getStartDate()));
            ps.setDate(4, Date.valueOf(application.getEndDate()));
            ps.setInt(5, application.getNumDays());
            ps.setString(6, application.getReason());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("DB ERROR in submitLeaveApplication");
            e.printStackTrace();
            throw new RemoteException("Failed to submit leave application: " + e.getMessage(), e);
        }
    }

    /*
     * PSEUDOCODE - getLeaveApplications():
     * 
     * PURPOSE: Get all leave applications for an employee
     * 
     * FUNCTION getLeaveApplications(employeeId)
     * 1. PREPARE SQL query with JOIN:
     * - SELECT from leave_applications
     * - JOIN with leave_types to get leave type name
     * - WHERE employee_id = ?
     * - ORDER BY applied_date DESC (newest first)
     * 2. CONNECT to database
     * 3. EXECUTE query
     * 4. CREATE empty list
     * 5. FOR each row:
     * a. CREATE LeaveApplication object
     * b. POPULATE all fields including leaveTypeName
     * c. HANDLE nullable fields (reviewedBy, reviewedDate, remarks)
     * d. ADD to list
     * 6. RETURN list
     * 
     * CATCH SQLException:
     * - LOG error
     * - THROW RemoteException
     * END FUNCTION
     */
    @Override
    public List<LeaveApplication> getLeaveApplications(String employeeId) throws RemoteException {
        final String sql = """
                    SELECT la.id, la.employee_id, la.leave_type_id, lt.leave_type_name,
                           la.start_date, la.end_date, la.num_days, la.reason, la.status,
                           la.applied_date, la.reviewed_by, la.reviewed_date, la.remarks
                    FROM leave_applications la
                    JOIN leave_types lt ON la.leave_type_id = lt.id
                    WHERE la.employee_id = ?
                    ORDER BY la.applied_date DESC
                """;

        List<LeaveApplication> applications = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LeaveApplication app = new LeaveApplication();
                app.setId(rs.getInt("id"));
                app.setEmployeeId(rs.getString("employee_id"));
                app.setLeaveTypeId(rs.getInt("leave_type_id"));
                app.setLeaveTypeName(rs.getString("leave_type_name"));

                // Convert SQL Date to String
                Date startDate = rs.getDate("start_date");
                app.setStartDate(startDate != null ? startDate.toString() : "");

                Date endDate = rs.getDate("end_date");
                app.setEndDate(endDate != null ? endDate.toString() : "");

                app.setNumDays(rs.getInt("num_days"));
                app.setReason(rs.getString("reason"));
                app.setStatus(rs.getString("status"));

                // Convert Timestamp to String
                Timestamp appliedDate = rs.getTimestamp("applied_date");
                app.setAppliedDate(appliedDate != null ? appliedDate.toString() : "");

                app.setReviewedBy(rs.getString("reviewed_by"));

                Timestamp reviewedDate = rs.getTimestamp("reviewed_date");
                app.setReviewedDate(reviewedDate != null ? reviewedDate.toString() : "");

                app.setRemarks(rs.getString("remarks"));
                applications.add(app);
            }

            return applications;

        } catch (SQLException e) {
            System.err.println("DB ERROR in getLeaveApplications");
            e.printStackTrace();
            throw new RemoteException("Failed to get leave applications: " + e.getMessage(), e);
        }
    }

    /*
     * PSEUDOCODE - cancelLeaveApplication():
     * 
     * PURPOSE: Cancel a pending leave application
     * 
     * FUNCTION cancelLeaveApplication(applicationId)
     * 1. PREPARE SQL DELETE statement
     * - DELETE from leave_applications
     * - WHERE id = ? AND status = 'Pending'
     * (Only pending applications can be cancelled)
     * 2. CONNECT to database
     * 3. SET parameter: applicationId
     * 4. EXECUTE delete
     * 5. IF rows affected > 0 THEN
     * - RETURN true (successfully cancelled)
     * ELSE
     * - RETURN false (not found or not pending)
     * 
     * CATCH SQLException:
     * - LOG error
     * - THROW RemoteException
     * END FUNCTION
     */
    @Override
    public boolean cancelLeaveApplication(int applicationId) throws RemoteException {
        // Only allow cancelling pending applications
        final String sql = "DELETE FROM leave_applications WHERE id = ? AND status = 'Pending'";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, applicationId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("DB ERROR in cancelLeaveApplication");
            e.printStackTrace();
            throw new RemoteException("Failed to cancel leave application: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Employee> getAllEmployees() throws RemoteException {
        try {
            return employeeRepository.getAllEmployees();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to load employees: " + e.getMessage(), e);
        }
    }

    @Override
    public YearlyReportDTO generateYearlyReport(int year) throws RemoteException {
        final String sql = """
                SELECT
                    e.employee_id,
                    e.first_name,
                    e.last_name,
                    ? AS year,
                    COALESCE(p.total_net_salary, 0) AS total_net_salary,
                    COALESCE(l.total_leave_taken, 0) AS total_leave_taken
                FROM employees e
                LEFT JOIN (
                    SELECT
                        employee_id,
                        SUM(net_salary) AS total_net_salary
                    FROM payroll_records
                    WHERE year = ?
                    GROUP BY employee_id
                ) p ON e.employee_id = p.employee_id
                LEFT JOIN (
                    SELECT
                        employee_id,
                        SUM(num_days) AS total_leave_taken
                    FROM leave_applications
                    WHERE status = 'Approved'
                      AND EXTRACT(YEAR FROM start_date) = ?
                    GROUP BY employee_id
                ) l ON e.employee_id = l.employee_id
                WHERE p.employee_id IS NOT NULL OR l.employee_id IS NOT NULL
                ORDER BY e.employee_id
                """;

        List<YearlyEmployeeReportDTO> employeeReports = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, year);
            ps.setInt(3, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    YearlyEmployeeReportDTO dto = new YearlyEmployeeReportDTO();
                    dto.setEmployeeId(rs.getString("employee_id"));
                    dto.setFirstName(rs.getString("first_name"));
                    dto.setLastName(rs.getString("last_name"));
                    dto.setYear(rs.getInt("year"));
                    dto.setTotalNetSalary(rs.getBigDecimal("total_net_salary"));
                    dto.setTotalLeaveTaken(rs.getInt("total_leave_taken"));

                    employeeReports.add(dto);
                }
            }

            YearlyReportDTO report = new YearlyReportDTO();
            report.setYear(year);
            report.setEmployeeReports(employeeReports);
            return report;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to generate yearly report: " + e.getMessage(), e);
        }
    }

    @Override
    public List<LeaveApplication> getAllPendingLeaveApplications() throws RemoteException {
        final String sql = """
                    SELECT la.id, la.employee_id, la.leave_type_id, lt.leave_type_name,
                           la.start_date, la.end_date, la.num_days, la.reason, la.status,
                           la.applied_date, la.reviewed_by, la.reviewed_date, la.remarks
                    FROM leave_applications la
                    JOIN leave_types lt ON la.leave_type_id = lt.id
                    WHERE UPPER(la.status) = 'PENDING'
                    ORDER BY la.applied_date DESC
                """;

        List<LeaveApplication> applications = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LeaveApplication app = new LeaveApplication();
                app.setId(rs.getInt("id"));
                app.setEmployeeId(rs.getString("employee_id"));
                app.setLeaveTypeId(rs.getInt("leave_type_id"));
                app.setLeaveTypeName(rs.getString("leave_type_name"));

                Date startDate = rs.getDate("start_date");
                app.setStartDate(startDate != null ? startDate.toString() : "");

                Date endDate = rs.getDate("end_date");
                app.setEndDate(endDate != null ? endDate.toString() : "");

                app.setNumDays(rs.getInt("num_days"));
                app.setReason(rs.getString("reason"));
                app.setStatus(rs.getString("status"));

                Timestamp appliedDate = rs.getTimestamp("applied_date");
                app.setAppliedDate(appliedDate != null ? appliedDate.toString() : "");

                app.setReviewedBy(rs.getString("reviewed_by"));

                Timestamp reviewedDate = rs.getTimestamp("reviewed_date");
                app.setReviewedDate(reviewedDate != null ? reviewedDate.toString() : "");

                app.setRemarks(rs.getString("remarks"));

                applications.add(app);
            }

            return applications;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to get pending leave applications: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateLeaveApplicationStatus(int applicationId, String newStatus) throws RemoteException {
        if (newStatus == null) {
            return false;
        }

        String status = newStatus.trim();

        if (!(status.equalsIgnoreCase("APPROVED") || status.equalsIgnoreCase("REJECTED"))) {
            return false;
        }

        final String sql = """
                    UPDATE leave_applications
                    SET status = ?, reviewed_date = CURRENT_TIMESTAMP
                    WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            if (status.equalsIgnoreCase("APPROVED")) {
                ps.setString(1, "Approved");
            } else {
                ps.setString(1, "Rejected");
            }

            ps.setInt(2, applicationId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Failed to update leave application status: " + e.getMessage(), e);
        }
    }

}
