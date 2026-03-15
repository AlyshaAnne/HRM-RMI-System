package client.ui.hr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import shared.models.Employee;
import shared.services.HRMService;

import java.util.List;

public class ManageEmployeesView {

    private final HRMService hrmService;

    public ManageEmployeesView(HRMService hrmService) {
        this.hrmService = hrmService;
    }

    public void show() {
        Stage stage = new Stage();

        TableView<Employee> table = new TableView<>();
        ObservableList<Employee> employeeList = FXCollections.observableArrayList();

        TableColumn<Employee, String> idCol = new TableColumn<>("Employee ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmployeeId()));

        TableColumn<Employee, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName()));

        TableColumn<Employee, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));

        TableColumn<Employee, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<Employee, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));

        TableColumn<Employee, String> departmentCol = new TableColumn<>("Department");
        departmentCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartment()));

        TableColumn<Employee, String> positionCol = new TableColumn<>("Position");
        positionCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPosition()));

        table.getColumns().addAll(idCol, firstNameCol, lastNameCol, emailCol, phoneCol, departmentCol, positionCol);
        table.setItems(employeeList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button refreshBtn = new Button("Refresh");

        refreshBtn.setOnAction(e -> loadEmployees(employeeList));

        VBox root = new VBox(10, refreshBtn, table);
        root.setPadding(new Insets(15));

        loadEmployees(employeeList);

        Scene scene = new Scene(root, 900, 500);
        stage.setTitle("Manage Employees");
        stage.setScene(scene);
        stage.show();
    }

    private void loadEmployees(ObservableList<Employee> employeeList) {
        try {
            List<Employee> employees = hrmService.getAllEmployees();
            employeeList.setAll(employees);
        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load employees");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}