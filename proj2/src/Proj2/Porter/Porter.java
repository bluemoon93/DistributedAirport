package Proj2.Porter;

import Proj2.General.Interfaces.GeneralPorter;
import Proj2.Luggage;
import Proj2.ZoneArrival.Interfaces.ZoneArrivalPorter;
import Proj2.ZoneLuggage.Interfaces.ZoneLuggagePorter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

/**
 * Thread Porter
 *
 * @author Tiago Soares, David Simoes
 */
public class Porter extends Thread {

    private static final int WAITING_FOR_A_PLANE_TO_LAND = 0, AT_THE_PLANES_HOLD = 1, AT_THE_LUGGAGE_BELT_CONVEYOR = 2, AT_THE_STOREROOM = 3;
    private int state;
    private final ZoneLuggagePorter zoneLug;
    private final ZoneArrivalPorter zoneA;
    private final GeneralPorter zoneGeneral;
    private final Random r;
    private int faultState, requestId;
    private static final int startingThread = 0, checkingIfPeopleHaveAllArrived = 1, gotBag = 2, endingThread = 3, checkingHowMuchLugLeft = 4, gettingBag = 5, noMoreBags = 6;

    /**
     * Constructor of Porter Thread
     *
     * @param a ZoneLuggagePorter
     * @param b ZoneArrivalPorter
     * @param g General Porter
     */
    public Porter(ZoneLuggagePorter a, ZoneArrivalPorter b, GeneralPorter g) {
        this.state = WAITING_FOR_A_PLANE_TO_LAND;

        this.zoneLug = a;
        this.zoneA = b;
        this.zoneGeneral = g;

        this.requestId = 0;
        this.zoneGeneral.setPorterState(state, requestId);

        this.r = new Random();

    }

    /**
     * Calls all of functions (transitions between states) that are involved in
     * the lifecycle of the Porter; At the beggining, we start with takeARest()
     * method and then do the full lifecycle until the thread is finished.
     */
    @Override
    public void run() {
        checkFaultState();
        //takeARest();
    }

    private void checkFaultState() {
        File oldFile = new File("PorterState.old");
        File newFile = new File("PorterState");

        // Check FaultFile (if it exists)
        if (newFile.isFile()) {
            System.out.println("Continuing thread sequence");
            // Parse FaultFiles
            if (!interpretStateFile(newFile) && oldFile.isFile()) {
                interpretStateFile(oldFile);
            }
            continueFromState();
        } else if (oldFile.isFile()) { // Check OldFaultFile (if Current one doesnt exists and if old one exists)
            System.out.println("Continuing thread sequence from .old");
            // parse file
            interpretStateFile(oldFile);

            // continue from a given state
            continueFromState();
        } else {
            // start from scratch
            System.out.println("Starting thread");
            setFaultState(startingThread);
            takeARest();
        }
    }

    private void continueFromState() {
        switch (faultState) {
            case startingThread:
                takeARest();
                break;
            case checkingIfPeopleHaveAllArrived:
                boolean hasEveryoneArrived = zoneA.takeArest(requestId);

                // If passengers arrived
                if (!hasEveryoneArrived) {
                    tryToCollectABag(false);
                } // If no more passengers
                else {
                    zoneGeneral.setPorterState(-1, requestId);
                    setFaultState(endingThread);
                    removeFaultFiles();
                }
                break;
            case gotBag:
                state = AT_THE_PLANES_HOLD;
                zoneGeneral.setPorterState(state, requestId);
                tryToCollectABag(false);
                break;
            case endingThread:
                removeFaultFiles();
                break;
            case checkingHowMuchLugLeft:
                tryToCollectABag(true);
                break;
            case gettingBag:
                // Take luggage and transport it
                Luggage temp = zoneA.getLug(requestId);
                carryItToAppropriateStore(temp);

                state = AT_THE_PLANES_HOLD;
                zoneGeneral.setPorterState(state, requestId);

                tryToCollectABag(false);
                break;
            case noMoreBags:
                zoneLug.noMoreBags(requestId);
                noMoreBagsToCollect();
                break;
            default:
                System.exit(0);
        }

    }

    private void removeFaultFiles() {
        File oldFile = new File("PorterState.old");
        File newFile = new File("PorterState");
        File confFile = new File("PorterFaultConfiguration");

        while (oldFile.isFile()) {
            oldFile.delete();
        }

        while (newFile.isFile()) {
            newFile.delete();
        }

        while (confFile.isFile()) {
            confFile.delete();
        }
    }

