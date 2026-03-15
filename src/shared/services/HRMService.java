package shared.services;

import java.util.List;
import shared.dto.ResetRequestDTO;
import java.rmi.Remote;
import java.rmi.RemoteException;

import shared.models.Employee;
import shared.models.FamilyMember;
import shared.models.LeaveApplication;
import shared.models.LeaveBalance;
import shared.models.LeaveType;
import shared.dto.LoginResultDTO;
import shared.dto.MonthlySalaryDTO;
import shared.dto.MonthlyReportDTO;
import shared.dto.YearlyReportDTO;

/*
 * PSEUDOCODE for HRMService Interface:
 * 
 * PURPOSE: Define all remote methods for HRM system
 * 
 * SECTIONS:
 * 1. Authentication & Account Management
 * 2. Employee Profile Management
 * 3. Family Details Management (YOUR METHODS)
 * 4. Password Reset Management
 * 5. Salary & Reports (Admin/HR)
 */
public interface HRMService extends Remote {

    // ==========================================
    // AUTHENTICATION & ACCOUNT MANAGEMENT
    // ==========================================

    /*
     * PSEUDOCODE - login():
     * Authenticate user credentials and return login result
     */
    LoginResultDTO login(String username, String password) throws RemoteException;

    /*
     * PSEUDOCODE - setAccountActive():
     * Enable or disable an account (HR/Admin only)
     */
    boolean setAccountActive(String username, boolean active) throws RemoteException;

    // EMPLOYEE PROFILE MANAGEMENT
    /*
     * PSEUDOCODE - registerEmployee():
     * Create new employee record (HR only)
     */
    String registerEmployee(Employee employee, String username, String password) throws RemoteException;

    /*
     * PSEUDOCODE - getAllEmployees():
     * Get all employees (HR use)
     */
    List<Employee> getAllEmployees() throws RemoteException;

    /*
     * PSEUDOCODE - getEmployeeById():
     * Get employee by ID (HR use)
     */
    Employee getEmployeeById(String employeeId) throws RemoteException;

    /*
     * PSEUDOCODE - getEmployeeProfile():
     * Get employee profile for viewing/editing (Employee use)
     */
    Employee getEmployeeProfile(String employeeId) throws RemoteException;

    /*
     * PSEUDOCODE - updateEmployeeProfile():
     * Update employee profile information (Employee use)
     */
    boolean updateEmployeeProfile(Employee employee) throws RemoteException;

    // FAMILY DETAILS MANAGEMENT
    /*
     * PSEUDOCODE - getFamilyDetails():
     * 
     * PURPOSE: Retrieve all family members for an employee
     * 
     * FUNCTION getFamilyDetails(employeeId)
     * QUERY family_members table WHERE employee_id = employeeId
     * RETURN list of FamilyMember objects
     * END FUNCTION
     */
    List<FamilyMember> getFamilyDetails(String employeeId) throws RemoteException;

    /*
     * PSEUDOCODE - addFamilyMember():
     * 
     * PURPOSE: Add a new family member record
     * 
     * FUNCTION addFamilyMember(member)
     * VALIDATE required fields (name, relationship)
     * INSERT into family_members table
     * RETURN true if successful, false otherwise
     * END FUNCTION
     */
    boolean addFamilyMember(FamilyMember member) throws RemoteException;

    /*
     * PSEUDOCODE - updateFamilyMember():
     * 
     * PURPOSE: Update existing family member record
     * 
     * FUNCTION updateFamilyMember(member)
     * VALIDATE member has valid ID
     * UPDATE family_members table WHERE id = member.id
     * RETURN true if successful, false otherwise
     * END FUNCTION
     */
    boolean updateFamilyMember(FamilyMember member) throws RemoteException;

    /*
     * PSEUDOCODE - deleteFamilyMember():
     * 
     * PURPOSE: Delete a family member record
     * 
     * FUNCTION deleteFamilyMember(memberId)
     * DELETE FROM family_members WHERE id = memberId
     * RETURN true if successful, false otherwise
     * END FUNCTION
     */
    boolean deleteFamilyMember(int memberId) throws RemoteException;

    // PASSWORD RESET MANAGEMENT

    /*
     * PSEUDOCODE - submitPasswordResetRequest():
     * Employee submits password reset request
     */
    String submitPasswordResetRequest(String fullName, String employeeId) throws RemoteException;

    /*
     * PSEUDOCODE - getResetRequests():
     * HR views all password reset requests
     */
    List<ResetRequestDTO> getResetRequests() throws RemoteException;

    /*
     * PSEUDOCODE - updateResetRequestStatus():
     * HR approves/rejects password reset requests
     */
    boolean updateResetRequestStatus(int requestId, String newStatus) throws RemoteException;

    // ==========================================
    // SALARY & REPORTS (Admin/HR)
    // ==========================================

    /*
     * PSEUDOCODE - getMonthlySalary():
     * Get employee salary for specific month
     */
    MonthlySalaryDTO getMonthlySalary(String employeeId, int year, int month) throws RemoteException;

    /*
     * PSEUDOCODE - generateMonthlyReport():
     * Generate monthly report for all employees
     */
    MonthlyReportDTO generateMonthlyReport(int year, int month) throws RemoteException;

    /*
     * PSEUDOCODE - generateYearlyReport():
     * Generate yearly report for all employees
     */
    YearlyReportDTO generateYearlyReport(int year) throws RemoteException;

    // LEAVE MANAGEMENT METHODS

    /*
     * PSEUDOCODE - getLeaveBalance():
     * Get leave balance for an employee for current year
     */
    List<LeaveBalance> getLeaveBalance(String employeeId, int year) throws RemoteException;

    /*
     * PSEUDOCODE - getAvailableLeaveTypes():
     * Get all active leave types
     */
    List<LeaveType> getAvailableLeaveTypes() throws RemoteException;

    /*
     * PSEUDOCODE - submitLeaveApplication():
     * Submit a new leave application
     */
    boolean submitLeaveApplication(LeaveApplication application) throws RemoteException;

    /*
     * PSEUDOCODE - getLeaveApplications():
     * Get all leave applications for an employee
     */
    List<LeaveApplication> getLeaveApplications(String employeeId) throws RemoteException;

    /*
     * PSEUDOCODE - cancelLeaveApplication():
     * Cancel a pending leave application
     */
    boolean cancelLeaveApplication(int applicationId) throws RemoteException;
}