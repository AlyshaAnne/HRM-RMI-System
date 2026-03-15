package shared.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class YearlyEmployeeReportDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String employeeId;
    private String firstName;
    private String lastName;
    private int year;
    private BigDecimal totalNetSalary;
    private int totalLeaveTaken;

    public YearlyEmployeeReportDTO() {
    }

    public YearlyEmployeeReportDTO(String employeeId, String firstName, String lastName,
            int year, BigDecimal totalNetSalary, int totalLeaveTaken) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.year = year;
        this.totalNetSalary = totalNetSalary;
        this.totalLeaveTaken = totalLeaveTaken;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getTotalNetSalary() {
        return totalNetSalary;
    }

    public void setTotalNetSalary(BigDecimal totalNetSalary) {
        this.totalNetSalary = totalNetSalary;
    }

    public int getTotalLeaveTaken() {
        return totalLeaveTaken;
    }

    public void setTotalLeaveTaken(int totalLeaveTaken) {
        this.totalLeaveTaken = totalLeaveTaken;
    }
}