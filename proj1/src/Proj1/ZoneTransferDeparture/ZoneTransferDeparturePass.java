package Proj1.ZoneTransferDeparture;

/**
 * Interface ZoneTransferDeparturePass
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneTransferDeparturePass {

    /**
     * Passengers wait until the bus arrives to this zone.
     */
    public void finishBusTrip();

    /**
     * Passenger leaves the bus and wakes up driver if the bus is empty
     *
     * @param id of the passengers
     */
    public void leaveBus(int id);
}
