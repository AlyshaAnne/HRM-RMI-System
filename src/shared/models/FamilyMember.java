package shared.models;

import java.io.Serializable;

/*
 * PSEUDOCODE for FamilyMember Model:
 * 
 * PURPOSE: Data structure for family member information
 * 
 * USED FOR:
 * - Transferring family member data via RMI
 * - Mapping database rows to Java objects
 * - Display in JavaFX TableView
 * 
 * FIELDS:
 * - id: Database auto-increment ID
 * - employeeId: Which employee this member belongs to (String like "EMP001")
 * - name: Family member's full name
 * - relationship: Type (Spouse, Child, Parent, Sibling, Other)
 * - icPassport: IC or passport number (optional)
 * - dateOfBirth: Date of birth in YYYY-MM-DD format (optional)
 * - contactNumber: Phone number (optional)
 */
public class FamilyMember implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Database fields
    private int id;                  // Primary key
    private String employeeId;       // Foreign key (EMP001, EMP002, etc.)
    private String name;             // Family member's full name
    private String relationship;     // Spouse, Child, Parent, Sibling, Other
    private String icPassport;       // IC or Passport number
    private String dateOfBirth;      // Format: YYYY-MM-DD
    private String contactNumber;    // Phone number

    // Constructors
    public FamilyMember() {
    }

    public FamilyMember(int id, String employeeId, String name, String relationship, 
                       String icPassport, String dateOfBirth, String contactNumber) {
        this.id = id;
        this.employeeId = employeeId;
        this.name = name;
        this.relationship = relationship;
        this.icPassport = icPassport;
        this.dateOfBirth = dateOfBirth;
        this.contactNumber = contactNumber;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getIcPassport() {
        return icPassport;
    }

    public void setIcPassport(String icPassport) {
        this.icPassport = icPassport;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return "FamilyMember{" +
                "id=" + id +
                ", employeeId='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                ", relationship='" + relationship + '\'' +
                ", icPassport='" + icPassport + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}