package client.ui.hr;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.dto.MonthlyReportDTO;
import shared.dto.MonthlySalaryDTO;
import shared.services.HRMService;

public class MonthlyReportView extends VBox {

    public MonthlyReportView(Stage stage, HRMService service) {
        setSpacing(15);
        setPadding(new Insets(20));

        Label title = new Label("Monthly Report");
        Label testLabel = new Label("NEW VERSION LOADED");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField yearField = new TextField();
        yearField.setPromptText("Year");

        TextField monthField = new TextField();
        monthField.setPromptText("Month (1-12)");

        Button loadBtn = new Button("Load Report");
        Button backBtn = new Button("Back");

        HBox topRow = new HBox(10, yearField, monthField, loadBtn, backBtn);

        TableView<MonthlySalaryDTO> table = new TableView<>();
        table.setPrefHeight(500);

        TableColumn<MonthlySalaryDTO, String> empIdCol = new TableColumn<>("Employee ID");
        empIdCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmployeeId()));

        TableColumn<MonthlySalaryDTO, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFirstName()));

        TableColumn<MonthlySalaryDTO, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLastName()));

        TableColumn<MonthlySalaryDTO, Number> baseSalaryCol = new TableColumn<>("Base Salary");
        baseSalaryCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getBaseSalary()));

        TableColumn<MonthlySalaryDTO, Number> allowanceCol = new TableColumn<>("Allowance");
        allowanceCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getAllowance()));

        TableColumn<MonthlySalaryDTO, Number> deductionCol = new TableColumn<>("Deduction");
        deductionCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDeduction()));

        TableColumn<MonthlySalaryDTO, Number> taxCol = new TableColumn<>("Tax");
        taxCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTax()));

        TableColumn<MonthlySalaryDTO, Number> netSalaryCol = new TableColumn<>("Net Salary");
        netSalaryCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getNetSalary()));

        TableColumn<MonthlySalaryDTO, Number> leaveTakenCol = new TableColumn<>("Leave Taken");
        leaveTakenCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getLeaveTaken()));

        table.getColumns().addAll(
                empIdCol,
                firstNameCol,
                lastNameCol,
                baseSalaryCol,
                allowanceCol,
                deductionCol,
                taxCol,
                netSalaryCol,
                leaveTakenCol);

        loadBtn.setOnAction(e -> {
            try {
                int year = Integer.parseInt(yearField.getText().trim());
                int month = Integer.parseInt(monthField.getText().trim());

                MonthlyReportDTO report = service.generateMonthlyReport(year, month);

                table.getItems().clear();

                if (report == null || report.getEmployeeReports() == null || report.getEmployeeReports().isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "No Data",
                            "No monthly report data found for " + month + "/" + year);
                    return;
                }

                table.getItems().addAll(report.getEmployeeReports());

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid year and month.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load monthly report: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> {
            stage.setScene(new Scene(new HRDashboardView(stage, service), 600, 400));
        });

        getChildren().addAll(title, testLabel, topRow, table);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}