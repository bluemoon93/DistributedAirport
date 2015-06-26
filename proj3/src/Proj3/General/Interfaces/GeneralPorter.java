package Proj3.General.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the General Monitor for the Porter Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface GeneralPorter extends Remote {

    /**
     * Sets state of Porter
     *
     * @param st state of Porter
     */
    public void setPorterState(int st) throws RemoteException;
}
