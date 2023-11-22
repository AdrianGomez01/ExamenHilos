package Ejercicio2;

import java.util.concurrent.*;

class Monitor {

    //Creo semáforos para los recursos para que no se usen en más de un Hilo a la vez
    private Semaphore semaforoR1 = new Semaphore(1);
    private Semaphore semaforoR2 = new Semaphore(1);
    private Semaphore semaforoR3 = new Semaphore(1);

    /**
     * Compruebo en cada uno de los requiere los recursos que se necesitan para cada hilo, mostrando cuando lo ha obtenido
     * y cuáles ha obtenido.
     */
    public void requiereR1() {
        System.out.println(Thread.currentThread().getName() + ": Esperando por R1");
        try {
            semaforoR1.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ": Obtenido R1");
    }

    public void requiereR2_R3() {
        System.out.println(Thread.currentThread().getName() + ": Esperando por R2 y R3");
        try {
            semaforoR2.acquire();
            semaforoR3.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ": Obtenido R2 y R3");
    }

    public void requiereR1_R2_R3() {
        System.out.println(Thread.currentThread().getName() + ": Esperando por R1, R2 y R3");
        try {
            semaforoR1.acquire();
            semaforoR2.acquire();
            semaforoR3.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ": Obtenido R1, R2 y R3");
    }

    /**
     * Cuando usa un recurso un hilo lo libera para que lo use otro y lo muestra por pantalla
     */
    public void liberaR1() {
        System.out.println(Thread.currentThread().getName() + ": Libera R1");
        semaforoR1.release();
    }

    public void liberaR2_R3() {
        System.out.println(Thread.currentThread().getName() + ": Libera R2 y R3");
        semaforoR2.release();
        semaforoR3.release();
    }

    public void liberaR1_R2_R3() {
        System.out.println(Thread.currentThread().getName() + ": Libera R1, R2 y R3");
        semaforoR1.release();
        semaforoR2.release();
        semaforoR3.release();
    }
}

/**
 * Cada clase de tipo Hilo se ejecuta pidiendo los recursos necesarios a través del método requiereX() y los libera al terminar.
 */
class T1 implements Runnable {
    private Monitor monitor;

    public T1(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
        monitor.requiereR1();
        System.out.println(Thread.currentThread().getName() + ": Usa R1");
        // Aquí realizaría las operaciones con R1
        monitor.liberaR1();
    }
}

class T2 implements Runnable {
    private Monitor monitor;

    public T2(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
        monitor.requiereR2_R3();
        System.out.println(Thread.currentThread().getName() + ": Usa R2 y R3");
        // Aquí realizaría las operaciones con R2 y R3
        monitor.liberaR2_R3();
    }
}

class T3 implements Runnable {
    private Monitor monitor;

    public T3(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
        monitor.requiereR1_R2_R3();
        System.out.println(Thread.currentThread().getName() + ": Usa R1, R2 y R3");
        // Aquí realizaría las operaciones con R1, R2 y R3
        monitor.liberaR1_R2_R3();
    }
}

/**
 * En el main simplemente creo el monitor, los hilos y los ejecuto
 */
public class Main {
    public static void main(String[] args) {
        Monitor monitor = new Monitor();

        Thread t1 = new Thread(new T1(monitor), "T1");
        Thread t2 = new Thread(new T2(monitor), "T2");
        Thread t3 = new Thread(new T3(monitor), "T3");

        t1.start();
        t2.start();
        t3.start();
    }
}
