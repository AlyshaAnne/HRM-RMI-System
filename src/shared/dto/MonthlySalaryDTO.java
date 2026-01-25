package shared.dto;

import java.io.Serializable;

public class MonthlySalaryDTO implements Serializable {
    private String employeeId;
    private int year;
    private int month;
    private double basicSalary;
    private double allowances;
    private double deductions;
    private double netSalary;

    public MonthlySalaryDTO(String employeeId, int year, int month,
            double basicSalary, double allowances,
            double deductions, double netSalary) {
        this.employeeId = employeeId;
        this.year = year;
        this.month = month;
        this.basicSalary = basicSalary;
        this.allowances = allowances;
        this.deductions = deductions;
        this.netSalary = netSalary;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public double getAllowances() {
        return allowances;
    }

    public double getDeductions() {
        return deductions;
    }

    public double getNetSalary() {
        return netSalary;
    }
}
