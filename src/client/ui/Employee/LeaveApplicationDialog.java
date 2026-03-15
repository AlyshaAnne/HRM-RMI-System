package client.ui.Employee;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import shared.models.LeaveApplication;
import shared.models.LeaveBalance;
import shared.models.LeaveType;
import shared.services.HRMService;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;

/*LeaveApplicationDialog:
 PURPOSE: Dialog for employee to apply for leave
 * 
 * WORKFLOW:
 * 1. Select leave type from dropdown
 * 2. Select start and end dates
 * 3. System calculates number of days
 * 4. Check if sufficient balance
 * 5. Enter reason
 * 6. Submit application
 * 
 * VALIDATION:
 * - Leave type must be selected
 * - Dates must be valid (start <= end)
 * - Must have sufficient leave balance
 * - Reason must be provided
 * 
 * CONSTRUCTOR(service, employeeId)
 *     1. CREATE form with input fields
 *     2. LOAD available leave types
 *     3. LOAD leave balances for validation
 *     4. SET up date change listeners
 *     5. DEFINE save behavior with validation*/

public class LeaveApplicationDialog extends Dialog<Boolean> {

    private List<LeaveBalance> leaveBalances;
    private ComboBox<LeaveType> leaveTypeBox;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Label daysLabel;
    private Label balanceWarningLabel;
    private TextArea reasonArea;

