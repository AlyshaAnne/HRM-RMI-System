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

import client.cache.ProfileCache;

/*PSEUDOCODE for ProfileView:
PURPOSE: Allow employee to view and update their personal profile
 * 
 * WORKFLOW:
 * 1. Load employee data from database using employeeId
 * 2. Display data in editable form fields
 * 3. Allow user to modify fields
 * 4. Validate input
 * 5. Save changes back to database
 * 
 * DATABASE INTERACTION:
 * - READ: service.getEmployeeProfile(employeeId) → Employee object
 * - WRITE: service.updateEmployeeProfile(employee) → boolean success
 * 
 * FUNCTION create(stage, service, loginResult)
 *     1. CREATE UI components (form fields)
 *     2. LOAD existing employee data from database
 *     3. POPULATE form fields with data
 *     4. ATTACH save and back button handlers
 *     5. RETURN Scene*/
public class ProfileView {

    public static Scene create(Stage stage, HRMService service, LoginResultDTO loginResult) {

        Label title = new Label("Personal Profile");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label subtitle = new Label("Update your personal information");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        
        // Editable fields
        Label fnameLabel = new Label("First Name:*");
        TextField fnameField = new TextField();
        fnameField.setPromptText("Enter first name");

        Label lnameLabel = new Label("Last Name:*");
        TextField lnameField = new TextField();
        lnameField.setPromptText("Enter last name");

        Label emailLabel = new Label("Email:*");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter email address");

        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField();
        phoneField.setPromptText("e.g., 012-3456789");

        Label addressLabel = new Label("Address:");
        TextArea addressArea = new TextArea();
        addressArea.setPrefRowCount(3);
        addressArea.setPromptText("Enter home address");
        addressArea.setWrapText(true);

        // Read-only fields (cannot be edited by employee)
        Label icLabel = new Label("IC / Passport No:");
        TextField icField = new TextField();
        icField.setEditable(false);
        icField.setStyle("-fx-background-color: #e0e0e0;");

        Label departmentLabel = new Label("Department:");
        TextField departmentField = new TextField();
        departmentField.setEditable(false);
        departmentField.setStyle("-fx-background-color: #e0e0e0;");

        Label positionLabel = new Label("Position:");
        TextField positionField = new TextField();
        positionField.setEditable(false);
        positionField.setStyle("-fx-background-color: #e0e0e0;");


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

        // Make form fields expand to fill space
        fnameField.setPrefWidth(300);
        lnameField.setPrefWidth(300);
        emailField.setPrefWidth(300);
        phoneField.setPrefWidth(300);
        addressArea.setPrefWidth(300);
        icField.setPrefWidth(300);
        departmentField.setPrefWidth(300);
        positionField.setPrefWidth(300);

        //Create action buttons
        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 20px;");

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle("-fx-padding: 8px 20px;");

        HBox buttonRow = new HBox(15, saveBtn, backBtn);
        buttonRow.setAlignment(javafx.geometry.Pos.CENTER);

       //Arrange all components
        VBox root = new VBox(15, 
            title, 
            subtitle,
            new Separator(),
            form, 
            new Separator(),
            buttonRow
        );
        root.setPadding(new Insets(25));

        /*Load Employee Data:
        WHEN view is created:
         * 1. GET employeeId from loginResult
         * 2. CALL service.getEmployeeProfile(employeeId)
         * 3. IF employee found THEN
         *    - Populate all form fields with employee data
         * 4. ELSE
         *    - Show error message
         *    - Disable save button*/

        loadEmployeeData(service, loginResult.getEmployeeId(), 
                        fnameField, lnameField, icField, emailField, 
                        phoneField, addressArea, departmentField, positionField);


        backBtn.setOnAction(e -> {
            stage.setScene(EmployeeDashboardView.create(stage, service, loginResult));
        });

/*Save Button with Cache Invalidation:
 When clicked:
 * 1. VALIDATE all required fields
 * 2. GET current employee from server
 * 3. UPDATE with form values
 * 4. SAVE to server
 * 5. INVALIDATE cache (force fresh load next time)
 * 6. Show result message*/

saveBtn.setOnAction(e -> {
    // STEP 1: Get and validate input
    String fname = fnameField.getText().trim();
    String lname = lnameField.getText().trim();
    String email = emailField.getText().trim();
    String phone = phoneField.getText().trim();
    String address = addressArea.getText().trim();

    // Validation
    if (fname.isEmpty() || lname.isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Validation Error",
                 "First Name and Last Name are required.");
        return;
    }

    if (email.isEmpty() || !email.contains("@")) {
        showAlert(Alert.AlertType.WARNING, "Validation Error",
                 "Please enter a valid email address.");
        return;
    }

    try {
        // STEP 2: Get current employee data
        Employee employee = service.getEmployeeProfile(loginResult.getEmployeeId());
        
        if (employee == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load employee data.");
            return;
        }

        // STEP 3: Update with form values
        employee.setFirstName(fname);
        employee.setLastName(lname);
        employee.setEmail(email);
        employee.setPhone(phone);
        employee.setAddress(address);

        // STEP 4: Save to server
        boolean success = service.updateEmployeeProfile(employee);

        if (success) {
            // STEP 5:  INVALIDATE CACHE 
            ProfileCache.getInstance().invalidateEmployee(loginResult.getEmployeeId());
            
            showAlert(Alert.AlertType.INFORMATION, "Success",
                     " Profile updated successfully!\n\n" +
                     "Cache invalidated - next load will fetch fresh data.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error",
                     " Failed to update profile.");
        }

    } catch (RemoteException ex) {
        showAlert(Alert.AlertType.ERROR, "Connection Error",
                 "Failed to save: " + ex.getMessage());
        ex.printStackTrace();
    }
});
        
