package shared.dto;

import java.io.Serializable;
import java.util.List;

public class YearlyReportDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int year;
    private List<YearlyEmployeeReportDTO> employeeReports;

    public YearlyReportDTO() {
    }

    public YearlyReportDTO(int year, List<YearlyEmployeeReportDTO> employeeReports) {
        this.year = year;
        this.employeeReports = employeeReports;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<YearlyEmployeeReportDTO> getEmployeeReports() {
        return employeeReports;
    }

    public void setEmployeeReports(List<YearlyEmployeeReportDTO> employeeReports) {
        this.employeeReports = employeeReports;
    }
}