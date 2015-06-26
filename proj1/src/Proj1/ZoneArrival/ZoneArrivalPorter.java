package Proj1.ZoneArrival;

import Proj1.Luggage;

/**
 * Interface ZoneArrivalPorter
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneArrivalPorter {

    /**
     * Porter returns true immediatly if all planes have arrived or waits all passengers from the new plane have landed
     *
     * @return true if all planes have arrived else false
     */
    public boolean takeArest();

    /**
     * Gets number of luggage at the Plane's Hold
     *
     * @return how many bags are still left in the plane's hold
     */
    public int getLugNumber();

    /**
     * Gets a random bag
     *
     * @return bag from the plane
     */
    public Luggage getLug();
}
