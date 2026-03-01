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
 * FUNCTION create(stage, service, loginResult)
 *     1. CREATE dashboard title and role label
 *     
 *     2. CREATE navigation buttons:
 *        - Profile button → ProfileView (personal information only)
 *        - Family Details button → FamilyDetailsView (family management)
 *        - Leave Management button → LeaveView (leave operations)
 *        - Logout button → Return to login screen
 *     
 *     3. ATTACH event handlers to each button
 *     
 *     4. ARRANGE in vertical layout
 *     
 *     5. RETURN Scene
 * END FUNCTION
 */
public class EmployeeDashboardView {

    public static Scene create(Stage stage, HRMService service, LoginResultDTO loginResult) {

        // STEP 1: Create title and labels
        Label title = new Label("Employee Dashboard");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label welcomeLabel = new Label("Welcome, " + loginResult.getEmployeeName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 14px;");

        Label roleLabel = new Label("Role: " + loginResult.getRole());
        roleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        // STEP 2: Create navigation buttons
        Button profileBtn = new Button("Update Personal Profile");
        profileBtn.setPrefWidth(250);
        profileBtn.setStyle("-fx-font-size: 13px;");

        Button familyBtn = new Button("Manage Family Details");
        familyBtn.setPrefWidth(250);
        familyBtn.setStyle("-fx-font-size: 13px;");

        Button leaveBtn = new Button("Leave Management");
        leaveBtn.setPrefWidth(250);
        leaveBtn.setStyle("-fx-font-size: 13px;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setPrefWidth(250);
        logoutBtn.setStyle("-fx-font-size: 13px; -fx-background-color: #f44336; -fx-text-fill: white;");

        // STEP 3: Attach event handlers
        /*
         * PSEUDOCODE - Profile Button:
         * When clicked → Navigate to ProfileView
         */
        profileBtn.setOnAction(e -> {
            stage.setScene(ProfileView.create(stage, service, loginResult));
        });

        /*
         * PSEUDOCODE - Family Details Button:
         * When clicked → Navigate to FamilyDetailsView
         */
        familyBtn.setOnAction(e -> {
            stage.setScene(FamilyDetailsView.create(stage, service, loginResult));
        });

        /*
         * PSEUDOCODE - Leave Management Button:
         * When clicked → Navigate to LeaveView
         */
        leaveBtn.setOnAction(e -> {
            stage.setScene(LeaveView.create(stage, service, loginResult));
        });

        /*
         * PSEUDOCODE - Logout Button:
         * When clicked → Return to login screen
         */
        logoutBtn.setOnAction(e -> {
            stage.setScene(client.ui.hr.LoginView.create(stage, service));
        });

        // STEP 4: Arrange components
        VBox root = new VBox(15, 
            title, 
            welcomeLabel, 
            roleLabel,
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
        return new Scene(root, 450, 450);
    }
}