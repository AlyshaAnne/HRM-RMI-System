package client.ui.hr;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.models.LeaveApplication;
import shared.services.HRMService;

import java.rmi.RemoteException;
import java.util.List;

public class LeaveApprovalView extends VBox {

    public LeaveApprovalView(Stage stage, HRMService service) {
        setSpacing(10);
        setPadding(new Insets(15));

        Label title = new Label("Pending Leave Applications");

        TableView<LeaveApplication> table = new TableView<>();

        TableColumn<LeaveApplication, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(
                c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());

        TableColumn<LeaveApplication, String> empCol = new TableColumn<>("Employee");
        empCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmployeeId()));

        TableColumn<LeaveApplication, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLeaveTypeName()));

        TableColumn<LeaveApplication, String> startCol = new TableColumn<>("Start");
        startCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStartDate()));

        TableColumn<LeaveApplication, String> endCol = new TableColumn<>("End");
        endCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEndDate()));

        TableColumn<LeaveApplication, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));

        table.getColumns().addAll(idCol, empCol, typeCol, startCol, endCol, statusCol);

        Button approveBtn = new Button("Approve");
        Button rejectBtn = new Button("Reject");
        Button backBtn = new Button("Back");

        approveBtn.setOnAction(e -> {
            LeaveApplication selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    service.updateLeaveApplicationStatus(selected.getId(), "Approved");
                    loadData(service, table);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        rejectBtn.setOnAction(e -> {
            LeaveApplication selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    service.updateLeaveApplicationStatus(selected.getId(), "Rejected");
                    loadData(service, table);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        backBtn.setOnAction(e -> {
            stage.setScene(new Scene(new HRDashboardView(stage, service), 500, 600));
        });

        loadData(service, table);

        getChildren().addAll(title, table, approveBtn, rejectBtn, backBtn);
    }

    private void loadData(HRMService service, TableView<LeaveApplication> table) {
        try {
            List<LeaveApplication> list = service.getAllPendingLeaveApplications();
            table.setItems(FXCollections.observableArrayList(list));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}