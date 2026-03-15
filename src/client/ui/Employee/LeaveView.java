package client.ui.Employee;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import shared.dto.LoginResultDTO;
import shared.models.LeaveApplication;
import shared.models.LeaveBalance;
import shared.services.HRMService;

import java.rmi.RemoteException;
import java.time.Year;
import java.util.List;

import client.cache.ProfileCache;

/*LeaveView:
 PURPOSE: Manage leave applications and view leave balance
 FEATURES:
 * 1. Display leave balance for current year
 * 2. Apply for new leave
 * 3. View leave application history
 * 4. Cancel pending applications
 * 
 * LAYOUT:
 * - Top: Leave balance summary (cards showing remaining days)
 * - Middle: Leave application history table
 * - Bottom: Action buttons (Apply, Cancel, Refresh, Back)
 * 
 * FUNCTION create(stage, service, loginResult)
 *     1. CREATE leave balance display
 *     2. CREATE leave applications table
 *     3. CREATE action buttons
 *     4. LOAD data from database
 *     5. ATTACH event handlers
 *     6. RETURN Scene
 * END FUNCTION
 */
public class LeaveView {

    private static TableView<LeaveApplication> applicationsTable;
    private static VBox balanceContainer;

    public static Scene create(Stage stage, HRMService service, LoginResultDTO loginResult) {

        // STEP 1: Create title
        Label title = new Label("Leave Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label subtitle = new Label("View your leave balance and manage leave applications");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        // STEP 2: Create leave balance section
        Label balanceTitle = new Label("Leave Balance (" + Year.now().getValue() + ")");
        balanceTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        balanceContainer = new VBox(10);
        balanceContainer.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15px; -fx-background-radius: 5px;");
        balanceContainer.setPrefHeight(120);

        // STEP 3: Create applications table
        Label historyTitle = new Label("Leave Application History");
        historyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10px 0 0 0;");

        applicationsTable = new TableView<>();
        applicationsTable.setPrefHeight(250);
        applicationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        applicationsTable.setPlaceholder(new Label("No leave applications yet. Click 'Apply for Leave' to start."));

        
        TableColumn<LeaveApplication, String> typeCol = new TableColumn<>("Leave Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("leaveTypeName"));
        typeCol.setPrefWidth(120);

        TableColumn<LeaveApplication, String> startCol = new TableColumn<>("Start Date");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startCol.setPrefWidth(100);

        TableColumn<LeaveApplication, String> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endCol.setPrefWidth(100);

        TableColumn<LeaveApplication, Integer> daysCol = new TableColumn<>("Days");
        daysCol.setCellValueFactory(new PropertyValueFactory<>("numDays"));
        daysCol.setPrefWidth(60);

        TableColumn<LeaveApplication, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
        reasonCol.setPrefWidth(150);

        TableColumn<LeaveApplication, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        // Color code status
        statusCol.setCellFactory(column -> new TableCell<LeaveApplication, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("Approved".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if ("Rejected".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if ("Pending".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    }
                }
            }
        });

        TableColumn<LeaveApplication, String> appliedCol = new TableColumn<>("Applied Date");
        appliedCol.setCellValueFactory(new PropertyValueFactory<>("appliedDate"));
        appliedCol.setPrefWidth(150);

        TableColumn<LeaveApplication, String> remarksCol = new TableColumn<>("HR Remarks");
        remarksCol.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        remarksCol.setPrefWidth(150);

        applicationsTable.getColumns().addAll(typeCol, startCol, endCol, daysCol, 
                                              reasonCol, statusCol, appliedCol, remarksCol);

        // STEP 4: Create action buttons
        Button applyBtn = new Button("Apply for Leave");
        applyBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px;");

        Button cancelBtn = new Button("Cancel Selected");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10px 20px;");

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-padding: 10px 20px;");

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle("-fx-padding: 10px 20px;");

        HBox buttonRow1 = new HBox(15, applyBtn, cancelBtn, refreshBtn);
        buttonRow1.setAlignment(javafx.geometry.Pos.CENTER);

        HBox buttonRow2 = new HBox(backBtn);
        buttonRow2.setAlignment(javafx.geometry.Pos.CENTER);

        // STEP 5: Arrange components
        VBox root = new VBox(15,
            title,
            subtitle,
            new Separator(),
            balanceTitle,
            balanceContainer,
            new Separator(),
            historyTitle,
            applicationsTable,
            buttonRow1,
            new Separator(),
            buttonRow2
        );
        root.setPadding(new Insets(25));

        // STEP 6: LOAD DATA
        loadLeaveBalance(service, loginResult.getEmployeeId());
        loadLeaveApplications(service, loginResult.getEmployeeId());

        /* Apply Button:
        When clicked:
         * 1. CREATE LeaveApplicationDialog
         * 2. SHOW dialog
         * 3. IF user submitted successfully THEN
         *    - Refresh both balance and applications*/

applyBtn.setOnAction(e -> {
    LeaveApplicationDialog dialog = new LeaveApplicationDialog(service, loginResult.getEmployeeId());
    dialog.showAndWait().ifPresent(success -> {
        if (success) {
            // Invalidate caches
            ProfileCache.getInstance().invalidateLeaveBalance(loginResult.getEmployeeId(), Year.now().getValue());
            ProfileCache.getInstance().invalidateLeaveApplications(loginResult.getEmployeeId());
            
            // Reload
            loadLeaveBalance(service, loginResult.getEmployeeId());
            loadLeaveApplications(service, loginResult.getEmployeeId());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Leave application submitted successfully!");
        }
    });
});

        /*Cancel Button:
        When clicked:
         * 1. GET selected application
         * 2. IF nothing selected THEN show warning
         * 3. IF selected application is not Pending THEN show warning
         * 4. SHOW confirmation dialog
         * 5. IF confirmed THEN
         *    - Call service.cancelLeaveApplication()
         *    - Refresh data*/

        cancelBtn.setOnAction(e -> {
            LeaveApplication selected = applicationsTable.getSelectionModel().getSelectedItem();
            
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection",
                         "Please select a leave application to cancel.");
                return;
            }

            if (!"Pending".equalsIgnoreCase(selected.getStatus())) {
                showAlert(Alert.AlertType.WARNING, "Cannot Cancel",
                         "Only pending applications can be cancelled.\n\nThis application is: " + selected.getStatus());
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Cancellation");
            confirm.setHeaderText("Cancel Leave Application");
            confirm.setContentText("Are you sure you want to cancel this leave application?\n\n" +
                                  "Leave Type: " + selected.getLeaveTypeName() + "\n" +
                                  "Dates: " + selected.getStartDate() + " to " + selected.getEndDate() + "\n" +
                                  "Days: " + selected.getNumDays() + "\n\n" +
                                  "This action cannot be undone.");
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        boolean success = service.cancelLeaveApplication(selected.getId());
                        if (success) {
                            applicationsTable.getItems().remove(selected);
                            showAlert(Alert.AlertType.INFORMATION, "Success",
                                     "Leave application cancelled successfully.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error",
                                     "Failed to cancel application. It may have already been processed.");
                        }
                    } catch (RemoteException ex) {
                        showAlert(Alert.AlertType.ERROR, "Connection Error",
                                 "Failed to cancel: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });
        });

    
        refreshBtn.setOnAction(e -> {
            loadLeaveBalance(service, loginResult.getEmployeeId());
            loadLeaveApplications(service, loginResult.getEmployeeId());
            showAlert(Alert.AlertType.INFORMATION, "Refreshed", "Data refreshed successfully.");
        });

        backBtn.setOnAction(e -> {
            stage.setScene(EmployeeDashboardView.create(stage, service, loginResult));
        });

        return new Scene(root, 1000, 700);
    }

    /*loadLeaveBalance():
     PURPOSE: Load and display leave balance for current year
     FUNCTION loadLeaveBalance(service, employeeId)
     *     TRY:
     *         1. GET current year
     *         2. CALL service.getLeaveBalance(employeeId, year)
     *         3. CLEAR balance container
     *         4. IF balances found THEN
     *            FOR each balance:
     *                CREATE balance card with:
     *                - Leave type name
     *                - Remaining days (large, bold)
     *                - Used days / Total days
     *                ADD card to container
     *         5. ELSE
     *            SHOW "No leave balance found" message
     *     
     *     CATCH RemoteException:
     *         SHOW error alert
     * END FUNCTION
     */
private static void loadLeaveBalance(HRMService service, String employeeId) {
    ProfileCache cache = ProfileCache.getInstance();
    
    try {
        int currentYear = Year.now().getValue();
        
        // Try cache first
        List<LeaveBalance> balances = cache.getLeaveBalance(employeeId, currentYear);
        
        // If not in cache, fetch from server
        if (balances == null) {
            long startTime = System.currentTimeMillis();
            balances = service.getLeaveBalance(employeeId, currentYear);
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("RMI call took: " + duration + "ms");
            
            // Store in cache
            if (balances != null) {
                cache.putLeaveBalance(employeeId, currentYear, balances);
            }
        }
        
        balanceContainer.getChildren().clear();
        
        if (balances != null && !balances.isEmpty()) {
            HBox cardsRow = new HBox(15);
            for (LeaveBalance balance : balances) {
                VBox card = createBalanceCard(balance);
                cardsRow.getChildren().add(card);
            }
            balanceContainer.getChildren().add(cardsRow);
        } else {
            Label noBalance = new Label("No leave balance found for " + currentYear);
            balanceContainer.getChildren().add(noBalance);
        }

    } catch (RemoteException ex) {
    // 1. LOG TECHNICAL DETAILS (for developers/debugging)
    System.err.println("NETWORK ERROR");
    System.err.println("Context: Loading Leave Balance");
    System.err.println("Error Type: " + ex.getClass().getSimpleName());
    System.err.println("Error Message: " + ex.getMessage());
    ex.printStackTrace();  
    
    // 2. SHOW USER-FRIENDLY MESSAGE
    showAlert(Alert.AlertType.ERROR, 
             "Connection Error", 
             "Unable to load leave balance from server.\n\n" +   
             "Please check:\n" +  
             "• Server is running\n" +
             "• Network connection is stable\n" +
             "• Contact IT support if problem persists");  
}

}

    /*createBalanceCard():
     PURPOSE: Create a visual card for one leave type balance
     FUNCTION createBalanceCard(balance)
     *     1. CREATE VBox container with styling
     *     2. ADD leave type name label
     *     3. ADD remaining days (large, prominent)
     *     4. ADD used/total info
     *     5. ADD color indicator based on remaining days
     *     6. RETURN card*/

    private static VBox createBalanceCard(LeaveBalance balance) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 15px; " +
                     "-fx-border-color: #ddd; -fx-border-width: 1px; " +
                     "-fx-background-radius: 5px; -fx-border-radius: 5px;");
        card.setPrefWidth(180);

