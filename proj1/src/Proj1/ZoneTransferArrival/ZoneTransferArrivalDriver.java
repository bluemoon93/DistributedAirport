package Proj1.ZoneTransferArrival;

/**
 * Interface ZoneTransferArrivalDriver
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneTransferArrivalDriver {

    /**
     * Bus leaves this zone.
     *
     */
    public void depart();

    /**
     * Bus waits for 2seconds until enough people have arrived;
     * After the 2 seconds, if zone is still empty, returns 0;
     * Else, wakes passengers and waits for them to fill the bus or until there are no more passengers.
     *
     * @return how many people entered the bus
     */
    public int announceBusBoarding();
}
