package client.ui.Employee;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import shared.models.FamilyMember;
import shared.services.HRMService;

import java.rmi.RemoteException;
import java.time.LocalDate;

/*FamilyMemberDialog:
 * 
 * PURPOSE: Popup dialog for adding/editing a single family member
 * 
 * MODES:
 * - ADD MODE: existingMember is null → Creates new family member
 * - EDIT MODE: existingMember is not null → Updates existing member
 * 
 * WORKFLOW:
 * 1. Display form with input fields
 * 2. If editing, pre-fill form with existing data
 * 3. User enters/modifies data
 * 4. User clicks Save or Cancel
 * 5. If Save: Validate → Save to database → Return true
 * 6. If Cancel: Return false (no changes)
 * 
 * CONSTRUCTOR(service, employeeId, existingMember)
 *     1. Determine mode (add or edit)
 *     2. Create form with input fields
 *     3. If editing, pre-fill fields
 *     4. Define save behavior
 *     5. Set up dialog buttons
 * END CONSTRUCTOR
 */
public class FamilyMemberDialog extends Dialog<Boolean> {

    /*Constructor:
     * 
     * PARAMETERS:
     * - service: RMI service for database operations
     * - employeeId: Which employee this family member belongs to (String like "EMP001")
     * - existing: null for ADD, FamilyMember object for EDIT
     * 
     * PROCESS:
     * 1. SET dialog title based on mode
     * 2. CREATE form layout with all fields
     * 3. IF editing THEN pre-fill fields with existing data
     * 4. SET result converter (what happens when Save is clicked)
     * 5. CONFIGURE dialog buttons*/

    public FamilyMemberDialog(HRMService service, String employeeId, FamilyMember existing) {
        
        // Determine mode and set title
        boolean isEditMode = (existing != null);
        setTitle(isEditMode ? "Edit Family Member" : "Add Family Member");
        setHeaderText(null);
        setResizable(true);

        // Create dialog buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Create form layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));



        // Name field (Required)
        Label nameLabel = new Label("Name:*");
        TextField nameField = new TextField();
        nameField.setPromptText("Full name (e.g., Mary Tan)");
        nameField.setPrefWidth(300);

        // Relationship field (Required)
        Label relationshipLabel = new Label("Relationship:*");
        ComboBox<String> relationshipBox = new ComboBox<>();
        relationshipBox.getItems().addAll("Spouse", "Child", "Parent", "Sibling", "Other");
        relationshipBox.setPromptText("Select relationship");
        relationshipBox.setPrefWidth(300);

        // IC/Passport field (Optional)
        Label icLabel = new Label("IC/Passport Number:");
        TextField icField = new TextField();
        icField.setPromptText("e.g., 920505-01-2345");
        icField.setPrefWidth(300);

        // Date of Birth field (Optional)
        Label dobLabel = new Label("Date of Birth:");
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Select date");
        dobPicker.setPrefWidth(300);
        dobPicker.setEditable(false);

        // Contact Number field (Optional)
        Label contactLabel = new Label("Contact Number:");
        TextField contactField = new TextField();
        contactField.setPromptText("e.g., 012-3456789");
        contactField.setPrefWidth(300);

        // Note about required fields
        Label noteLabel = new Label("* Required fields");
        noteLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        /*Pre-fill fields if editing
         * IF isEditMode THEN
         *     FOR each field:
         *         GET value from existing object
         *         SET field value
         *         Handle null/empty values appropriately
         */
        if (isEditMode) {
            nameField.setText(existing.getName());
            relationshipBox.setValue(existing.getRelationship());
            icField.setText(existing.getIcPassport() != null ? existing.getIcPassport() : "");
            
            // Parse date of birth
            if (existing.getDateOfBirth() != null && !existing.getDateOfBirth().isEmpty()) {
                try {
                    dobPicker.setValue(LocalDate.parse(existing.getDateOfBirth()));
                } catch (Exception e) {
                    // Invalid date format, leave empty
                    System.err.println("Invalid date format: " + existing.getDateOfBirth());
                }
            }
            
            contactField.setText(existing.getContactNumber() != null ? existing.getContactNumber() : "");
        }

        //Add fields to grid layout
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(relationshipLabel, 0, 1);
        grid.add(relationshipBox, 1, 1);
        grid.add(icLabel, 0, 2);
        grid.add(icField, 1, 2);
        grid.add(dobLabel, 0, 3);
        grid.add(dobPicker, 1, 3);
        grid.add(contactLabel, 0, 4);
        grid.add(contactField, 1, 4);
        grid.add(noteLabel, 0, 5, 2, 1);

        getDialogPane().setContent(grid);

        /*Define what happens when Save button is clicked
        Result Converter:
         * 
         * IF Save button clicked THEN
         *     1. VALIDATE required fields:
         *        - Name must not be empty
         *        - Relationship must be selected
         *        IF validation fails THEN
         *          - Show warning alert
         *          - RETURN false (keep dialog open)
         *     
         *     2. CREATE or GET FamilyMember object:
         *        - IF adding new: CREATE new FamilyMember, SET employeeId
         *        - IF editing: USE existing FamilyMember object
         *     
         *     3. UPDATE FamilyMember fields with form data:
         *        - member.name = nameField.text
         *        - member.relationship = relationshipBox.value
         *        - member.icPassport = icField.text
         *        - member.dateOfBirth = dobPicker.value (convert to string)
         *        - member.contactNumber = contactField.text
         *     
         *     4. SAVE to database via RMI:
         *        - IF adding: CALL service.addFamilyMember(member)
         *        - IF editing: CALL service.updateFamilyMember(member)
         *     
         *     5. IF save successful THEN
         *        - RETURN true (close dialog)
         *        ELSE
         *        - Show error alert
         *        - RETURN false (keep dialog open)
         * 
         * ELSE IF Cancel button clicked THEN
         *     RETURN false (close dialog, no changes)
         * 
         * CATCH RemoteException:
         *     - Show error alert
         *     - RETURN false */

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                
                // VALIDATION: Check required fields
                String name = nameField.getText().trim();
                String relationship = relationshipBox.getValue();
                
                if (name.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", 
                             "Name is required. Please enter the family member's name.");
                    return false;
                }
                
