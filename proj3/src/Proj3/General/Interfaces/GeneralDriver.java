package Proj3.General.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the General Monitor for the Driver Thread.
 *
 * @author Tiago Soares, David Simoes
 */
public interface GeneralDriver extends Remote {

    /**
     * Sets state of Driver
     *
     * @param st state of Driver
     */
    public void setDriverState(int st) throws RemoteException;
}
