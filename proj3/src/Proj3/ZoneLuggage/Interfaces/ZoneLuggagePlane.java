package Proj3.ZoneLuggage.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the ZoneLuggage Monitor for the Plane Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneLuggagePlane extends Remote {
    
    /**
     * Plane waits until porter has finished all his luggage.
     */
    public void resetPlane() throws RemoteException;
}
