package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HRMService extends Remote {
    void registerEmployee(Employee employee) throws RemoteException;

    Employee getEmployeeById(String employeeId) throws RemoteException;
}
