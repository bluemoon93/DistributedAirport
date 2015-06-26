package Proj3.General.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the General Monitor for the ZoneTransferArrival Monitor
 *
 * @author Tiago Soares, David Simoes
 */
public interface GeneralTransferArrival extends Remote {

    /**
     * Adds the passenger with ID = id to the waiting queue at the Arrival Transfer Zone
     *
     * @param id of passenger
     */
    public void addWaitQueue(int id) throws RemoteException;

    /**
     * Removes the 1st passenger from the waiting queue at the Arrival Transfer Zone.
     */
    public void leaveWaitQueue() throws RemoteException;

    /**
     * Adds the passenger with ID = id to the bus queue
     *
     * @param id of passenger
     */
    public void addBusQueue(int id) throws RemoteException;
}
