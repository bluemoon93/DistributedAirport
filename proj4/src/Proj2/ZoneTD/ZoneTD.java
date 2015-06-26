package Proj2.ZoneTD;

import Proj2.General.Interfaces.GeneralTransferDeparture;
import Proj2.ZoneTD.Interfaces.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Class ZoneTD implements ZoneTDPass and ZoneTDDriver interfaces
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneTD implements ZoneTDPass, ZoneTDDriver {

    private boolean driver;
    private int peopleOnTheBus;
    private GeneralTransferDeparture zoneGeneral;
    private int peopleMax;

    private final static int waitingForPeopleToLeave = 0, departed = 1, leftBus = 2;

    /**
     * Constructor ZoneTD
     *
     * @param a GeneralTransfer monitor
     * @param b The number of people per plane
     */
    public ZoneTD(GeneralTransferDeparture a, int b) {
        this.restart(a, b);
    }

    /**
     * Constructor ZoneTD for resuming states
     *
     * @param driver The state of the driver
     * @param peopleOnTheBus How many people (if any) are currently in the bus
     * @param zoneGeneral GeneralTransfer monitor
     * @param b The number of people per plane
     */
    public ZoneTD(boolean driver, int peopleOnTheBus, GeneralTransferDeparture zoneGeneral, int b) {
        this.driver = driver;
        this.peopleOnTheBus = peopleOnTheBus;
        this.zoneGeneral = zoneGeneral;
        this.peopleMax = b;
    }
    
    private void saveState2(int tId, int state, int rId) {
        File tempFile = new File("MonitorThreadState" + tId + ";" + rId + ".temp");
        File oldFile = new File("MonitorState.old");
        File newFile = new File("MonitorState");
        File newState = new File("MonitorThreadState" + tId + ";" + rId);

        System.out.println("About to save state");
        halfSecondSleep(1000);
        System.out.println("\t... done!");
        
        // Delete Monitor.OLD State
        while (oldFile.isFile() && newFile.isFile()) {
            oldFile.delete();
        }
        
        System.out.println("Deleted OLD");
        halfSecondSleep(1000);
        System.out.println("\t... done!");

        // Rename Monitor State to .OLD
        if (newFile.isFile()) {
            newFile.renameTo(oldFile);
        }

        // Delete Thread State
        while (newState.isFile()) {
            newState.delete();
        }
        
        System.out.println("Renamed current to OLD");
        halfSecondSleep(1000);
        System.out.println("\t... done!");

        // Create Temp file
        if (!tempFile.isFile()) {
            try {
                tempFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // Create current monitor state
        try {
            PrintWriter out = new PrintWriter(newFile);
            out.println(driver + ";" + peopleOnTheBus + ";");
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        
        System.out.println("TEMP is created and current monitor status as well");
        halfSecondSleep(1000);
        System.out.println("\t... done!");

        // Create current thread state
        try {
            PrintWriter out = new PrintWriter(newState);
            out.println(state);
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        // Remove Temp file
        while (tempFile.isFile()) {
            tempFile.delete();
        }
        
        System.out.println("Temp is removed");
        halfSecondSleep(1000);
        System.out.println("\t... done!");
    }

    private void saveState(int tId, int state, int rId) {
        File tempFile = new File("MonitorThreadState" + tId + ";" + rId + ".temp");
        File oldFile = new File("MonitorState.old");
        File newFile = new File("MonitorState");
        File newState = new File("MonitorThreadState" + tId + ";" + rId);

        // Delete Monitor.OLD State
        while (oldFile.isFile() && newFile.isFile()) {
            oldFile.delete();
        }

        // Rename Monitor State to .OLD
        if (newFile.isFile()) {
            newFile.renameTo(oldFile);
        }

        // Delete Thread State
        while (newState.isFile()) {
            newState.delete();
        }

        // Create Temp file
        if (!tempFile.isFile()) {
            try {
                tempFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // Create current monitor state
        try {
            PrintWriter out = new PrintWriter(newFile);
            out.println(driver + ";" + peopleOnTheBus + ";");
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        // Create current thread state
        try {
            PrintWriter out = new PrintWriter(newState);
            out.println(state);
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        // Remove Temp file
        while (tempFile.isFile()) {
            tempFile.delete();
        }
    }

    /**
     * Parks bus in the transfer departure zone and sets how many people are
     * about to leave; Wakes all passengers on the bus and waits until they have
     * left
     *
     * @param potb people on the bus
     * @param reqId The ID of this Request
     */
    @Override
    public synchronized void park(int potb, int reqId) {
        // Park bus in this zone and set how many people are about to leave
        driver = true;
        peopleOnTheBus = potb;

        // Wake all passengers on the bus
        this.notifyAll();

        //SAVE MONITOR STATE
        //SAVE THREAD STATE
        saveState2(peopleMax, waitingForPeopleToLeave, reqId);

        System.out.println("Bus parked and going to wait");
        halfSecondSleep(1000);
        System.out.println("\t... done!");

        // Wait while people on the bus
        while (peopleOnTheBus > 0) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * Part of the park() method. Bus just waits until passengers have left
     *
     */
    public synchronized void specialPark() {
        // Wait while people on the bus
        while (peopleOnTheBus > 0) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * Bus leaves this zone.
     *
     * @param reqId The ID of this Request
     */
    @Override
    public synchronized void depart(int reqId) {
        // Take bus from this zone
        driver = false;

        //SAVE MONITOR STATE
        //SAVE THREAD STATE
        saveState(peopleMax, departed, reqId);
    }

    /**
     * Passengers wait until the bus arrives to this zone.
     */
    @Override
    public synchronized void finishBusTrip() {//
        // While bus not here
        while (driver == false) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
            }
        }

        //saveState(id, finishedTrip);
    }

    /**
     * Passenger leaves the bus and wakes up driver if the bus is empty
     *
     * @param id of the passengers
     * @param reqId The ID of this Request
     */
    @Override
    public synchronized void leaveBus(int id, int reqId) {
        // Leave the bus
        peopleOnTheBus--;
        zoneGeneral.leaveBusQueue(id);

        // Wake driver up if the bus is empty
        if (peopleOnTheBus == 0) {
            this.notify();
        }

        //SAVE MONITOR STATE
        //SAVE THREAD STATE
        saveState(id, leftBus, reqId);

        //System.out.println("\tPassenger " + id + " left bus. People left: " + peopleOnTheBus);
    }

    /**
     * Re-initialized all the variables in the monitor
     *
     * @param a GeneralTransfer monitor
     * @param b The number of people per plane
     */
    protected final void restart(GeneralTransferDeparture a, int b) {
        this.zoneGeneral = a;
        this.peopleMax = b;

        this.driver = false;
        this.peopleOnTheBus = 0;
        
        File newFile = new File("MonitorState");
        
        // Create current monitor state
        try {
            PrintWriter out = new PrintWriter(newFile);
            out.println(driver + ";" + peopleOnTheBus + ";");
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void halfSecondSleep(int n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException ex) {
        }
    }
}
