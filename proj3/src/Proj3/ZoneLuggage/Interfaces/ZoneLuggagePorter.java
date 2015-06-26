package Proj3.ZoneLuggage.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the ZoneLuggage Monitor for the Porter Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneLuggagePorter extends Remote {

    /**
     * Puts a bag at the luggage belt conveyor and wakes waiting passengers
     *
     * @param ownerId identification of the luggage owner
     */
    public void putABagLBC(int ownerId) throws RemoteException;

    /**
     * Announces that there are no more bags and wakes waiting passengers.
     */
    public void noMoreBags() throws RemoteException;

    /**
     * Puts a bag in the storeroom.
     *
     */
    public void putABagSR() throws RemoteException;
}
