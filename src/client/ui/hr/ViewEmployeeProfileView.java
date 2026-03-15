package client.ui.hr;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.models.Employee;
import shared.services.HRMService;

public class ViewEmployeeProfileView extends VBox {

    public ViewEmployeeProfileView(Stage stage, HRMService service) {
        setSpacing(15);
        setPadding(new Insets(20));

        Label title = new Label("View Employee Profile Details");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField employeeIdField = new TextField();
        employeeIdField.setPromptText("Enter Employee ID");

        Button loadBtn = new Button("Load");
        Button backBtn = new Button("Back");

        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField icField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField addressField = new TextField();
        TextField departmentField = new TextField();
        TextField positionField = new TextField();

        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
        icField.setEditable(false);
        emailField.setEditable(false);
        phoneField.setEditable(false);
        addressField.setEditable(false);
        departmentField.setEditable(false);
        positionField.setEditable(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);

        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);

        grid.add(new Label("IC / Passport:"), 0, 2);
        grid.add(icField, 1, 2);

        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);

        grid.add(new Label("Phone:"), 0, 4);
        grid.add(phoneField, 1, 4);

        grid.add(new Label("Address:"), 0, 5);
        grid.add(addressField, 1, 5);

        grid.add(new Label("Department:"), 0, 6);
        grid.add(departmentField, 1, 6);

        grid.add(new Label("Position:"), 0, 7);
        grid.add(positionField, 1, 7);

        loadBtn.setOnAction(e -> {
            String employeeId = employeeIdField.getText();

            if (employeeId == null || employeeId.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Input", "Please enter an Employee ID.");
                return;
            }

            try {
                Employee emp = service.getEmployeeProfile(employeeId.trim());

                if (emp == null) {
                    showAlert(Alert.AlertType.INFORMATION, "Not Found",
                            "No employee found for ID: " + employeeId);
                    firstNameField.clear();
                    lastNameField.clear();
                    icField.clear();
                    emailField.clear();
                    phoneField.clear();
                    addressField.clear();
                    departmentField.clear();
                    positionField.clear();
                    return;
                }

                firstNameField.setText(emp.getFirstName());
                lastNameField.setText(emp.getLastName());
                icField.setText(emp.getIcPassport());
                emailField.setText(emp.getEmail());
                phoneField.setText(emp.getPhone());
                addressField.setText(emp.getAddress());
                departmentField.setText(emp.getDepartment());
                positionField.setText(emp.getPosition());

            } catch (Exception ex) {
                ex.printStackTrace();

                String msg = ex.toString();
                if (ex.getCause() != null) {
                    msg += "\nCause: " + ex.getCause().toString();
                }

                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to load employee profile:\n" + msg);
            }
        });

        backBtn.setOnAction(e -> {
            stage.setScene(new Scene(new HRDashboardView(stage, service), 600, 400));
        });

        HBox topRow = new HBox(10, employeeIdField, loadBtn, backBtn);

        getChildren().addAll(title, topRow, grid);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}