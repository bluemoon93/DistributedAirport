package Proj2.General.Interfaces;

/**
 * Interface of the General Monitor for the ZoneTransferDeparture Monitor
 *
 * @author Tiago Soares, David Simoes
 */
public interface GeneralTransferDeparture {

    /**
     * Removes the passenger with ID = id to the bus queue
     *
     * @param id of passenger
     */
    public void leaveBusQueue(int id);
}
