import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MiInterfazRemotaRMI extends Remote {
    public void miMetodo1() throws RemoteException;

    public int miMetodo2() throws RemoteException;
}