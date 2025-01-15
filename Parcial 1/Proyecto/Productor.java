package productorconsumidor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Productor extends Thread {

    private Buffer buffer;
    private final String letras = "abcdefghijklmnopqrstuvxyz";
    private boolean pausado = false;
    private boolean detenido = false;
    private final InterfazGrafica interfaz;

    public Productor(Buffer b, InterfazGrafica interfaz) {
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
            interfaz.actualizarEstadoProductor("Pausado");
            wait();
        }
    }

    @Override
    public void run() {
        while (!detenido) {
            try {
                esperarSiEstaPausado();
                char c = letras.charAt((int) (Math.random() * letras.length()));
                buffer.producir(c);
                System.out.println("Productor agrega el caracter: " + c + " en el buffer");
                interfaz.actualizarBuffer();
                interfaz.actualizarEstadoProductor("Agregando caracter: " + c);
                Thread.sleep((int) (Math.random() * 4000));
            } catch (InterruptedException ex) {
                Logger.getLogger(Productor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        interfaz.actualizarEstadoProductor("Detenido");
    }
}
