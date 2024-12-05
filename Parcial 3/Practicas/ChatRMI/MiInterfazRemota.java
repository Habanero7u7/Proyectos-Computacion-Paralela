import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface MiInterfazRemota extends Remote {
    void enviarMensaje(String mensaje) throws RemoteException;
    void registrarCliente(String nombre, ClienteCallback cliente) throws RemoteException;
    void enviarMensajeDirecto(String clienteDestino, String mensaje) throws RemoteException;
    List<String> obtenerClientes() throws RemoteException;
}
