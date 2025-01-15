package cenafilosofica;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

public class GUI extends JFrame {

    private Mesa mesa;
    private ArrayList<Filosofo> filosofos;
    private JTextArea[] estadosFilosofos;
    private JTextArea[] estadosTenedores;
    private JButton iniciarBtn, detenerBtn;
    private boolean ejecutando = false;

    public GUI() {
        // Configuración de la ventana
        setTitle("Cena de los Filósofos");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);  // Centrar la ventana

        // Establecer panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Panel central para mostrar el estado de los filósofos y los tenedores
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new GridLayout(2, 1, 10, 10));

        // Área de texto para mostrar los estados de los filósofos
        JPanel panelFilosofos = new JPanel();
        panelFilosofos.setLayout(new GridLayout(1, 5, 10, 10));
        estadosFilosofos = new JTextArea[5];
        for (int i = 0; i < 5; i++) {
            estadosFilosofos[i] = new JTextArea("Filósofo " + (i + 1) + ": Pensando");
            estadosFilosofos[i].setEditable(false);
            estadosFilosofos[i].setBackground(new Color(230, 230, 255));
            estadosFilosofos[i].setFont(new Font("Arial", Font.PLAIN, 14));
            estadosFilosofos[i].setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            panelFilosofos.add(estadosFilosofos[i]);
        }
        panelCentro.add(panelFilosofos);

        // Área de texto para mostrar el estado de los tenedores
        JPanel panelTenedores = new JPanel();
        panelTenedores.setLayout(new GridLayout(1, 5, 10, 10));
        estadosTenedores = new JTextArea[5];
        for (int i = 0; i < 5; i++) {
            estadosTenedores[i] = new JTextArea("Tenedor " + (i + 1) + ": Libre");
            estadosTenedores[i].setEditable(false);
            estadosTenedores[i].setBackground(new Color(255, 255, 230));
            estadosTenedores[i].setFont(new Font("Arial", Font.PLAIN, 14));
            estadosTenedores[i].setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            panelTenedores.add(estadosTenedores[i]);
        }
        panelCentro.add(panelTenedores);

        mainPanel.add(panelCentro, BorderLayout.CENTER);

        // Panel inferior con botones de control
        JPanel panelControl = new JPanel();
        panelControl.setLayout(new FlowLayout());

        iniciarBtn = new JButton("Iniciar");
        iniciarBtn.setFont(new Font("Arial", Font.BOLD, 14));
        iniciarBtn.setBackground(new Color(144, 238, 144));
        iniciarBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        detenerBtn = new JButton("Detener");
        detenerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        detenerBtn.setBackground(new Color(240, 128, 128));
        detenerBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        panelControl.add(iniciarBtn);
        panelControl.add(detenerBtn);

        mainPanel.add(panelControl, BorderLayout.SOUTH);

        // Acción del botón iniciar
        iniciarBtn.addActionListener(e -> {
            if (!ejecutando) {
                iniciarSimulacion();
            }
        });

        // Acción del botón detener
        detenerBtn.addActionListener(e -> detenerSimulacion());
    }

    public void iniciarSimulacion() {
        mesa = new Mesa(5);
        filosofos = new ArrayList<>();
        ejecutando = true;

        for (int i = 0; i < 5; i++) {
            final int indice = i;
            Filosofo f = new Filosofo(mesa, i + 1) {
                @Override
                public void run() {
                    while (ejecutando) {
                        pensando();
                        actualizarEstadoFilosofo(indice, "Pensando", Color.YELLOW);
                        mesa.cogerTenedores(indice);
                        comiendo();
                        actualizarEstadoFilosofo(indice, "Comiendo", Color.GREEN);
                        System.out.println("Filósofo " + (indice + 1) + " está comiendo con tenedores " +
                                (mesa.tenedorIzquierda(indice) + 1) + " y " + (mesa.tenedorDerecha(indice) + 1));
                        mesa.dejarTenedores(indice);
                    }
                }

                @Override
                public void pensando() {
                    super.pensando();
                    actualizarEstadoFilosofo(indice, "Pensando", Color.YELLOW);
                }

                @Override
                public void comiendo() {
                    super.comiendo();
                    actualizarEstadoFilosofo(indice, "Comiendo", Color.GREEN);
                    actualizarEstadoTenedores();
                }
            };
            filosofos.add(f);
            f.start();
        }
    }

    public void detenerSimulacion() {
        ejecutando = false;
        System.out.println("Simulación detenida.");
    }

    // Actualiza el estado visual de los filósofos con color
    public void actualizarEstadoFilosofo(int filosofo, String estado, Color color) {
        SwingUtilities.invokeLater(() -> {
            estadosFilosofos[filosofo].setText("Filósofo " + (filosofo + 1) + ": " + estado);
            estadosFilosofos[filosofo].setBackground(color);
        });
    }

    // Actualiza el estado visual de los tenedores
    public void actualizarEstadoTenedores() {
        for (int i = 0; i < 5; i++) {
            final int tenedorIzq = mesa.tenedorIzquierda(i);
            final int tenedorDer = mesa.tenedorDerecha(i);
            SwingUtilities.invokeLater(() -> {
                estadosTenedores[tenedorIzq].setText("Tenedor " + (tenedorIzq + 1) + ": " + (mesa.estaOcupado(tenedorIzq) ? "Ocupado" : "Libre"));
                estadosTenedores[tenedorIzq].setBackground(mesa.estaOcupado(tenedorIzq) ? Color.RED : new Color(255, 255, 230));
                estadosTenedores[tenedorDer].setText("Tenedor " + (tenedorDer + 1) + ": " + (mesa.estaOcupado(tenedorDer) ? "Ocupado" : "Libre"));
                estadosTenedores[tenedorDer].setBackground(mesa.estaOcupado(tenedorDer) ? Color.RED : new Color(255, 255, 230));
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.setVisible(true);
        });
    }
}
