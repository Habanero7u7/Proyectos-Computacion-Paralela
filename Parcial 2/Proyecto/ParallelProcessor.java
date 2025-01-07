import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelProcessor implements FractalProcessor {
    private static class FractalTask extends RecursiveAction {
        private int start, end, height, maxIterations;
        private double xMin, xMax, yMin, yMax;
        private int[][] result;
        private int threshold;

        public FractalTask(int start, int end, int height, double xMin, double xMax, double yMin, double yMax, int maxIterations, int[][] result, int threshold) {
            this.start = start;
            this.end = end;
            this.height = height;
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
            this.maxIterations = maxIterations;
            this.result = result;
            this.threshold = threshold;
        }

        @Override
        protected void compute() {
            if (end - start <= threshold) { // Umbral dinámico
                for (int i = start; i < end; i++) {
                    for (int j = 0; j < height; j++) {
                        double x = xMin + i * (xMax - xMin) / result.length;
                        double y = yMin + j * (yMax - yMin) / height;
                        result[i][j] = FractalGenerator.computePoint(x, y, maxIterations);
                    }
                }
            } else {
                int mid = (start + end) / 2;
                invokeAll(new FractalTask(start, mid, height, xMin, xMax, yMin, yMax, maxIterations, result, threshold),
                        new FractalTask(mid, end, height, xMin, xMax, yMin, yMax, maxIterations, result, threshold));
            }
        }
    }

    @Override
    public void generateFractal(int width, int height, double xMin, double xMax, double yMin, double yMax, int maxIterations, int[][] result) {
        int threshold = width / Runtime.getRuntime().availableProcessors(); // Ajuste dinámico del umbral
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new FractalTask(0, width, height, xMin, xMax, yMin, yMax, maxIterations, result, threshold));
    }
}
