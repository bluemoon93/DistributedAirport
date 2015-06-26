package Proj3.ZoneTA.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface ZoneTAPass
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneTAPass extends Remote {

    /**
     * Passenger enters the queue waiting for the bus; 
     * If enough people to fill the Bus are in the Zone, wakes up driver; Else, waits; 
     * After the bus has opened it's doors, passenger waits until he is in front of the queue;
     * When he is, he leaves the queue and seats inside the bus;
     * If there is still room in the bus, alerts the next passenger in the queue; Else, tells the driver to leave.
     *
     * @param id of the passenger
     */
    public void takeABus(int id) throws RemoteException;
}
