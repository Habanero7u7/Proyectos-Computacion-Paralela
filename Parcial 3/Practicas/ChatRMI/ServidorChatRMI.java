import java.net.InetAddress;
import java.net.UnknownHostException;
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
    private final Map<String, String> clienteDirecciones = new HashMap<>();
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
    public synchronized void registrarCliente(String nombreYDireccion, ClienteCallback cliente) throws RemoteException {
        String[] partes = nombreYDireccion.split("#");
        if (partes.length != 2) {
            System.out.println("[Servidor] Error: Formato inválido para el cliente: " + nombreYDireccion);
            return;
        }

        String nombre = partes[0];
        String direccion = partes[1];

        System.out.println("[Servidor] Nuevo cliente registrado: " + nombre + " en " + direccion);

        // Registrar el cliente y su dirección
        clientes.put(nombre, cliente);
        clienteDirecciones.put(nombre, direccion);

        // Notificar a todos los clientes conectados
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
        List<String> listaClientes = new ArrayList<>();

        // Combinar el nombre y la dirección de cada cliente
        for (Map.Entry<String, String> entry : clienteDirecciones.entrySet()) {
            listaClientes.add(entry.getKey() + "#" + entry.getValue());
        }

        System.out.println("[Servidor] Lista de clientes solicitada: " + listaClientes);
        return listaClientes;
    }

    // **ACTUALIZACIÓN DE LA GUI DEL SERVIDOR**
    private void actualizarListaClientesEnGUI() {
        gui.actualizarListaClientes(new ArrayList<>(clientes.keySet()));
    }

    // **NOTIFICACIÓN A LOS CLIENTES SOBRE ACTUALIZACIONES**
    private void notificarClientesActualizados() throws RemoteException {
        List<String> listaClientes = obtenerClientes(); // Lista en formato nombre:direccion
        for (ClienteCallback cliente : clientes.values()) {
            cliente.actualizarListaClientes(listaClientes);
        }
    }

    // **CONEXIÓN INICIAL DEL SERVIDOR**
    public static void main(String[] args) {
        try {
            int puerto = Integer.parseInt(args[0]);

            // Crear el registro RMI en el puerto especificado
            LocateRegistry.createRegistry(puerto);

            // Obtener la dirección IP de la máquina donde se ejecuta el servidor
            String direccionServidor = InetAddress.getLocalHost().getHostAddress();
            ServidorChatRMI servidor = new ServidorChatRMI();

            // Registrar el servidor en el RMI Registry
            Naming.rebind("//" + direccionServidor + ":" + puerto + "/ServidorChatRMI", servidor);

            System.out.println("[Servidor] Registrado en RMI Registry con nombre: ServidorChatRMI");
            System.out.println("[Servidor] Dirección del servidor: " + direccionServidor + ":" + puerto);
        } catch (UnknownHostException e) {
            System.out.println("[Servidor] Error al obtener la dirección IP: " + e.getMessage());
        } catch (RemoteException e) {
            System.out.println("[Servidor] Error al crear el registro RMI: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Servidor] Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
