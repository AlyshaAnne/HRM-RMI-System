package client.ui.Employee;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.dto.LoginResultDTO;
import shared.models.FamilyMember;
import shared.services.HRMService;

import java.rmi.RemoteException;
import java.util.List;

/*
 * PSEUDOCODE for FamilyDetailsView:
 * 
 * PURPOSE: Manage family member records (CRUD operations)
 * 
 * FEATURES:
 * - Display all family members in a table
 * - Add new family member
 * - Edit existing family member
 * - Delete family member
 * 
 * DATABASE INTERACTION:
 * - READ: service.getFamilyDetails(employeeId) → List<FamilyMember>
 * - CREATE: service.addFamilyMember(member) → boolean
 * - UPDATE: service.updateFamilyMember(member) → boolean
 * - DELETE: service.deleteFamilyMember(memberId) → boolean
 * 
 * FUNCTION create(stage, service, loginResult)
 *     1. CREATE UI components:
 *        - TableView to display family members
 *        - Buttons for Add, Edit, Delete operations
 *     2. LOAD existing family members from database
 *     3. ATTACH event handlers for CRUD operations
 *     4. RETURN Scene
 * END FUNCTION
 */
public class FamilyDetailsView {

    private static TableView<FamilyMember> familyTable;

    public static Scene create(Stage stage, HRMService service, LoginResultDTO loginResult) {

        /*
         * STEP 1: Create title and subtitle
         */
        Label title = new Label("Family Details Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label subtitle = new Label("Manage your family members information");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        /*
         * STEP 2: Create family members table
         * 
         * PSEUDOCODE:
         * - Create TableView to display family members
         * - Define columns for each field
         * - Set column widths and properties
         * - Enable row selection
         */
        familyTable = new TableView<>();
        familyTable.setPrefHeight(350);
        familyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        familyTable.setPlaceholder(new Label("No family members added yet. Click 'Add Family Member' to start."));

        /*
         * PSEUDOCODE - Table Columns:
         * 
         * FOR each column:
         *     1. CREATE TableColumn with display name
         *     2. SET CellValueFactory to bind to FamilyMember property
         *     3. SET preferred width
         *     4. ADD to table
         * 
         * PropertyValueFactory automatically maps to getter methods:
         * - "name" → calls member.getName()
         * - "relationship" → calls member.getRelationship()
         * etc.
         */
        
        TableColumn<FamilyMember, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);

        TableColumn<FamilyMember, String> relationshipCol = new TableColumn<>("Relationship");
        relationshipCol.setCellValueFactory(new PropertyValueFactory<>("relationship"));
        relationshipCol.setPrefWidth(120);

        TableColumn<FamilyMember, String> icCol = new TableColumn<>("IC/Passport");
        icCol.setCellValueFactory(new PropertyValueFactory<>("icPassport"));
        icCol.setPrefWidth(130);

        TableColumn<FamilyMember, String> dobCol = new TableColumn<>("Date of Birth");
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        dobCol.setPrefWidth(110);

        TableColumn<FamilyMember, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        contactCol.setPrefWidth(120);

        familyTable.getColumns().addAll(nameCol, relationshipCol, icCol, dobCol, contactCol);

        /*
         * STEP 3: Create action buttons
         * 
         * PSEUDOCODE:
         * - Create buttons for CRUD operations
         * - Apply styling and colors
         * - Set appropriate widths
         */
        Button addBtn = new Button("Add Family Member");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 15px;");
        addBtn.setPrefWidth(180);

        Button editBtn = new Button("Edit Selected");
        editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8px 15px;");
        editBtn.setPrefWidth(150);

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8px 15px;");
        deleteBtn.setPrefWidth(150);

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle("-fx-padding: 8px 15px;");
        backBtn.setPrefWidth(180);

        HBox buttonRow1 = new HBox(15, addBtn, editBtn, deleteBtn);
        buttonRow1.setAlignment(javafx.geometry.Pos.CENTER);

        HBox buttonRow2 = new HBox(backBtn);
        buttonRow2.setAlignment(javafx.geometry.Pos.CENTER);

        /*
         * STEP 4: Arrange components in layout
         */
        VBox root = new VBox(15, 
            title, 
            subtitle,
            new Separator(),
            familyTable, 
            buttonRow1,
            new Separator(),
            buttonRow2
        );
        root.setPadding(new Insets(25));

        // -----------------------------
        // STEP 5: LOAD EXISTING DATA
        // -----------------------------
        /*
         * PSEUDOCODE - Initial Data Load:
         * 
         * WHEN view is created:
         * 1. GET employeeId from loginResult
         * 2. CALL loadFamilyData() to fetch from database
         * 3. POPULATE table with results
         */
        loadFamilyData(service, loginResult.getEmployeeId());

        // -----------------------------
        // STEP 6: EVENT HANDLERS
        // -----------------------------

        /*
         * PSEUDOCODE - Add Button:
         * 
         * When clicked:
         * 1. CREATE FamilyMemberDialog with null (add mode)
         * 2. SHOW dialog and WAIT for user input
         * 3. IF user clicked Save AND save successful THEN
         *    - Reload family data to refresh table
         *    - Show success feedback
         */
        addBtn.setOnAction(e -> {
            FamilyMemberDialog dialog = new FamilyMemberDialog(service, loginResult.getEmployeeId(), null);
            dialog.showAndWait().ifPresent(success -> {
                if (success) {
                    loadFamilyData(service, loginResult.getEmployeeId());
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                             "✅ Family member added successfully!");
                }
            });
        });

