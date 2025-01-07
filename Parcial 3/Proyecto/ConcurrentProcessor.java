import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentProcessor implements FractalProcessor {
    @Override
    public void generateFractal(int width, int height, double xMin, double xMax, double yMin, double yMax, int maxIterations, int[][] result) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < width; i++) {
            final int row = i;
            executor.submit(() -> {
                for (int j = 0; j < height; j++) {
                    double x = xMin + row * (xMax - xMin) / width;
                    double y = yMin + j * (yMax - yMin) / height;
                    result[row][j] = FractalGenerator.computePoint(x, y, maxIterations);
                }
            });
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
    }
}
