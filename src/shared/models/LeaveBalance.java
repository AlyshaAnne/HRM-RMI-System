package shared.models;

import java.io.Serializable;

/*
 * PSEUDOCODE for LeaveBalance Model:
 * 
 * PURPOSE: Represent leave balance for one employee, one leave type, one year
 * 
 * FIELDS:
 * - id: Primary key
 * - employeeId: Which employee
 * - leaveTypeId: Which leave type
 * - leaveTypeName: Name for display (not in DB, populated from join)
 * - year: Calendar year
 * - totalDays: Annual entitlement
 * - usedDays: Days already taken
 * - remainingDays: Balance left
 */
public class LeaveBalance implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String employeeId;
    private int leaveTypeId;
    private String leaveTypeName;  // For display (populated from join)
    private int year;
    private int totalDays;
    private int usedDays;
    private int remainingDays;

    // Constructors
    public LeaveBalance() {
    }

    public LeaveBalance(int id, String employeeId, int leaveTypeId, String leaveTypeName, 
                       int year, int totalDays, int usedDays, int remainingDays) {
        this.id = id;
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.leaveTypeName = leaveTypeName;
        this.year = year;
        this.totalDays = totalDays;
        this.usedDays = usedDays;
        this.remainingDays = remainingDays;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public int getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(int leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public int getUsedDays() {
        return usedDays;
    }

    public void setUsedDays(int usedDays) {
        this.usedDays = usedDays;
    }

    public int getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(int remainingDays) {
        this.remainingDays = remainingDays;
    }

    @Override
    public String toString() {
        return "LeaveBalance{" +
                "leaveTypeName='" + leaveTypeName + '\'' +
                ", year=" + year +
                ", totalDays=" + totalDays +
                ", usedDays=" + usedDays +
                ", remainingDays=" + remainingDays +
                '}';
    }
}