package shared.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class MonthlySalaryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String employeeId;
    private String firstName;
    private String lastName;
    private int year;
    private int month;
    private BigDecimal baseSalary;
    private BigDecimal allowance;
    private BigDecimal deduction;
    private BigDecimal tax;
    private BigDecimal netSalary;
    private int leaveTaken;

    public MonthlySalaryDTO() {
    }

    public MonthlySalaryDTO(String employeeId, String firstName, String lastName,
            int year, int month,
            BigDecimal baseSalary, BigDecimal allowance,
            BigDecimal deduction, BigDecimal tax,
            BigDecimal netSalary, int leaveTaken) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.year = year;
        this.month = month;
        this.baseSalary = baseSalary;
        this.allowance = allowance;
        this.deduction = deduction;
        this.tax = tax;
        this.netSalary = netSalary;
        this.leaveTaken = leaveTaken;
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

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getAllowance() {
        return allowance;
    }

    public void setAllowance(BigDecimal allowance) {
        this.allowance = allowance;
    }

    public BigDecimal getDeduction() {
        return deduction;
    }

    public void setDeduction(BigDecimal deduction) {
        this.deduction = deduction;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    public int getLeaveTaken() {
        return leaveTaken;
    }

    public void setLeaveTaken(int leaveTaken) {
        this.leaveTaken = leaveTaken;
    }
}