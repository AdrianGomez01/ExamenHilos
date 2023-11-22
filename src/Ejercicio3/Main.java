package Ejercicio3;

class Sincronizador {
    private int numPatas = 0;
    private int numTableros = 0;
    private int numMesas = 0;
    private final int MAX_NUM_PATAS = 10;
    private final int MAX_NUM_TABLEROS = 5;
    private final int MAX_NUM_MESAS = 5;

    /**
     * Este método añade una pata si es posible y notifica al ensamblador por si cumple las condiciones para crear una mesa.
     */
    public synchronized void ponPata() {
        //Si el numero de patas llega al máximo espera.
        while (numPatas == MAX_NUM_PATAS) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        numPatas++;
        System.out.println("Se ha depositado una pata. Total de patas: " + numPatas);
        // Notifico al ensamblador
        notifyAll();
    }


    /**
     * Este método añade un tablero si es posible y notifica al ensamblador por si cumple las condiciones para crear una mesa.
     */
    public synchronized void ponTablero() {
        //Si el numero de tableros llega al máximo espera.
        while (numTableros == MAX_NUM_TABLEROS) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        numTableros++;
        System.out.println("Se ha depositado un tablero. Total de tableros: " + numTableros);
        // Notifico al ensamblador
        notifyAll();
    }

    /**
     * Este método ensambla una mesa si hay disponibles 4 patas y 1 tablero, le resta a sus valores lo correspondiente
     * y notifica al los fabricantes para que fabriquen otra vez si es necesario. Si llega a 5 mesas para de ensamblar.
     */
    public synchronized void cogePatasyTablero() {
        while (numPatas < 4 || numTableros < 1 || numMesas == MAX_NUM_MESAS) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        numPatas -= 4;
        numTableros--;
        numMesas++;
        System.out.println("Mesa ensamblada. Total de mesas: " + numMesas);
        // Notifico al los fabricantes
        notifyAll();
    }


}

class FabricantePatas implements Runnable {
    private Sincronizador sincronizador;

    public FabricantePatas(Sincronizador sincronizador) {
        this.sincronizador = sincronizador;
    }

    @Override
    public void run() {
        while (true) {
            try {
                sincronizador.ponPata();
                //Añado 1 segundos al tiempo de ejecución para poder ver claro la ejecución por consola
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class FabricanteTableros implements Runnable {
    private Sincronizador sincronizador;

    public FabricanteTableros(Sincronizador sincronizador) {
        this.sincronizador = sincronizador;
    }

    @Override
    public void run() {
        while (true) {
            try {
                sincronizador.ponTablero();
                //Añado 2 segundos al tiempo de ejecución para poder ver claro la ejecución por consola
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class EnsambladorMesas implements Runnable {
    private Sincronizador sincronizador;

    public EnsambladorMesas(Sincronizador sincronizador) {
        this.sincronizador = sincronizador;
    }

    @Override
    public void run() {
        while (true) {
            //A las mesas no les añado tiempo de ejecución para que se ensamblen en cuanto se cumplan las condiciones.
            sincronizador.cogePatasyTablero();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Sincronizador sincronizador = new Sincronizador();

        Thread fabricantePatas = new Thread(new FabricantePatas(sincronizador));
        Thread fabricanteTableros = new Thread(new FabricanteTableros(sincronizador));
        Thread ensambladorMesas = new Thread(new EnsambladorMesas(sincronizador));

        fabricantePatas.start();
        fabricanteTableros.start();
        ensambladorMesas.start();

    }
}
