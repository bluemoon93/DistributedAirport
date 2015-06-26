package Proj3.ZoneLuggage.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of the ZoneLuggage Monitor for the Passenger Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneLuggagePass extends Remote {

    /**
     * Reports more missing bags
     *
     * @param bags number of bags missing from this passenger
     */
    public void reportLuggageMissing(int id, int bags) throws RemoteException;

    /**
     * Gets all the luggage from passenger with ID=id; Waits until passenger has all his bags or until there are no more bags
     *
     * @param id of the passenger
     * @param lugTotal total number of bags he has to catch
     * @return how much luggage this passenger caught
     */
    public int getLuggage(int id, int lugTotal) throws RemoteException;
}
