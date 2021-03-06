package Proj3.Porter;

import Proj3.General.Interfaces.GeneralPorter;
import Proj3.Luggage;
import Proj3.ZoneArrival.Interfaces.ZoneArrivalPorter;
import Proj3.ZoneLuggage.Interfaces.ZoneLuggagePorter;
import java.rmi.RemoteException;
import java.util.Random;

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

    /**
     * Constructor of Porter Thread
     *
     * @param a ZoneLuggagePorter
     * @param b ZoneArrivalPorter
     * @param g General Porter
     */
    protected Porter(ZoneLuggagePorter a, ZoneArrivalPorter b, GeneralPorter g) throws RemoteException {
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
        try{
        takeARest();
        } catch (RemoteException ex) {
            System.out.println("Remote exception ocurred! "+ex);
            ex.printStackTrace();
        }
    }

    private void takeARest() throws RemoteException {
        // If passengers arrived
        if (!zoneA.takeArest()) {
            tryToCollectABag();
        } // If no more passengers
        else {
            zoneGeneral.setPorterState(-1);
        }
    }

    private void noMoreBagsToCollect() throws RemoteException {
        if (state == AT_THE_PLANES_HOLD) {
            state = WAITING_FOR_A_PLANE_TO_LAND;
            zoneGeneral.setPorterState(state);
        }

        randomSleep(100);

        takeARest();
    }

    private void tryToCollectABag() throws RemoteException {
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

    private void carryItToAppropriateStore(Luggage t) throws RemoteException {
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
