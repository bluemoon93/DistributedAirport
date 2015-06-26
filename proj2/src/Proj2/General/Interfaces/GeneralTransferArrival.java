package Proj2.General.Interfaces;

/**
 * Interface of the General Monitor for the ZoneTransferArrival Monitor
 *
 * @author Tiago Soares, David Simoes
 */
public interface GeneralTransferArrival {

    /**
     * Adds the passenger with ID = id to the waiting queue at the Arrival Transfer Zone
     *
     * @param id of passenger
     */
    public void addWaitQueue(int id);

    /**
     * Removes the 1st passenger from the waiting queue at the Arrival Transfer Zone.
     */
    public void leaveWaitQueue();

    /**
     * Adds the passenger with ID = id to the bus queue
     *
     * @param id of passenger
     */
    public void addBusQueue(int id);
}
