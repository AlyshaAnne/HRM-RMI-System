package client.ui.hr;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.dto.LoginResultDTO;
import shared.services.HRMService;

public class LoginView {

    public static Scene create(Stage stage, HRMService service) {
        Label title = new Label("HRM Login");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label statusLabel = new Label();

        Button loginBtn = new Button("Login");

        ProgressIndicator loading = new ProgressIndicator();
        loading.setVisible(false);
        loading.setMaxSize(20, 20);

        // Put button + spinner on same line (nicer)
        HBox actionRow = new HBox(10, loginBtn, loading);

        Hyperlink forgotLink = new Hyperlink("Forgot password?");
        forgotLink.setOnAction(e -> showForgotPasswordDialog(service, statusLabel));

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
            String password = passwordField.getText() == null ? "" : passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter username and password.");
                return;
            }

            // UI loading state
            setLoading(true, loginBtn, loading, usernameField, passwordField, forgotLink);
            statusLabel.setText("");

            Task<LoginResultDTO> task = new Task<>() {
                @Override
                protected LoginResultDTO call() throws Exception {
                    return service.login(username, password);
                }
            };

            task.setOnSucceeded(ev -> {
                setLoading(false, loginBtn, loading, usernameField, passwordField, forgotLink);

                LoginResultDTO result = task.getValue();
                if (result != null && result.isSuccess()) {
                    statusLabel.setText("✅ " + safe(result.getMessage()));

                    String role = safe(result.getRole());

                    // Route by role (placeholder scenes for now)
                    if ("HR".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
                        stage.setScene(makePlaceholderScene("HR/Admin Dashboard (placeholder)\nRole: " + role));
                    } else {
                        stage.setScene(makePlaceholderScene("Employee Dashboard (placeholder)\nRole: " + role));
                    }
                } else {
                    statusLabel.setText(result == null ? "Login failed." : safe(result.getMessage()));
                }
            });

            task.setOnFailed(ev -> {
                setLoading(false, loginBtn, loading, usernameField, passwordField, forgotLink);
                Throwable ex = task.getException();
                statusLabel.setText("Server error: " + (ex == null ? "Unknown error" : ex.getMessage()));
            });

            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        });

        VBox root = new VBox(12, title, usernameField, passwordField, actionRow, forgotLink, statusLabel);
        root.setPadding(new Insets(20));
        root.setPrefWidth(380);

        return new Scene(root, 380, 260);
    }

    private static void showForgotPasswordDialog(HRMService service, Label statusLabel) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Forgot Password");
        dialog.setHeaderText("Submit a reset request to HR/Admin");

        // Inputs
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full name (as registered)");

        TextField employeeIdField = new TextField();
        employeeIdField.setPromptText("Employee ID (e.g., EMP001)");

        VBox content = new VBox(10,
                new Label("Full name:"),
                fullNameField,
                new Label("Employee ID:"),
                employeeIdField);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK)
                return;

            String fullName = fullNameField.getText() == null ? "" : fullNameField.getText().trim();
            String employeeId = employeeIdField.getText() == null ? "" : employeeIdField.getText().trim();

            if (fullName.isEmpty() || employeeId.isEmpty()) {
                statusLabel.setText("Full name and Employee ID are required.");
                return;
            }

            // Call RMI method (simple call; can be async later if you want)
            try {
                String msg = service.submitPasswordResetRequest(fullName, employeeId);
                statusLabel.setText(msg == null ? "Request submitted." : msg);
            } catch (Exception ex) {
                statusLabel.setText("Failed to submit request: " + ex.getMessage());
            }
        });
    }

    private static void setLoading(
            boolean isLoading,
            Button loginBtn,
            ProgressIndicator loading,
            TextField usernameField,
            PasswordField passwordField,
            Hyperlink forgotLink) {
        loginBtn.setDisable(isLoading);
        usernameField.setDisable(isLoading);
        passwordField.setDisable(isLoading);
        forgotLink.setDisable(isLoading);
        loading.setVisible(isLoading);
    }

    private static Scene makePlaceholderScene(String text) {
        Label label = new Label(text);
        VBox root = new VBox(10, label);
        root.setPadding(new Insets(20));
        return new Scene(root, 520, 320);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
