package client.ui.Employee;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.dto.LoginResultDTO;
import shared.services.HRMService;

/*
 * PSEUDOCODE for EmployeeDashboardView:
 * 
 * PURPOSE: Main menu for employee operations
 * 
 * RECEIVES: LoginResultDTO from login containing:
 * - employeeId (String): "EMP001", "EMP002", etc.
 * - employeeName (String): Full name for display
 * - role (String): "EMPLOYEE", "HR", "ADMIN"
 * 
 * PROVIDES: Navigation buttons to:
 * - ProfileView: Update personal information
 * - FamilyDetailsView: Manage family members
 * - LeaveView: Apply for leave (future)
 * - Logout: Return to login screen
 * 
 * FUNCTION create(stage, service, loginResult)
 *     1. EXTRACT employee info from loginResult
 *     2. CREATE personalized welcome UI
 *     3. CREATE navigation buttons
 *     4. ATTACH event handlers passing loginResult to child views
 *     5. RETURN Scene
 * END FUNCTION
 */
public class EmployeeDashboardView {

    public static Scene create(Stage stage, HRMService service, LoginResultDTO loginResult) {

        /*
         * STEP 1: Create title and personalized labels
         * 
         * PSEUDOCODE:
         * - Display "Employee Dashboard" as main title
         * - Show welcome message with employee name from LoginResultDTO
         * - Display role and employee ID for reference
         */
        Label title = new Label("Employee Dashboard");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Null-safe employee name access
        String employeeName = loginResult.getEmployeeName() != null ? 
                             loginResult.getEmployeeName() : "User";

        Label welcomeLabel = new Label("Welcome, " + employeeName + "!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        Label roleLabel = new Label("Role: " + safeString(loginResult.getRole()));
        roleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        // Display employee ID for reference
        Label idLabel = new Label("Employee ID: " + safeString(loginResult.getEmployeeId()));
        idLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

        /*
         * STEP 2: Create navigation buttons
         * 
         * PSEUDOCODE:
         * - Create styled buttons for each module
         * - Set consistent width and padding
         * - Use color coding (logout = red)
         */
        Button profileBtn = new Button("Update Personal Profile");
        profileBtn.setPrefWidth(250);
        profileBtn.setStyle("-fx-font-size: 13px; -fx-padding: 10px;");

        Button familyBtn = new Button("Manage Family Details");
        familyBtn.setPrefWidth(250);
        familyBtn.setStyle("-fx-font-size: 13px; -fx-padding: 10px;");

        Button leaveBtn = new Button("Leave Management");
        leaveBtn.setPrefWidth(250);
        leaveBtn.setStyle("-fx-font-size: 13px; -fx-padding: 10px;");
        leaveBtn.setDisable(true); // Disabled until leave module is implemented
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setPrefWidth(250);
        logoutBtn.setStyle("-fx-font-size: 13px; -fx-padding: 10px; -fx-background-color: #f44336; -fx-text-fill: white;");

        /*
         * STEP 3: Attach event handlers
         * 
         * IMPORTANT: Pass loginResult to all child views
         * This provides them with employeeId needed for database queries
         */
        
        /*
         * PSEUDOCODE - Profile Button:
         * When clicked:
         * 1. Navigate to ProfileView
         * 2. PASS stage, service, and loginResult
         * 3. ProfileView will use loginResult.employeeId to load data
         */
        profileBtn.setOnAction(e -> {
            stage.setScene(ProfileView.create(stage, service, loginResult));
        });

        /*
         * PSEUDOCODE - Family Details Button:
         * When clicked:
         * 1. Navigate to FamilyDetailsView
         * 2. PASS stage, service, and loginResult
         * 3. FamilyDetailsView will use loginResult.employeeId
         */
        familyBtn.setOnAction(e -> {
            stage.setScene(FamilyDetailsView.create(stage, service, loginResult));
        });

        /*
         * PSEUDOCODE - Leave Management Button:
         * When clicked:
         * 1. Navigate to LeaveView (future implementation)
         * 2. PASS stage, service, and loginResult
         */
        leaveBtn.setOnAction(e -> {
            // TODO: Implement LeaveView
            // stage.setScene(LeaveView.create(stage, service, loginResult));
        });

        /*
         * PSEUDOCODE - Logout Button:
         * When clicked:
         * 1. Return to LoginView
         * 2. Clear current session (loginResult will be discarded)
         * 3. User must login again
         */
        logoutBtn.setOnAction(e -> {
            stage.setScene(client.ui.hr.LoginView.create(stage, service));
        });

        /*
         * STEP 4: Arrange components in vertical layout
         * 
         * PSEUDOCODE:
         * - Use VBox with 15px spacing
         * - Add title, labels, separators, and buttons
         * - Center align everything
         * - Apply background color
         */
        VBox root = new VBox(15, 
            title, 
            welcomeLabel, 
            roleLabel,
            idLabel,
            new javafx.scene.control.Separator(),
            profileBtn, 
            familyBtn,
            leaveBtn,
            new javafx.scene.control.Separator(),
            logoutBtn
        );
        root.setPadding(new Insets(30));
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // STEP 5: Return scene
        return new Scene(root, 450, 500);
    }

    /*
     * PSEUDOCODE - safeString():
     * 
     * PURPOSE: Prevent NullPointerException when displaying data
     * 
     * FUNCTION safeString(s)
     *     IF s is null THEN
     *         RETURN empty string
     *     ELSE
     *         RETURN s
     * END FUNCTION
     */
    private static String safeString(String s) {
        return s == null ? "" : s;
    }
}