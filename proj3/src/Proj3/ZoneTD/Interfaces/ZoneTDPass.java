package Proj3.ZoneTD.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface ZoneTDPass
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneTDPass extends Remote {

    /**
     * Passengers wait until the bus arrives to this zone.
     */
    public void finishBusTrip() throws RemoteException;

    /**
     * Passenger leaves the bus and wakes up driver if the bus is empty.
     *
     * @param id of the passengers
     */
    public void leaveBus(int id) throws RemoteException;
}
