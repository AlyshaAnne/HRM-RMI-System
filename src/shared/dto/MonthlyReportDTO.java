package shared.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MonthlyReportDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int year;
    private int month;
    private List<MonthlySalaryDTO> employeeReports = new ArrayList<>();
    private BigDecimal totalBaseSalary = BigDecimal.ZERO;
    private BigDecimal totalAllowance = BigDecimal.ZERO;
    private BigDecimal totalDeduction = BigDecimal.ZERO;
    private BigDecimal totalTax = BigDecimal.ZERO;
    private BigDecimal totalNetSalary = BigDecimal.ZERO;

    public MonthlyReportDTO() {
    }

    public MonthlyReportDTO(int year, int month, List<MonthlySalaryDTO> employeeReports) {
        this.year = year;
        this.month = month;
        this.employeeReports = employeeReports;
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

    public List<MonthlySalaryDTO> getEmployeeReports() {
        return employeeReports;
    }

    public void setEmployeeReports(List<MonthlySalaryDTO> employeeReports) {
        this.employeeReports = employeeReports;
    }

    public BigDecimal getTotalBaseSalary() {
        return totalBaseSalary;
    }

    public void setTotalBaseSalary(BigDecimal totalBaseSalary) {
        this.totalBaseSalary = totalBaseSalary;
    }

    public BigDecimal getTotalAllowance() {
        return totalAllowance;
    }

    public void setTotalAllowance(BigDecimal totalAllowance) {
        this.totalAllowance = totalAllowance;
    }

    public BigDecimal getTotalDeduction() {
        return totalDeduction;
    }

    public void setTotalDeduction(BigDecimal totalDeduction) {
        this.totalDeduction = totalDeduction;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getTotalNetSalary() {
        return totalNetSalary;
    }

    public void setTotalNetSalary(BigDecimal totalNetSalary) {
        this.totalNetSalary = totalNetSalary;
    }
}