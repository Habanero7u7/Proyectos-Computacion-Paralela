public class FractalGenerator {
    private static final double CX = -0.36; // Parte real de c
    private static final double CY = 0.11; // Parte imaginaria de c

    public static int computePoint(double x, double y, int maxIterations) {
        double zx = x;
        double zy = y;
        int iterations = 0;

        while (zx * zx + zy * zy < 4 && iterations < maxIterations) {
            double temp = zx * zx - zy * zy + CX;
            zy = 2.0 * zx * zy + CY;
            zx = temp;
            iterations++;
        }

        return iterations;
    }
}
