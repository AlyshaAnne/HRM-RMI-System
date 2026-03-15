package client.ui.hr;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.services.HRMService;

public class HRDashboardView extends VBox {

    public HRDashboardView(Stage stage, HRMService service) {
        setSpacing(15);
        setPadding(new Insets(20));

        Button registerEmployeeBtn = new Button("Register Employee");
        Button resetRequestsBtn = new Button("View Reset Requests");
        Button logoutBtn = new Button("Logout");
        Button manageEmployeesBtn = new Button("Manage Employee Information");
        Button viewFamilyBtn = new Button("View Employee Family Details");
        Button viewProfileBtn = new Button("View Employee Profile Details");
        Button viewLeaveHistoryBtn = new Button("View Leave History");
        Button monthlyReportBtn = new Button("View Monthly Report");
        Button yearlyReportBtn = new Button("View Yearly Report");
        Button manageAccountsBtn = new Button("Manage Accounts");

        getChildren().addAll(
                registerEmployeeBtn,
                manageEmployeesBtn,
                manageAccountsBtn,
                viewProfileBtn,
                viewFamilyBtn,
                viewLeaveHistoryBtn,
                monthlyReportBtn,
                yearlyReportBtn,
                resetRequestsBtn,
                logoutBtn);

        registerEmployeeBtn.setOnAction(e -> {
            stage.setScene(new Scene(new RegisterEmployeeView(stage, service), 500, 600));
        });

        resetRequestsBtn.setOnAction(e -> {
            stage.setScene(new Scene(new ResetRequestsView(stage, service), 900, 600));
        });

        logoutBtn.setOnAction(e -> {
            stage.setScene(LoginView.create(stage, service));
        });

        manageEmployeesBtn.setOnAction(e -> {
            new ManageEmployeesView(service).show();
        });

        viewFamilyBtn.setOnAction(e -> {
            stage.setScene(HRFamilyViewer.create(stage, service));
        });

        viewProfileBtn.setOnAction(e -> {
            stage.setScene(new Scene(new ViewEmployeeProfileView(stage, service), 700, 500));
        });

        viewLeaveHistoryBtn.setOnAction(e -> {
            stage.setScene(new Scene(new ViewLeaveHistoryView(stage, service), 1000, 600));
        });

        monthlyReportBtn.setOnAction(e -> {
            System.out.println("Monthly report button clicked");
            stage.setScene(new Scene(new MonthlyReportView(stage, service), 1300, 700));
        });

        yearlyReportBtn.setOnAction(e -> {
            stage.setScene(new Scene(new YearlyReportView(stage, service), 700, 500));
        });

        manageAccountsBtn.setOnAction(e -> {
            stage.setScene(new Scene(new ManageAccountsView(stage, service), 900, 550));
        });

    }
}