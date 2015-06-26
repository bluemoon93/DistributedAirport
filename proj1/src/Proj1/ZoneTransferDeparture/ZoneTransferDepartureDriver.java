package Proj1.ZoneTransferDeparture;

/**
 * Interface ZoneTransferDepartureDriver
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneTransferDepartureDriver {

    /**
     * Parks bus in the transfer departure zone and sets how many people are about to
     * leave, Wakes all passengers on the bus and until they have left
     *
     * @param potb people on the bus
     */
    public void park(int potb);

    /**
     * Bus leaves this zone.
     */
    public void depart();
}
