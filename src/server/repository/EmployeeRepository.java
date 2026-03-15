package server.repository;

import server.DatabaseConnection;
import shared.models.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    public boolean employeeIdExists(String employeeId) throws SQLException {
        String sql = "SELECT 1 FROM employees WHERE employee_id = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean icPassportExists(String icPassport) throws SQLException {
        String sql = "SELECT 1 FROM employees WHERE ic_passport = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, icPassport);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM employees WHERE email = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM accounts WHERE username = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean registerEmployee(Employee employee, String username, String password) throws SQLException {
        String insertEmployeeSql = """
                    INSERT INTO employees (
                        employee_id, first_name, last_name, ic_passport,
                        email, phone, address, department, "position"
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String insertAccountSql = """
                    INSERT INTO accounts (
                        username, password, role, active, full_name, employee_id
                    ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement ps = conn.prepareStatement(insertEmployeeSql)) {
                    ps.setString(1, employee.getEmployeeId());
                    ps.setString(2, employee.getFirstName());
                    ps.setString(3, employee.getLastName());
                    ps.setString(4, employee.getIcPassport());
                    ps.setString(5, employee.getEmail());
                    ps.setString(6, employee.getPhone());
                    ps.setString(7, employee.getAddress());
                    ps.setString(8, employee.getDepartment());
                    ps.setString(9, employee.getPosition());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(insertAccountSql)) {
                    String fullName = employee.getFirstName() + " " + employee.getLastName();

                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, "EMPLOYEE");
                    ps.setBoolean(4, true);
                    ps.setString(5, fullName);
                    ps.setString(6, employee.getEmployeeId());
                    ps.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Employee> getAllEmployees() throws SQLException {
        String sql = """
                    SELECT id, employee_id, first_name, last_name, ic_passport,
                           email, phone, address, department, "position"
                    FROM employees
                    ORDER BY employee_id
                """;

        List<Employee> employees = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getInt("id"));
                emp.setEmployeeId(rs.getString("employee_id"));
                emp.setFirstName(rs.getString("first_name"));
                emp.setLastName(rs.getString("last_name"));
                emp.setIcPassport(rs.getString("ic_passport"));
                emp.setEmail(rs.getString("email"));
                emp.setPhone(rs.getString("phone"));
                emp.setAddress(rs.getString("address"));
                emp.setDepartment(rs.getString("department"));
                emp.setPosition(rs.getString("position"));
                employees.add(emp);
            }
        }

        return employees;
    }
}
