import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServidorMain {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            ServitorGUI gui = new ServitorGUI();
            ServidorRMIImpl servidor = new ServidorRMIImpl(gui);
            Naming.rebind("ServidorFractales", servidor);
            System.out.println("Servidor iniciado y registrado en RMI Registry.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
