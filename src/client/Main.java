package client;

import client.ui.hr.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shared.services.HRMService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        HRMService service = (HRMService) registry.lookup("HRMService");

        Scene scene = LoginView.create(stage, service);

        stage.setTitle("HRM System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
