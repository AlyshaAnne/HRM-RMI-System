package server;

import server.impl.HRMServiceImpl;
import shared.services.HRMService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HRMServer {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);

            HRMService service = new HRMServiceImpl();

            registry.rebind("HRMService", service);

            System.out.println("✅ HRM RMI Server running on port 1099");
            System.out.println("✅ Service bound as: HRMService");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
