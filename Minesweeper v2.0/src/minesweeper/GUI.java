package minesweeper;

//importai funkciju ir pelytes
import javax.swing.*;//numato įvairius galimus vartotojo sąsajos komponentus, tokius kaip mygtukai, teksto laukai, lentelės, medžiai 
import java.util.*;//Contains the collections framework, legacy collection classes, event model
import java.awt.*;//pelyte pridedama 7, 8, 9
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

//jframe yra programos startavimo tipas
public class GUI extends JFrame {//GUI bus atidaromas JFrame metodu

    public boolean resetter = true;//grazinama ar programos nestabdyt laimejus ar pralaimejus

    public boolean flagger = false;//grazina, jog jei true, veliavos uzdejimo funkcija veikia visada

    int spacing = 10;//tarpai tarp langeliu pixeliais matuojami

    int neighs = 0;

    public int mx = -100;//koordinates 
    public int my = -100;

    public int flaggerX = 445; //veliavos vieta
    public int flaggerY = 5;

    public int flaggerCenterX = flaggerX + 35; //veliavos centras
    public int flaggerCenterY = flaggerY + 35;

    Random rand = new Random();

    int[][] mines = new int[16][9]; //sukuriamas masyvas, kiek bus kvadrateliu x ir y asyje
    int[][] neighbours = new int[16][9];//kaimyna
    boolean[][] revealed = new boolean[16][9];//masyvas, kuriame aprasyta, kuriuose langeliuose pasizymes pele nuvedus
    boolean[][] flagged = new boolean[16][9];//masyvas, kuriame aprasyta, kuriuose langeliuose pasizymes veliava

    
    //gui konfiguracija
    public GUI() {
        this.setTitle("Minesweeper");
        this.setSize(1286, 829);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//spaudziant X virsuj, uzdaro langa
        this.setVisible(true); //kad matytusi ir isijungtu
        this.setResizable(false);//negali dydzo keitaliot

        //bombu ir kaimynu aprasymas
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                if (rand.nextInt(100) < 20) {//jei random skaicius (0-99) maziau uz 20 langelyje buna  mina, jei, tai neubna
                    mines[i][j] = 1;
                } else {
                    mines[i][j] = 0;
                }
            }
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                neighs = 0;
                for (int m = 0; m < 16; m++) {//m, n kitas langelis, tai yra kaimynas
                    for (int n = 0; n < 9; n++) {//m, n kitas langelis, tai yra kaimynas
                        if (!(m == i && n == j)) {// jei m ir n sulyingus m su n buna priesingai, tai yra kaimynas
                            if (isN(i, j, m, n) == true) {
                                neighs++;
                            }
                        }
                    }
                }
                neighbours[i][j] = neighs;
            }
        }

        Board board = new Board();
        this.setContentPane(board);

        Move move = new Move();
        this.addMouseMotionListener(move);

        Click click = new Click();
        this.addMouseListener(click);
    }

    public class Board extends JPanel {//sukurta klase grafikai ir spalvinimui, ir kad tai vyktu paciam guie

        //spalvos langeliu, fono, minu, skaiciu ir tt
        @Override
        public void paint(Graphics g) {//ivedimui nebutinai turi buti g pasirinkta, gali but bet koks
            g.setColor(Color.lightGray);
            g.fillRect(0, 0, 1280, 800);//nudazo visa plota, skaiciuota pixeliais
            for (int i = 0; i < 16; i++) {//paiso kubelius x asyje
                for (int j = 0; j < 9; j++) {//paiso kubeliu y asyje
                    int spaccing = 0;//tarpai tarp langeiu dydizio paklaida yra 0
                    g.setColor(Color.gray);
                    if (revealed[i][j] == true) {//jei paspaudus ant kubelio, nusidazo baltai
                        g.setColor(Color.white);
                        if (mines[i][j] == 1) {// 75 eiil, jei ant paspausto kubelio yra bommba, nusidazo raudonnai
                            g.setColor(Color.red);
                        }
                    }
                    g.fillRect(spacing + i * 80, spacing + j * 80 + 80, 80 - 2 * spacing, 80 - 2 * spacing);
                    if (revealed[i][j] == true) {
                        g.setColor(Color.black);
                        if (mines[i][j] == 0){
                            g.setFont(new Font("Tahoma", Font.BOLD, 40));
                            g.drawString(Integer.toString(neighbours[i][j]), i * 80 + 27, j * 80 + 80 + 55);
                        } else {
                            g.fillRect(i * 80 + 10 + 20, j * 80 + 80 + 20, 20, 40);
                            g.fillRect(i * 80 + 20, j * 80 + 80 + 10 + 20, 40, 20);
                            g.fillRect(i * 80 + 5 + 20, j * 80 + 80 + 5 + 20, 30, 30);
                            g.fillRect(i * 80 + 38, j * 80 + 80 + 15, 4, 50);
                            g.fillRect(i * 80 + 15, j * 80 + 80 + 38, 50, 4);
                        }
                    }

                    //flagerio paveiksliukas ant kvadrateliu
                    if (flagged[i][j] == true) {
                        g.setColor(Color.black);
                        g.fillRect(i*80+32+5, j*80+80+15, 7, 40);
                        g.fillRect(i*80+20+5, j*80+80+50, 30, 10);
                        g.setColor(Color.red);
                        g.fillRect(i*80+16+5, j*80+80+15, 20, 15);
                        g.setColor(Color.black);
                        g.drawRect(i*80+16+5, j*80+80+15, 20, 15);
                        g.drawRect(i*80+17+5, j*80+80+16, 18, 13);
                        g.drawRect(i*80+18+5, j*80+80+17, 16, 11);
                    }

                }
            }



            //flagger paveikslas
            g.setColor(Color.lightGray);
            g.fillOval(flaggerX, flaggerY, 70, 70);
            g.setColor(Color.black);
            g.fillRect(flaggerX + 32, flaggerY + 10, 7, 40);
            g.fillRect(flaggerX + 20, flaggerY + 50, 30, 10);
            g.setColor(Color.red);
            g.fillRect(flaggerX + 16, flaggerY + 15, 20, 15);
            g.setColor(Color.black);
            g.drawRect(flaggerX + 16, flaggerY + 15, 20, 15);
            g.drawRect(flaggerX + 17, flaggerY + 16, 18, 13);
            g.drawRect(flaggerX + 18, flaggerY + 17, 16, 11);

            if (flagger == true) {
                g.setColor(Color.red);
            }

            g.drawOval(flaggerX, flaggerY, 70, 70);
            g.drawOval(flaggerX + 1, flaggerY + 1, 68, 68);
            g.drawOval(flaggerX + 2, flaggerY + 2, 66, 66);

        }
    }

    public class Move implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {//kai tempiama pele kazkoki objekta 

        }

        @Override
        public void mouseMoved(MouseEvent e) {//kai pelyte judinama
            mx = e.getX();
            my = e.getY();
            /*
            System.out.println("The mouse was moved!");
            System.out.println("X: " + mx + ", Y: " +my);
             */
        }

    }

    public class Click implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {

            mx = e.getX();//kad neglicintu smailikas, kai greitai nori paresetint zaidima
            my = e.getY();

            if (inBoxX() != -1 && inBoxY() != -1) {
                System.out.println("The mouse is in the [" + inBoxX() + ", " + inBoxY() + "], Number of neighs: " + neighbours[inBoxX()][inBoxY()]);
                if (flagger == true && revealed[inBoxX()][inBoxY()] == false) {
                    if (flagged[inBoxX()][inBoxY()] == false) {
                        flagged[inBoxX()][inBoxY()] = true;
                    } else {
                        flagged[inBoxX()][inBoxY()] = false;
                    }
                } else {
                    if (flagged[inBoxX()][inBoxY()] == false){
                    revealed[inBoxX()][inBoxY()] = true;
                    }
                }
            } else {
                System.out.println("The pointer is not inside of any box!");
            }

            if (inFlagger() == true) {
                if (flagger == false) {
                    flagger = true;
                    System.out.println("In flagger = true!");
                } else {
                    flagger = false;
                    System.out.println("In flagger = flase!");
                }
            }

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

    }

    // tikrina visu minu skaiciu ir grazina rezultata
    public int totalMines() {
        int total = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                if (mines[i][j] == 1) {
                    total++;
                }
            }
        }
        return total;
    }

    //skaiciuoja atskleistas dezutes
    public int totalBoxesRevealed() {
        int total = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                if (revealed[i][j] == true) {
                    total++;
                }
            }
        }
        return total;
    }

    public boolean inFlagger() {
        int dif = (int) Math.sqrt(Math.abs(mx - flaggerCenterX) * Math.abs(mx - flaggerCenterX) + Math.abs(my - flaggerCenterY) * Math.abs(my - flaggerCenterY));
        if (dif < 35) {
            return true;
        }
        return false;
    }

    //dezuciu vietos, jei pelyte ant dezute, pagrazina i ir j, tai yra kurioj vietoj  dezute
    public int inBoxX() {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                int spaccing = 0;
                if (mx >= spacing + i * 80 && mx < spacing + i * 80 + 80 - 2 * spacing && my >= spaccing + j * 80 + 80 + 26 && my < spacing + j * 80 + 80 + 80 - 2 * spacing) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int inBoxY() {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                int spaccing = 0;
                if (mx >= spacing + i * 80 && mx < spacing + i * 80 + 80 - 2 * spacing && my >= spaccing + j * 80 + 80 + 26 && my < spacing + j * 80 + 80 + 80 - 2 * spacing) {
                    return j;
                }
            }
        }
        return -1;
    }

    public boolean isN(int mX, int mY, int cX, int cY) { // m pirmas langelis, c kitas
        if (mX - cX < 2 && mX - cX > - 2 && mY - cY < 2 && mY - cY > - 2 && mines[cX][cY] == 1) { //tikrina ar vieno langeio kitas langelis yra pirmo kaimynas
            return true; // grazina kazkoki kaimynu skaiciu
        }
        return false;// nieko negrazina
    }

}
