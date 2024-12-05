import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ClienteGUI {
    private JTextArea areaChat;
    private JTextField campoMensaje;
    private JComboBox<String> clientesLista;
    private ClienteChatRMI cliente;

    // Constructor que recibe una referencia al cliente para comunicarse con él
    public ClienteGUI(ClienteChatRMI cliente) {
        this.cliente = cliente;

        // Configuración de la ventana principal
        JFrame frame = new JFrame("Cliente Chat RMI - " + cliente.getNombre());
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

        // Lista desplegable de clientes (inicialmente vacía con "Todos")
        clientesLista = new JComboBox<>(new String[] { "Todos" });

        panelInferior.add(clientesLista, BorderLayout.WEST);
        panelInferior.add(campoMensaje, BorderLayout.CENTER);
        panelInferior.add(botonEnviar, BorderLayout.EAST);

        frame.add(panelInferior, BorderLayout.SOUTH);
        frame.setVisible(true);

        // **Evento para botón Enviar**
        botonEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje();
            }
        });

        // **Evento para tecla Enter**
        campoMensaje.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMensaje();
                }
            }
        });
    }

    // **Método para enviar mensajes**
    private void enviarMensaje() {
        try {
            String mensaje = campoMensaje.getText();
            String destino = (String) clientesLista.getSelectedItem();

            if (destino == null || mensaje.isEmpty()) {
                return; // No enviar si no hay destino o mensaje
            }

            if (destino.equals("Todos")) {
                cliente.enviarMensaje("Todos", mensaje);
            } else {
                cliente.enviarMensaje(destino, mensaje);
            }

            campoMensaje.setText(""); // Limpiar campo de mensaje
        } catch (Exception ex) {
            System.out.println("[GUI] Error al enviar mensaje: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Método para actualizar el área de chat
    public void agregarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> areaChat.append(mensaje + "\n"));
    }

    // Método para actualizar la lista de clientes
    public void actualizarListaClientes(List<String> clientes) {
        SwingUtilities.invokeLater(() -> {
            clientesLista.removeAllItems();
            clientesLista.addItem("Todos");
            for (String clienteInfo : clientes) {
                String[] partes = clienteInfo.split("#");
                if (partes.length == 2) {
                    String clienteNombre = partes[0];
                    if (!clienteNombre.equals(cliente.getNombre())) {
                        clientesLista.addItem(clienteNombre);
                    }
                }
            }
        });
    }

}
