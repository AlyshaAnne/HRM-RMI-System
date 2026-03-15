package client.ui.hr;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.models.LeaveApplication;
import shared.services.HRMService;

import java.util.List;

public class ViewLeaveHistoryView extends VBox {

    public ViewLeaveHistoryView(Stage stage, HRMService service) {
        setSpacing(15);
        setPadding(new Insets(20));

        Label title = new Label("View Leave History");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField employeeIdField = new TextField();
        employeeIdField.setPromptText("Enter Employee ID");

        Button loadBtn = new Button("Load");
        Button backBtn = new Button("Back");

        TableView<LeaveApplication> table = new TableView<>();

        TableColumn<LeaveApplication, String> leaveTypeCol = new TableColumn<>("Leave Type");
        leaveTypeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLeaveTypeName()));

        TableColumn<LeaveApplication, String> startDateCol = new TableColumn<>("Start Date");
        startDateCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStartDate()));

        TableColumn<LeaveApplication, String> endDateCol = new TableColumn<>("End Date");
        endDateCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEndDate()));

        TableColumn<LeaveApplication, String> numDaysCol = new TableColumn<>("Days");
        numDaysCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getNumDays())));

        TableColumn<LeaveApplication, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getReason()));

        TableColumn<LeaveApplication, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));

        TableColumn<LeaveApplication, String> appliedDateCol = new TableColumn<>("Applied Date");
        appliedDateCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAppliedDate()));

        table.getColumns().addAll(
                leaveTypeCol, startDateCol, endDateCol,
                numDaysCol, reasonCol, statusCol, appliedDateCol);

        table.setPrefHeight(450);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadBtn.setOnAction(e -> {
            String employeeId = employeeIdField.getText();

            if (employeeId == null || employeeId.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Input", "Please enter an Employee ID.");
                return;
            }

            try {
                List<LeaveApplication> applications = service.getLeaveApplications(employeeId.trim());

                table.getItems().clear();

                if (applications == null || applications.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "No Data",
                            "No leave history found for employee ID: " + employeeId);
                    return;
                }

                table.getItems().addAll(applications);

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to load leave history: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> {
            stage.setScene(new Scene(new HRDashboardView(stage, service), 700, 450));
        });

        HBox topRow = new HBox(10, employeeIdField, loadBtn, backBtn);

        getChildren().addAll(title, topRow, table);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}