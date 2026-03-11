package shared.models;

import java.io.Serializable;

/*
 * PSEUDOCODE for LeaveApplication Model:
 * 
 * PURPOSE: Represent a leave application from an employee
 * 
 * FIELDS:
 * - id: Primary key
 * - employeeId: Who applied
 * - leaveTypeId: What type of leave
 * - leaveTypeName: Name for display (populated from join)
 * - startDate: Leave start date
 * - endDate: Leave end date
 * - numDays: Duration in days
 * - reason: Why employee needs leave
 * - status: Pending/Approved/Rejected
 * - appliedDate: When submitted
 * - reviewedBy: HR who processed (nullable)
 * - reviewedDate: When reviewed (nullable)
 * - remarks: HR's comments (nullable)
 */
public class LeaveApplication implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String employeeId;
    private int leaveTypeId;
    private String leaveTypeName;  
    private String startDate;      
    private String endDate;      
    private int numDays;
    private String reason;
    private String status;         // Pending, Approved, Rejected
    private String appliedDate;    
    private String reviewedBy;
    private String reviewedDate;   
    private String remarks;

    // Constructors
    public LeaveApplication() {
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getNumDays() {
        return numDays;
    }

    public void setNumDays(int numDays) {
        this.numDays = numDays;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(String appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getReviewedDate() {
        return reviewedDate;
    }

    public void setReviewedDate(String reviewedDate) {
        this.reviewedDate = reviewedDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "LeaveApplication{" +
                "id=" + id +
                ", leaveTypeName='" + leaveTypeName + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", numDays=" + numDays +
                ", status='" + status + '\'' +
                '}';
    }
}