package server.impl;

import shared.services.HRMService;
import shared.dto.LoginResultDTO;
import shared.dto.MonthlyReportDTO;
import shared.dto.MonthlySalaryDTO;
import shared.dto.YearlyReportDTO;
import shared.models.Employee;

import java.io.*;
import java.nio.file.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.*;

public class HRMServiceImpl extends UnicastRemoteObject implements HRMService {

    private static final String ACCOUNTS_PATH = "src/server/data/accounts.csv";
    private static final String RESET_REQ_PATH = "src/server/data/reset_requests.csv";

    private static class Account {
        String username;
        String password;
        String role; // "HR" or "EMPLOYEE"
        boolean active; // true/false
        String fullName;
        String employeeId;

        Account(String username, String password, String role, boolean active, String fullName, String employeeId) {
            this.username = username;
            this.password = password;
            this.role = role;
            this.active = active;
            this.fullName = fullName;
            this.employeeId = employeeId;
        }
    }

    private final Map<String, Account> accounts = new HashMap<>();

    public HRMServiceImpl() throws RemoteException {
        super();
        loadAccountsFromCsv();
        ensureResetRequestFileExists();
    }

    private void loadAccountsFromCsv() throws RemoteException {
        accounts.clear();

        Path path = Paths.get(ACCOUNTS_PATH);
        if (!Files.exists(path)) {
            throw new RemoteException("accounts.csv not found at: " + path.toAbsolutePath());
        }

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String header = br.readLine(); // skip header
            if (header == null)
                return;

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                // username,password,role,active,fullName,employeeId
                String[] parts = line.split(",", -1);
                if (parts.length < 6)
                    continue;

                String username = parts[0].trim();
                String password = parts[1].trim();
                String role = parts[2].trim();
                boolean active = Boolean.parseBoolean(parts[3].trim());
                String fullName = parts[4].trim();
                String employeeId = parts[5].trim();

                accounts.put(username, new Account(username, password, role, active, fullName, employeeId));
            }
        } catch (IOException e) {
            throw new RemoteException("Failed reading accounts.csv: " + e.getMessage(), e);
        }
    }

    private void saveAccountsToCsv() throws RemoteException {
        Path path = Paths.get(ACCOUNTS_PATH);

        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            bw.write("username,password,role,active,fullName,employeeId");
            bw.newLine();

            for (Account a : accounts.values()) {
                bw.write(String.join(",",
                        a.username,
                        a.password,
                        a.role,
                        String.valueOf(a.active),
                        a.fullName,
                        a.employeeId));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RemoteException("Failed writing accounts.csv: " + e.getMessage(), e);
        }
    }

    private void ensureResetRequestFileExists() throws RemoteException {
        Path path = Paths.get(RESET_REQ_PATH);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "timestamp,fullName,employeeId,status\n", StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            throw new RemoteException("Failed creating reset_requests.csv: " + e.getMessage(), e);
        }
    }

    @Override
    public LoginResultDTO login(String username, String password) throws RemoteException {
        if (username == null || password == null) {
            return new LoginResultDTO(false, null, "Username/password cannot be null");
        }

        Account acc = accounts.get(username.trim());
        if (acc == null) {
            return new LoginResultDTO(false, null, "User not found");
        }

        if (!acc.active) {
            return new LoginResultDTO(false, null, "Account is deactivated. Contact HR/Admin.");
        }

        if (!acc.password.equals(password)) {
            return new LoginResultDTO(false, null, "Invalid password");
        }

        return new LoginResultDTO(true, acc.role, "Login successful");
    }

    // Admin/HR: activate/deactivate
    @Override
    public boolean setAccountActive(String username, boolean active) throws RemoteException {
        if (username == null)
            return false;

        Account acc = accounts.get(username.trim());
        if (acc == null)
            return false;

        acc.active = active;
        saveAccountsToCsv();
        return true;
    }

    // User: submits a request, admin handles later
    @Override
    public String submitPasswordResetRequest(String fullName, String employeeId) throws RemoteException {
        if (fullName == null || employeeId == null || fullName.trim().isEmpty() || employeeId.trim().isEmpty()) {
            return "Full name and employee ID are required.";
        }

        // Optional: verify employeeId exists in accounts
        boolean exists = accounts.values().stream()
                .anyMatch(a -> a.employeeId.equalsIgnoreCase(employeeId.trim()) &&
                        a.fullName.equalsIgnoreCase(fullName.trim()));

        if (!exists) {
            return "No matching employee found. Please check your details.";
        }

        String row = String.join(",",
                LocalDateTime.now().toString(),
                fullName.trim().replace(",", " "),
                employeeId.trim().replace(",", " "),
                "PENDING");

        try {
            Files.writeString(Paths.get(RESET_REQ_PATH), row + "\n", StandardOpenOption.APPEND);
            return "Reset request submitted. HR/Admin will process it.";
        } catch (IOException e) {
            throw new RemoteException("Failed saving reset request: " + e.getMessage(), e);
        }
    }

    // ----- stubs for now (so it compiles) -----

    @Override
    public void registerEmployee(Employee employee) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Employee getEmployeeById(String employeeId) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MonthlySalaryDTO getMonthlySalary(String employeeId, int year, int month) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public MonthlyReportDTO generateMonthlyReport(int year, int month) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public YearlyReportDTO generateYearlyReport(int year) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
