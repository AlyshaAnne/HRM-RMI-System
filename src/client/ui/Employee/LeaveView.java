package client.ui.Employee;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.services.HRMService;
import shared.dto.LoginResultDTO;
public class LeaveView {
    public static Scene create(Stage stage, HRMService service, LoginResultDTO loginResult) {
        Label label = new Label("Leave View (Coming Soon)");
        Button back = new Button("Back");
        back.setOnAction(e ->
            stage.setScene(EmployeeDashboardView.create(stage, service, loginResult))
        );
        return new Scene(new VBox(20, label, back), 400, 300);
    }
}
