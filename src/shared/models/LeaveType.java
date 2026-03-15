package shared.models;

import java.io.Serializable;

/*
 * PSEUDOCODE for LeaveType Model:
 * 
 * PURPOSE: Represent a type of leave
 * 
 * FIELDS:
 * - id: Primary key
 * - leaveTypeName: Name (Annual Leave, Medical Leave, etc.)
 * - defaultDays: Default annual entitlement
 * - description: Description of the leave type
 * - isActive: Whether this leave type is currently available
 */
public class LeaveType implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String leaveTypeName;
    private int defaultDays;
    private String description;
    private boolean isActive;

    // Constructors
    public LeaveType() {
    }

    public LeaveType(int id, String leaveTypeName, int defaultDays, String description, boolean isActive) {
        this.id = id;
        this.leaveTypeName = leaveTypeName;
        this.defaultDays = defaultDays;
        this.description = description;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }

    public int getDefaultDays() {
        return defaultDays;
    }

    public void setDefaultDays(int defaultDays) {
        this.defaultDays = defaultDays;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return leaveTypeName; // Used in ComboBox display
    }
}