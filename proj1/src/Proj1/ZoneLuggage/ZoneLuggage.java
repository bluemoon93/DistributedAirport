package Proj1.ZoneLuggage;

import Proj1.General.GeneralLuggage;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Class ZoneLuggage implements ZoneLuggagePass and ZoneLuggagePorter interfaces
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneLuggage implements ZoneLuggagePass, ZoneLuggagePorter {

    private int bagsInStorage, people, lostBags;
    private Queue<Integer> bagagens;
    private boolean noMoreLuggage;
    private GeneralLuggage zoneGeneral;
    private Object[] objectArr;
    private List<Integer> lista;

    /**
     * Constructor ZoneLuggage
     *
     * @param g GeneralLuggage Monitor
     */
    public ZoneLuggage(GeneralLuggage g, int pass) {
        lostBags = 0;
        bagsInStorage = 0;
        people = 0;
        bagagens = new LinkedList();
        noMoreLuggage = false;
        zoneGeneral = g;

        lista = new LinkedList();

        objectArr = new Object[pass]; //
        for (int i = 0; i < objectArr.length; i++) {
            objectArr[i] = new Object();
        }
    }

    /**
     * Puts a bag at the luggage belt conveyor and wakes waiting passengers
     *
     * @param ownerId identification of the luggage owner
     */
    @Override
    public synchronized void putABagLBC(int ownerId) {
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
            for (int i = 0; i < lista.size(); i++) {
                synchronized (objectArr[lista.get(i)]) {
                    objectArr[lista.get(i)].notify();
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
    public synchronized void putABagSR() {
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
    public synchronized void reportLuggageMissing(int id, int bags) {
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
    public int getLuggage(int id, int lugTotal) {
        int lugCaught = 0;

        // Increase people in this zone
        synchronized (this) {
            people++;
            lista.add(id);
        }

        boolean wait1 = true, wait2;

        while (wait1) {
            synchronized (this) {
                wait1 = lugCaught < lugTotal && (!noMoreLuggage || bagagens.size() > 0);
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
            lista.remove((Integer) id);

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
}
