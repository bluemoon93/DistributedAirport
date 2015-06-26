package Proj3.ZoneEntryExit.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the ZoneEntryExit Monitor for the Driver Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneEntryExitDriver extends Remote {

    /**
     * Gets total people that have left the airport
     *
     * @return number of total people
     */
    public int peopleLeft() throws RemoteException;
}
