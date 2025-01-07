import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ServidorRMIImpl extends UnicastRemoteObject implements ServidorRMI {
    private final Map<String, ClienteRMI> clientes = new HashMap<>();

    public ServidorRMIImpl() throws RemoteException {
        super();
        System.out.println("[Servidor] Servidor RMI inicializado.");
    }

    @Override
    public synchronized void registrarCliente(String nombre, ClienteRMI cliente) throws RemoteException {
        clientes.put(nombre, cliente);
        System.out.println("[Servidor] Cliente registrado: " + nombre);
    }

    @Override
    public synchronized void enviarTiempo(String cliente, long tiempo) throws RemoteException {
        System.out.println("[Servidor] Tiempo recibido de " + cliente + ": " + tiempo + " ms");
    }

    @Override
    public synchronized void iniciarProcesos() throws RemoteException {
        System.out.println("[Servidor] Enviando orden de inicio a todos los clientes...");
        for (Map.Entry<String, ClienteRMI> entry : clientes.entrySet()) {
            String cliente = entry.getKey();
            try {
                entry.getValue().iniciarProceso();
                System.out.println("[Servidor] Orden de inicio enviada a: " + cliente);
            } catch (RemoteException e) {
                System.err.println("[Servidor] Error al enviar orden a: " + cliente);
                e.printStackTrace();
            }
        }
    }
}
