package Proj2.ZoneTD.Interfaces;

/**
 * Interface ZoneTDPass
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneTDPass {

    /**
     * Passengers wait until the bus arrives to this zone.
     */
    public void finishBusTrip();

    /**
     * Passenger leaves the bus and wakes up driver if the bus is empty.
     *
     * @param id of the passengers
     * @param reqId The ID of the Request
     */
    public void leaveBus(int id, int reqId);
}
