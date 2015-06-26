package Proj1.ZoneLuggage;

/**
 * Interface ZoneLuggagePorter
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneLuggagePorter {

    /**
     * Puts a bag at the luggage belt conveyor and wakes waiting passengers
     *
     * @param ownerId identification of the luggage owner
     */
    public void putABagLBC(int ownerId);

    /**
     * Announces that there are no more bags and wakes waiting passengers.
     */
    public void noMoreBags();

    /**
     * Puts a bag in the storeroom.
     *
     */
    public void putABagSR();
}
