import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClienteRMIImpl extends UnicastRemoteObject implements ClienteRMI {
    private final ClientGUI gui;
    private final ServidorRMI servidor;
    private final String nombre;

    public ClienteRMIImpl(String nombre, ServidorRMI servidor) throws RemoteException {
        super();
        this.nombre = nombre;
        this.servidor = servidor;
        this.gui = new ClientGUI(this);

        servidor.registrarCliente(nombre, this);
        System.out.println("[Cliente - " + nombre + "] Registrado en el servidor.");
    }

    @Override
    public void iniciarProceso() throws RemoteException {
        System.out.println("[Cliente - " + nombre + "] Recibida orden de inicio.");
        long startTime = System.nanoTime();
        gui.generarFractal(); // Generar el fractal en la GUI
        long endTime = System.nanoTime();
        long tiempoEjecucion = (endTime - startTime) / 1_000_000; // Convertir a ms
        System.out.println("[Cliente - " + nombre + "] Fractal generado en: " + tiempoEjecucion + " ms.");
        servidor.enviarTiempo(nombre, tiempoEjecucion); // Enviar tiempo al servidor
        System.out.println("[Cliente - " + nombre + "] Tiempo enviado al servidor.");
    }

    public String getNombre() {
        return nombre;
    }
}
