package client.ui.hr;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.dto.YearlyEmployeeReportDTO;
import shared.dto.YearlyReportDTO;
import shared.services.HRMService;

public class YearlyReportView extends VBox {

    public YearlyReportView(Stage stage, HRMService service) {
        setSpacing(15);
        setPadding(new Insets(20));

        Label title = new Label("Yearly Report");

        TextField yearField = new TextField();
        yearField.setPromptText("Enter year");

        Button generateBtn = new Button("Generate Report");
        Button backBtn = new Button("Back");

        TableView<YearlyEmployeeReportDTO> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<YearlyEmployeeReportDTO, String> idCol = new TableColumn<>("Employee ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmployeeId()));

        TableColumn<YearlyEmployeeReportDTO, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName()));

        TableColumn<YearlyEmployeeReportDTO, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));

        TableColumn<YearlyEmployeeReportDTO, Number> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getYear()));

        TableColumn<YearlyEmployeeReportDTO, String> salaryCol = new TableColumn<>("Total Net Salary");
        salaryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTotalNetSalary().toString()));

        TableColumn<YearlyEmployeeReportDTO, Number> leaveCol = new TableColumn<>("Total Leave Taken");
        leaveCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTotalLeaveTaken()));

        table.getColumns().setAll(idCol, firstNameCol, lastNameCol, yearCol, salaryCol, leaveCol);

        generateBtn.setOnAction(e -> {
            try {
                if (yearField.getText().trim().isEmpty()) {
                    showAlert("Please enter a year.");
                    return;
                }

                int year = Integer.parseInt(yearField.getText().trim());

                YearlyReportDTO report = service.generateYearlyReport(year);

                if (report == null || report.getEmployeeReports() == null) {
                    table.setItems(FXCollections.observableArrayList());
                    showAlert("No yearly report data found.");
                    return;
                }

                table.setItems(FXCollections.observableArrayList(report.getEmployeeReports()));

            } catch (NumberFormatException ex) {
                showAlert("Please enter a valid year.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Failed to load yearly report: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> {
            stage.setScene(new Scene(new HRDashboardView(stage, service), 800, 600));
        });

        getChildren().addAll(title, yearField, generateBtn, table, backBtn);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Yearly Report");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}