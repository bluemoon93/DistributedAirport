package Proj3.ZoneEntryExit.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the ZoneEntryExit Monitor for the Passenger Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneEntryExitPass extends Remote {

    /**
     * Checks if caller is the last passenger leaving; If true, wakes up all other passengers; else, waits;
     * When everyone else has left, wakes up Plane thread for a new plane to come.
     *
     * @param id of the passenger
     */
    public void goingHome(int id) throws RemoteException;
}
