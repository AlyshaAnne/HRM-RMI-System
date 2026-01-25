package shared.dto;

import java.io.Serializable;

public class LoginResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String role; // "HR" or "EMPLOYEE"
    private final String message;

    public LoginResultDTO(boolean success, String role, String message) {
        this.success = success;
        this.role = role;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }
}