        /*
         * PSEUDOCODE - Edit Button:
         * 
         * When clicked:
         * 1. GET selected family member from table
         * 2. IF nothing selected THEN
         *    - Show warning: "Please select a family member"
         *    - EXIT
         * 3. ELSE
         *    - CREATE FamilyMemberDialog with selected member (edit mode)
         *    - SHOW dialog and WAIT for user input
         *    - IF user clicked Save AND save successful THEN
         *      - Reload family data to refresh table
         *      - Show success feedback
         */
        editBtn.setOnAction(e -> {
            FamilyMember selected = familyTable.getSelectionModel().getSelectedItem();
            
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                         "Please select a family member from the table to edit.");
                return;
            }

            FamilyMemberDialog dialog = new FamilyMemberDialog(service, loginResult.getEmployeeId(), selected);
            dialog.showAndWait().ifPresent(success -> {
                if (success) {
                    loadFamilyData(service, loginResult.getEmployeeId());
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                             "✅ Family member updated successfully!");
                }
            });
        });

        /*
         * PSEUDOCODE - Delete Button:
         * 
         * When clicked:
         * 1. GET selected family member from table
         * 2. IF nothing selected THEN
         *    - Show warning: "Please select a family member"
         *    - EXIT
         * 
         * 3. SHOW confirmation dialog:
         *    - Message: "Are you sure you want to delete [name]?"
         *    - Buttons: OK and Cancel
         * 
         * 4. IF user confirms (clicked OK) THEN
         *    TRY:
         *        - CALL service.deleteFamilyMember(memberId)
         *        - IF successful THEN
         *          - Remove from table immediately
         *          - Show success message
         *          ELSE
         *          - Show error message
         *    CATCH RemoteException:
         *        - Show error alert with exception details
         */
        deleteBtn.setOnAction(e -> {
            FamilyMember selected = familyTable.getSelectionModel().getSelectedItem();
            
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                         "Please select a family member from the table to delete.");
                return;
            }

            // Create confirmation dialog
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete Family Member");
            confirm.setContentText("Are you sure you want to delete " + selected.getName() + "?\n\nThis action cannot be undone.");
            
            // Show and wait for user confirmation
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Call RMI method to delete from database
                        boolean success = service.deleteFamilyMember(selected.getId());
                        
                        if (success) {
                            // Remove from table UI
                            familyTable.getItems().remove(selected);
                            showAlert(Alert.AlertType.INFORMATION, "Success", 
                                     "✅ Family member deleted successfully.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", 
                                     "❌ Failed to delete family member. Please try again.");
                        }
                    } catch (RemoteException ex) {
                        showAlert(Alert.AlertType.ERROR, "Connection Error", 
                                 "Failed to delete: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });
        });

        /*
         * PSEUDOCODE - Back Button:
         * 
         * When clicked:
         * 1. Return to EmployeeDashboardView
         * 2. PASS same stage, service, loginResult
         */
        backBtn.setOnAction(e -> {
            stage.setScene(EmployeeDashboardView.create(stage, service, loginResult));
        });

        // STEP 7: Return scene
        return new Scene(root, 800, 600);
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    /*
     * PSEUDOCODE - loadFamilyData():
     * 
     * PURPOSE: Load family members from database and populate table
     * 
     * FUNCTION loadFamilyData(service, employeeId)
     *     TRY:
     *         1. CALL RMI: familyList = service.getFamilyDetails(employeeId)
     *         
     *         2. CLEAR existing items in table
     *         
     *         3. IF familyList is not null AND not empty THEN
     *            - ADD all family members to table
     *            - Table will automatically display them
     *         4. ELSE
     *            - Table shows placeholder: "No family members added yet"
     *     
     *     CATCH RemoteException:
     *         - Show error alert
     *         - Log exception for debugging
     * END FUNCTION
     */
    private static void loadFamilyData(HRMService service, String employeeId) {
        try {
            // Make RMI call to get family members from database
            List<FamilyMember> familyMembers = service.getFamilyDetails(employeeId);
            
            // Clear table
            familyTable.getItems().clear();
            
            // Populate table with data
            if (familyMembers != null && !familyMembers.isEmpty()) {
                familyTable.getItems().addAll(familyMembers);
            }
            
        } catch (RemoteException ex) {
            showAlert(Alert.AlertType.ERROR, "Connection Error", 
                     "Failed to load family data: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /*
     * PSEUDOCODE - showAlert():
     * 
     * PURPOSE: Display alert dialog to user
     * 
     * FUNCTION showAlert(type, title, content)
     *     1. CREATE Alert with specified type
     *     2. SET title and content
     *     3. SHOW and WAIT for user to close
     * END FUNCTION
     */
    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}