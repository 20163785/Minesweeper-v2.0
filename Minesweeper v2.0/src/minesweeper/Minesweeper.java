package minesweeper;

/**
 * Gytis Jankauskas
 * PRif-16/1
 * 20163785
 */
public class Minesweeper implements Runnable {

    GUI gui = new GUI();//sukuriamas gui

    public static void main(String[] args) {
        new Thread(new Minesweeper()).start(); //startuojant sukuria nauja thread metoda, sukuria nauja miinesweeper, kuris paleidzia run metoda 17 eilutej
    }

    @Override
    public void run() { //13 eilute
       while(true){
           gui.repaint();
       }
    }
    
}