        Label typeName = new Label(balance.getLeaveTypeName());
        typeName.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #555;");

        Label remaining = new Label(balance.getRemainingDays() + " days");
        remaining.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Color code based on remaining days
        if (balance.getRemainingDays() == 0) {
            remaining.setTextFill(Color.RED);
        } else if (balance.getRemainingDays() <= 3) {
            remaining.setTextFill(Color.ORANGE);
        } else {
            remaining.setTextFill(Color.GREEN);
        }

        Label details = new Label("Used: " + balance.getUsedDays() + " / " + balance.getTotalDays());
        details.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

        Label status = new Label("Remaining");
        status.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        card.getChildren().addAll(typeName, remaining, status, details);
        return card;
    }

    /*loadLeaveApplications():
     PURPOSE: Load and display all leave applications
     FUNCTION loadLeaveApplications(service, employeeId)
     *     TRY:
     *         1. CALL service.getLeaveApplications(employeeId)
     *         2. CLEAR table
     *         3. IF applications found THEN
     *            ADD all to table
     *         4. ELSE
     *            Table shows placeholder message
     *     
     *     CATCH RemoteException:
     *         SHOW error alert*/

private static void loadLeaveApplications(HRMService service, String employeeId) {
    ProfileCache cache = ProfileCache.getInstance();
    
    try {
        // Try cache first
        List<LeaveApplication> applications = cache.getLeaveApplications(employeeId);
        
        // If not in cache, fetch from server
        if (applications == null) {
            long startTime = System.currentTimeMillis();
            applications = service.getLeaveApplications(employeeId);
            long duration = System.currentTimeMillis() - startTime;
            System.out.println(" RMI call took: " + duration + "ms");
            
            // Store in cache
            if (applications != null) {
                cache.putLeaveApplications(employeeId, applications);
            }
        }
        
        applicationsTable.getItems().clear();
        if (applications != null && !applications.isEmpty()) {
            applicationsTable.getItems().addAll(applications);
        }

    } catch (RemoteException ex) {
    // 1. LOG TECHNICAL DETAILS (for developers/debugging)
    System.err.println("NETWORK ERROR");
    System.err.println("Context: Loading Leave Application");
    System.err.println("Error Type: " + ex.getClass().getSimpleName());
    System.err.println("Error Message: " + ex.getMessage());
    ex.printStackTrace();  
    
    // 2. SHOW USER-FRIENDLY MESSAGE
    showAlert(Alert.AlertType.ERROR, 
             "Connection Error",  
             "Unable to load details from server.\n\n" +  
             "Please check:\n" +  
             "• Server is running\n" +
             "• Network connection is stable\n" +
             "• Contact IT support if problem persists");  
}
}

    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}