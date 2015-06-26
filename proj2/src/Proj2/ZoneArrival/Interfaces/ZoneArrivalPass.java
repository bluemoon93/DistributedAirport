package Proj2.ZoneArrival.Interfaces;

/**
 * Interface of the Zone Arrival Monitor for the Passenger Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneArrivalPass {

    /**
     * Increases number of passengers in the zone and wakes up porter if there are enough people.
     */
    public void iGotHere();
}
