package Proj3.ZoneTA.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface ZoneTADriver
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneTADriver extends Remote {

    /**
     * Bus leaves this zone.
     *
     */
    public void depart() throws RemoteException;

    /**
     * Bus waits for 2seconds until enough people have arrived;
     * After the 2 seconds, if zone is still empty, returns 0;
     * Else, wakes passengers and waits for them to fill the bus or until there are no more passengers.
     *
     * @return how many people entered the bus
     */
    public int announceBusBoarding() throws RemoteException;
}
