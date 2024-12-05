import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteChatRMI extends UnicastRemoteObject implements ClienteCallback, ClienteP2P {
    private static MiInterfazRemota servidor;
    private String nombre;
    private ClienteGUI gui;
    private int puertoLocal;

    // **Mapa de conexiones P2P (cliente a cliente)**
    private final Map<String, ClienteP2P> conexionesP2P = new HashMap<>();

    // Constructor
    public ClienteChatRMI(String nombre) throws RemoteException {
        super();
        this.nombre = nombre;
        this.gui = new ClienteGUI(this);

        try {
            // Generar un puerto dinámico y registrar el cliente en el RMI Registry
            String direccionCliente = InetAddress.getLocalHost().getHostAddress();
            puertoLocal = 2000 + (int) (Math.random() * 5000); // Puerto aleatorio en el rango 2000-6999
            LocateRegistry.createRegistry(puertoLocal);

            Naming.rebind("//" + direccionCliente + ":" + puertoLocal + "/" + nombre, this);
            System.out.println("[Cliente] Servidor P2P iniciado para este cliente en '" + direccionCliente + ":" + puertoLocal + "/"
                    + nombre + "'");
        } catch (Exception e) {
            System.out.println("[Cliente] Error al iniciar servidor P2P: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // **Método P2P: recibir mensajes directamente**
    @Override
    public void recibirMensajeDirecto(String mensaje) throws RemoteException {
        System.out.println("[Cliente] Mensaje directo recibido: " + mensaje);
        gui.agregarMensaje("[Directo] " + mensaje);
    }

    // **Método para enviar mensajes (directo o broadcast)**
    public void enviarMensaje(String destino, String mensaje) throws RemoteException {
        if (destino.equals("Todos")) {
            servidor.enviarMensaje(nombre + ": " + mensaje); // Broadcast al servidor principal
            System.out.println("[Cliente] Mensaje enviado a todos: " + mensaje);
        } else {
            // **Enviar mensaje directo a otro cliente (P2P)**
            ClienteP2P clienteDestino = conexionesP2P.get(destino);
            if (clienteDestino != null) {
                clienteDestino.recibirMensajeDirecto("[De " + nombre + "] " + mensaje);
                System.out.println("[Cliente] Mensaje directo enviado a " + destino + ": " + mensaje);
            } else {
                System.out.println("[Cliente] No se encontró conexión P2P con " + destino);
            }
        }
    }

    // **Conectar con otros clientes para P2P**
    public void conectarClienteP2P(String nombreCliente, String direccion) {
        try {
            ClienteP2P clienteRemoto = (ClienteP2P) Naming.lookup(direccion);
            conexionesP2P.put(nombreCliente, clienteRemoto);
            System.out.println("[Cliente] Conexión P2P establecida con " + nombreCliente + " en " + direccion);
        } catch (Exception e) {
            System.out.println("[Cliente] Error al conectar con cliente P2P " + nombreCliente + ": " + e.getMessage());
        }
    }

    // **Método llamado por el servidor para actualizar la lista de clientes**
    @Override
    public void actualizarListaClientes(List<String> clientes) throws RemoteException {
        gui.actualizarListaClientes(clientes);

        for (String clienteInfo : clientes) {
            String[] partes = clienteInfo.split("#");
            if (partes.length != 2) {
                System.out.println("[Cliente] Formato inválido en lista de clientes: " + clienteInfo);
                continue; // Ignorar entradas inválidas
            }

            String clienteNombre = partes[0];
            String clienteDireccion = partes[1];

            if (!clienteNombre.equals(nombre) && !conexionesP2P.containsKey(clienteNombre)) {
                conectarClienteP2P(clienteNombre, clienteDireccion);
            }
        }
    }

    // **Resto de métodos (recibir mensajes del servidor, getters, etc.)**
    @Override
    public void recibirMensaje(String mensaje) throws RemoteException {
        System.out.println("[Cliente] Mensaje recibido del servidor: " + mensaje);
        gui.agregarMensaje(mensaje);
    }

    public String getNombre() {
        return nombre;
    }

    // **Main: conexión inicial con el servidor principal**
    public static void main(String[] args) {
        try {
            String host = args[0];
            int puertoServidor = Integer.parseInt(args[1]);
            String nombre = args[2];

            System.out.println("[Cliente] Conectando al servidor principal en " + host + ":" + puertoServidor);
            servidor = (MiInterfazRemota) Naming.lookup("//" + host + ":" + puertoServidor + "/ServidorChatRMI");
            ClienteChatRMI cliente = new ClienteChatRMI(nombre);

            // Registrar cliente en el servidor principal
            servidor.registrarCliente(nombre + "#" + "//" + host + ":" + cliente.puertoLocal + "/" + nombre, cliente);
            System.out.println("[Cliente] Cliente registrado en el servidor principal");

            // Obtener lista inicial de clientes
            List<String> listaClientes = servidor.obtenerClientes();
            cliente.gui.actualizarListaClientes(listaClientes);

            cliente.gui.agregarMensaje("Conectado al servidor...");
        } catch (Exception e) {
            System.out.println("[Cliente] Error al conectar con el servidor principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
