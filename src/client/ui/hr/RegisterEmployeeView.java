package client.ui.hr;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import shared.models.Employee;
import shared.services.HRMService;

public class RegisterEmployeeView extends GridPane {

    public RegisterEmployeeView(Stage stage, HRMService service) {
        setPadding(new Insets(20));
        setHgap(10);
        setVgap(10);

        TextField employeeIdField = new TextField();
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField icPassportField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField addressField = new TextField();
        TextField departmentField = new TextField();
        TextField positionField = new TextField();
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        Button registerBtn = new Button("Register");
        Button backBtn = new Button("Back");

        add(new Label("Employee ID:"), 0, 0);
        add(employeeIdField, 1, 0);

        add(new Label("First Name:"), 0, 1);
        add(firstNameField, 1, 1);

        add(new Label("Last Name:"), 0, 2);
        add(lastNameField, 1, 2);

        add(new Label("IC / Passport:"), 0, 3);
        add(icPassportField, 1, 3);

        add(new Label("Email:"), 0, 4);
        add(emailField, 1, 4);

        add(new Label("Phone:"), 0, 5);
        add(phoneField, 1, 5);

        add(new Label("Address:"), 0, 6);
        add(addressField, 1, 6);

        add(new Label("Department:"), 0, 7);
        add(departmentField, 1, 7);

        add(new Label("Position:"), 0, 8);
        add(positionField, 1, 8);

        add(new Label("Username:"), 0, 9);
        add(usernameField, 1, 9);

        add(new Label("Password:"), 0, 10);
        add(passwordField, 1, 10);

        add(registerBtn, 0, 11);
        add(backBtn, 1, 11);

        registerBtn.setOnAction(e -> {
            try {
                Employee employee = new Employee();
                employee.setEmployeeId(employeeIdField.getText().trim());
                employee.setFirstName(firstNameField.getText().trim());
                employee.setLastName(lastNameField.getText().trim());
                employee.setIcPassport(icPassportField.getText().trim());
                employee.setEmail(emailField.getText().trim());
                employee.setPhone(phoneField.getText().trim());
                employee.setAddress(addressField.getText().trim());
                employee.setDepartment(departmentField.getText().trim());
                employee.setPosition(positionField.getText().trim());

                String result = service.registerEmployee(
                        employee,
                        usernameField.getText().trim(),
                        passwordField.getText().trim());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Register Employee");
                alert.setHeaderText("Result");
                alert.setContentText(result);
                alert.showAndWait();

            } catch (Exception ex) {
                ex.printStackTrace();

                String msg = ex.toString();
                if (ex.getCause() != null) {
                    msg += "\nCause: " + ex.getCause().toString();
                }

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Registration failed");
                alert.setContentText(msg);
                alert.showAndWait();
            }
        });

        backBtn.setOnAction(e -> {
            stage.setScene(new javafx.scene.Scene(new HRDashboardView(stage, service), 300, 200));
        });
    }
}