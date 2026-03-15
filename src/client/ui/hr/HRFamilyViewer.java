package client.ui.hr;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.models.FamilyMember;
import shared.services.HRMService;

import java.util.List;

public class HRFamilyViewer {

    public static Scene create(Stage stage, HRMService service) {
        Label title = new Label("View Employee Family Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label instruction = new Label("Enter Employee ID to view family members");

        TextField employeeIdField = new TextField();
        employeeIdField.setPromptText("Employee ID (e.g. EMP02)");

        Button loadBtn = new Button("Load");
        Button backBtn = new Button("Back");

        TableView<FamilyMember> table = new TableView<>();
        table.setPrefHeight(400);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<FamilyMember, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<FamilyMember, String> relationshipCol = new TableColumn<>("Relationship");
        relationshipCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRelationship()));

        TableColumn<FamilyMember, String> icCol = new TableColumn<>("IC/Passport");
        icCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIcPassport()));

        TableColumn<FamilyMember, String> dobCol = new TableColumn<>("Date of Birth");
        dobCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateOfBirth()));

        TableColumn<FamilyMember, String> contactCol = new TableColumn<>("Contact Number");
        contactCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContactNumber()));

        table.getColumns().addAll(nameCol, relationshipCol, icCol, dobCol, contactCol);

        loadBtn.setOnAction(e -> {
            String employeeId = employeeIdField.getText() == null ? "" : employeeIdField.getText().trim();

            if (employeeId.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Employee ID", "Please enter an employee ID.");
                return;
            }

            try {
                List<FamilyMember> members = service.getFamilyDetails(employeeId);
                table.getItems().clear();

                if (members == null || members.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "No Data",
                            "No family details found for employee ID: " + employeeId);
                } else {
                    table.getItems().addAll(members);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load family details: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> {
            stage.setScene(new Scene(new HRDashboardView(stage, service), 600, 400));
        });

        HBox topRow = new HBox(10, employeeIdField, loadBtn, backBtn);

        VBox root = new VBox(15, title, instruction, topRow, table);
        root.setPadding(new Insets(20));

        return new Scene(root, 850, 550);
    }

    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}