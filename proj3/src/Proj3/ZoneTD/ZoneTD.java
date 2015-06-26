package Proj3.ZoneTD;

import Proj3.General.Interfaces.GeneralTransferDeparture;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class ZoneTD implements ZoneTDPass and
 * ZoneTDDriver interfaces
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneTD implements ZoneTDInterface {

    private boolean driver;
    private int peopleOnTheBus;
    private GeneralTransferDeparture zoneGeneral;

    /**
     * Constructor ZoneTD
     *
     * @param a GeneralTransfer monitor
     */
    public ZoneTD(GeneralTransferDeparture a) {
        this.restart(a);
    }

    /**
     * Parks bus in the transfer departure zone and sets how many people are about to
     * leave; Wakes all passengers on the bus and until they have left
     *
     * @param potb people on the bus
     */
    @Override
    public synchronized void park(int potb) {
        // Park bus in this zone and set how many people are about to leave
        driver = true;
        peopleOnTheBus = potb;

        // Wake all passengers on the bus
        this.notifyAll();

        // Wait while people on the bus
        while (peopleOnTheBus > 0) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * Bus leaves this zone.
     */
    @Override
    public synchronized void depart() {
        // Take bus from this zone
        driver = false;
    }

    /**
     * Passengers wait until the bus arrives to this zone.
     */
    @Override
    public synchronized void finishBusTrip() {
        // While bus not here
        while (driver == false) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * Passenger leaves the bus and wakes up driver if the bus is empty
     *
     * @param id of the passengers
     */
    @Override
    public synchronized void leaveBus(int id) throws RemoteException {
        // Leave the bus
        peopleOnTheBus--;
        zoneGeneral.leaveBusQueue(id);

        // Wake driver up if the bus is empty
        if (peopleOnTheBus == 0) {
            this.notify();
        }
    }

    @Override
    public void unexport() throws RemoteException {
        UnicastRemoteObject.unexportObject(this, true);
    }

    @Override
    public void restart(GeneralTransferDeparture a) {
        this.zoneGeneral = a;

        this.driver = false;
        this.peopleOnTheBus = 0;
    }
}
