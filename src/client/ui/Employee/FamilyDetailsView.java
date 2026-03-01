package client.ui.Employee;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.dto.LoginResultDTO;
import shared.models.Employee;
//import shared.models.FamilyMember;
import shared.services.HRMService;
import java.rmi.RemoteException;
import java.util.List;
/*
 * PSEUDOCODE for FamilyView:
 * 
 * PURPOSE: Manage family member records (CRUD operations)
 * 
 * FUNCTION create(stage, service, loginResult)
 *     1. CREATE UI components:
 *        - Title and subtitle
 *        - TableView to display all family members
 *        - Buttons for Add, Edit, Delete operations
 *        - Back button
 *     
 *     2. CONFIGURE table columns (Name, Relationship, IC, DOB, Contact)
 *     
 *     3. LOAD existing family members from server
 *        - Call service.getFamilyDetails(employeeId)
 *        - Populate table with results
 *     
 *     4. ATTACH event handlers:
 *        - Add button: Open dialog to add new family member
 *        - Edit button: Open dialog to edit selected member
 *        - Delete button: Delete selected member with confirmation
 *        - Back button: Return to dashboard
 *     
 *     5. RETURN Scene
 * END FUNCTION
 */
public class FamilyDetailsView {
    private static TableView<Employee> familyTable;
    public static Scene create(Stage stage, HRMService service, LoginResultDTO loginResult) {
        // STEP 1: Create title
        Label title = new Label("Family Details Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label subtitle = new Label("Manage your family members information");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        // STEP 2: Create family members table
        familyTable = new TableView<>();
        familyTable.setPrefHeight(300);
        familyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Configure table columns
        TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);
        TableColumn<Employee, String> relationshipCol = new TableColumn<>("Relationship");
        relationshipCol.setCellValueFactory(new PropertyValueFactory<>("relationship"));
        relationshipCol.setPrefWidth(120);
        TableColumn<Employee, String> icCol = new TableColumn<>("IC/Passport");
        icCol.setCellValueFactory(new PropertyValueFactory<>("icPassport"));
        icCol.setPrefWidth(130);
        TableColumn<Employee, String> dobCol = new TableColumn<>("Date of Birth");
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        dobCol.setPrefWidth(110);
        TableColumn<Employee, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        contactCol.setPrefWidth(120);
        familyTable.getColumns().addAll(nameCol, relationshipCol, icCol, dobCol, contactCol);
        // STEP 3: Create action buttons
        Button addBtn = new Button("Add Family Member");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        Button editBtn = new Button("Edit Selected");
        editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        Button backBtn = new Button("Back to Dashboard");
        HBox buttonRow1 = new HBox(15, addBtn, editBtn, deleteBtn);
        buttonRow1.setAlignment(javafx.geometry.Pos.CENTER);
        HBox buttonRow2 = new HBox(backBtn);
        buttonRow2.setAlignment(javafx.geometry.Pos.CENTER);
        // STEP 4: Arrange components
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
         * PSEUDOCODE - loadFamilyData():
         * TRY:
         *     1. Call RMI: familyList = service.getFamilyDetails(employeeId)
         *     2. Clear table
         *     3. IF familyList is not null THEN
         *        - Add all members to table
         * CATCH RemoteException:
         *     - Show error alert
         */
       // loadFamilyData(service, loginResult.getEmployeeId());
        // -----------------------------
        // STEP 6: EVENT HANDLERS
        // -----------------------------
        /*
         * PSEUDOCODE - Add Button:
         * When clicked:
         * 1. CREATE FamilyMemberDialog with null (add mode)
         * 2. SHOW dialog
         * 3. IF user saved successfully THEN
         *    - Reload family table
         */
        // addBtn.setOnAction(e -> {
        //     FamilyMemberDialog dialog = new FamilyMemberDialog(service, loginResult.getEmployeeId(), null);
        //     dialog.showAndWait().ifPresent(success -> {
        //         if (success) {
        //             loadFamilyData(service, loginResult.getEmployeeId());
        //         }
        //     });
        // });
        /*
         * PSEUDOCODE - Edit Button:
         * When clicked:
         * 1. GET selected family member from table
         * 2. IF nothing selected THEN
         *    - Show warning alert
         *    - EXIT
         * 3. ELSE
         *    - CREATE FamilyMemberDialog with selected member (edit mode)
         *    - SHOW dialog
         *    - IF user saved successfully THEN
         *      - Reload family table
         */
        editBtn.setOnAction(e -> {
            Employee selected = familyTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                         "Please select a family member to edit.");
                return;
            }
            // FamilyMemberDialog dialog = new FamilyMemberDialog(service, loginResult.getEmployeeId(), selected);
            // dialog.showAndWait().ifPresent(success -> {
            //     if (success) {
            //         loadFamilyData(service, loginResult.getEmployeeId());
            //     }
            // });
        });
        /*
         * PSEUDOCODE - Delete Button:
         * When clicked:
         * 1. GET selected family member from table
         * 2. IF nothing selected THEN
         *    - Show warning alert
         *    - EXIT
         * 
         * 3. SHOW confirmation dialog
         *    - Message: "Are you sure you want to delete [name]?"
         * 
         * 4. IF user confirms THEN
         *    TRY:
         *        - Call RMI: success = service.deleteFamilyMember(memberId)
         *        - IF success THEN
         *          - Remove from table
         *          - Show success message
         *          ELSE
         *          - Show error message
         *    CATCH RemoteException:
         *        - Show error alert
         */
        // deleteBtn.setOnAction(e -> {
        //     Employee selected = familyTable.getSelectionModel().getSelectedItem();
        //     if (selected == null) {
        //         showAlert(Alert.AlertType.WARNING, "No Selection", 
        //                  "Please select a family member to delete.");
        //         return;
        //     }
        //     Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        //     confirm.setTitle("Confirm Delete");
        //     confirm.setHeaderText("Delete Family Member");
        //     confirm.setContentText("Are you sure you want to delete " + selected.getName() + "?");
            
        //     confirm.showAndWait().ifPresent(response -> {
        //         if (response == ButtonType.OK) {
        //             try {
        //                 boolean success = service.deleteFamilyMember(selected.getId());
        //                 if (success) {
        //                     familyTable.getItems().remove(selected);
        //                     showAlert(Alert.AlertType.INFORMATION, "Success", 
        //                              "Family member deleted successfully.");
        //                 } else {
        //                     showAlert(Alert.AlertType.ERROR, "Error", 
        //                              "Failed to delete family member.");
        //                 }
        //             } catch (RemoteException ex) {
        //                 showAlert(Alert.AlertType.ERROR, "Error", 
        //                          "Failed to delete: " + ex.getMessage());
        //             }
        //         }
        //     });
        // });
        /*
         * PSEUDOCODE - Back Button:
         * When clicked → Return to EmployeeDashboardView
         */
        backBtn.setOnAction(e -> {
            stage.setScene(EmployeeDashboardView.create(stage, service, loginResult));
        });
        // STEP 7: Return scene
        return new Scene(root, 750, 550);
    }
    /*
     * PSEUDOCODE - loadFamilyData():
     * TRY:
     *     1. Call RMI service to get family members
     *     2. Clear existing table data
     *     3. Add retrieved members to table
     * CATCH RemoteException:
     *     - Display error alert
     */
    // private static void loadFamilyData(HRMService service, int employeeId) {
    //     try {
    //         List<FamilyMember> familyMembers = service.getFamilyDetails(employeeId);
    //         familyTable.getItems().clear();
    //         if (familyMembers != null && !familyMembers.isEmpty()) {
    //             familyTable.getItems().addAll(familyMembers);
    //         }
    //     } catch (RemoteException ex) {
    //         showAlert(Alert.AlertType.ERROR, "Error", 
    //                  "Failed to load family data: " + ex.getMessage());
    //     }
    // }
    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
