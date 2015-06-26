package Proj3.ZoneTD.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface ZoneTDDriver
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneTDDriver extends Remote {

    /**
     * Parks bus in the transfer departure zone and sets how many people are about to
     * leave; Wakes all passengers on the bus and until they have left.
     *
     * @param potb people on the bus
     */
    public void park(int potb) throws RemoteException;

    /**
     * Bus leaves this zone.
     *
     */
    public void depart() throws RemoteException;
}
