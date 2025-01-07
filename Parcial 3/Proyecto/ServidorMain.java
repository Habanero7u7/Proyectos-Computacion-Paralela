import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServidorMain {
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.err.println("Uso: java ServidorMain <puerto>");
                System.exit(1);
            }

            int puerto = Integer.parseInt(args[0]);
            LocateRegistry.createRegistry(puerto); // Crear el registro en el puerto especificado

            String ip = InetAddress.getLocalHost().getHostAddress(); // Obtener la IP local

            // Crear GUI del servidor
            ServitorGUI gui = new ServitorGUI();

            // Crear instancia del servidor RMI con referencia a la GUI
            ServidorRMIImpl servidor = new ServidorRMIImpl(gui);

            // Registrar el servidor en el RMI Registry
            Naming.rebind("rmi://" + ip + ":" + puerto + "/ServidorFractales", servidor);

            System.out.println("[Servidor] Servidor iniciado en: " + ip + ":" + puerto);
        } catch (Exception e) {
            System.err.println("[Servidor] Error al iniciar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
