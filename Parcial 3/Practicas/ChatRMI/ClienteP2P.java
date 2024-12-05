import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClienteP2P extends Remote {
    void recibirMensajeDirecto(String mensaje) throws RemoteException;
}
