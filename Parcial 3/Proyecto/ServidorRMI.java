import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServidorRMI extends Remote {
    void registrarCliente(String nombre, ClienteRMI cliente) throws RemoteException;
    void iniciarProcesos() throws RemoteException;
    void enviarTiempo(String cliente, long tiempo) throws RemoteException;
}
