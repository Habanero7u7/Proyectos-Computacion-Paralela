public class Contenedor {
    private int dato;
    private boolean hayDato = false;

    public synchronized int get(){
        while(hayDato == false){
            try{
                // Espera a que el productor coloque un valor
                wait();
            } catch(InterruptedException e){ }
        }
        hayDato = false;
        // Notificar que el valor ha sido consumido
        notifyAll();
        return dato;
    }
    public synchronized void put(int valor){
        while(hayDato == true){
            try{
                // Espera a que se consuma el dato
                wait();
            } catch(InterruptedException e) { }
        }
        dato = valor;
        hayDato = true;
        // Notificar que ya hay dato.
        notifyAll();
    }
}