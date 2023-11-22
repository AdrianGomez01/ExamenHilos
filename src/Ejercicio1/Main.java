package Ejercicio1;

class GestorCruce {
    private boolean semaforoNorteVerde = true;
    private boolean semaforoOesteVerde = false;
    //Compruebo que el semáforo no esté cambiando para que no se meta ningún coche si es así
    private boolean cambiando = false;
    private int cochesPasando = 0;

    public synchronized void llegaNorte() {
        while (!semaforoNorteVerde || cambiando || cochesPasando > 0) {
            try {
                // Espero hasta que el semáforo Norte esté verde y no haya cambios o coches pasando
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        cochesPasando++;
        // El coche pasa el cruce
        System.out.println("Coche norte pasa el cruce");
    }

    public synchronized void llegaOeste() {
        while (!semaforoOesteVerde || cambiando || cochesPasando > 0) {
            try {
                // Espero hasta que el semáforo Oeste esté verde y no haya cambios o coches pasando
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // El coche pasa el cruce
        cochesPasando++;
        System.out.println("Coche oeste pasa el cruce");
    }

    public synchronized void sale() {
        System.out.println("Coche sale del cruce");
        // El coche sale del cruce
        cochesPasando--;
        if (cochesPasando == 0) {
            // Despierta a todos los hilos (coches) en espera cuando el coche ha salido
            notifyAll();
        }
    }

    public synchronized void cambiaSemaforos() {
        cambiando = true;

        // Cambia los colores de los semáforos
        semaforoNorteVerde = !semaforoNorteVerde;
        semaforoOesteVerde = !semaforoOesteVerde;

        //Indico los cambios de los semáforos para tener una ayuda visual en consola de que se está ejecutando correctamente
        if (semaforoNorteVerde) {
            System.out.println("Semáforo norte en verde");
        } else {
            System.out.println("Semáforo norte en rojo");
        }

        if (semaforoOesteVerde) {
            System.out.println("Semáforo oeste en verde");
        } else {
            System.out.println("Semáforo oeste en rojo");
        }

        cambiando = false;
        // Despierta a todos los hilos (coches) en espera
        notifyAll();
    }
}



class Coche implements Runnable {
    private GestorCruce gestorCruce;
    private String direccion;

    //Los coches solo tendrán una direccion a la que van y su gestor.
    public Coche(GestorCruce gestorCruce, String direccion) {
        this.gestorCruce = gestorCruce;
        this.direccion = direccion;
    }

    @Override
    public void run() {

        //Compruebo la dirección a la que va el coche para llamar a su método correspondiente
        if (direccion.equals("norte")) {
            gestorCruce.llegaNorte();
        } else if (direccion.equals("oeste")) {
            gestorCruce.llegaOeste();
        }

        // Simulación de coche pasando por el cruce
        try {
            // Tiempo que tarda en pasar el cruce
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        gestorCruce.sale();
    }
}

class ControladorSemaforos implements Runnable {
    private GestorCruce gestorCruce;

    public ControladorSemaforos(GestorCruce gestorCruce) {
        this.gestorCruce = gestorCruce;
    }

    @Override
    public void run() {
        while (true) {
            // Cambia los semáforos cada cierto tiempo
            gestorCruce.cambiaSemaforos();
            try {
                // Tiempo que tarda en cambiar el semáforo (6 seg)
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        GestorCruce gestorCruce = new GestorCruce();
        Thread controlador = new Thread(new ControladorSemaforos(gestorCruce));
        controlador.start();

        // Crear 5 coches que vienen del norte
        for (int i = 0; i < 5; i++) {
            Thread cocheNorte = new Thread(new Coche(gestorCruce, "norte"));
            cocheNorte.start();
        }

        // Crear 3 coches que vienen del oeste
        for (int i = 0; i < 3; i++) {
            Thread cocheOeste = new Thread(new Coche(gestorCruce, "oeste"));
            cocheOeste.start();
        }
    }
}
