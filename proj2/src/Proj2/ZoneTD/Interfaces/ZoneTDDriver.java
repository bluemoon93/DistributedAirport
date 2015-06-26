package Proj2.ZoneTD.Interfaces;

/**
 * Interface ZoneTDDriver
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneTDDriver {

    /**
     * Parks bus in the transfer departure zone and sets how many people are about to
     * leave; Wakes all passengers on the bus and until they have left.
     *
     * @param potb people on the bus
     * @param reqId The ID of the Request
     */
    public void park(int potb, int reqId);

    /**
     * Bus leaves this zone.
     *
     * @param reqId The ID of the Request
     */
    public void depart(int reqId);
}
