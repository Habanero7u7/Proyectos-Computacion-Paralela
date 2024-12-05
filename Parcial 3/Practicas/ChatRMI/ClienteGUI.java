import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        clientesLista = new JComboBox<>(new String[]{"Todos"});

        panelInferior.add(clientesLista, BorderLayout.WEST);
        panelInferior.add(campoMensaje, BorderLayout.CENTER);
        panelInferior.add(botonEnviar, BorderLayout.EAST);

        frame.add(panelInferior, BorderLayout.SOUTH);
        frame.setVisible(true);

        // Acción del botón "Enviar"
        botonEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mensaje = campoMensaje.getText();
                String destino = (String) clientesLista.getSelectedItem();

                if (mensaje.isEmpty()) {
                    System.out.println("[ClienteGUI] Mensaje vacío. No se enviará.");
                    return;
                }

                if (destino != null) {
                    try {
                        cliente.enviarMensaje(destino, mensaje);
                        campoMensaje.setText("");
                    } catch (Exception ex) {
                        System.out.println("[ClienteGUI] Error al enviar mensaje: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("[ClienteGUI] No se seleccionó un destino.");
                }
            }
        });
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
            for (String cliente : clientes) {
                if (!cliente.equals(this.cliente.getNombre())) {
                    clientesLista.addItem(cliente);
                }
            }
        });
    }
}
