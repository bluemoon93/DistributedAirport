package Proj3.General.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the General Monitor for the ZoneLuggage Monitor
 *
 * @author Tiago Soares, David Simoes
 */
public interface GeneralLuggage extends Remote {

    /**
     * Sets how many bags are currently in the Luggage Belt Conveyor at the LuggageZone
     *
     * @param n number of luggages in luggage belt conveyor
     * @throws java.rmi.RemoteException
     */
    public void setLCB(int n, int owner, boolean add) throws RemoteException;

    /**
     * Sets how many bags are currently in the StoreRoom at the LuggageZone
     *
     * @param n number of luggages in storeRoom
     */
    public void setST(int n) throws RemoteException;

    /**
     * Sets how many bags the passenger currently has
     *
     * @param st number of pieces of luggage the passenger that he has presently
     * collected
     * @param id of the passenger
     */
    public void setPassengerCurrBags(int id, int st) throws RemoteException;

    /**
     * Sets how many bags have been given as missing through the day
     *
     * @param b number of missing bags
     */
    public void reportBagsMissing(int id, int b) throws RemoteException;
}