    private boolean interpretStateFile(File f) {
        int tState, tFaultState, tID;

        try {
            Scanner fin = new Scanner(f);
            String info = fin.nextLine();
            fin.close();
            if (info.charAt(info.length() - 1) != ';') {
                return false;
            }
            String[] fields = info.split(";");
            tFaultState = Integer.parseInt(fields[0]);
            tID = Integer.parseInt(fields[1]);
            tState = Integer.parseInt(fields[2]);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        state = tState;
        faultState = tFaultState;
        requestId = tID;

        return true;
    }

    private void takeARest() {
        setFaultState(checkingIfPeopleHaveAllArrived);
        boolean hasEveryoneArrived = zoneA.takeArest(requestId);

        // If passengers arrived
        if (!hasEveryoneArrived) {
            System.out.println("Took a rest and changed some variables!");
            halfSecondSleep();
            System.out.println("\t... done!");
            tryToCollectABag(false);
        } // If no more passengers
        else {
            zoneGeneral.setPorterState(-1, requestId);
            setFaultState(endingThread);
            removeFaultFiles();
        }
    }

    private void noMoreBagsToCollect() {
        if (state == AT_THE_PLANES_HOLD) {
            state = WAITING_FOR_A_PLANE_TO_LAND;
            zoneGeneral.setPorterState(state, requestId);
        }

        randomSleep(100);

        takeARest();
    }

    private void tryToCollectABag(boolean resuming) {
        if (!resuming) {
            if (state == WAITING_FOR_A_PLANE_TO_LAND) {
                state = AT_THE_PLANES_HOLD;
                zoneGeneral.setPorterState(state, requestId);
            }
            setFaultState(checkingHowMuchLugLeft);

            randomSleep(100);
        }
        int luggageLeft = zoneA.getLugNumber(requestId);

        System.out.println("Called a method on a monitor");
        halfSecondSleep();
        System.out.println("\t... done!");

        // While there is still luggage on the Plane's Hold
        while (luggageLeft != 0) {
            setFaultState(gettingBag);
            // Take luggage and transport it
            Luggage temp = zoneA.getLug(requestId);
            carryItToAppropriateStore(temp);

            // Come back to Plane's Hold
            if (state == AT_THE_LUGGAGE_BELT_CONVEYOR || state == AT_THE_STOREROOM) {
                state = AT_THE_PLANES_HOLD;
                zoneGeneral.setPorterState(state, requestId);
            }

            setFaultState(checkingHowMuchLugLeft);
            luggageLeft = zoneA.getLugNumber(requestId);
        }

        System.out.println("Found out there were no more bags but haven't saved the state yet!");
        halfSecondSleep();
        System.out.println("\t... done!");

        setFaultState2(noMoreBags);

        randomSleep(100);

        // Announce bags have ended
        zoneLug.noMoreBags(requestId);

        noMoreBagsToCollect();
    }

    private void carryItToAppropriateStore(Luggage t) {
        // If luggage is from a travelling passenger, store it in the StoreRoom
        if (t.isOwnerTravelling() == true) {
            if (state == AT_THE_PLANES_HOLD) {
                state = AT_THE_STOREROOM;
                zoneGeneral.setPorterState(state, requestId);
            }

            randomSleep(100);

            zoneLug.putABagSR(requestId);
        } // If luggage is from a passenger whose journey is over, take it to the LBC
        else {
            if (state == AT_THE_PLANES_HOLD) {
                state = AT_THE_LUGGAGE_BELT_CONVEYOR;
                zoneGeneral.setPorterState(state, requestId);
            }

            randomSleep(100);

            zoneLug.putABagLBC(t.getOwnerId(), requestId);
        }

        setFaultState(gotBag);
    }

    private void halfSecondSleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
    }

    private void randomSleep(int n) {
        try {
            Thread.sleep(Math.abs(r.nextInt()) % n);
        } catch (InterruptedException ex) {
        }
    }

    private void setFaultState(int stateL) {
        faultState = stateL;
        requestId++;

        File oldFile = new File("PorterState.old");
        File newFile = new File("PorterState");

        // If an old state exists, delete it
        if (oldFile.isFile() && newFile.isFile()) {
            oldFile.delete();
        }

        // If a current state exists, set it as old
        if (newFile.isFile()) {
            newFile.renameTo(oldFile);
        }

        // Create a current state
        try {
            PrintWriter out = new PrintWriter(newFile);
            out.println(faultState + ";" + requestId + ";" + state + ";");
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        System.out.println("State saved");
    }

    private void setFaultState2(int stateL) {
        faultState = stateL;
        requestId++;

        System.out.println("About to save the state");
        halfSecondSleep();
        halfSecondSleep();
        System.out.println("\t... done!");

        File oldFile = new File("PorterState.old");
        File newFile = new File("PorterState");

        // If an old state exists, delete it
        if (oldFile.isFile() && newFile.isFile()) {
            oldFile.delete();
        }

        System.out.println("Deleted the .OLD");
        halfSecondSleep();
        halfSecondSleep();
        System.out.println("\t... done!");

        // If a current state exists, set it as old
        if (newFile.isFile()) {
            newFile.renameTo(oldFile);
        }

        System.out.println("Renamed the Current state to .OLD");
        halfSecondSleep();
        halfSecondSleep();
        halfSecondSleep();
        System.out.println("\t... done!");

        // Create a current state
        try {
            PrintWriter out = new PrintWriter(newFile);

            System.out.println("Created a file");
            halfSecondSleep();
            halfSecondSleep();
            halfSecondSleep();
            System.out.println("\t... done!");

            out.println(faultState + ";" + requestId + ";" + state + ";");

            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        System.out.println("State saved");
    }
}
