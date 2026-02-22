package shared.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class ResetRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int requestId;
    private Timestamp requestTime;
    private String fullName;
    private String employeeId;
    private String status;

    public ResetRequestDTO(int requestId, Timestamp requestTime, String fullName, String employeeId, String status) {
        this.requestId = requestId;
        this.requestTime = requestTime;
        this.fullName = fullName;
        this.employeeId = employeeId;
        this.status = status;
    }

    public int getRequestId() {
        return requestId;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}