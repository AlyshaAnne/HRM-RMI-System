package client.ui.hr;

import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import client.ui.hr.LoginView;

import shared.dto.ResetRequestDTO;
import shared.services.HRMService;

import java.rmi.RemoteException;
import java.util.List;

public class ResetRequestsView extends BorderPane {

    private final HRMService service;

    private final TableView<ResetRequestDTO> table = new TableView<>();
    private final ObservableList<ResetRequestDTO> data = FXCollections.observableArrayList();

    public ResetRequestsView(Stage stage, HRMService service) {
        this.service = service;

        setPadding(new Insets(15));

        // --- Table columns ---
        TableColumn<ResetRequestDTO, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getRequestId()).asObject());
        colId.setPrefWidth(60);

        TableColumn<ResetRequestDTO, String> colTime = new TableColumn<>("Request Time");
        colTime.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getRequestTime().toString()));
        colTime.setPrefWidth(200);

        TableColumn<ResetRequestDTO, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFullName()));
        colName.setPrefWidth(180);

        TableColumn<ResetRequestDTO, String> colEmpId = new TableColumn<>("Employee ID");
        colEmpId.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEmployeeId()));
        colEmpId.setPrefWidth(120);

        TableColumn<ResetRequestDTO, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(
                cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus()));
        colStatus.setPrefWidth(120);

        table.getColumns().setAll(java.util.List.of(colId, colTime, colName, colEmpId, colStatus));
        table.setItems(data);

        setCenter(table);

        // --- Buttons ---
        Button btnRefresh = new Button("Refresh");
        Button btnApprove = new Button("Approve");
        Button btnReject = new Button("Reject");
        Button btnBack = new Button("Back");

        btnRefresh.setOnAction(e -> loadRequests());

        btnApprove.setOnAction(e -> updateSelected("APPROVED"));
        btnReject.setOnAction(e -> updateSelected("REJECTED"));

        btnBack.setOnAction(e -> stage.setScene(LoginView.create(stage, service)));

        HBox actions = new HBox(10, btnBack, btnRefresh, btnApprove, btnReject);
        actions.setPadding(new Insets(10, 0, 0, 0));
        setBottom(actions);

        // Load at start
        loadRequests();
    }

    private void loadRequests() {
        try {
            List<ResetRequestDTO> list = service.getResetRequests();
            table.getItems().setAll(list);
        } catch (Exception ex) {
            ex.printStackTrace(); // shows full error in terminal

            String msg = ex.toString();
            if (ex.getCause() != null) {
                msg += "\nCause: " + ex.getCause().toString();
            }

            showError("Failed to load reset requests", msg);
        }
    }

    private void updateSelected(String newStatus) {
        ResetRequestDTO selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Please select a request first.");
            return;
        }

        try {
            boolean ok = service.updateResetRequestStatus(selected.getRequestId(), newStatus);
            if (ok) {
                loadRequests();
                showInfo("Updated", "Request " + selected.getRequestId() + " set to " + newStatus);
            } else {
                showInfo("Not updated", "No row updated. Maybe the request ID doesn't exist.");
            }
        } catch (RemoteException e) {
            showError("Failed to update status", e.getMessage());
        }
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Info");
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();

    }
}