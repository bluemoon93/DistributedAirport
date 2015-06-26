package Proj2.ZoneEntryExit.Interfaces;

/**
 * Interface of the ZoneEntryExit Monitor for the Driver Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneEntryExitDriver {

    /**
     * Gets total people that have left the airport
     *
     * @return number of total people
     */
    public int peopleLeft();
}
