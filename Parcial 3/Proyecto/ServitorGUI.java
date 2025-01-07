import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

public class ServitorGUI {
    private final DefaultListModel<String> listaClientesModel = new DefaultListModel<>();
    private final Map<String, JLabel> tiempos = new HashMap<>();
    private final JPanel panelTiempos;

    public ServitorGUI() {
        JFrame frame = new JFrame("Servidor RMI - Generador de Fractales");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Lista de clientes
        JList<String> listaClientes = new JList<>(listaClientesModel);
        JScrollPane scrollClientes = new JScrollPane(listaClientes);

        // Panel para mostrar tiempos
        panelTiempos = new JPanel();
        panelTiempos.setLayout(new BoxLayout(panelTiempos, BoxLayout.Y_AXIS));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollClientes, panelTiempos);
        splitPane.setDividerLocation(150);

        frame.add(splitPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void agregarCliente(String nombre) {
        SwingUtilities.invokeLater(() -> {
            listaClientesModel.addElement(nombre);
            JLabel tiempoLabel = new JLabel(nombre + ": 0 ms");
            panelTiempos.add(tiempoLabel);
            tiempos.put(nombre, tiempoLabel);
            panelTiempos.revalidate();
        });
    }

    public void actualizarTiempo(String cliente, long tiempo) {
        SwingUtilities.invokeLater(() -> {
            JLabel tiempoLabel = tiempos.get(cliente);
            if (tiempoLabel != null) {
                tiempoLabel.setText(cliente + ": " + tiempo + " ms");
            }
        });
    }
}
