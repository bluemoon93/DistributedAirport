package Proj1;

import Proj1.General.GeneralPass;
import Proj1.ZoneTransferDeparture.ZoneTransferDeparturePass;
import Proj1.ZoneTransferArrival.ZoneTransferArrivalPass;
import Proj1.ZoneLuggage.ZoneLuggagePass;
import Proj1.ZoneEntryExit.ZoneEntryExitPass;
import Proj1.ZoneArrival.ZoneArrivalPass;
import java.util.Random;

/**
 * Thread Passenger
 *
 * @author Tiago Soares, David Simoes
 */
public class Passenger extends Thread {

    private int state, luggageMissing, id;
    private boolean journeyOver;

    private ZoneArrivalPass zoneA;
    private ZoneLuggagePass zoneLug;
    private ZoneEntryExitPass zoneEnEx;
    private ZoneTransferArrivalPass zoneTA;
    private ZoneTransferDeparturePass zoneTD;
    private GeneralPass zoneGeneral;

    private Random r;

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
    protected Passenger(int i, ZoneArrivalPass zoneA, ZoneLuggagePass zoneLC, ZoneEntryExitPass zoneEn, ZoneTransferArrivalPass zoneTA, ZoneTransferDeparturePass zoneTD,
            int malas, boolean over, GeneralPass g) {
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
     * whatShouldIDo() method and then do the full lifecycle until the thread is finished.
     */
    @Override
    public void run() {
        whatShouldIDo();
    }

    /**
     * Check if journey is over or not
     *
     * @return journeyOver
     */
    public boolean getJourney() {
        return journeyOver;
    }

    /** Check what passenger should do in the next step
     If passenger is not travelling and have bags then go collect a bag;
     If passenger is not travelling and haven't bags then go home;
     Else take a Bus;
     Call iGotHere() method wich belongs to the ZoneArrival monitor.
     */
    private void whatShouldIDo() {
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

    /**
     * When passenger is not travelling and have bags so will go collect a bag
     * and If lost bags so will report missing bags call getLuggage wich belongs
     * to ZoneLuggage monitor.
     */
    private void goCollectABag() {
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

    /**
     * When passenger report missing bags call reportLuggageMissing(id, bags)
     * method wich belongs to ZoneLuggage monitor.
     * 
     * @param bags number of bags that are missing
     */
    private void reportMissingBags(int bags) {
        if (state == AT_THE_LUGGAGE_COLLECTION_POINT) {
            state = AT_THE_BAGGAGE_RECLAIM_OFFICE;
            zoneGeneral.setPassengerState(id, state);
        }

        randomSleep(100);

        // Report luggage missing
        zoneLug.reportLuggageMissing(id, bags);

        goHome();
    }

    /**
     * When passenger go home because is not travelling and does not have any
     * bag call goingHome(id) method wich belongs to ZoneEntryExit monitor.
     */
    private void goHome() {
        if (state == AT_THE_LUGGAGE_COLLECTION_POINT || state == AT_THE_BAGGAGE_RECLAIM_OFFICE || state == AT_THE_DISEMBARKING_ZONE) {
            state = EXITING_THE_ARRIVAL_TERMINAL;
            zoneGeneral.setPassengerState(id, state);
        }

        randomSleep(100);

        // Wait until everyone is ready and leave
        zoneEnEx.goingHome(id);
    }

    /**
     * When passenger will take a bus call takeABus(id) method wich belongs to
     * ZoneTransferArrival monitor.
     */
    private void takeABus() {
        if (state == AT_THE_DISEMBARKING_ZONE) {
            state = AT_THE_ARRIVAL_TRANSFER_TERMINAL;
            zoneGeneral.setPassengerState(id, state);
        }

        randomSleep(100);

        // Get on the bus
        zoneTA.takeABus(id);

        enterTheBus();
    }

    /**
     * When passenger enter the bus and wait for the bus to finish call
     * finishBusTrip() method wich belongs to ZoneTransferDeparture monitor.
     */
    private void enterTheBus() {
        if (state == AT_THE_ARRIVAL_TRANSFER_TERMINAL) {
            state = TERMINAL_TRANSFER;
            zoneGeneral.setPassengerState(id, state);
        }

        randomSleep(100);

        // Wait for the bus to finish
        zoneTD.finishBusTrip();

        leaveTheBus();
    }

    /**
     * When passenger leave the bus call leaveBus(id) wich belongs to
     * ZoneTransferDeparture monitor.
     */
    private void leaveTheBus() {
        if (state == TERMINAL_TRANSFER) {
            state = AT_THE_DEPARTURE_TRANSFER_TERMINAL;
            zoneGeneral.setPassengerState(id, state);
        }

        randomSleep(100);

        // Leave the bus
        zoneTD.leaveBus(id);

        prepareNextLeg();
    }

    /**
     * When the last passenger of each plane notify all passengers in
     * EXITING\_THE\_ARRIVAL\_TERMINAL or ENTERING\_THE\_DEPARTURE\_TERMINAL
     * call goingHome(id) wich belongs to ZoneEntryExit monitor.
     */
    private void prepareNextLeg() {
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
