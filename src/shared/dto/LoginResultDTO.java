package shared.dto;

import java.io.Serializable;

public class LoginResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String role; // "HR" or "EMPLOYEE"
    private String employeeId;       // Employee's database ID
    private String employeeName;  // First + Last name
    private final String message;

    /*Constructor 1: For FAILED login or simple responses
       (No employee data available)*/

    public LoginResultDTO(boolean success, String role, String message) {
        this.success = success;
        this.role = role;
        this.message = message;
        this.employeeId = null;     // default invalid ID
        this.employeeName = null;
    }

    /*  Constructor 2: For SUCCESSFUL login
       (Full employee data available)*/

    public LoginResultDTO(boolean success, String role, String message, String employeeId, String employeeName) {
        this.success = success;
        this.role = role;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }
      public String getEmployeeId() {
        return employeeId;
    }
    public String getEmployeeName() {
        return employeeName;
    }
}
