public class Prac1P2HilosHerencia extends Thread {
    public Prac1P2HilosHerencia(String str) {
        super(str);
    }

    public void run() {
        for (int i = 0; i < 10; i++)
            System.out.println(i + " " + getName());
        System.out.println("Termina thread " + getName());
    }

    public static void main(String[] args) {
        new Prac1P2HilosHerencia("Pepe").start();
        new Prac1P2HilosHerencia("Juan").start();
        System.out.println("Termina thread main");
    }
}