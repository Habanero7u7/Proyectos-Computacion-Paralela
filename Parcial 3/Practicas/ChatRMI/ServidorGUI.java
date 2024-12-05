import java.awt.BorderLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ServidorGUI {
    private JTextArea areaMensajes;
    private JList<String> listaClientes;
    private DefaultListModel<String> modeloListaClientes;

    public ServidorGUI() {
        JFrame frame = new JFrame("Servidor Chat RMI");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        areaMensajes = new JTextArea();
        areaMensajes.setEditable(false);
        JScrollPane scrollMensajes = new JScrollPane(areaMensajes);

        modeloListaClientes = new DefaultListModel<>();
        listaClientes = new JList<>(modeloListaClientes);
        JScrollPane scrollClientes = new JScrollPane(listaClientes);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollClientes, scrollMensajes);
        splitPane.setDividerLocation(150);

        frame.add(splitPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void agregarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> areaMensajes.append(mensaje + "\n"));
    }

    public void actualizarListaClientes(List<String> clientes) {
        SwingUtilities.invokeLater(() -> {
            modeloListaClientes.clear();
            for (String cliente : clientes) {
                modeloListaClientes.addElement(cliente);
            }
        });
    }
}
