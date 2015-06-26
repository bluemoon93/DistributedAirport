package Proj2.ZoneEntryExit.Interfaces;

/**
 * Interface of the ZoneEntryExit Monitor for the Passenger Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneEntryExitPass {

    /**
     * Checks if caller is the last passenger leaving; If true, wakes up all other passengers; else, waits;
     * When everyone else has left, wakes up Plane thread for a new plane to come.
     *
     * @param id of the passenger
     */
    public void goingHome(int id);
}
