package Proj1.General;

/**
 * Interface GeneralTransfer
 *
 * @author Tiago Soares, David Simoes
 */
public interface GeneralTransfer {

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

    /**
     * Removes the passenger with ID = id to the bus queue
     *
     * @param id of passenger
     */
    public void leaveBusQueue(int id);
}
