import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClienteRMI extends Remote {
    void iniciarProceso() throws RemoteException;
}
