import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MergeSortComparison {
    public static void main(String[] args) {
        int[] sizes = {1000000000};
        System.out.println("Elementos\tTiempo Secuencial (ms)\tTiempo Concurrente(ms)");
        for (int size : sizes) {
            Integer[] array = generateRandomArray(size);
            // Ejecución secuencial
            Integer[] arrayCopy1 = Arrays.copyOf(array, array.length);
            long start = System.currentTimeMillis();
            sequentialMergeSort(arrayCopy1);
            long timeSequential = System.currentTimeMillis() - start;
            // Ejecución concurrente (paralela)
            Integer[] arrayCopy2 = Arrays.copyOf(array, array.length);
            start = System.currentTimeMillis();
            parallelMergeSort(arrayCopy2);
            long timeConcurrent = System.currentTimeMillis() - start;
            // resultados
            System.out.printf("%d\t\t%d\t\t\t\t%d%n", size, timeSequential,
                    timeConcurrent);
        }
    }

    // Genera un arreglo aleatorio de enteros
    private static Integer[] generateRandomArray(int size) {
        Random rand = new Random();
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = rand.nextInt(200001) - 100000; // Entre -100000 y 100000
        }
        return array;
    }

    // Implementación secuencial de Merge Sort
    public static void sequentialMergeSort(Integer[] array) {
        if (array.length <= 1)
            return;
        Integer[] helper = new Integer[array.length];
        mergeSort(array, helper, 0, array.length - 1);
    }

    private static void mergeSort(Integer[] array, Integer[] helper, int lo, int hi) {
        if (lo < hi) {
            int mid = (lo + hi) / 2;
            mergeSort(array, helper, lo, mid);
            mergeSort(array, helper, mid + 1, hi);
            merge(array, helper, lo, mid, hi);
        }
    }

    private static void merge(Integer[] array, Integer[] helper, int lo, int mid, int hi) {
        System.arraycopy(array, lo, helper, lo, hi - lo + 1);
        int left = lo, right = mid + 1, current = lo;
        while (left <= mid && right <= hi) {
            if (helper[left] <= helper[right]) {
                array[current++] = helper[left++];
            } else {
                array[current++] = helper[right++];
            }
        }
        while (left <= mid) {
            array[current++] = helper[left++];
        }
    }

    // Implementación paralela utilizando Fork/Join
    public static void parallelMergeSort(Integer[] array) {
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new MergeSortTask(array, new Integer[array.length], 0, array.length -
                1));
    }

    private static class MergeSortTask extends RecursiveAction {
        private static final int THRESHOLD = 500; // Tamaño mínimo para dividir
        private final Integer[] array;
        private final Integer[] helper;
        private final int lo, hi;

        public MergeSortTask(Integer[] array, Integer[] helper, int lo, int hi) {
            this.array = array;
            this.helper = helper;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected void compute() {
            if (hi - lo < THRESHOLD) {
                mergeSort(array, helper, lo, hi);
            } else {
                int mid = (lo + hi) / 2;
                MergeSortTask left = new MergeSortTask(array, helper, lo, mid);
                MergeSortTask right = new MergeSortTask(array, helper, mid + 1, hi);
                invokeAll(left, right);
                merge(array, helper, lo, mid, hi);
            }
        }
    }
}