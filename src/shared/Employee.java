package shared;

import java.io.Serializable;

public class Employee implements Serializable {

    private String firstName;
    private String lastName;
    private String icNumber;

    public Employee(String firstName, String lastName, String icNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.icNumber = icNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIcNumber() {
        return icNumber;
    }
}
