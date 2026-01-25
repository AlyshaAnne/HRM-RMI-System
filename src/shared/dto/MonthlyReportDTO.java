package shared.dto;

import java.io.Serializable;

public class MonthlyReportDTO implements Serializable {
    private int year;
    private int month;
    private int totalEmployees;
    private double totalPayrollCost;

    public MonthlyReportDTO(int year, int month, int totalEmployees, double totalPayrollCost) {
        this.year = year;
        this.month = month;
        this.totalEmployees = totalEmployees;
        this.totalPayrollCost = totalPayrollCost;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public double getTotalPayrollCost() {
        return totalPayrollCost;
    }
}
