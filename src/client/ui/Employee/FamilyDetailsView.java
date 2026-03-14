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

import client.cache.ProfileCache;

/*FamilyDetailsView:
 PURPOSE: Manage family member records (CRUD operations) with caching
 CACHING STRATEGY:
 - Cache family member list to reduce network calls
 - Invalidate cache after Add/Edit/Delete operations
 - Force fresh fetch after data modifications
 DATABASE INTERACTION:
 * - READ: service.getFamilyDetails(employeeId) → List<FamilyMember>
 * - CREATE: service.addFamilyMember(member) → boolean
 * - UPDATE: service.updateFamilyMember(member) → boolean
 * - DELETE: service.deleteFamilyMember(memberId) → boolean*/

public class FamilyDetailsView {

    private static TableView<FamilyMember> familyTable;

    public static Scene create(Stage stage, HRMService service, LoginResultDTO loginResult) {


        Label title = new Label("Family Details Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label subtitle = new Label("Manage your family members information");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        familyTable = new TableView<>();
        familyTable.setPrefHeight(350);
        familyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        familyTable.setPlaceholder(new Label("No family members added yet. Click 'Add Family Member' to start."));

        /*PropertyValueFactory automatically maps to getter methods:
         - "name" → calls member.getName()
         - "relationship" → calls member.getRelationship()*/
        
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


        /*LOAD EXISTING DATA
         Initial Data Load:
         WHEN view is created:
         1. GET employeeId from loginResult
         2. CALL loadFamilyData() to fetch from database (or cache)
         3. POPULATE table with results*/

        loadFamilyData(service, loginResult.getEmployeeId());

        /*EVENT HANDLERS
         Add Button:
         When clicked:
         1. CREATE FamilyMemberDialog with null (add mode)
         2. SHOW dialog and WAIT for user input
         3. IF user clicked Save AND save successful THEN
           - INVALIDATE cache 
           - Reload family data to refresh table
           - Show success feedback*/

        addBtn.setOnAction(e -> {
            FamilyMemberDialog dialog = new FamilyMemberDialog(service, loginResult.getEmployeeId(), null);
            dialog.showAndWait().ifPresent(success -> {
                if (success) {
                    //INVALIDATE CACHE after adding new family member
                    ProfileCache.getInstance().invalidateFamilyMembers(loginResult.getEmployeeId());
                    
                    // Reload data (will fetch fresh from server since cache is invalidated)
                    loadFamilyData(service, loginResult.getEmployeeId());
                    
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                             "Family member added successfully!");
                }
            });
        });

        /*Edit Button:
         When clicked:
         1. GET selected family member from table
         2. IF nothing selected THEN
            - Show warning: "Please select a family member"
            - EXIT
        3. ELSE
         - CREATE FamilyMemberDialog with selected member (edit mode)
         - SHOW dialog and WAIT for user input
         - IF user clicked Save AND save successful THEN
         - INVALIDATE cache 
         - Reload family data to refresh table
         - Show success feedback*/

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
                    // INVALIDATE CACHE after editing family member
                    ProfileCache.getInstance().invalidateFamilyMembers(loginResult.getEmployeeId());
                    
                    // Reload data (will fetch fresh from server)
                    loadFamilyData(service, loginResult.getEmployeeId());
                    
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                             "Family member updated successfully!");
                }
            });
        });

        /*Delete Button:
         When clicked:
        1. GET selected family member from table
        2. IF nothing selected THEN
           - Show warning: "Please select a family member"
           - EXIT
        3. SHOW confirmation dialog:
           - Message: "Are you sure you want to delete [name]?"
            - Buttons: OK and Cancel
        4. IF user confirms (clicked OK) THEN
         TRY:
        - CALL service.deleteFamilyMember(memberId)
        - IF successful THEN
        - INVALIDATE cache (important!)
        - Remove from table immediately
        - Show success message
         ELSE
        - Show error message
        CATCH RemoteException:
        - Show error alert with exception details*/
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
                            // INVALIDATE CACHE after deleting family member
                            ProfileCache.getInstance().invalidateFamilyMembers(loginResult.getEmployeeId());
                            
                            // Remove from table UI
                            familyTable.getItems().remove(selected);
                            
                            showAlert(Alert.AlertType.INFORMATION, "Success", 
                                     "Family member deleted successfully.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", 
                                     "Failed to delete family member. Please try again.");
                        }
                    } catch (RemoteException ex) {
                        showAlert(Alert.AlertType.ERROR, "Connection Error", 
                                 "Failed to delete: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });
        });


        backBtn.setOnAction(e -> {
            stage.setScene(EmployeeDashboardView.create(stage, service, loginResult));
        });

        return new Scene(root, 800, 600);
    }


    /*loadFamilyData() with Caching:
    PURPOSE: Load family members from cache or database
    FUNCTION loadFamilyData(service, employeeId)
     1. GET cache instance     
     TRY:
     2. TRY to get family members from cache
     3. IF not in cache (null) THEN
      a. MEASURE start time
      b. CALL RMI: service.getFamilyDetails(employeeId)
      c. MEASURE end time and calculate duration
      d. LOG performance metrics
      e. IF data retrieved successfully THEN
       - STORE in cache for future use
     4. CLEAR existing items in table
     5. IF familyList is not null AND not empty THEN
      - ADD all family members to table
      - Table will automatically display them
      6. ELSE
     - Table shows placeholder: "No family members added yet"
     CATCH RemoteException:
     - Show error alert
     - Log exception for debugging*/

    private static void loadFamilyData(HRMService service, String employeeId) {
        ProfileCache cache = ProfileCache.getInstance();
        
        try {
            // STEP 1: Try cache first
            List<FamilyMember> members = cache.getFamilyMembers(employeeId);
            
            // STEP 2: If not in cache, fetch from server
            if (members == null) {
                long startTime = System.currentTimeMillis();
                members = service.getFamilyDetails(employeeId);
                long duration = System.currentTimeMillis() - startTime;
                System.out.println("RMI call took: " + duration + "ms");
                
                // STEP 3: Store in cache for future use
                if (members != null) {
                    cache.putFamilyMembers(employeeId, members);
                }
            }
            
            // STEP 4: Update table
            familyTable.getItems().clear();
            
            if (members != null && !members.isEmpty()) {
                familyTable.getItems().addAll(members);
            }

        } catch (RemoteException ex) {
            showAlert(Alert.AlertType.ERROR, "Connection Error",
                     "Failed to load family details: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /*showAlert():
    PURPOSE: Display alert dialog to user
    FUNCTION showAlert(type, title, content)
          1. CREATE Alert with specified type
          2. SET title and content
         3. SHOW and WAIT for user to close
     */
    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}