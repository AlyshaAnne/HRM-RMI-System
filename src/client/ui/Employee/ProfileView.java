package client.ui.Employee;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.dto.LoginResultDTO;
import shared.models.Employee;
import shared.services.HRMService;
import java.rmi.RemoteException;
/*
 * PSEUDOCODE for ProfileView:
 * 
 * PURPOSE: Allow employee to view and update their PERSONAL INFORMATION only
 * 
 * FUNCTION create(stage, service, loginResult)
 *     1. CREATE UI components:
 *        - Title label
 *        - Form fields for personal data (name, email, phone, address)
 *        - Save and Back buttons
 *     
 *     2. LOAD existing employee data from server
 *        - Call service.getEmployeeProfile(employeeId)
 *        - Populate form fields with retrieved data
 *     
 *     3. ATTACH event handlers:
 *        - Save button: Validate and update employee profile
 *        - Back button: Return to dashboard
 *     
 *     4. RETURN Scene
 * END FUNCTION
 */
public class ProfileView {
    public static Scene create(Stage stage, HRMService service, LoginResultDTO loginResult) {
        // STEP 1: Create title
        Label title = new Label("Personal Profile");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label subtitle = new Label("Update your personal information");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        // STEP 2: Create form fields
        Label fnameLabel = new Label("First Name:*");
        TextField fnameField = new TextField();
        Label lnameLabel = new Label("Last Name:*");
        TextField lnameField = new TextField();
        Label icLabel = new Label("IC / Passport No:");
        TextField icField = new TextField();
        icField.setEditable(false); // IC cannot be changed by employee
        icField.setStyle("-fx-background-color: #e0e0e0;");
        Label emailLabel = new Label("Email:*");
        TextField emailField = new TextField();
        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField();
        Label addressLabel = new Label("Address:");
        TextArea addressArea = new TextArea();
        addressArea.setPrefRowCount(3);
        Label departmentLabel = new Label("Department:");
        TextField departmentField = new TextField();
        departmentField.setEditable(false); // Department cannot be changed by employee
        departmentField.setStyle("-fx-background-color: #e0e0e0;");
        Label positionLabel = new Label("Position:");
        TextField positionField = new TextField();
        positionField.setEditable(false); // Position cannot be changed by employee
        positionField.setStyle("-fx-background-color: #e0e0e0;");
        // STEP 3: Arrange form in grid layout
        GridPane form = new GridPane();
        form.setVgap(12);
        form.setHgap(10);
        form.add(fnameLabel, 0, 0);      form.add(fnameField, 1, 0);
        form.add(lnameLabel, 0, 1);      form.add(lnameField, 1, 1);
        form.add(icLabel, 0, 2);         form.add(icField, 1, 2);
        form.add(emailLabel, 0, 3);      form.add(emailField, 1, 3);
        form.add(phoneLabel, 0, 4);      form.add(phoneField, 1, 4);
        form.add(addressLabel, 0, 5);    form.add(addressArea, 1, 5);
        form.add(departmentLabel, 0, 6); form.add(departmentField, 1, 6);
        form.add(positionLabel, 0, 7);   form.add(positionField, 1, 7);
        // STEP 4: Create buttons
        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        Button backBtn = new Button("Back to Dashboard");
        HBox buttonRow = new HBox(15, saveBtn, backBtn);
        buttonRow.setAlignment(javafx.geometry.Pos.CENTER);
        // STEP 5: Arrange all components
        VBox root = new VBox(15, 
            title, 
            subtitle,
            new Separator(),
            form, 
            new Separator(),
            buttonRow
        );
        root.setPadding(new Insets(25));
        // -----------------------------
        // STEP 6: LOAD EXISTING DATA
        // -----------------------------
        /*
         * PSEUDOCODE - loadEmployeeData():
         * TRY:
         *     1. Call RMI: employee = service.getEmployeeProfile(employeeId)
         *     2. IF employee exists THEN
         *        - Populate ALL form fields with employee data
         * CATCH RemoteException:
         *     - Show error alert
         */
        // try {
        //     Employee employee = service.getEmployeeProfile(loginResult.getEmployeeId());
        //     if (employee != null) {
        //         fnameField.setText(employee.getFirstName());
        //         lnameField.setText(employee.getLastName());
        //         icField.setText(employee.getIcPassport());
        //         emailField.setText(employee.getEmail());
        //         phoneField.setText(employee.getPhone());
        //         addressArea.setText(employee.getAddress());
        //         departmentField.setText(employee.getDepartment());
        //         positionField.setText(employee.getPosition());
        //     }
        // } catch (RemoteException ex) {
        //     showAlert(Alert.AlertType.ERROR, "Error", "Failed to load profile: " + ex.getMessage());
        // }
        // -----------------------------
        // STEP 7: EVENT HANDLERS
        // -----------------------------
        /*
         * PSEUDOCODE - Back Button:
         * When clicked → Return to EmployeeDashboardView
         */
        backBtn.setOnAction(e -> {
            stage.setScene(EmployeeDashboardView.create(stage, service, loginResult));
        });
        /*
         * PSEUDOCODE - Save Button:
         * 1. VALIDATE required fields (firstName, lastName, email)
         *    - IF any required field is empty THEN
         *      - Show warning alert
         *      - EXIT
         * 
         * 2. TRY:
         *    a. Get current employee: employee = service.getEmployeeProfile(employeeId)
         *    b. UPDATE employee object with form values
         *    c. Call RMI: success = service.updateEmployeeProfile(employee)
         *    d. IF success THEN
         *       - Show success alert
         *       ELSE
         *       - Show error alert
         * 
         * 3. CATCH RemoteException:
         *    - Show error alert
         */
        // saveBtn.setOnAction(e -> {
        //     // Validate required fields
        //     if (fnameField.getText().trim().isEmpty() || 
        //         lnameField.getText().trim().isEmpty() ||
        //         emailField.getText().trim().isEmpty()) {
        //         showAlert(Alert.AlertType.WARNING, "Validation Error", 
        //                  "First Name, Last Name, and Email are required fields.");
        //         return;
        //     }
        //     // Email validation (simple check)
        //     if (!emailField.getText().contains("@")) {
        //         showAlert(Alert.AlertType.WARNING, "Validation Error", 
        //                  "Please enter a valid email address.");
        //         return;
        //     }
            //try {
                // Get current employee object
               // Employee employee = service.getEmployeeProfile(loginResult.getEmployeeId());
                
                // Update with form values
                // employee.setFirstName(fnameField.getText().trim());
                // employee.setLastName(lnameField.getText().trim());
                // employee.setEmail(emailField.getText().trim());
                // employee.setPhone(phoneField.getText().trim());
               // employee.setAddress(addressArea.getText().trim());
                // Note: IC, Department, Position are NOT updated (not editable by employee)
                // Save to database via RMI
              //  boolean success = service.updateEmployeeProfile(employee);
                
        //         if (success) {
        //             showAlert(Alert.AlertType.INFORMATION, "Success", 
        //                      "Your profile has been updated successfully!");
        //         } else {
        //             showAlert(Alert.AlertType.ERROR, "Error", 
        //                      "Failed to update profile. Please try again.");
        //         }
        //     } catch (RemoteException ex) {
        //         showAlert(Alert.AlertType.ERROR, "Error", 
        //                  "Failed to save profile: " + ex.getMessage());
        //     }
        // });
        // STEP 8: Return scene
        return new Scene(root, 600, 650);
    }
    /*
     * PSEUDOCODE - showAlert():
     * 1. CREATE alert dialog with specified type
     * 2. SET title and content
     * 3. SHOW and WAIT for user to close
     */
    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
