public class Prac2P2HilosInterface implements Runnable {
    public void run() {
        for (int i = 0; i < 10; i++)
            System.out.println(i + " " +
                    Thread.currentThread().getName());
        System.out.println("Termina thread " +
                Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        new Thread(new Prac2P2HilosInterface(), "Pepe").start();
        new Thread(new Prac2P2HilosInterface(), "Juan").start();
        System.out.println("Termina thread main");
    }
}