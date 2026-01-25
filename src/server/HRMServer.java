package server;

import java.rmi.registry.LocateRegistry;

public class HRMServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            System.out.println("HRM RMI Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
