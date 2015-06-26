package Proj3.ZoneEntryExit.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the ZoneEntryExit Monitor for the Plane Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneEntryExitPlane extends Remote {
    
    /**
     * Checks if everyone has left or not and waits while people are still moving around the airport.
     */
    public void resetPlane() throws RemoteException;
}
