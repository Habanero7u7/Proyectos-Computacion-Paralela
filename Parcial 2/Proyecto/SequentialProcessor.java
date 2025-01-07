public class SequentialProcessor implements FractalProcessor {
    @Override
    public void generateFractal(int width, int height, double xMin, double xMax, double yMin, double yMax, int maxIterations, int[][] result) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double x = xMin + i * (xMax - xMin) / width;
                double y = yMin + j * (yMax - yMin) / height;
                result[i][j] = FractalGenerator.computePoint(x, y, maxIterations);
            }
        }
    }
}
