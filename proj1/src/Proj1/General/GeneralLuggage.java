package Proj1.General;

/**
 * Interface GeneralLuggage
 *
 * @author Tiago Soares, David Simoes
 */
public interface GeneralLuggage {

    /**
     * Sets how many bags are currently in the Luggage Belt Conveyor at the LuggageZone
     *
     * @param n number of luggages in luggage belt conveyor
     */
    public void setLCB(int n, int owner, boolean add);

    /**
     * Sets how many bags are currently in the StoreRoom at the LuggageZone
     *
     * @param n number of luggages in storeRoom
     */
    public void setST(int n);

    /**
     * Sets how many bags the passenger currently has
     *
     * @param st number of pieces of luggage the passenger that he has presently
     * collected
     * @param id of the passenger
     */
    public void setPassengerCurrBags(int id, int st);

    /**
     * Sets how many bags have been given as missing through the day
     *
     * @param b number of missing bags
     */
    public void reportBagsMissing(int id, int b);
}
