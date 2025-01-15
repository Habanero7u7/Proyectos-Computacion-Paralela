public class Prac3P2HilosPrioridad implements Runnable {

    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i + " " + Thread.currentThread().getName());
        }
        System.out.println("Termina thread " + Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        // Crear hilos
        Thread hilo1 = new Thread(new Prac3P2HilosPrioridad(), "Pepe");
        Thread hilo2 = new Thread(new Prac3P2HilosPrioridad(), "Juan");

        // Establecer prioridades
        hilo1.setPriority(Thread.MAX_PRIORITY); // Máxima prioridad (10)
        hilo2.setPriority(Thread.MIN_PRIORITY); // Mínima prioridad (1)

        // Iniciar hilos
        hilo1.start();
        hilo2.start();

        System.out.println("Termina thread main");
    }
}
