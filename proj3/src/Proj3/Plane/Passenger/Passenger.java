package Proj3.Plane.Passenger;

import Proj3.General.Interfaces.GeneralPass;
import Proj3.ZoneArrival.Interfaces.ZoneArrivalPass;
import Proj3.ZoneEntryExit.Interfaces.ZoneEntryExitPass;
import Proj3.ZoneLuggage.Interfaces.ZoneLuggagePass;
import Proj3.ZoneTA.Interfaces.ZoneTAPass;
import Proj3.ZoneTD.Interfaces.ZoneTDPass;
import java.rmi.RemoteException;
import java.util.Random;

/**
 * Thread Passenger
 *
 * @author Tiago Soares, David Simoes
 */
public class Passenger extends Thread {

    private final int id;
    private int state, luggageMissing;
    private final boolean journeyOver;

    private final ZoneArrivalPass zoneA;
    private final ZoneLuggagePass zoneLug;
    private final ZoneEntryExitPass zoneEnEx;
    private final ZoneTAPass zoneTA;
    private final ZoneTDPass zoneTD;
    private final GeneralPass zoneGeneral;

    private final Random r;

    private static final int AT_THE_DISEMBARKING_ZONE = 0, AT_THE_LUGGAGE_COLLECTION_POINT = 1, AT_THE_BAGGAGE_RECLAIM_OFFICE = 2, EXITING_THE_ARRIVAL_TERMINAL = 3,
            AT_THE_ARRIVAL_TRANSFER_TERMINAL = 4, TERMINAL_TRANSFER = 5, AT_THE_DEPARTURE_TRANSFER_TERMINAL = 6, ENTERING_THE_DEPARTURE_TERMINAL = 7;

    /**
     * Constructor of Passenger Thread
     *
     * @param i id
     * @param zoneA ZoneArrivalPass monitor
     * @param zoneLC ZoneLuggagePass monitor
     * @param zoneEn ZoneEntryExitPass monitor
     * @param zoneTA ZoneTransferArrivalPass monitor
     * @param zoneTD ZoneTransferDeparturePass monitor
     * @param g GeneralPassenger monitor
     * @param malas number of bags this passenger is carrying
     * @param over if journey is over or not
     */
    public Passenger(int i, ZoneArrivalPass zoneA, ZoneLuggagePass zoneLC, ZoneEntryExitPass zoneEn, ZoneTAPass zoneTA, ZoneTDPass zoneTD,
            int malas, boolean over, GeneralPass g) throws RemoteException {
        this.state = AT_THE_DISEMBARKING_ZONE;
        this.journeyOver = over;
        this.luggageMissing = malas;
        this.id = i;

        this.zoneA = zoneA;
        this.zoneLug = zoneLC;
        this.zoneEnEx = zoneEn;
        this.zoneTA = zoneTA;
        this.zoneTD = zoneTD;
        this.zoneGeneral = g;

        this.zoneGeneral.setPassengerState(id, state);

        this.r = new Random();
    }

    /**
     * Calls all of functions (transitions between states) that are involved in
     * the lifecycle of the Passenger; At the beggining, we start with
     * whatShouldIDo() method and then do the full lifecycle until the thread is
     * finished.
     */
    @Override
    public void run() {
        try{
        whatShouldIDo();
        } catch (RemoteException ex) {
            System.out.println("Remote exception ocurred! "+ex);
            ex.printStackTrace();
        }
    }

    private void whatShouldIDo() throws RemoteException {
        zoneA.iGotHere();

        randomSleep(100);

        // If not travelling and have bags
        if (journeyOver && luggageMissing > 0) {
            goCollectABag();
        } // If not travelling and no bags
        else if (journeyOver) {
            goHome();
        } // If travelling
        else {
            takeABus();
        }
    }

    private void goCollectABag() throws RemoteException {
        if (state == AT_THE_DISEMBARKING_ZONE) {
            state = AT_THE_LUGGAGE_COLLECTION_POINT;
            zoneGeneral.setPassengerState(id, state);
        }

        randomSleep(100);

        // Pick up all his luggage
        luggageMissing -= zoneLug.getLuggage(id, luggageMissing);

        // If no lost bags
        if (luggageMissing == 0) {
            goHome();
        } // If any bags lost
        else {
            reportMissingBags(luggageMissing);
        }
    }

    private void reportMissingBags(int bags) throws RemoteException {
        if (state == AT_THE_LUGGAGE_COLLECTION_POINT) {
            state = AT_THE_BAGGAGE_RECLAIM_OFFICE;
            zoneGeneral.setPassengerState(id, state);
        }

        randomSleep(100);

        // Report luggage missing
        zoneLug.reportLuggageMissing(id, bags);

        goHome();
    }

    private void goHome() throws RemoteException {
        if (state == AT_THE_LUGGAGE_COLLECTION_POINT || state == AT_THE_BAGGAGE_RECLAIM_OFFICE || state == AT_THE_DISEMBARKING_ZONE) {
            state = EXITING_THE_ARRIVAL_TERMINAL;
            zoneGeneral.setPassengerState(id, state);
        }

        randomSleep(100);

        // Wait until everyone is ready and leave
        zoneEnEx.goingHome(id);
    }

    private void takeABus() throws RemoteException {
        if (state == AT_THE_DISEMBARKING_ZONE) {
            state = AT_THE_ARRIVAL_TRANSFER_TERMINAL;
            zoneGeneral.setPassengerState(id, state);
        }

        randomSleep(100);

        // Get on the bus
        zoneTA.takeABus(id);

        enterTheBus();
    }

    private void enterTheBus() throws RemoteException {
        if (state == AT_THE_ARRIVAL_TRANSFER_TERMINAL) {
            state = TERMINAL_TRANSFER;
            zoneGeneral.setPassengerState(id, state);
        }

        randomSleep(100);

        // Wait for the bus to finish
        zoneTD.finishBusTrip();

        leaveTheBus();
    }

    private void leaveTheBus() throws RemoteException {
        if (state == TERMINAL_TRANSFER) {
            state = AT_THE_DEPARTURE_TRANSFER_TERMINAL;
            zoneGeneral.setPassengerState(id, state);
        }

        randomSleep(100);

        // Leave the bus
        zoneTD.leaveBus(id);

        prepareNextLeg();
    }

    private void prepareNextLeg() throws RemoteException {
        if (state == AT_THE_DEPARTURE_TRANSFER_TERMINAL) {
            state = ENTERING_THE_DEPARTURE_TERMINAL;
            zoneGeneral.setPassengerState(id, state);
        }

        randomSleep(100);

        // Wait until everyone is ready and leave
        zoneEnEx.goingHome(id);
    }

    private void randomSleep(int n) {
        try {
            Thread.sleep(Math.abs(r.nextInt()) % n);
        } catch (InterruptedException ex) {
        }
    }
}
