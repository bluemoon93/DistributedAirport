package Proj3.General.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the General Monitor for the ZoneTransferDeparture Monitor
 *
 * @author Tiago Soares, David Simoes
 */
public interface GeneralTransferDeparture extends Remote {

    /**
     * Removes the passenger with ID = id to the bus queue
     *
     * @param id of passenger
     */
    public void leaveBusQueue(int id) throws RemoteException;
}
