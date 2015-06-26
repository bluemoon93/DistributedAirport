package Proj1;

import Proj1.General.GeneralPorter;
import Proj1.ZoneLuggage.ZoneLuggagePorter;
import Proj1.ZoneArrival.ZoneArrivalPorter;
import java.util.Random;

/**
 * Thread Porter
 *
 * @author Tiago Soares, David Simoes
 */
public class Porter extends Thread {

    private static final int WAITING_FOR_A_PLANE_TO_LAND = 0, AT_THE_PLANES_HOLD = 1, AT_THE_LUGGAGE_BELT_CONVEYOR = 2, AT_THE_STOREROOM = 3;
    private int state;
    private ZoneLuggagePorter zoneLug;
    private ZoneArrivalPorter zoneA;
    private GeneralPorter zoneGeneral;
    private Random r;

    /**
     * Constructor of Porter Thread
     *
     * @param a ZoneLuggagePorter
     * @param b ZoneArrivalPorter
     * @param g General Porter
     */
    protected Porter(ZoneLuggagePorter a, ZoneArrivalPorter b, GeneralPorter g) {
        this.state = WAITING_FOR_A_PLANE_TO_LAND;

        this.zoneLug = a;
        this.zoneA = b;
        this.zoneGeneral = g;

        this.zoneGeneral.setPorterState(state);

        this.r = new Random();
    }

    /**
     * Calls all of functions (transitions between states) that are involved in
     * the lifecycle of the Porter; At the beggining, we start with takeARest()
     * method and then do the full lifecycle until the thread is finished.
     */
    @Override
    public void run() {
        takeARest();
    }

    /**
     * When porter will take a rest until passengers arrived call takeARest()
     * method wich belongs to ZoneArrival monitor.
     */
    private void takeARest() {
        // If passengers arrived
        if (!zoneA.takeArest()) {
            tryToCollectABag();
        } // If no more passengers
        else {
            zoneGeneral.setPorterState(-1);
        }
    }

    /**
     * When there is no more bags to collect, the porter will take a rest.
     */
    private void noMoreBagsToCollect() {
        if (state == AT_THE_PLANES_HOLD) {
            state = WAITING_FOR_A_PLANE_TO_LAND;
            zoneGeneral.setPorterState(state);
        }

        randomSleep(100);

        takeARest();
    }

    /**
     * When porter try to collect a bag, one by one, and distribute them by
     * luggage belt conveyor or storeroom call getLugNumber() and getLug()
     * methods wich belongs to ZoneArrival monitor and noMoreBags() method wich
     * belongs to ZoneLuggage monitor.
     */
    private void tryToCollectABag() {
        if (state == WAITING_FOR_A_PLANE_TO_LAND) {
            state = AT_THE_PLANES_HOLD;
            zoneGeneral.setPorterState(state);
        }

        randomSleep(100);

        // While there is still luggage on the Plane's Hold
        while (zoneA.getLugNumber() != 0) {
            // Take luggage and transport it
            Luggage temp = zoneA.getLug();
            carryItToAppropriateStore(temp);

            // Come back to Plane's Hold
            if (state == AT_THE_LUGGAGE_BELT_CONVEYOR || state == AT_THE_STOREROOM) {
                state = AT_THE_PLANES_HOLD;
                zoneGeneral.setPorterState(state);
            }
        }

        randomSleep(100);

        // Announce bags have ended
        zoneLug.noMoreBags();

        noMoreBagsToCollect();
    }

    /**
     * When porter carry bags wich belongs to passengers that still travelling
     * or wich belongs to passengers that their journey is over call putABagSR()
     * or putABagLBC() wich belongs to ZoneLuggage monitor.
     * 
     * @param t the luggage
     */
    private void carryItToAppropriateStore(Luggage t) {
        // If luggage is from a travelling passenger, store it in the StoreRoom
        if (t.isOwnerTravelling() == true) {
            if (state == AT_THE_PLANES_HOLD) {
                state = AT_THE_STOREROOM;
                zoneGeneral.setPorterState(state);
            }

            randomSleep(100);

            zoneLug.putABagSR();
        } // If luggage is from a passenger whose journey is over, take it to the LBC
        else {
            if (state == AT_THE_PLANES_HOLD) {
                state = AT_THE_LUGGAGE_BELT_CONVEYOR;
                zoneGeneral.setPorterState(state);
            }

            randomSleep(100);

            zoneLug.putABagLBC(t.getOwnerId());
        }
    }

    private void randomSleep(int n) {
        try {
            Thread.sleep(Math.abs(r.nextInt()) % n);
        } catch (InterruptedException ex) {
        }
    }
}
