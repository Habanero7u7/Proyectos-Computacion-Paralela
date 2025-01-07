import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class FractalFrame extends JFrame {
    private BufferedImage fractalImage;
    private int width = 1000;
    private int height = 1000;
    private int maxIterations = 5000;
    private FractalProcessor currentProcessor;
    private final HashMap<String, FractalProcessor> processors = new HashMap<>();
    private JTable timeTable;
    private DefaultTableModel tableModel;
    private JLabel fractalLabel;

    private volatile boolean isRunning;
    private volatile boolean isPaused;
    private Thread fractalThread;

    private volatile double cx = -0.8;
    private volatile double cy = 0.156;

    private ServidorRMI servidor; // Referencia al servidor RMI
    private String clienteNombre; // Nombre del cliente
    private JButton startButton; // Referencia al botón de inicio

    public FractalFrame(String clienteNombre, String servidorHost) {
        this.clienteNombre = clienteNombre;
        try {
            servidor = (ServidorRMI) Naming.lookup("rmi://" + servidorHost + "/ServidorFractales");
            servidor.registrarCliente(clienteNombre, new ClienteRMIImpl());
            System.out.println("[Cliente - " + clienteNombre + "] Registrado exitosamente en el servidor.");
        } catch (Exception e) {
            System.err.println("[Cliente - " + clienteNombre + "] Error al conectarse al servidor: " + e.getMessage());
            e.printStackTrace();
        }

        setTitle("Fractal Generator - Cliente: " + clienteNombre);
        setSize(1500, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel de fractales
        fractalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        fractalLabel = new JLabel(new ImageIcon(fractalImage));
        add(fractalLabel, BorderLayout.CENTER);

        // Panel de control (lado derecho)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 50);

        JLabel processorLabel = new JLabel("Selecciona procesamiento:");
        JComboBox<String> processorSelector = new JComboBox<>(new String[]{"Secuencial", "Concurrente", "Paralelo"});

        startButton = new JButton("Iniciar");
        JButton stopButton = new JButton("Detener");
        JButton continueButton = new JButton("Continuar");

        stopButton.setEnabled(false);
        continueButton.setEnabled(false);

        // Botones y selector
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        controlPanel.add(processorLabel, gbc);

        gbc.gridy = 1;
        controlPanel.add(processorSelector, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        controlPanel.add(startButton, gbc);

        gbc.gridx = 1;
        controlPanel.add(stopButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        controlPanel.add(continueButton, gbc);

        add(controlPanel, BorderLayout.EAST);

        // Tabla de tiempos (abajo)
        tableModel = new DefaultTableModel(new String[]{"Procesamiento", "Tiempo (ms)"}, 0);
        timeTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(timeTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 100));
        add(tableScrollPane, BorderLayout.SOUTH);

        // Acciones de botones
        startButton.addActionListener(e -> {
            String selected = (String) processorSelector.getSelectedItem();
            currentProcessor = processors.get(selected);
            clearFractalImage();
            startFractalGeneration(startButton, stopButton, continueButton);
        });

        stopButton.addActionListener(e -> {
            isPaused = true;
            stopButton.setEnabled(false);
            continueButton.setEnabled(true);
        });

        continueButton.addActionListener(e -> {
            isPaused = false;
            synchronized (fractalThread) {
                fractalThread.notify();
            }
            stopButton.setEnabled(true);
            continueButton.setEnabled(false);
        });

        // Inicializar procesadores
        processors.put("Secuencial", new SequentialProcessor());
        processors.put("Concurrente", new ConcurrentProcessor());
        processors.put("Paralelo", new ParallelProcessor());
    }

    private void clearFractalImage() {
        Graphics2D g = fractalImage.createGraphics();
        g.setColor(new Color(75, 0, 130));
        g.fillRect(0, 0, width, height);
        g.dispose();
        fractalLabel.repaint();
    }

    private void startFractalGeneration(JButton startButton, JButton stopButton, JButton continueButton) {
        isRunning = true;
        isPaused = false;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        continueButton.setEnabled(false);

        fractalThread = new Thread(() -> {
            int[][] result = new int[width][height];
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < width && isRunning; i++) {
                synchronized (fractalThread) {
                    while (isPaused) {
                        try {
                            fractalThread.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

                for (int j = 0; j < height && isRunning; j++) {
                    double x = -2.0 + i * (3.0) / width;
                    double y = -1.5 + j * (3.0) / height;
                    result[i][j] = computeJuliaPoint(x, y);
                }
                updatePartialFractal(result, i);
            }

            if (isRunning) {
                long endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;

                tableModel.addRow(new Object[]{currentProcessor.getClass().getSimpleName(), executionTime});

                // Enviar tiempo al servidor
                try {
                    servidor.enviarTiempo(clienteNombre, executionTime);
                    System.out.println("[Cliente - " + clienteNombre + "] Tiempo enviado al servidor: " + executionTime + " ms.");
                } catch (RemoteException e) {
                    System.err.println("[Cliente - " + clienteNombre + "] Error al enviar tiempo al servidor: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            SwingUtilities.invokeLater(() -> {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                continueButton.setEnabled(false);
            });
        });

        fractalThread.start();
    }

    private int computeJuliaPoint(double x, double y) {
        double zx = x;
        double zy = y;
        int iterations = 300;

        while (zx * zx + zy * zy < 4 && iterations < maxIterations) {
            double temp = zx * zx - zy * zy + cx;
            zy = 2.0 * zx * zy + cy;
            zx = temp;
            iterations++;
        }

        return iterations;
    }

    private void updatePartialFractal(int[][] result, int currentRow) {
        for (int i = 0; i <= currentRow; i++) {
            for (int j = 0; j < height; j++) {
                int value = result[i][j];

                int color = mapToCustomColor(value);

                fractalImage.setRGB(i, j, color);
            }
        }
        fractalLabel.repaint();
    }

    private int mapToCustomColor(int value) {
        if (value == maxIterations) {
            return new Color(75, 0, 130).getRGB();
        }
        float t = (float) value / maxIterations;
        int red = (int) (9 * (1 - t) * t * t * t * 255);
        int green = (int) (15 * (1 - t) * (1 - t) * t * t * 255);
        int blue = (int) (8.5 * (1 - t) * (1 - t) * (1 - t) * t * 255);

        return new Color(red, green, blue).getRGB();
    }

    private class ClienteRMIImpl extends UnicastRemoteObject implements ClienteRMI {
        public ClienteRMIImpl() throws RemoteException {
            super();
        }

        @Override
        public void iniciarProceso() throws RemoteException {
            System.out.println("[Cliente - " + clienteNombre + "] Orden de inicio recibida del servidor.");
            SwingUtilities.invokeLater(() -> startButton.doClick());
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java FractalFrame <nombreCliente> <servidorHost>");
            System.exit(1);
        }

        String clienteNombre = args[0];
        String servidorHost = args[1];

        SwingUtilities.invokeLater(() -> new FractalFrame(clienteNombre, servidorHost).setVisible(true));
    }
}
