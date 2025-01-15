package productorconsumidor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Consumidor extends Thread {

    private Buffer buffer;
    private boolean pausado = false;
    private boolean detenido = false;
    private final InterfazGrafica interfaz;

    public Consumidor(Buffer b, InterfazGrafica interfaz) {
        this.buffer = b;
        this.interfaz = interfaz;
    }

    public synchronized void pausar() {
        pausado = true;
    }

    public synchronized void reanudar() {
        pausado = false;
        notify();
    }

    public synchronized void detener() {
        detenido = true;
        notify();
    }

    private synchronized void esperarSiEstaPausado() throws InterruptedException {
        while (pausado) {
            interfaz.actualizarEstadoConsumidor("Pausado");
            wait();
        }
    }

    @Override
    public void run() {
        while (!detenido) {
            try {
                esperarSiEstaPausado();
                char c = buffer.consumir();
                System.out.println("Consumidor retira el caracter: " + c + " del buffer");
                interfaz.actualizarBuffer();
                interfaz.actualizarEstadoConsumidor("Retirando caracter: " + c);
                Thread.sleep((int) (Math.random() * 4000));
            } catch (InterruptedException ex) {
                Logger.getLogger(Consumidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        interfaz.actualizarEstadoConsumidor("Detenido");
    }
}
