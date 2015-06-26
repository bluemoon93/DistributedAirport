package Proj2.ZoneTA;

import Proj2.General.Interfaces.GeneralTransferArrival;
import Proj2.ZoneTA.Interfaces.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class ZoneTA implements ZoneTADriver and ZoneTAPass interfaces
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneTA implements ZoneTADriver, ZoneTAPass {

    private int nLugares, peopleOnTheBus;
    private boolean peopleCanComeIn;
    private GeneralTransferArrival zoneGeneral;

    private ReentrantLock lock;
    private Condition conditionDriver;
    private Queue<Condition> conditionPeople;

    /**
     * Constructor ZoneTA
     *
     * @param g GeneralTransfer monitor
     * @param nLug number of seats on the bus
     */
    public ZoneTA(GeneralTransferArrival g, int nLug) {
        this.restart(g, nLug);
    }

    /**
     * Bus leaves this zone.
     */
    @Override
    public void depart() {
        lock.lock();
        try {
            // Take bus from this zone
            peopleOnTheBus = 0;
            peopleCanComeIn = false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Bus waits for 2seconds until enough people have arrived; After the 2
     * seconds, if zone is still empty, returns 0; Else, wakes passengers and
     * waits for them to fill the bus or until there are no more passengers.
     *
     * @return how many people entered the bus
     */
    @Override
    public int announceBusBoarding() {
        lock.lock();

        try {
            // Wait for 2sec or until enough people arrive
            while (conditionPeople.size() == 0) {
                try {
                    this.conditionDriver.await(2000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ex) {
                }

                // If schedule over and no one here, go check if day is over
                if (conditionPeople.size() == 0) {
                    return 0;
                }
            }

            // Wake people waiting to enter the bus
            peopleCanComeIn = true;

            this.conditionPeople.peek().signal();

            // Wait while people in the zone and bus not full
            while (conditionPeople.size() > 0 && peopleOnTheBus < nLugares) {
                try {
                    this.conditionDriver.await();
                } catch (InterruptedException ex) {
                }
            }

            return peopleOnTheBus;
        } finally {
            lock.unlock();
        } 
    }

    /**
     * Passenger enters the queue waiting for the bus; If enough people to fill
     * the Bus are in the Zone, wakes up driver; Else, waits; After the bus has
     * opened it's doors, passenger waits until he is in front of the queue;
     * When he is, he leaves the queue and seats inside the bus; If there is
     * still room in the bus, alerts the next passenger in the queue; Else,
     * tells the driver to leave.
     *
     * @param id of the passenger
     */
    @Override
    public void takeABus(int id) {
        lock.lock();
        try {
            // Enter the queue waiting for the bus
            zoneGeneral.addWaitQueue(id);

            Condition tempConditionPeople = lock.newCondition();
            conditionPeople.add(tempConditionPeople);

            // If enough people to fill the bus here, wake up driver
            if (conditionPeople.size() == nLugares) {
                this.conditionDriver.signal();
            }

            // While passenger can't enter bus or not in front of queue
            while (!peopleCanComeIn || peopleOnTheBus >= nLugares || !conditionPeople.peek().equals(tempConditionPeople)) {
                try {
                    tempConditionPeople.await();
                } catch (InterruptedException ex) {
                }
            }

            // Leave wait queue, enter bus
            zoneGeneral.leaveWaitQueue();
            peopleOnTheBus++;
            zoneGeneral.addBusQueue(id);

            // Wake other passengers waiting (if bus has room left) or driver (if bus has no room left)
            this.conditionPeople.poll();

            if (!conditionPeople.isEmpty()) {
                this.conditionPeople.peek().signal();
            }

            this.conditionDriver.signal();

        } finally {
            lock.unlock();
        }
    }

    /**
     * Re-initialized all the variables in the monitor
     *
     * @param g GeneralTransfer monitor
     * @param nLug number of seats on the bus
     */
    protected final void restart(GeneralTransferArrival g, int nLug) {
        this.peopleCanComeIn = false;
        this.peopleOnTheBus = 0;

        this.nLugares = nLug;
        this.zoneGeneral = g;

        this.lock = new ReentrantLock();
        this.conditionDriver = lock.newCondition();
        this.conditionPeople = new LinkedList();
    }
}