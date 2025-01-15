package productorconsumidor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfazGrafica extends JFrame {

    private Productor productor;
    private Consumidor consumidor;
    private Buffer buffer;
    
    private JPanel panelBuffer;
    private JLabel estadoProductorLabel;
    private JLabel estadoConsumidorLabel;
    private JButton iniciarButton;
    private JButton pausarButton;
    private JButton detenerButton;

    private static final int BUFFER_SIZE = 5;

    public InterfazGrafica() {
        buffer = new Buffer(BUFFER_SIZE);
        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        setTitle("Productor Consumidor");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior con el estado de los procesos
        JPanel panelEstado = new JPanel();
        panelEstado.setLayout(new GridLayout(2, 1));

        estadoProductorLabel = new JLabel("Estado del Productor: Inactivo");
        estadoConsumidorLabel = new JLabel("Estado del Consumidor: Inactivo");

        panelEstado.add(estadoProductorLabel);
        panelEstado.add(estadoConsumidorLabel);

        // Panel central para el buffer con cuadros flotantes
        panelBuffer = new JPanel();
        panelBuffer.setLayout(new GridLayout(1, BUFFER_SIZE, 10, 10));
        panelBuffer.setPreferredSize(new Dimension(500, 100));
        actualizarBuffer();

        // Panel inferior con los botones de control
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout());

        iniciarButton = new JButton("Iniciar");
        pausarButton = new JButton("Pausar");
        detenerButton = new JButton("Detener");

        pausarButton.setEnabled(false);
        detenerButton.setEnabled(false);

        panelBotones.add(iniciarButton);
        panelBotones.add(pausarButton);
        panelBotones.add(detenerButton);

        // Eventos de botones
        iniciarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (productor == null || !productor.isAlive()) {
                    productor = new Productor(buffer, InterfazGrafica.this);
                    productor.start();
                }
                if (consumidor == null || !consumidor.isAlive()) {
                    consumidor = new Consumidor(buffer, InterfazGrafica.this);
                    consumidor.start();
                }

                iniciarButton.setEnabled(false);
                pausarButton.setEnabled(true);
                detenerButton.setEnabled(true);
            }
        });

        pausarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pausarButton.getText().equals("Pausar")) {
                    productor.pausar();
                    consumidor.pausar();
                    pausarButton.setText("Reanudar");
                } else {
                    productor.reanudar();
                    consumidor.reanudar();
                    pausarButton.setText("Pausar");
                }
            }
        });

        detenerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productor.detener();
                consumidor.detener();
                iniciarButton.setEnabled(true);
                pausarButton.setEnabled(false);
                detenerButton.setEnabled(false);
                pausarButton.setText("Pausar");
            }
        });

        // Añadir los componentes al JFrame
        add(panelEstado, BorderLayout.NORTH);
        add(panelBuffer, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public void actualizarBuffer() {
        panelBuffer.removeAll();
        for (int i = 0; i < BUFFER_SIZE; i++) {
            JPanel cuadro = new JPanel();
            cuadro.setPreferredSize(new Dimension(60, 60));
            cuadro.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            cuadro.setLayout(new BorderLayout());

            JLabel letraLabel = new JLabel(buffer.getBuffer()[i] != '\0' ? String.valueOf(buffer.getBuffer()[i]) : "", SwingConstants.CENTER);
            letraLabel.setFont(new Font("Arial", Font.BOLD, 20));
            letraLabel.setOpaque(true);
            letraLabel.setBackground(buffer.getBuffer()[i] != '\0' ? Color.GREEN : Color.WHITE);

            JLabel estadoLabel = new JLabel(buffer.getBuffer()[i] != '\0' ? "Ocupado" : "Vacío", SwingConstants.CENTER);

            cuadro.add(letraLabel, BorderLayout.CENTER);
            cuadro.add(estadoLabel, BorderLayout.SOUTH);

            panelBuffer.add(cuadro);
        }
        panelBuffer.revalidate();
        panelBuffer.repaint();
    }

    public void actualizarEstadoProductor(String estado) {
        estadoProductorLabel.setText("Estado del Productor: " + estado);
    }

    public void actualizarEstadoConsumidor(String estado) {
        estadoConsumidorLabel.setText("Estado del Consumidor: " + estado);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InterfazGrafica().setVisible(true);
            }
        });
    }
}
