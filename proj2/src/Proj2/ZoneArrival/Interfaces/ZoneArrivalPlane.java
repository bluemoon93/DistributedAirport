package Proj2.ZoneArrival.Interfaces;

import Proj2.Luggage;

/**
 * Interface of the Zone Arrival Monitor for the Plane Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneArrivalPlane {
    /**
     * Adds new luggage to plane's hold
     *
     * @param t luggage to add
     */
    public void addLuggage(Luggage t);
}
