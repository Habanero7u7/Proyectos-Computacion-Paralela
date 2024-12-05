import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServidorChatRMI extends UnicastRemoteObject implements MiInterfazRemota {
    // **ALMACÉN DE CLIENTES**: mapa de nombres y callbacks
    private final Map<String, ClienteCallback> clientes = new HashMap<>();
    private final ServidorGUI gui;

    // **CONSTRUCTOR DEL SERVIDOR**: inicialización de GUI y registro
    public ServidorChatRMI() throws RemoteException {
        super();
        this.gui = new ServidorGUI();
        System.out.println("[Servidor] Servidor iniciado correctamente");
    }

    // **ENVÍO DE MENSAJES A TODOS LOS CLIENTES (BROADCAST)**
    @Override
    public synchronized void enviarMensaje(String mensaje) throws RemoteException {
        gui.agregarMensaje("[Todos] " + mensaje);
        for (ClienteCallback cliente : clientes.values()) {
            cliente.recibirMensaje(mensaje);
        }
    }

    // **REGISTRO DE CLIENTES**
    @Override
    public synchronized void registrarCliente(String nombre, ClienteCallback cliente) throws RemoteException {
        System.out.println("[Servidor] Nuevo cliente registrado: " + nombre);
        clientes.put(nombre, cliente);
        actualizarListaClientesEnGUI();
        notificarClientesActualizados();
    }

    // **ENVÍO DE MENSAJES DIRECTOS ENTRE CLIENTES**
    @Override
    public synchronized void enviarMensajeDirecto(String clienteDestino, String mensaje) throws RemoteException {
        System.out.println("[Servidor] Intentando enviar mensaje directo a: " + clienteDestino);
        ClienteCallback cliente = clientes.get(clienteDestino);
        if (cliente != null) {
            cliente.recibirMensaje("[Mensaje directo de " + clienteDestino + "]: " + mensaje);
            gui.agregarMensaje("[Directo a " + clienteDestino + "] " + mensaje);
            System.out.println("[Servidor] Mensaje directo enviado a " + clienteDestino);
        } else {
            System.out.println("[Servidor] Cliente destino no encontrado");
        }
    }

    // **OBTENCIÓN DE LA LISTA DE CLIENTES**
    @Override
    public synchronized List<String> obtenerClientes() throws RemoteException {
        return new ArrayList<>(clientes.keySet());
    }

    // **ACTUALIZACIÓN DE LA GUI DEL SERVIDOR**
    private void actualizarListaClientesEnGUI() {
        gui.actualizarListaClientes(new ArrayList<>(clientes.keySet()));
    }

    // **NOTIFICACIÓN A LOS CLIENTES SOBRE ACTUALIZACIONES**
    private void notificarClientesActualizados() throws RemoteException {
        List<String> listaClientes = new ArrayList<>(clientes.keySet());
        for (ClienteCallback cliente : clientes.values()) {
            cliente.actualizarListaClientes(listaClientes);
        }
    }

    // **CONEXIÓN INICIAL DEL SERVIDOR**
    public static void main(String[] args) {
        try {
            int puerto = Integer.parseInt(args[0]);
            LocateRegistry.createRegistry(puerto);
            ServidorChatRMI servidor = new ServidorChatRMI();
            Naming.rebind("//" + InetAddress.getLocalHost().getHostAddress() + ":" + puerto + "/ServidorChatRMI", servidor);
            System.out.println("[Servidor] Registrado en RMI Registry con nombre: ServidorChatRMI");
        } catch (Exception e) {
            System.out.println("[Servidor] Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
