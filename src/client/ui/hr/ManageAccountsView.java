package client.ui.hr;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.services.HRMService;

public class ManageAccountsView extends VBox {

    public ManageAccountsView(Stage stage, HRMService service) {
        setSpacing(15);
        setPadding(new Insets(20));

        Label title = new Label("Manage Accounts");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        Button deactivateBtn = new Button("Deactivate Account");
        Button activateBtn = new Button("Activate Account");
        Button backBtn = new Button("Back");

        deactivateBtn.setOnAction(e -> {
            try {
                String username = usernameField.getText().trim();

                if (username.isEmpty()) {
                    showAlert("Please enter a username.");
                    return;
                }

                boolean success = service.setAccountActive(username, false);

                if (success) {
                    showAlert("Account deactivated successfully.");
                } else {
                    showAlert("Failed to deactivate account. Username may not exist.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error: " + ex.getMessage());
            }
        });

        activateBtn.setOnAction(e -> {
            try {
                String username = usernameField.getText().trim();

                if (username.isEmpty()) {
                    showAlert("Please enter a username.");
                    return;
                }

                boolean success = service.setAccountActive(username, true);

                if (success) {
                    showAlert("Account activated successfully.");
                } else {
                    showAlert("Failed to activate account. Username may not exist.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> {
            stage.setScene(new Scene(new HRDashboardView(stage, service), 800, 600));
        });

        getChildren().addAll(title, usernameField, deactivateBtn, activateBtn, backBtn);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Manage Accounts");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}