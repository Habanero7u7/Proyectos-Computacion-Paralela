import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MiClaseRemotaRMI extends UnicastRemoteObject implements MiInterfazRemotaRMI {
   public MiClaseRemotaRMI() throws RemoteException {
   }

   public void miMetodo1() throws RemoteException {
      System.out.println("Estoy en miMetodo1()");
   }

   public int miMetodo2() throws RemoteException {
      return 5;
   }

   public void otroMetodo() {
   }

   public static void main(String[] var0) {
      try {
         Registry var1 = LocateRegistry.createRegistry(Integer.parseInt(var0[0]));
         MiClaseRemotaRMI var2 = new MiClaseRemotaRMI();
         Naming.rebind("//" + InetAddress.getLocalHost().getHostAddress() + ":" + var0[0] + "/PruebaRMI", var2);
      } catch (Exception var3) {
         System.out.println(var3);
      }

   }
}