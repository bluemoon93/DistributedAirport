package Proj3.ZoneArrival.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the Zone Arrival Monitor for the Passenger Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneArrivalPass extends Remote {

    /**
     * Increases number of passengers in the zone and wakes up porter if there are enough people.
     */
    public void iGotHere() throws RemoteException;
}