    /*Constructor:
     * 
     * PARAMETERS:
     * - service: RMI service for database operations
     * - employeeId: Which employee is applying
     * 
     * PROCESS:
     * 1. SET dialog title and buttons
     * 2. CREATE form layout
     * 3. LOAD leave types from database
     * 4. LOAD leave balances for validation
     * 5. SET up automatic day calculation
     * 6. SET up balance checking
     * 7. DEFINE save behavior with validation
     */
    public LeaveApplicationDialog(HRMService service, String employeeId) {
        
        // STEP 1: Set dialog properties
        setTitle("Apply for Leave");
        setHeaderText("Submit a new leave application");
        setResizable(true);

        // Create buttons
        ButtonType submitButtonType = new ButtonType("Submit Application", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(submitButtonType, cancelButtonType);

        // STEP 2: Create form layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        //STEP 3: Create input fields

        // Leave Type selection (Required)
        Label leaveTypeLabel = new Label("Leave Type:*");
        leaveTypeBox = new ComboBox<>();
        leaveTypeBox.setPromptText("Select leave type");
        leaveTypeBox.setPrefWidth(300);

        // Start Date (Required)
        Label startDateLabel = new Label("Start Date:*");
        startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Select start date");
        startDatePicker.setPrefWidth(300);
        
        // Disable past dates
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        // End Date (Required)
        Label endDateLabel = new Label("End Date:*");
        endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Select end date");
        endDatePicker.setPrefWidth(300);
        
        // Disable past dates and dates before start date
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate start = startDatePicker.getValue();
                setDisable(empty || date.isBefore(LocalDate.now()) || 
                          (start != null && date.isBefore(start)));
            }
        });

        // Number of Days (Auto-calculated)
        Label daysDisplayLabel = new Label("Number of Days:");
        daysLabel = new Label("0 days");
        daysLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        // Balance Warning Label
        balanceWarningLabel = new Label("");
        balanceWarningLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        balanceWarningLabel.setWrapText(true);
        balanceWarningLabel.setPrefWidth(300);
        balanceWarningLabel.setVisible(false);

        // Reason (Required)
        Label reasonLabel = new Label("Reason:*");
        reasonArea = new TextArea();
        reasonArea.setPromptText("Enter reason for leave (e.g., Family vacation, Medical appointment, etc.)");
        reasonArea.setPrefRowCount(4);
        reasonArea.setPrefWidth(300);
        reasonArea.setWrapText(true);

        // Note about required fields
        Label noteLabel = new Label("* Required fields");
        noteLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        // Info label
        Label infoLabel = new Label("Note: Your application will be reviewed by HR.");
        infoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: gray; -fx-font-style: italic;");
        infoLabel.setWrapText(true);
        infoLabel.setPrefWidth(300);

        // STEP 4: Add fields to grid
        grid.add(leaveTypeLabel, 0, 0);
        grid.add(leaveTypeBox, 1, 0);
        grid.add(startDateLabel, 0, 1);
        grid.add(startDatePicker, 1, 1);
        grid.add(endDateLabel, 0, 2);
        grid.add(endDatePicker, 1, 2);
        grid.add(daysDisplayLabel, 0, 3);
        grid.add(daysLabel, 1, 3);
        
        VBox warningBox = new VBox(5, balanceWarningLabel);
        grid.add(warningBox, 1, 4);
        
        grid.add(reasonLabel, 0, 5);
        grid.add(reasonArea, 1, 5);
        grid.add(noteLabel, 0, 6, 2, 1);
        grid.add(infoLabel, 0, 7, 2, 1);

        getDialogPane().setContent(grid);

        
        /*Load data from database
         Load Leave Types:
         TRY:
         *     1. CALL service.getAvailableLeaveTypes()
         *     2. POPULATE leaveTypeBox with types
         * CATCH RemoteException:
         *     SHOW error alert
         */
        try {
            List<LeaveType> leaveTypes = service.getAvailableLeaveTypes();
            if (leaveTypes != null && !leaveTypes.isEmpty()) {
                leaveTypeBox.getItems().addAll(leaveTypes);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Leave Types", 
                         "No leave types available. Please contact HR.");
            }
        } catch (RemoteException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Failed to load leave types: " + ex.getMessage());
            ex.printStackTrace();
        }

        /*Load Leave Balances: 
         TRY:
         *     1. GET current year
         *     2. CALL service.getLeaveBalance(employeeId, year)
         *     3. STORE in leaveBalances for validation
         CATCH RemoteException:
         *     SHOW error alert
         */
        try {
            int currentYear = Year.now().getValue();
            leaveBalances = service.getLeaveBalance(employeeId, currentYear);
        } catch (RemoteException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Failed to load leave balance: " + ex.getMessage());
            ex.printStackTrace();
        }

        /*Set up listeners
         Date Change Listeners:
        When start date OR end date changes:
         * 1. CALCULATE number of days
         * 2. UPDATE days label
         * 3. CHECK leave balance
         * 4. SHOW warning if insufficient balance*/

        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            calculateDays();
            checkLeaveBalance();
        });

        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            calculateDays();
            checkLeaveBalance();
        });

        leaveTypeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            checkLeaveBalance();
        });

        /*Define save behavior
        Submit Button Action:
         * 
         * IF Submit button clicked THEN
         *     1. VALIDATE all required fields:
         *        - Leave type selected
         *        - Start date selected
         *        - End date selected
         *        - Start date <= End date
         *        - Reason not empty
         *        IF validation fails THEN
         *          SHOW warning alert
         *          RETURN false (keep dialog open)
         *     
         *     2. CALCULATE number of days
         *     
         *     3. CHECK leave balance:
         *        IF insufficient balance THEN
         *          SHOW confirmation dialog asking if they want to proceed
         *          IF user cancels THEN RETURN false
         *     
         *     4. CREATE LeaveApplication object
         *     5. SET all fields from form
         *     6. CALL service.submitLeaveApplication()
         *     7. IF successful THEN
         *        RETURN true (close dialog)
         *        ELSE
         *        SHOW error alert
         *        RETURN false (keep dialog open)
         * 
         * ELSE IF Cancel button clicked THEN
         *     RETURN false (close dialog, no submission)
         * 
         * CATCH RemoteException:
         *     SHOW error alert
         *     RETURN false*/

        setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                
                // VALIDATION: Check required fields
                LeaveType selectedType = leaveTypeBox.getValue();
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                String reason = reasonArea.getText().trim();

                if (selectedType == null) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error",
                             "Please select a leave type.");
                    return false;
                }

                if (startDate == null) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error",
                             "Please select a start date.");
                    return false;
                }

                if (endDate == null) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error",
                             "Please select an end date.");
                    return false;
                }

                if (endDate.isBefore(startDate)) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error",
                             "End date cannot be before start date.");
                    return false;
                }

                if (reason.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error",
                             "Please provide a reason for your leave.");
                    return false;
                }

                // Calculate number of days
                long numDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

                // Check leave balance
                LeaveBalance balance = findBalance(selectedType.getId());
                if (balance != null && numDays > balance.getRemainingDays()) {
                    Alert confirm = new Alert(Alert.AlertType.WARNING);
                    confirm.setTitle("Insufficient Balance");
                    confirm.setHeaderText("Insufficient Leave Balance");
                    confirm.setContentText("You are applying for " + numDays + " days, but you only have " +
                                          balance.getRemainingDays() + " days remaining for " + 
                                          selectedType.getLeaveTypeName() + ".\n\n" +
                                          "Do you still want to submit this application?\n" +
                                          "(HR may reject applications with insufficient balance)");
                    confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                    
                    if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.NO) {
                        return false;
                    }
                }

                // Create leave application
                try {
                    LeaveApplication application = new LeaveApplication();
                    application.setEmployeeId(employeeId);
                    application.setLeaveTypeId(selectedType.getId());
                    application.setStartDate(startDate.toString());
                    application.setEndDate(endDate.toString());
                    application.setNumDays((int) numDays);
                    application.setReason(reason);
                    application.setStatus("Pending");

                    // Submit to database
                    boolean success = service.submitLeaveApplication(application);

                    if (success) {
                        return true; // Close dialog, indicate success
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error",
                                 "Failed to submit leave application. Please try again.");
                        return false; // Keep dialog open
                    }

                } catch (RemoteException ex) {
                    showAlert(Alert.AlertType.ERROR, "Connection Error",
                             "Failed to submit application: " + ex.getMessage());
                    ex.printStackTrace();
                    return false;
                }
            }

            // Cancel button clicked
            return false;
        });

        // Set initial focus
        leaveTypeBox.requestFocus();
    }


    /*calculateDays():
     PURPOSE: Calculate number of days between start and end date
     FUNCTION calculateDays()
     *     IF both start date AND end date selected THEN
     *         1. CALCULATE days = (end - start) + 1
     *         2. UPDATE days label
     *     ELSE
     *         SHOW "0 days"*/
    private void calculateDays() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start != null && end != null && !end.isBefore(start)) {
            long days = ChronoUnit.DAYS.between(start, end) + 1;
            daysLabel.setText(days + " day" + (days > 1 ? "s" : ""));
        } else {
            daysLabel.setText("0 days");
        }
    }

    /*checkLeaveBalance():
     PURPOSE: Check if employee has sufficient leave balance
     FUNCTION checkLeaveBalance()
     *     1. GET selected leave type
     *     2. GET start and end dates
     *     3. IF all selected THEN
     *        a. CALCULATE number of days
     *        b. FIND corresponding leave balance
     *        c. IF balance found THEN
     *           IF days > remaining balance THEN
     *               SHOW warning (red text)
     *           ELSE
     *               HIDE warning
     *     4. ELSE
     *        HIDE warning
     * END FUNCTION
     */
    private void checkLeaveBalance() {
        LeaveType selectedType = leaveTypeBox.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (selectedType != null && start != null && end != null && !end.isBefore(start)) {
            long days = ChronoUnit.DAYS.between(start, end) + 1;
            LeaveBalance balance = findBalance(selectedType.getId());

            if (balance != null) {
                if (days > balance.getRemainingDays()) {
                    balanceWarningLabel.setText("Warning: You only have " + 
                                               balance.getRemainingDays() + 
                                               " day(s) remaining for " + 
                                               selectedType.getLeaveTypeName() + "!");
                    balanceWarningLabel.setVisible(true);
                } else {
                    balanceWarningLabel.setText("You have " + 
                                               balance.getRemainingDays() + 
                                               " day(s) remaining");
                    balanceWarningLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    balanceWarningLabel.setVisible(true);
                }
            } else {
                balanceWarningLabel.setVisible(false);
            }
        } else {
            balanceWarningLabel.setVisible(false);
        }
    }

    /*findBalance():
     PURPOSE: Find leave balance for specific leave type
    FUNCTION findBalance(leaveTypeId)
     *     FOR each balance in leaveBalances:
     *         IF balance.leaveTypeId == leaveTypeId THEN
     *             RETURN balance
     *     RETURN null (not found)
     * END FUNCTION
     */
    private LeaveBalance findBalance(int leaveTypeId) {
        if (leaveBalances != null) {
            for (LeaveBalance balance : leaveBalances) {
                if (balance.getLeaveTypeId() == leaveTypeId) {
                    return balance;
                }
            }
        }
        return null;
    }

    /*showAlert():
     * Display alert dialog to user
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}