        return new Scene(root, 650, 700);
    }


/*loadEmployeeData() with Caching:
 FUNCTION loadEmployeeData(service, employeeId, fields...)
 *     1. GET cache instance
 *     2. TRY to get employee from cache
 *     3. IF found in cache THEN
 *        - Use cached data (no network call!)
 *        - Display immediately
 *     4. ELSE (cache miss)
 *        - Call server via RMI
 *        - STORE result in cache
 *        - Display data
 *     
 *     BENEFIT: Second visit to profile view is instant!*/

private static void loadEmployeeData(HRMService service, String employeeId,
                                    TextField fnameField, TextField lnameField,
                                    TextField icField, TextField emailField,
                                    TextField phoneField, TextArea addressArea,
                                    TextField deptField, TextField posField) {
    
    ProfileCache cache = ProfileCache.getInstance();
    
    try {
        // STEP 1: Try cache first
        Employee employee = cache.getEmployee(employeeId);
        
        // STEP 2: If not in cache, fetch from server
        if (employee == null) {
            long startTime = System.currentTimeMillis();
            employee = service.getEmployeeProfile(employeeId);
            long duration = System.currentTimeMillis() - startTime;
            System.out.println(" RMI call took: " + duration + "ms");
            
            // STEP 3: Store in cache for future use
            if (employee != null) {
                cache.putEmployee(employeeId, employee);
            }
        }
        
        // STEP 4: Display data (whether from cache or server)
        if (employee != null) {
            fnameField.setText(safeString(employee.getFirstName()));
            lnameField.setText(safeString(employee.getLastName()));
            icField.setText(safeString(employee.getIcPassport()));
            emailField.setText(safeString(employee.getEmail()));
            phoneField.setText(safeString(employee.getPhone()));
            addressArea.setText(safeString(employee.getAddress()));
            deptField.setText(safeString(employee.getDepartment()));
            posField.setText(safeString(employee.getPosition()));
        } else {
            showAlert(Alert.AlertType.WARNING, "Not Found",
                     "Employee profile not found for ID: " + employeeId);
        }

    } catch (RemoteException ex) {
        // LOG TECHNICAL DETAILS TO CONSOLE (for debugging)
        System.err.println("NETWORK ERROR: Failed to load employee data");
        System.err.println("Employee ID: " + employeeId);
        System.err.println("Error Type: " + ex.getClass().getSimpleName());
        System.err.println("Error Message: " + ex.getMessage());
        ex.printStackTrace();
        
        // SHOW USER-FRIENDLY MESSAGE
        showAlert(Alert.AlertType.ERROR, 
                 "Connection Error",
                 "Unable to connect to the server.\n\n" +
                 "Please check:\n" +
                 "• Server is running\n" +
                 "• Network connection is stable\n" +
                 "• Contact IT support if problem persists");
    }
}

    /*saveProfile():
     PURPOSE: Validate input and save changes to database
     * 
     * FUNCTION saveProfile(service, employeeId, form_fields...)
     *     1. VALIDATE required fields:
     *        - firstName must not be empty
     *        - lastName must not be empty
     *        - email must not be empty
     *        - email must contain '@'
     *        IF validation fails THEN
     *          - Show warning alert
     *          - EXIT function
     *     
     *     2. TRY:
     *        a. GET current employee: employee = service.getEmployeeProfile(employeeId)
     *        
     *        b. IF employee is null THEN
     *           - Show error: "Cannot find employee record"
     *           - EXIT
     *        
     *        c. UPDATE employee object with form values:
     *           - employee.firstName = fnameField.text
     *           - employee.lastName = lnameField.text
     *           - employee.email = emailField.text
     *           - employee.phone = phoneField.text
     *           - employee.address = addressArea.text
     *           NOTE: Do NOT update IC, department, position (read-only)
     *        
     *        d. CALL RMI: success = service.updateEmployeeProfile(employee)
     *        
     *        e. IF success THEN
     *           - Show success alert: "Profile updated successfully!"
     *           ELSE
     *           - Show error alert: "Failed to update profile"
     *     
     *     3. CATCH RemoteException:
     *        - Show error alert with exception message
     * END FUNCTION
     */
    private static void saveProfile(HRMService service, String employeeId,
                                   TextField fnameField, TextField lnameField,
                                   TextField emailField, TextField phoneField,
                                   TextArea addressArea) {
        try {
            // STEP 1: Validate required fields
            String firstName = fnameField.getText().trim();
            String lastName = lnameField.getText().trim();
            String email = emailField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", 
                         "First Name and Last Name are required fields.");
                return;
            }

            if (email.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", 
                         "Email is required.");
                return;
            }

            // Simple email validation
            if (!email.contains("@") || !email.contains(".")) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", 
                         "Please enter a valid email address (e.g., name@company.com).");
                return;
            }

            // STEP 2: Get current employee object from database
            Employee employee = service.getEmployeeProfile(employeeId);
            
            if (employee == null) {
                showAlert(Alert.AlertType.ERROR, "Error", 
                         "Cannot find employee record. Please contact HR.");
                return;
            }

            // STEP 3: Update employee object with form values
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setEmail(email);
            employee.setPhone(phoneField.getText().trim());
            employee.setAddress(addressArea.getText().trim());
            
            // Note: IC, Department, Position are NOT updated (read-only for employees)

            // STEP 4: Save to database via RMI
            boolean success = service.updateEmployeeProfile(employee);
            
            // STEP 5: Show result to user
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                         " Your profile has been updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", 
                         " Failed to update profile. Please try again or contact HR.");
            }
            
        } catch (RemoteException ex) {
        // LOG TECHNICAL DETAILS TO CONSOLE (for debugging)
        System.err.println("NETWORK ERROR: Failed to save profile");
        System.err.println("Employee ID: " + employeeId);
        System.err.println("Error Type: " + ex.getClass().getSimpleName());
        System.err.println("Error Message: " + ex.getMessage());
        ex.printStackTrace();
        
        // SHOW USER-FRIENDLY MESSAGE 
        showAlert(Alert.AlertType.ERROR, 
                 "Connection Error",
                 "Unable to save changes to the server.\n\n" +
                 "Your changes were NOT saved.\n\n" +
                 "Please check:\n" +
                 "• Server is running\n" +
                 "• Network connection is stable\n" +
                 "• Try again in a few moments\n\n" +
                 "Contact IT support if problem persists.");
    }
    }


    private static String safeString(String s) {
        return s == null ? "" : s;
    }


    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}