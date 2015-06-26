package Proj2.ZoneEntryExit.Interfaces;

/**
 * Interface of the ZoneEntryExit Monitor for the Plane Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneEntryExitPlane {
    
    /**
     * Checks if everyone has left or not and waits while people are still moving around the airport.
     */
    public void resetPlane();
}
