import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ClientGUI {
    private final JFrame frame;
    private final JButton iniciarButton;
    private final JLabel fractalLabel;
    private BufferedImage fractalImage;

    public ClientGUI(ClienteRMIImpl cliente) {
        frame = new JFrame("Cliente RMI - " + cliente.getNombre());
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel para mostrar el fractal
        fractalImage = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
        fractalLabel = new JLabel(new ImageIcon(fractalImage));
        mainPanel.add(fractalLabel, BorderLayout.CENTER);

        // Botón para iniciar el proceso
        iniciarButton = new JButton("Iniciar");
        iniciarButton.addActionListener(e -> {
            try {
                System.out.println("[GUI - " + cliente.getNombre() + "] Botón 'Iniciar' presionado.");
                cliente.iniciarProceso();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        mainPanel.add(iniciarButton, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public void generarFractal() {
        int width = 800;
        int height = 800;
        double xMin = -2.0;
        double xMax = 1.0;
        double yMin = -1.5;
        double yMax = 1.5;
        int maxIterations = 1000;

        System.out.println("[GUI] Generando fractal...");
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double zx = xMin + x * (xMax - xMin) / width;
                double zy = yMin + y * (yMax - yMin) / height;
                int iterations = computeJuliaPoint(zx, zy, maxIterations);

                // Mapeo de colores
                int color = mapToColor(iterations, maxIterations);
                fractalImage.setRGB(x, y, color);
            }
        }
        SwingUtilities.invokeLater(() -> fractalLabel.repaint());
        System.out.println("[GUI] Fractal generado.");
    }

    private int computeJuliaPoint(double zx, double zy, int maxIterations) {
        double cx = 0.36;
        double cy = -0.11;
        int iterations = 0;

        while (zx * zx + zy * zy < 4 && iterations < maxIterations) {
            double temp = zx * zx - zy * zy + cx;
            zy = 2.0 * zx * zy + cy;
            zx = temp;
            iterations++;
        }

        return iterations;
    }

    private int mapToColor(int iterations, int maxIterations) {
        if (iterations == maxIterations) {
            return Color.BLACK.getRGB(); // Fondo negro
        }

        // Gradiente de color
        float t = (float) iterations / maxIterations;
        int red = (int) (9 * (1 - t) * t * t * t * 255);
        int green = (int) (15 * (1 - t) * (1 - t) * t * t * 255);
        int blue = (int) (8.5 * (1 - t) * (1 - t) * (1 - t) * t * 255);
        return new Color(red, green, blue).getRGB();
    }
}
