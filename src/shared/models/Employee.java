package shared.models;

import java.io.Serializable;

/*
 * PSEUDOCODE for Employee Model:
 * 
 * PURPOSE: Data structure for employee information
 * 
 * USED FOR:
 * - Transferring employee data between client and server via RMI
 * - Mapping database rows to Java objects
 * 
 * FIELDS:
 * - id: Database auto-increment ID (internal)
 * - employeeId: Business ID like "EMP001" (visible to users)
 * - firstName, lastName: Employee name
 * - icPassport: IC or passport number (unique, cannot be changed by employee)
 * - email: Contact email (editable)
 * - phone: Phone number (editable)
 * - address: Home address (editable)
 * - department: Department name (set by HR, read-only for employee)
 * - position: Job title (set by HR, read-only for employee)
 */
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Database fields
    private int id;                  // Database primary key
    private String employeeId;       // Business ID (EMP001, EMP002, etc.)
    private String firstName;
    private String lastName;
    private String icPassport;
    private String email;
    private String phone;
    private String address;
    private String department;
    private String position;


    // Constructors
    public Employee() {
    }

    public Employee(int id, String employeeId, String firstName, String lastName,
                   String icPassport, String email, String phone, String address,
                   String department, String position) {
        this.id = id;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.icPassport = icPassport;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.department = department;
        this.position = position;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getIcPassport() {
        return icPassport;
    }

    public void setIcPassport(String icPassport) {
        this.icPassport = icPassport;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }



    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", employeeId='" + employeeId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + 
                '}';
    }
}