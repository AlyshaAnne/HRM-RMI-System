package shared.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

import shared.models.Employee;
import shared.dto.LoginResultDTO;
import shared.dto.MonthlySalaryDTO;
import shared.dto.MonthlyReportDTO;
import shared.dto.YearlyReportDTO;

public interface HRMService extends Remote {

    void registerEmployee(Employee employee) throws RemoteException;

    Employee getEmployeeById(String employeeId) throws RemoteException;

    LoginResultDTO login(String username, String password) throws RemoteException;

    MonthlySalaryDTO getMonthlySalary(String employeeId, int year, int month) throws RemoteException;

    MonthlyReportDTO generateMonthlyReport(int year, int month) throws RemoteException;

    YearlyReportDTO generateYearlyReport(int year) throws RemoteException;

    // Admin / HR actions
    boolean setAccountActive(String username, boolean active) throws RemoteException;

    String submitPasswordResetRequest(String fullName, String employeeId) throws RemoteException;
}
