package Proj1.ZoneArrival;

import Proj1.Luggage;
import java.util.LinkedList;
import java.util.List;

/**
 * Class ZoneArrival implements ZoneArrivalPass and ZoneArrivalPorter interfaces
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneArrival implements ZoneArrivalPass, ZoneArrivalPorter {

    private int peopleArriving, planesArrived, nAvioes, nPassageiros;
    private List<Luggage> bagagens = new LinkedList<>();

    /**
     * Constructor of Class ZoneArrival
     *
     * @param nAv number of planes for the day
     * @param nPass number of passengers per plane
     */
    public ZoneArrival(int nAv, int nPass) {
        nAvioes = nAv;
        nPassageiros = nPass;
    }

    /**
     * Adds new luggage to plane's hold
     *
     * @param t luggage to add
     */
    public synchronized void addLuggage(Luggage t) {
        // Add new luggage to plane's hold
        bagagens.add(t);
    }

    /**
     * Porter returns true immediatly if all planes have arrived or waits all passengers from the new plane have landed
     *
     * @return true if all planes have arrived else false
     */
    @Override
    public synchronized boolean takeArest() {
        // If all planes have arrived, return true
        if (planesArrived == nAvioes) {
            return true;
        }

        // Wait while not enough people here
        while (peopleArriving < nPassageiros) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
            }
        }

        // Reset people, increase plane count
        peopleArriving = 0;
        planesArrived++;

        return false;
    }

    /**
     * Increases number of passengers in the zone and wakes up porter if there are enough people.
     */
    @Override
    public synchronized void iGotHere() {
        // Increase people in this zone
        peopleArriving++;

        // If enough people here, wake up porter
        if (peopleArriving % nPassageiros == 0) {
            this.notify();
        }
    }

    /**
     * Gets number of luggage at the Plane's Hold
     *
     * @return how many bags are still left in the plane's hold
     */
    @Override
    public synchronized int getLugNumber() {
        // Return how many bags are still left in the plane's hold
        return bagagens.size();
    }

    /**
     * Gets a random bag
     *
     * @return bag from the plane
     */
    @Override
    public synchronized Luggage getLug() {
        // Get a random bag from the plane
        Luggage temp = bagagens.remove((int) (Math.random() * bagagens.size()));
        return temp;
    }
}
