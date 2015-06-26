package Proj2.ZoneLuggage.Interfaces;

/**
 * Interface of the ZoneLuggage Monitor for the Porter Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneLuggagePorter {

    /**
     * Puts a bag at the luggage belt conveyor and wakes waiting passengers
     *
     * @param ownerId identification of the luggage owner
     */
    public void putABagLBC(int ownerId, int reqId);

    /**
     * Announces that there are no more bags and wakes waiting passengers.
     */
    public void noMoreBags(int reqId);

    /**
     * Puts a bag in the storeroom.
     *
     */
    public void putABagSR(int reqId);
}
