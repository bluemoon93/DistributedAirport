package Proj3.Plane;

import Proj3.Plane.Passenger.Passenger;
import Proj3.General.Interfaces.GeneralPass;
import Proj3.General.Interfaces.GeneralPlane;
import Proj3.Luggage;
import Proj3.ZoneArrival.Interfaces.ZoneArrivalPass;
import Proj3.ZoneArrival.Interfaces.ZoneArrivalPlane;
import Proj3.ZoneEntryExit.Interfaces.ZoneEntryExitPass;
import Proj3.ZoneEntryExit.Interfaces.ZoneEntryExitPlane;
import Proj3.ZoneLuggage.Interfaces.ZoneLuggagePass;
import Proj3.ZoneLuggage.Interfaces.ZoneLuggagePlane;
import Proj3.ZoneTA.Interfaces.ZoneTAPass;
import Proj3.ZoneTD.Interfaces.ZoneTDPass;
import java.rmi.RemoteException;
import java.util.Random;

/**
 * Thread Plane
 *
 * @author Tiago Soares, David Simoes
 */
public class Plane extends Thread {

    private final int nAvioes, nPassageiros, nMalas;
    private final GeneralPlane zoneGeneralPlane;
    private final ZoneArrivalPlane zoneAPlane;
    private final ZoneEntryExitPlane zoneEnExPlane;
    private final ZoneLuggagePlane zoneLug;
    private final GeneralPass zoneGeneralPass;
    private final ZoneArrivalPass zoneAPass;
    private final ZoneEntryExitPass zoneEnExPass;
    private final ZoneLuggagePass zoneLugPass;
    private final ZoneTAPass zoneTAPass;
    private final ZoneTDPass zoneTDPass;

    /**
     * Constructor of Driver Thread
     *
     * @param nAvioes number of planes for the airport
     * @param nPassageiros number of passengers for each plane
     * @param nMalas maximum number of bags for each passenger
     * @param zoneGeneralPlane General monitor (with the Plane's Interface)
     * @param zoneAPlane ZoneArrival monitor (with the Plane's Interface)
     * @param zoneEnExPlane ZoneExtryExit monitor (with the Plane's Interface)
     * @param zoneTDPass ZoneTransferDeparture monitor (with the Passenger's
     * Interface)
     * @param zoneLug ZoneArrival monitor (with the Plane's Interface)
     * @param zoneAPass ZoneArrival monitor (with the Passenger's Interface)
     * @param zoneEnExPass ZoneEntryExit monitor (with the Passenger's
     * Interface)
     * @param zoneGeneralPass General monitor (with the Passenger's Interface)
     * @param zoneTAPass ZoneTransferArrival monitor (with the Passenger's
     * Interface)
     * @param zoneLugPass ZoneLuggage monitor (with the Passenger's Interface)
     */
    public Plane(int nAvioes, int nPassageiros, int nMalas, GeneralPlane zoneGeneralPlane, ZoneArrivalPlane zoneAPlane, ZoneEntryExitPlane zoneEnExPlane, ZoneLuggagePlane zoneLug,
            GeneralPass zoneGeneralPass, ZoneArrivalPass zoneAPass, ZoneEntryExitPass zoneEnExPass, ZoneLuggagePass zoneLugPass, ZoneTAPass zoneTAPass, ZoneTDPass zoneTDPass) {
        this.nAvioes = nAvioes;
        this.nPassageiros = nPassageiros;
        this.nMalas = nMalas;

        this.zoneGeneralPlane = zoneGeneralPlane;
        this.zoneAPlane = zoneAPlane;
        this.zoneEnExPlane = zoneEnExPlane;
        this.zoneLug = zoneLug;

        this.zoneGeneralPass = zoneGeneralPass;
        this.zoneAPass = zoneAPass;
        this.zoneEnExPass = zoneEnExPass;
        this.zoneLugPass = zoneLugPass;
        this.zoneTAPass = zoneTAPass;
        this.zoneTDPass = zoneTDPass;
    }

    /**
     * Simulates the coming and going of multiple airplanes in the airport, each
     * with some passengers and some bags; Is responsible for starting the
     * Passenger Threads as well as resetting zones between planes.
     *
     */
    @Override
    public void run() {
        // For each plane
        try {
            for (int i = 0; i < nAvioes; i++) {
                zoneGeneralPlane.resetLog();

                // Set number of bags for each person
                int malasNoAviao = 0;
                int[] malas = new int[nPassageiros];
                Random r = new Random();
                for (int j = 0; j < nPassageiros; j++) {
                    malas[j] = Math.abs(r.nextInt()) % (nMalas + 1);
                    malasNoAviao += malas[j];
                }

                // Set FN and how many bags should be on the plane (some may still get lost)
                zoneGeneralPlane.setMalas(i, malasNoAviao);

                // Create all passengers and set their bags/travel status
                Passenger[] people = new Passenger[nPassageiros];
                for (int j = 0; j < nPassageiros; j++) {

                    boolean journeyOver = r.nextBoolean();
                    
                    for (int k = 0; k < malas[j]; k++) {
                        if (r.nextDouble() < 0.8 || !journeyOver) {
                            zoneAPlane.addLuggage(new Luggage(j, !journeyOver));
                        }
                    }

                    zoneGeneralPlane.setPassengerTravel(j, journeyOver);
                    zoneGeneralPlane.setPassengerMaxBags(j, malas[j]);

                    people[j] = new Passenger(j, zoneAPass, zoneLugPass, zoneEnExPass, zoneTAPass, zoneTDPass, malas[j], journeyOver, zoneGeneralPass);
                }

                // Start all passengers
                for (int j = 0; j < nPassageiros; j++) {
                    people[j].start();
                }

                // Wait for passengers to end
                zoneEnExPlane.resetPlane();
                // Wait for porter to be ready for a new plane
                zoneLug.resetPlane();

                // Reset FN
                zoneGeneralPlane.setMalas(-1, 0);
                System.out.print(".");
            }
        } catch (RemoteException ex) {
            System.out.println("Remote exception ocurred! " + ex);
            ex.printStackTrace();
        }

        System.out.println();
    }
}
