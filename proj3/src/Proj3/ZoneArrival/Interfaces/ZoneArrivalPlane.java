package Proj3.ZoneArrival.Interfaces;

import Proj3.Luggage;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the Zone Arrival Monitor for the Plane Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneArrivalPlane extends Remote {
    /**
     * Adds new luggage to plane's hold
     *
     * @param t luggage to add
     */
    public void addLuggage(Luggage t) throws RemoteException;
}
