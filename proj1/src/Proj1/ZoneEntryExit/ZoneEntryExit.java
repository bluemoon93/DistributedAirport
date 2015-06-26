package Proj1.ZoneEntryExit;

import Proj1.General.GeneralPass;

/**
 * Class ZoneEntryExit implements ZoneEntryExitDriver and ZoneEntryExitPass
 * interfaces
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneEntryExit implements ZoneEntryExitDriver, ZoneEntryExitPass {

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
        everyoneIsFree = false;
        this.peopleWaitingToGoHome = 0;

        nPassageiros = nPass;
        zoneGeneral = g;
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
     * Checks if everyone has left or not and waits while people are still moving around the airport
     *
     * @return true or false
     */
    public synchronized boolean resetPlane() {
        // Wait while people are still moving around the airport
        while (peopleWaitingToGoHome != 0 || !everyoneIsFree) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
            }
        }

        everyoneIsFree = false;

        return true;
    }
}
