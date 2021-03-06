package Proj3.ZoneLuggage;

import Proj3.General.Interfaces.GeneralLuggage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Class ZoneLuggage implements ZoneLuggagePass, ZoneLuggagePlane and
 * ZoneLuggagePorter interfaces
 *
 * @author Tiago Soares, David Simoes
 *
 */
public class ZoneLuggage implements ZoneLuggageInterface {

    private int bagsInStorage, people, lostBags;
    private Queue<Integer> bagagens;
    private boolean noMoreLuggage;
    private GeneralLuggage zoneGeneral;
    private Object[] objectArr;
    private List<Integer> peopleList;

    /**
     * Constructor ZoneLuggage
     *
     * @param g GeneralLuggage Monitor
     * @param pass Number of passengers from each flight
     */
    public ZoneLuggage(GeneralLuggage g, int pass) {
        this.restart(g, pass);
    }

    /**
     * Puts a bag at the luggage belt conveyor and wakes waiting passengers
     *
     * @param ownerId identification of the luggage owner
     */
    @Override
    public synchronized void putABagLBC(int ownerId) throws RemoteException {
        // Lay bag in belt
        bagagens.add(ownerId);
        zoneGeneral.setLCB(bagagens.size(), ownerId, true);

        // Wake waiting passengers
        synchronized (objectArr[ownerId]) {
            objectArr[ownerId].notify();
        }
    }

    /**
     * Announces that there are no more bags and wakes waiting passengers.
     *
     */
    @Override
    public synchronized void noMoreBags() {
        // Announce no more bags and wake waiting passengers
        noMoreLuggage = true;

        // Wake up all passengers
        if (people > 0) {
            for (int i = 0; i < peopleList.size(); i++) {
                synchronized (objectArr[peopleList.get(i)]) {
                    objectArr[peopleList.get(i)].notify();
                }
            }
        } else {
            this.notify();
        }

    }

    /**
     * Puts a bag in the storeroom.
     *
     */
    @Override
    public synchronized void putABagSR() throws RemoteException {
        // Lay bag in storage
        bagsInStorage++;
        zoneGeneral.setST(bagsInStorage);
    }

    /**
     * Reports more missing bags
     *
     * @param bags number of bags missing from this passenger
     */
    @Override
    public synchronized void reportLuggageMissing(int id, int bags) throws RemoteException {
        // Report bags lost
        lostBags += bags;
        zoneGeneral.reportBagsMissing(id, lostBags);
    }

    /**
     * Gets all the luggage from passenger with ID=id; Waits until passenger has
     * all his bags or until there are no more bags
     *
     * @param id of the passenger
     * @param lugTotal total number of bags he has to catch
     * @return how much luggage this passenger caught
     */
    @Override
    public int getLuggage(int id, int lugTotal) throws RemoteException {
        int lugCaught = 0;

        // Increase people in this zone
        synchronized (this) {
            people++;
            peopleList.add(id);
        }

        boolean wait1 = true, wait2;

        while (wait1) {
            synchronized (this) {
                // While passenger doesn't have all his bags and there are still bags to come
                wait1 = lugCaught < lugTotal && (!noMoreLuggage || bagagens.size() > 0);
                
                // While no bags in belt and baggage is coming OR the 1st baggage in the belt isn't from the passenger
                wait2 = (bagagens.size() == 0 && !noMoreLuggage) || (bagagens.size() > 0 && bagagens.peek() != id);
                
                if(!wait1)
                    break;
            }

            if (wait2) {
                synchronized (objectArr[id]) {
                    try {
                        objectArr[id].wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }

            synchronized (this) {
                // Take all bags that are his
                while (bagagens.size() > 0 && bagagens.peek() == id) {
                    bagagens.poll();
                    zoneGeneral.setLCB(bagagens.size(), id, false);

                    lugCaught++;
                    zoneGeneral.setPassengerCurrBags(id, lugCaught);
                }

                // Wake up passanger from the next bag
                if (bagagens.size() > 0) {
                    synchronized (objectArr[bagagens.peek()]) {
                        objectArr[bagagens.peek()].notify();
                    }
                }
            }
        }

        // Decrease people in this zone
        synchronized (this) {
            people--;
            peopleList.remove((Integer) id);

            // If last person, wake up plane
            if (people == 0) {
                this.notify();
            }

            // Return how much luggage this passenger caught
            return lugCaught;
        }
    }

    /**
     * Plane waits until porter has finished all his luggage.
     */
    @Override
    public synchronized void resetPlane() {
        // Wait while porter hasn't finished his luggage distribution
        while (!noMoreLuggage) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
            }
        }

        noMoreLuggage = false;
    }

    @Override
    public void unexport() throws RemoteException {
        UnicastRemoteObject.unexportObject(this, true);
    }

    @Override
    public final void restart(GeneralLuggage g, int pass) {
        lostBags = 0;
        bagsInStorage = 0;
        people = 0;
        bagagens = new LinkedList();
        noMoreLuggage = false;
        zoneGeneral = g;

        peopleList = new LinkedList();

        objectArr = new Object[pass]; //
        for (int i = 0; i < objectArr.length; i++) {
            objectArr[i] = new Object();
        }
    }
}
