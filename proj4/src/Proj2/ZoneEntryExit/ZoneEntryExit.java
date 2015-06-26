package Proj2.ZoneEntryExit;

import Proj2.General.Interfaces.GeneralPass;
import Proj2.ZoneEntryExit.Interfaces.*;

/**
 * Class ZoneEntryExit implements ZoneEntryExitDriver and ZoneEntryExitPass
 * interfaces
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneEntryExit implements ZoneEntryExitDriver, ZoneEntryExitPass, ZoneEntryExitPlane {

    private int peopleWaitingToGoHome, totalPeople, nPassageiros;
    private boolean everyoneIsFree;
    private GeneralPass zoneGeneral;

    /**
     * Constructor ZoneEntryExit
     *
     * @param nPass number of passengers per plane
     * @param g GeneralPassenger monitor
     */
    public ZoneEntryExit(int nPass, GeneralPass g) {
        this.restart(nPass, g);
    }

    /**
     * Gets total people that have left the airport
     *
     * @return number of total people
     */
    @Override
    public synchronized int peopleLeft() {
        return totalPeople;
    }

    /**
     * Checks if caller is the last passenger leaving If true, wakes up all other passengers; else, waits
     * When everyone else has left, wakes up Plane thread for a new plane to come.
     *
     * @param id of the passenger
     */
    @Override
    public synchronized void goingHome(int id) {
        // Increase people in this zone
        peopleWaitingToGoHome++;

        // If last passenger, wake up all others sleeping here
        if (peopleWaitingToGoHome == nPassageiros) {
            everyoneIsFree = true;
            this.notifyAll();
        } // Else sleep until everyone arrives
        else {
            while (!everyoneIsFree) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                }
            }
        }

        // Increase total people that have come through this zone
        totalPeople++;

        // Decrease people in the zone
        peopleWaitingToGoHome--;

        // If last passenger leaving airport, wake up Plane thread
        if (peopleWaitingToGoHome == 0) {
            this.notify();
        }

        // Show Log that thread has ended
        zoneGeneral.setPassengerState(id, -1);
    }

    /**
     * Checks if everyone has left or not and waits while people are still moving around the airport.
     */
    @Override
    public synchronized void resetPlane() {
        // Wait while people are still moving around the airport
        while (peopleWaitingToGoHome != 0 || !everyoneIsFree) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
            }
        }

        everyoneIsFree = false;
    }

    /**
     * Re-initialized all the variables in the monitor
     *
     * @param nPass number of passengers per plane
     * @param g GeneralPassenger monitor
     */
    protected final void restart(int nPass, GeneralPass g) {
        this.everyoneIsFree = false;
        
        this.peopleWaitingToGoHome = 0;
        this.totalPeople=0;
        this.nPassageiros = nPass;
        
        this.zoneGeneral = g;
    }
}
