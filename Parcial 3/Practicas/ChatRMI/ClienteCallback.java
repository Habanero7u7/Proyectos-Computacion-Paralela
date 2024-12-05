import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClienteCallback extends Remote {
    void recibirMensaje(String mensaje) throws RemoteException;
    void actualizarListaClientes(List<String> clientes) throws RemoteException;
}
