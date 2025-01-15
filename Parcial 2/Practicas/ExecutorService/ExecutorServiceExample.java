import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceExample {
   public static void main(String[] args) {
      ExecutorService executor = Executors.newFixedThreadPool(3);
      executor.execute(new ThreadExample("Hugo"));
      executor.execute(new ThreadExample("Paco"));
      executor.execute(new ThreadExample("Luis"));
      executor.shutdown();
   }
}

class ThreadExample implements Runnable {
   private String name;

   public ThreadExample(String name) {
      this.name = name;
   }

   public void run() {
      for (int i = 1; i <= 10; i++) {
         System.out.println("Running " + name + ": " + i);
      }
   }
}
