import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClienteChatRMI extends UnicastRemoteObject implements ClienteCallback {
    private static MiInterfazRemota servidor;
    private String nombre;
    private ClienteGUI gui;

    // Constructor
    public ClienteChatRMI(String nombre) throws RemoteException {
        this.nombre = nombre;
        this.gui = new ClienteGUI(this); // Vincula la interfaz gráfica
        System.out.println("[Cliente] Cliente inicializado con nombre: " + nombre);
    }

    // Método para enviar mensajes (directo o broadcast)
    public void enviarMensaje(String destino, String mensaje) throws RemoteException {
        if (destino.equals("Todos")) {
            servidor.enviarMensaje(nombre + ": " + mensaje);
            System.out.println("[Cliente] Mensaje enviado a todos: " + mensaje);
        } else {
            servidor.enviarMensajeDirecto(destino, nombre + ": " + mensaje);
            System.out.println("[Cliente] Mensaje directo enviado a " + destino + ": " + mensaje);
        }
    }

    // Método llamado por el servidor para recibir mensajes
    @Override
    public void recibirMensaje(String mensaje) throws RemoteException {
        System.out.println("[Cliente] Mensaje recibido: " + mensaje);
        gui.agregarMensaje(mensaje);
    }

    // Método llamado por el servidor para actualizar la lista de clientes
    @Override
    public void actualizarListaClientes(List<String> clientes) throws RemoteException {
        gui.actualizarListaClientes(clientes);
    }

    // Método para obtener el nombre del cliente
    public String getNombre() {
        return nombre;
    }

    // Método principal para la conexión con el servidor
    public static void main(String[] args) {
        try {
            String host = args[0];
            int puerto = Integer.parseInt(args[1]);
            String nombre = args[2];

            System.out.println("[Cliente] Conectando al servidor en " + host + ":" + puerto);
            servidor = (MiInterfazRemota) Naming.lookup("//" + host + ":" + puerto + "/ServidorChatRMI");
            ClienteChatRMI cliente = new ClienteChatRMI(nombre);

            // Registro del cliente en el servidor
            servidor.registrarCliente(nombre, cliente);
            System.out.println("[Cliente] Cliente registrado en el servidor");

            // Obtención inicial de la lista de clientes
            List<String> listaClientes = servidor.obtenerClientes();
            cliente.gui.actualizarListaClientes(listaClientes);

            cliente.gui.agregarMensaje("Conectado al servidor...");
        } catch (Exception e) {
            System.out.println("[Cliente] Error al conectar con el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