                if (relationship == null || relationship.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error", 
                             "Relationship is required. Please select a relationship type.");
                    return false;
                }

                try {
                    FamilyMember member;
                    
                    // Determine if adding new or editing existing
                    if (isEditMode) {
                        // EDIT MODE: Use existing member object
                        member = existing;
                    } else {
                        // ADD MODE: Create new member object
                        member = new FamilyMember();
                        member.setEmployeeId(employeeId);  // Link to employee
                    }

                    // Update member object with form data
                    member.setName(name);
                    member.setRelationship(relationship);
                    member.setIcPassport(icField.getText().trim());
                    
                    // Convert date to string format (YYYY-MM-DD)
                    if (dobPicker.getValue() != null) {
                        member.setDateOfBirth(dobPicker.getValue().toString());
                    } else {
                        member.setDateOfBirth("");
                    }
                    
                    member.setContactNumber(contactField.getText().trim());

                    // Save to database via RMI
                    boolean success;
                    if (isEditMode) {
                        // Call update method
                        success = service.updateFamilyMember(member);
                    } else {
                        // Call add method
                        success = service.addFamilyMember(member);
                    }

                    // Return result
                    if (success) {
                        return true;  // Close dialog, indicate success
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", 
                                 "Failed to save family member. Please try again.");
                        return false;  // Keep dialog open
                    }
                    
                } catch (RemoteException ex) {
                    // Handle network/RMI errors
                    showAlert(Alert.AlertType.ERROR, "Connection Error", 
                             "Failed to save: " + ex.getMessage());
                    ex.printStackTrace();
                    return false;  // Keep dialog open
                }
            }
            
            // Cancel button clicked or dialog closed
            return false;
        });

        //Set initial focus to name field for better UX
        nameField.requestFocus();
    }

    /*showAlert():
     * 
     * PURPOSE: Display alert dialog within this dialog
     * 
     * FUNCTION showAlert(type, title, content)
     *     1. CREATE Alert with specified type
     *     2. SET title and content
     *     3. SHOW and WAIT for user to close*/
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}