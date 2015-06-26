package Proj1;

import Proj1.General.GeneralDriver;
import Proj1.ZoneTransferDeparture.ZoneTransferDepartureDriver;
import Proj1.ZoneTransferArrival.ZoneTransferArrivalDriver;
import Proj1.ZoneEntryExit.ZoneEntryExitDriver;
import java.util.Random;

/**
 * Thread Driver
 *
 * @author Tiago Soares, David Simoes
 */
public class Driver extends Thread {

    private int state, nAvioes, nPassageiros, people;
    private ZoneTransferArrivalDriver zonaTA;
    private ZoneTransferDepartureDriver zonaTD;
    private ZoneEntryExitDriver zoneEnEx;
    private GeneralDriver zoneGeneral;
    private Random r;
    private static final int PARKING_AT_THE_ARRIVAL_TERMINAL = 0, DRIVING_FORWARD = 1, PARKING_AT_THE_DEPARTURE_TERMINAL = 2, DRIVING_BACKWARD = 3;

    /**
     * Constructor of Driver Thread
     *
     * @param a ZoneTransferArrivalDriver monitor
     * @param b ZoneTransferDepartureDriver monitor
     * @param c ZoneEntryExitDriver monitor
     * @param g GeneralDriver monitor
     * @param nAv number of planes for the airport
     * @param nPass number of passengers for each plane
     */
    protected Driver(ZoneTransferArrivalDriver a, ZoneTransferDepartureDriver b, ZoneEntryExitDriver c, GeneralDriver g, int nAv, int nPass) {
        this.nAvioes = nAv;
        this.nPassageiros = nPass;
        this.state = PARKING_AT_THE_ARRIVAL_TERMINAL;

        this.zonaTA = a;
        this.zonaTD = b;
        this.zoneEnEx = c;
        this.zoneGeneral = g;

        this.zoneGeneral.setDriverState(state);

        this.r = new Random();
    }

    /**
     * Calls all of functions (transitions between states) that are involved in
     * the lifecycle of the Driver; At the beggining, we start with parkTheBus()
     * method and then do the full lifecycle until the thread is finished.
     *
     */
    @Override
    public void run() {
        parkTheBus();
    }

    /** The driver leaves the Transfer Arrival Zone and starts going to the Departure Terminal Zone;
     * calls depart() method which belongs to the ZoneTransferArrival monitor and sets the Driver state.
     */
    private void goToDepartureTerminal() {
        // Leave from the Transfer Arrival Zone and drive to the TDZ
        zonaTA.depart();

        if (state == PARKING_AT_THE_ARRIVAL_TERMINAL) {
            state = DRIVING_FORWARD;
            zoneGeneral.setDriverState(state);
        }

        randomSleep(100);

        parkTheBusAndLetPassOf();
    }

    /** The driver parks the bus in the departure terminal zone;
     * calls park(people) method which belongs to the ZoneTransferDeparture monitor.
     */
    private void parkTheBusAndLetPassOf() {
        if (state == DRIVING_FORWARD) {
            state = PARKING_AT_THE_DEPARTURE_TERMINAL;
            zoneGeneral.setDriverState(state);
        }

        randomSleep(100);

        // Park at the Transfer Departure Zone and let passengers off
        zonaTD.park(people);

        goToArrivalTerminal();
    }

    /** The driver goes to the arrival terminal 
     * call depart() method wich belongs to the ZoneTransferDeparture monitor. 
     */
    private void goToArrivalTerminal() {
        // Leave from the Transfer Departure Zone and let passengers off
        zonaTD.depart();

        if (state == PARKING_AT_THE_DEPARTURE_TERMINAL) {
            state = DRIVING_BACKWARD;
            zoneGeneral.setDriverState(state);
        }

        randomSleep(100);

        parkTheBus();
    }

    /**When driver park the bus in arrival terminal.
     */
    private void parkTheBus() {
        if (state == DRIVING_BACKWARD) {
            state = PARKING_AT_THE_ARRIVAL_TERMINAL;
            zoneGeneral.setDriverState(state);
        }

        randomSleep(100);

        // If there are still people in the airport
        if (!hasDaysWorkEnded()) {
            announcingBusBoarding();
        } // If no more passengers
        else {
            zoneGeneral.setDriverState(-1);
        }
    }
    
    /** Checks if the driver days work has ended or not.
     */

    private boolean hasDaysWorkEnded() {
        return zoneEnEx.peopleLeft() == nAvioes * nPassageiros;
    }

    /** Driver waits until there are enough passengers to wait for entering the bus
     and will notify them to get in
     call announceBusBoarding() method wich belongs to the ZoneTransferArrival monitor.
     */
    private void announcingBusBoarding() {
        // If people entered the bus before the schedule
        if ((people = zonaTA.announceBusBoarding()) != 0) {
            goToDepartureTerminal();
        } // If no one entered the bus but there are still people in the airport
        else if (!hasDaysWorkEnded()) {
            announcingBusBoarding();
        } // If there are no more people in the airport
        else {
            zoneGeneral.setDriverState(-1);
        }
    }

    private void randomSleep(int n) {
        try {
            Thread.sleep(Math.abs(r.nextInt()) % n);
        } catch (InterruptedException ex) {
        }
    }
}
