package Proj1.General;

import java.awt.Color;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI extends javax.swing.JFrame implements Serializable {

    final static private Scanner in = new Scanner(System.in);
    final private JPanel[] passageiros, lugares, bags;
    final private int [] currBags, maxBags;
    final private List bagOwners;
    final private JPanel driver, driver2, plane, porter;
    final private ImageIcon passImage = new ImageIcon("pass.png"), drivImage = new ImageIcon("driver.png"), portImage = new ImageIcon("porter.png"),
            emptySeat = new ImageIcon("seatEmpty.png"), fullSeat = new ImageIcon("seatFull.png"), airport = new ImageIcon("airport.png"),
            bag = new ImageIcon("mala.png"), driv2Image = new ImageIcon("driver2.png"), planeImage = new ImageIcon("plane.png");
    final boolean swiftSimulation;
    final private String zoneArr = "zoneArr", zoneTA = "zoneTA", zoneTD = "zoneTD", zoneLug = "zoneLug", zoneTATD = "zoneTATD", zoneEE = "zoneEE", zoneTAQueue = "zoneTAQ", landingStrip = "ls";
    final private JLabel storage, lost, left;
    private int bagsLeft, delayVal = 1;

    public void setMalas(int fn, int ln) {
        bagsLeft = ln;
        if (bagsLeft != -1) {
            left.setText("Bags Left: " + bagsLeft);
        } else {
            left.setText("Bags Left: ?");
        }
        if (fn == -1) {
            plane.setLocation(0, -100);
        } else {
            this.setLocation(plane, landingStrip, 0, 0);
            HumanDelay();
            for (int i = 0; i < passageiros.length; i++) {
                passageiros[i].setLocation(getXfromLoc(landingStrip) + i * passImage.getIconWidth(), getYfromLoc(landingStrip) + 60);
            }
        }

        HumanDelay();
    }

    public void setDriverState(int st) {
        String loc = "";
        boolean start = false;
        final int PARKING_AT_THE_ARRIVAL_TERMINAL = 0, DRIVING_FORWARD = 1, PARKING_AT_THE_DEPARTURE_TERMINAL = 2, DRIVING_BACKWARD = 3;
        switch (st) {
            case PARKING_AT_THE_ARRIVAL_TERMINAL:
                if (driver.getY() < 0) {
                    start = true;
                }
                loc = zoneTA;
                break;
            case DRIVING_FORWARD:
                loc = zoneTATD;
                break;
            case PARKING_AT_THE_DEPARTURE_TERMINAL:
                loc = zoneTD;
                break;
            case DRIVING_BACKWARD:
                loc = zoneTATD;
                break;
            default:
                hidePanel(driver);
                for (int i = 0; i < lugares.length; i++) {
                    hidePanel(lugares[i]);
                }
                hidePanel(driver2);
        }

        if (!start) {
            JPanel[] panels = new JPanel[2 + lugares.length];
            int[] offsetsX = new int[2 + lugares.length], offsetsY = new int[2 + lugares.length];

            panels[0] = driver;
            offsetsX[0] = 0;
            offsetsY[0] = 0;
            panels[1 + lugares.length] = driver2;
            offsetsX[1 + lugares.length] = driver.getWidth() + lugares.length * emptySeat.getIconWidth();
            offsetsY[1 + lugares.length] = 0;

            for (int i = 0; i < lugares.length; i++) {
                panels[i + 1] = lugares[i];
                offsetsX[i + 1] = driver.getWidth() + i * emptySeat.getIconWidth();
                offsetsY[i + 1] = 0;
            }

            this.setLocation(panels, loc, offsetsX, offsetsY);
        } else {
            driver.setLocation(getXfromLoc(loc), getYfromLoc(loc));
            driver2.setLocation(getXfromLoc(loc) + driver.getWidth() + lugares.length * emptySeat.getIconWidth(), getYfromLoc(loc));
            for (int i = 0; i < lugares.length; i++) {
                lugares[i].setLocation(getXfromLoc(loc) + driver.getWidth() + i * emptySeat.getIconWidth(), getYfromLoc(loc));
            }
        }
        HumanDelay();
    }

    public void setPorterState(int st) {
        final int WAITING_FOR_A_PLANE_TO_LAND = 0, AT_THE_PLANES_HOLD = 1, AT_THE_LUGGAGE_BELT_CONVEYOR = 2, AT_THE_STOREROOM = 3;
        boolean start = false;
        String loc = "";
        int offsetX = 0, offsetY = 0;
        switch (st) {
            case WAITING_FOR_A_PLANE_TO_LAND:
                if (porter.getY() < 0) {
                    start = true;
                }
                loc = zoneArr;
                offsetY=30;
                break;
            case AT_THE_PLANES_HOLD:
                loc = zoneArr;
                offsetX=100;
                offsetY=30;
                break;
            case AT_THE_LUGGAGE_BELT_CONVEYOR:
                loc = zoneLug;
                offsetY = -20;
                break;
            case AT_THE_STOREROOM:
                offsetY = 200;
                offsetX = 200;
                loc = zoneLug;
                break;
            default:
                hidePanel(porter);

        }

        if (!start) {
            this.setLocation(porter, loc, offsetX, offsetY);
        } else {
            porter.setLocation(getXfromLoc(loc)+offsetX, getYfromLoc(loc)+offsetY);
        }
        if (st == AT_THE_PLANES_HOLD) {
            if (bagsLeft > 0) {
                bagsLeft--;
                left.setText("Bags Left: " + bagsLeft);
            }
        }

        HumanDelay();
    }

    public void setPassengerState(int id, int st) {
        final int AT_THE_DISEMBARKING_ZONE = 0, AT_THE_LUGGAGE_COLLECTION_POINT = 1, AT_THE_BAGGAGE_RECLAIM_OFFICE = 2, EXITING_THE_ARRIVAL_TERMINAL = 3,
                AT_THE_ARRIVAL_TRANSFER_TERMINAL = 4, ENTERING_THE_DEPARTURE_TERMINAL = 7;
        passageiros[id].setSize(passImage.getIconWidth(), passImage.getIconHeight()+30);
        String loc = "";
        switch (st) {
            case AT_THE_DISEMBARKING_ZONE:
                loc = zoneArr;
                break;
            case AT_THE_LUGGAGE_COLLECTION_POINT:
            case AT_THE_BAGGAGE_RECLAIM_OFFICE:
                loc = zoneLug;
                passageiros[id].setSize(passImage.getIconWidth(), passImage.getIconHeight()+60);
                break;
            case AT_THE_ARRIVAL_TRANSFER_TERMINAL:
                loc = zoneTA;
                break;
            case ENTERING_THE_DEPARTURE_TERMINAL:
            case EXITING_THE_ARRIVAL_TERMINAL:
                loc = zoneEE;
                break;
            default:
                hidePanel(passageiros[id]);
        }

        this.setLocation(passageiros[id], loc, id * passImage.getIconWidth(), 125);

        HumanDelay();
    }
    
    private void updateBags(int owner){
        currBags[owner]++;
        ((JLabel)passageiros[owner].getComponent(2)).setText(currBags[owner]+"/"+maxBags[owner]);
        this.repaint();
    }

    public void setMaxBags(int id, int max){
        currBags[id]=0;
        maxBags[id]=max;
        ((JLabel)passageiros[id].getComponent(2)).setText(currBags[id]+"/"+maxBags[id]);
        this.repaint();
    }
    
    public void setLCB(int n, int owner, boolean adding) {
        if (!adding) {
            this.setLocation(passageiros[owner], zoneLug, owner * passImage.getIconWidth(), 115);
            HumanDelay();
            bagOwners.remove(0);
            updateBags(owner);
            this.hidePanel(bags[bagOwners.size()]);
        } else {
            bags[bagOwners.size()].setLocation(600 + bagOwners.size() * bag.getIconWidth(), 375);
            bagOwners.add(owner);
        }
        this.repaint();
        HumanDelay();
    }

    public void setST(int n) {
        storage.setText("Stored Bags: " + n);

        HumanDelay();
    }

    public void setPassengerCurrBags(int id, int st) {
        //this.setLocation(passageiros[id], zoneLug, id * passImage.getIconWidth(), 115);
        //HumanDelay();
        this.setLocation(passageiros[id], zoneLug, id * passImage.getIconWidth(), 125);
        HumanDelay();
    }

    public void reportBagsMissing(int id, int b) {
        this.setLocation(passageiros[id], zoneLug, 340, -50);
        lost.setText("Lost Bags: " + b);
        this.repaint();

        HumanDelay();

    }

    public void addWaitQueue(int id, int i) {
        this.setLocation(passageiros[id], zoneTAQueue, i * passImage.getIconWidth(), 0);

        HumanDelay();
    }

    public void leaveWaitQueue(int id) {
        this.setLocation(passageiros[id], zoneTAQueue, 0, -20);

        HumanDelay();
    }

    public void addBusQueue(int id, int i) {
        ((JLabel) lugares[i].getComponent(0)).setIcon(fullSeat);
        hidePanel(passageiros[id]);
        this.repaint();

        HumanDelay();
    }

    public void leaveBusQueue(int id, int i) {
        ((JLabel) lugares[i].getComponent(0)).setIcon(emptySeat);
        passageiros[id].setLocation(200, 600);
        HumanDelay();
        
        this.setLocation(passageiros[id], zoneTD, id * passImage.getIconWidth(), 125);
        HumanDelay();
    }

    public GUI(int nPass, int nLug, boolean swift) {
        this.bagOwners = new LinkedList();
        
        this.passageiros = new JPanel[nPass];
        this.currBags = new int[nPass];
        this.maxBags = new int[nPass];
        
        for (int i = 0; i < passageiros.length; i++) {
            currBags[i]=0;
            maxBags[i]=0;
            
            passageiros[i] = new JPanel();
            passageiros[i].setSize(passImage.getIconWidth(), passImage.getIconHeight()+30);
            passageiros[i].setBackground(Color.WHITE);
            
            JLabel label = new JLabel();
            label.setIcon(passImage);
            passageiros[i].add(label);
            
            JLabel label2 = new JLabel("P"+i);
            passageiros[i].add(label2);
            
            JLabel label3 = new JLabel("0/0");
            passageiros[i].add(label3);
            
            this.getContentPane().add(passageiros[i]);
            hidePanel(passageiros[i]);
        }

        this.bags = new JPanel[nPass * nLug];

        for (int i = 0; i < bags.length; i++) {
            bags[i] = new JPanel();
            bags[i].setSize(bag.getIconWidth(), bag.getIconHeight());
            bags[i].setBackground(Color.LIGHT_GRAY);
            JLabel label = new JLabel();
            label.setIcon(bag);
            bags[i].add(label);
            this.getContentPane().add(bags[i]);
            hidePanel(bags[i]);
        }

        this.lugares = new JPanel[nLug];

        for (int i = 0; i < lugares.length; i++) {
            lugares[i] = new JPanel();
            lugares[i].setSize(emptySeat.getIconWidth(), emptySeat.getIconHeight());
            lugares[i].setBackground(Color.WHITE);
            JLabel label = new JLabel();
            label.setIcon(emptySeat);
            lugares[i].add(label);
            this.getContentPane().add(lugares[i]);
            hidePanel(lugares[i]);
        }

        this.swiftSimulation = swift;

        this.driver = new JPanel();
        driver.setSize(drivImage.getIconWidth(), drivImage.getIconHeight());
        driver.setBackground(Color.WHITE);
        JLabel labelD = new JLabel();
        labelD.setIcon(drivImage);
        driver.add(labelD);
        this.getContentPane().add(driver);
        hidePanel(driver);

        this.driver2 = new JPanel();
        driver2.setSize(driv2Image.getIconWidth(), driv2Image.getIconHeight());
        driver2.setBackground(Color.WHITE);
        JLabel labelD2 = new JLabel();
        labelD2.setIcon(driv2Image);
        driver2.add(labelD2);
        this.getContentPane().add(driver2);
        hidePanel(driver2);

        this.plane = new JPanel();
        plane.setSize(planeImage.getIconWidth(), planeImage.getIconHeight());
        plane.setBackground(Color.WHITE);
        JLabel labelPl = new JLabel();
        labelPl.setIcon(planeImage);
        plane.add(labelPl);
        this.getContentPane().add(plane);
        hidePanel(plane);

        this.porter = new JPanel();
        porter.setSize(portImage.getIconWidth(), portImage.getIconHeight());
        porter.setBackground(Color.WHITE);
        JLabel labelP = new JLabel();
        labelP.setIcon(portImage);
        porter.add(labelP);
        this.getContentPane().add(porter);
        hidePanel(porter);

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        initComponents();
        this.setSize(airport.getIconWidth(), airport.getIconHeight() + 50);

        JPanel panel = new JPanel(null);
        panel.setSize(airport.getIconWidth(), airport.getIconHeight());
        panel.setBackground(Color.WHITE);

        lost = new JLabel("Lost Bags: 0");
        lost.setBounds(890, 320, 200, 50);
        panel.add(lost);

        storage = new JLabel("Stored Bags: 0");
        storage.setBounds(840, 520, 200, 50);
        panel.add(storage);

        left = new JLabel("Bags Left: ?");
        left.setBounds(1300, 225, 200, 50);
        panel.add(left);

        JLabel label = new JLabel();
        label.setBounds(0, 0, airport.getIconWidth(), airport.getIconHeight());
        label.setIcon(airport);
        panel.add(label);

        this.getContentPane().add(panel);
        this.repaint();

        HumanDelay();
    }

    private void hidePanel(JPanel pan) {
        pan.setLocation(0, -100);
    }

    private void HumanDelay() {
        if (!swiftSimulation) {
            in.nextLine();
        } else {
            synchronized (this) {
                try {
                    this.wait(delayVal);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void setLocation(JPanel[] pan, String loc, int[] offsetX, int[] offsetY) {
        int x = 0, y = 0;
        switch (loc) {
            case zoneArr:
                x = 1100;
                y = 250;
                break;
            case zoneTA:
                x = 1060;
                y = 530;
                break;
            case zoneTD:
                x = 160;
                y = 525;
                break;
            case zoneLug:
                x = 540;
                y = 320;
                break;
            case zoneTATD:
                x = 500;
                y = 650;
                break;
            case zoneEE:
                x = 150;
                y = 235;
                break;
            case zoneTAQueue:
                x = 1060;
                y = 590;
                break;
            default:
                return;
        }
        int[] moveX = new int[pan.length], moveY = new int[pan.length];
        for (int i = 0; i < pan.length; i++) {
            moveX[i] = ((x + offsetX[i]) - pan[i].getX()) / 10;
            moveY[i] = ((y + offsetY[i]) - pan[i].getY()) / 10;
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < pan.length; j++) {
                pan[j].setLocation(pan[j].getX() + moveX[j], pan[j].getY() + moveY[j]);
            }

            this.repaint();

            synchronized (this) {
                try {
                    this.wait(20);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        for (int i = 0; i < pan.length; i++) {
            pan[i].setLocation(x + offsetX[i], y + offsetY[i]);
        }

        this.repaint();
    }

    private int getXfromLoc(String loc) {
        switch (loc) {
            case zoneArr:
                return 1100;
            case zoneTA:
                return 1060;
            case zoneTD:
                return 160;
            case zoneLug:
                return 540;
            case zoneTATD:
                return 650;
            case zoneEE:
                return 150;
            case zoneTAQueue:
                return 1060;
            case landingStrip:
                return 1100;
            default:
                return -1;
        }
    }

    private int getYfromLoc(String loc) {
        switch (loc) {
            case zoneArr:
                return 220;
            case zoneTA:
                return 530;
            case zoneTD:
                return 525;
            case zoneLug:
                return 320;
            case zoneTATD:
                return 650;
            case zoneEE:
                return 175;
            case zoneTAQueue:
                return 590;
            case landingStrip:
                return 78;
            default:
                return -1;
        }
    }

    private void setLocation(JPanel pan, String loc, int offsetX, int offsetY) {
        int x = 0, y = 0;
        switch (loc) {
            case zoneArr:
                x = getXfromLoc(loc);
                y = getYfromLoc(loc);
                break;
            case zoneTA:
                x = getXfromLoc(loc);
                y = getYfromLoc(loc);
                break;
            case zoneTD:
                x = getXfromLoc(loc);
                y = getYfromLoc(loc);
                break;
            case zoneLug:
                x = getXfromLoc(loc);
                y = getYfromLoc(loc);
                break;
            case zoneTATD:
                x = getXfromLoc(loc);
                y = getYfromLoc(loc);
                break;
            case zoneEE:
                x = getXfromLoc(loc);
                y = getYfromLoc(loc);
                break;
            case zoneTAQueue:
                x = getXfromLoc(loc);
                y = getYfromLoc(loc);
                break;
            case landingStrip:
                x = getXfromLoc(loc);
                y = getYfromLoc(loc);
                break;
            default:
                return;
        }

        int moveX = ((x + offsetX) - pan.getX()) / 10, moveY = ((y + offsetY) - pan.getY()) / 10;
        for (int i = 0; i < 9; i++) {
            pan.setLocation(pan.getX() + moveX, pan.getY() + moveY);
            this.repaint();

            synchronized (this) {
                try {
                    this.wait(20);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        pan.setLocation(x + offsetX, y + offsetY);
        this.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
