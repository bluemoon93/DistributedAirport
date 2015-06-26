package Proj2.Driver;

import Proj2.General.Interfaces.GeneralDriver;
import Proj2.ZoneEntryExit.Interfaces.ZoneEntryExitDriver;
import Proj2.ZoneTA.Interfaces.ZoneTADriver;
import Proj2.ZoneTD.Interfaces.ZoneTDDriver;
import java.util.Random;

/**
 * Thread Driver
 *
 * @author Tiago Soares, David Simoes
 */
public class Driver extends Thread {

    private final int nAvioes, nPassageiros;
    private int state, people, requestId;
    private final ZoneTADriver zonaTA;
    private final ZoneTDDriver zonaTD;
    private final ZoneEntryExitDriver zoneEnEx;
    private final GeneralDriver zoneGeneral;
    private final Random r;
    private static final int PARKING_AT_THE_ARRIVAL_TERMINAL = 0, DRIVING_FORWARD = 1, PARKING_AT_THE_DEPARTURE_TERMINAL = 2, DRIVING_BACKWARD = 3;

    /**
     * Constructor of Driver Thread
     *
     * @param a ZoneTransferArrival monitor (with the Driver's Interface)
     * @param b ZoneTransferDeparture monitor (with the Driver's Interface)
     * @param c ZoneEntryExit monitor (with the Driver's Interface)
     * @param g General monitor (with the Driver's Interface)
     * @param nAv number of planes for the airport
     * @param nPass number of passengers for each plane
     */
    public Driver(ZoneTADriver a, ZoneTDDriver b, ZoneEntryExitDriver c, GeneralDriver g, int nAv, int nPass) {
        this.nAvioes = nAv;
        this.nPassageiros = nPass;
        this.state = PARKING_AT_THE_ARRIVAL_TERMINAL;
        this.people = 0;
        
        this.zonaTA = a;
        this.zonaTD = b;
        this.zoneEnEx = c;
        this.zoneGeneral = g;

        this.zoneGeneral.setDriverState(state);

        this.r = new Random();
        this.requestId=0;
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

    private void parkTheBusAndLetPassOf() {
        if (state == DRIVING_FORWARD) {
            state = PARKING_AT_THE_DEPARTURE_TERMINAL;
            zoneGeneral.setDriverState(state);
        }

        randomSleep(100);

        // Park at the Transfer Departure Zone and let passengers off
        requestId++;
        zonaTD.park(people, requestId);

        goToArrivalTerminal();
    }

    private void goToArrivalTerminal() {
        // Leave from the Transfer Departure Zone and let passengers off
        requestId++;
        zonaTD.depart(requestId);

        if (state == PARKING_AT_THE_DEPARTURE_TERMINAL) {
            state = DRIVING_BACKWARD;
            zoneGeneral.setDriverState(state);
        }

        randomSleep(100);

        parkTheBus();
    }

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
    
    private boolean hasDaysWorkEnded() {
        return zoneEnEx.peopleLeft() == nAvioes * nPassageiros;
    }

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