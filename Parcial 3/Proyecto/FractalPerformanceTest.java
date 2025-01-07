import java.util.concurrent.TimeUnit;

public class FractalPerformanceTest {
    public static void main(String[] args) {
        // Configuraciones de prueba
        int[][] imageSizes = {{400, 400}, {800, 800}, {1000, 1000}}; // Tamaños de imagen
        int[] iterations = {500, 1000, 2000}; // Número de iteraciones

        // Procesadores
        FractalProcessor sequentialProcessor = new SequentialProcessor();
        FractalProcessor concurrentProcessor = new ConcurrentProcessor();
        FractalProcessor parallelProcessor = new ParallelProcessor();

        System.out.println("Iniciando pruebas de rendimiento...");
        System.out.printf("%-15s %-15s %-20s %-20s %-20s\n", "Tamaño", "Iteraciones", "Secuencial (ms)", "Concurrente (ms)", "Paralelo (ms)");

        // Ejecutar pruebas para cada configuración
        for (int[] size : imageSizes) {
            int width = size[0];
            int height = size[1];
            for (int iter : iterations) {
                int[][] result = new int[width][height];

                // Secuencial
                long sequentialTime = measureExecutionTime(() -> sequentialProcessor.generateFractal(
                        width, height, -2.0, 1.0, -1.5, 1.5, iter, result));

                // Concurrente
                long concurrentTime = measureExecutionTime(() -> concurrentProcessor.generateFractal(
                        width, height, -2.0, 1.0, -1.5, 1.5, iter, result));

                // Paralelo
                long parallelTime = measureExecutionTime(() -> parallelProcessor.generateFractal(
                        width, height, -2.0, 1.0, -1.5, 1.5, iter, result));

                // Imprimir resultados
                System.out.printf("%-15s %-15s %-20d %-20d %-20d\n",
                        width + "x" + height, iter, sequentialTime, concurrentTime, parallelTime);
            }
        }
    }

    /**
     * Mide el tiempo de ejecución de una tarea.
     *
     * @param task Tarea que se desea medir
     * @return Tiempo en milisegundos
     */
    private static long measureExecutionTime(Runnable task) {
        long startTime = System.nanoTime();
        task.run();
        long endTime = System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
    }
}
