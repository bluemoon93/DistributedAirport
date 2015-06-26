package Proj3.ZoneArrival.Interfaces;

import Proj3.Luggage;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the Zone Arrival Monitor for the Porter Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneArrivalPorter extends Remote {

    /**
     * Porter returns true immediatly if all planes have arrived or waits all passengers from the new plane have landed
     *
     * @return true if all planes have arrived else false
     */
    public boolean takeArest() throws RemoteException;

    /**
     * Gets number of luggage at the Plane's Hold
     *
     * @return how many bags are still left in the plane's hold
     */
    public int getLugNumber() throws RemoteException;

    /**
     * Gets a random bag
     *
     * @return bag from the plane
     */
    public Luggage getLug() throws RemoteException;
}
