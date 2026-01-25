package client;

import shared.services.HRMService;
import shared.dto.LoginResultDTO;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HRMClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            HRMService service = (HRMService) registry.lookup("HRMService");

            LoginResultDTO res = service.login("hr", "1234");
            System.out.println(res.isSuccess() + " | " + res.getRole() + " | " + res.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
