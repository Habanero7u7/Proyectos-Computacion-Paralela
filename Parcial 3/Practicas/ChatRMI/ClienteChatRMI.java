import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ClienteChatRMI extends UnicastRemoteObject implements ClienteCallback {
    // **CONEXIÓN AL SERVIDOR**: referencia remota al servidor
    private static MiInterfazRemota servidor;
    private String nombre;

    // Componentes de la interfaz gráfica del cliente
    private JTextArea areaChat;
    private JTextField campoMensaje;
    private JComboBox<String> clientesLista;

    // **CONSTRUCTOR DEL CLIENTE**: configuración de la GUI y registro en el servidor
    public ClienteChatRMI(String nombre) throws RemoteException {
        this.nombre = nombre;
        System.out.println("[Cliente] Cliente inicializado con nombre: " + nombre);

        // Configuración de la ventana principal
        JFrame frame = new JFrame("Cliente Chat RMI - " + nombre);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        areaChat = new JTextArea();
        areaChat.setEditable(false);
        frame.add(new JScrollPane(areaChat), BorderLayout.CENTER);

        // Panel inferior con campo de mensaje, botón de enviar y lista de clientes
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BorderLayout());
        campoMensaje = new JTextField();
        JButton botonEnviar = new JButton("Enviar");

        // **LISTA DE CLIENTES**: inicializada con "Todos" por defecto
        clientesLista = new JComboBox<>(new String[]{"Todos"});

        panelInferior.add(clientesLista, BorderLayout.WEST);
        panelInferior.add(campoMensaje, BorderLayout.CENTER);
        panelInferior.add(botonEnviar, BorderLayout.EAST);

        frame.add(panelInferior, BorderLayout.SOUTH);
        frame.setVisible(true);

        // **ENVÍO DE MENSAJES AL SERVIDOR**
        botonEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String mensaje = campoMensaje.getText();
                    String destino = (String) clientesLista.getSelectedItem();

                    if (destino == null) {
                        System.out.println("[Cliente] No se seleccionó un destino.");
                        return;
                    }

                    if (destino.equals("Todos")) {
                        // **MENSAJE DE BROADCAST**: enviar a todos los clientes
                        servidor.enviarMensaje(nombre + ": " + mensaje);
                        System.out.println("[Cliente] Mensaje enviado a todos: " + mensaje);
                    } else {
                        // **MENSAJE DIRECTO**: enviar a un cliente específico
                        servidor.enviarMensajeDirecto(destino, nombre + ": " + mensaje);
                        System.out.println("[Cliente] Mensaje directo enviado a " + destino + ": " + mensaje);
                    }
                    campoMensaje.setText("");
                } catch (Exception ex) {
                    System.out.println("[Cliente] Error al enviar mensaje: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }

    // **MENSAJES RECIBIDOS DEL SERVIDOR**: actualiza el área de chat
    @Override
    public void recibirMensaje(String mensaje) throws RemoteException {
        System.out.println("[Cliente] Mensaje recibido: " + mensaje);
        areaChat.append(mensaje + "\n");
    }

    // **ACTUALIZACIÓN DE LISTA DE CLIENTES**: muestra la lista actualizada de clientes conectados
    @Override
    public void actualizarListaClientes(List<String> clientes) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            clientesLista.removeAllItems();
            clientesLista.addItem("Todos");
            for (String cliente : clientes) {
                if (!cliente.equals(nombre)) {
                    clientesLista.addItem(cliente);
                }
            }
            System.out.println("[Cliente] Lista de clientes actualizada: " + clientes);
        });
    }

    // **CONEXIÓN AL SERVIDOR**: establecer conexión y registro
    public static void main(String[] args) {
        try {
            String host = args[0];
            int puerto = Integer.parseInt(args[1]);
            String nombre = args[2];

            System.out.println("[Cliente] Conectando al servidor en " + host + ":" + puerto);
            servidor = (MiInterfazRemota) Naming.lookup("//" + host + ":" + puerto + "/ServidorChatRMI");
            ClienteChatRMI cliente = new ClienteChatRMI(nombre);

            // **REGISTRO DEL CLIENTE EN EL SERVIDOR**
            servidor.registrarCliente(nombre, cliente);
            System.out.println("[Cliente] Cliente registrado en el servidor");
            cliente.areaChat.append("Conectado al servidor...\n");

            // **OBTENCIÓN DE LA LISTA DE CLIENTES**
            List<String> listaClientes = servidor.obtenerClientes();
            cliente.actualizarListaClientes(listaClientes);
        } catch (Exception e) {
            System.out.println("[Cliente] Error al conectar con el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
