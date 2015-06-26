package Proj1;

import Proj1.General.*;
import Proj1.ZoneTransferDeparture.*;
import Proj1.ZoneTransferArrival.*;
import Proj1.ZoneLuggage.*;
import Proj1.ZoneEntryExit.*;
import Proj1.ZoneArrival.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

/**
 * Class Main
 *
 * @author Tiago Soares, David Simoes
 */
public class Airport {

    //private static int nAvioes = 1000, nPassageiros = 30, nMalas = 15, nLugares = 10;
    private static int nAvioes = 5, nPassageiros = 6, nMalas = 2, nLugares = 3;
    private static String logName = "log.txt";
    private static BufferedWriter outFile = null;

    /**
     * Function which parses arguments, initializes logging and zone monitors,
     * creates and starts threads Porter, Driver e Passenger and creates log file
     *
     * @param args list of arguments (the length of args could be 4 ([nAvioes,
     * nPassageiros, nMalas, nLugares]), 1 ([logName]), 5 ([nAvioes, nPassageiros,
     * nMalas, nLugares, logName]) or 0 (nothing is changed). Variables will be
     * defaulted when they are not defined with arguments)
     */
    public static void main(String[] args) {
        //Parse arguments, set variables
        try {
            if (args.length == 4) {
                nAvioes = Integer.parseInt(args[0]);
                nPassageiros = Integer.parseInt(args[1]);
                nMalas = Integer.parseInt(args[2]);
                nLugares = Integer.parseInt(args[3]);
            } else if (args.length == 1) {
                logName = args[0];
            } else if (args.length == 5) {
                nAvioes = Integer.parseInt(args[0]);
                nPassageiros = Integer.parseInt(args[1]);
                nMalas = Integer.parseInt(args[2]);
                nLugares = Integer.parseInt(args[3]);
                logName = args[4];
            } else if (args.length == 0) {
                // DO NOTHING
            } else {
                throw new Exception();
            }
            //logName is the log file
            outFile = new BufferedWriter(new FileWriter(logName));
        } catch (Exception ex) {
            System.out.println("Usage: java -jar SD_T1_P1_G2.jar [nAvioes nPassageiros nMalas nLugares] [logNameFile]");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar log.txt");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar 5 6 2 3");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar 5 6 2 3 log.txt");
            System.exit(1);
        }

        System.out.println("Simulation started.");
        System.out.println("nAvioes = " + nAvioes);
        System.out.println("nPassageiros = " + nPassageiros);
        System.out.println("nMalas = " + nMalas);
        System.out.println("nLugares = " + nLugares);
        System.out.println("The log file is named \"" + logName + "\"");

        // Intialize logging monitor
        General zoneGeneral = new General(nPassageiros, nLugares, outFile);

        // Intialize zone monitors
        ZoneArrival zoneA = new ZoneArrival(nAvioes, nPassageiros);
        ZoneEntryExit zoneEnEx = new ZoneEntryExit(nPassageiros, (GeneralPass) zoneGeneral);
        ZoneLuggage zoneLug = new ZoneLuggage((GeneralLuggage) zoneGeneral, nPassageiros);
        ZoneTransferArrival zoneTA = new ZoneTransferArrival((GeneralTransfer) zoneGeneral, nLugares);
        ZoneTransferDeparture zoneTD = new ZoneTransferDeparture((GeneralTransfer) zoneGeneral);

        // Create and start driver and porter threads
        zoneGeneral.reportLog();
        Driver driver = new Driver((ZoneTransferArrivalDriver) zoneTA, (ZoneTransferDepartureDriver) zoneTD, (ZoneEntryExitDriver) zoneEnEx, (GeneralDriver) zoneGeneral, nAvioes, nPassageiros);
        Porter luggage = new Porter((ZoneLuggagePorter) zoneLug, (ZoneArrivalPorter) zoneA, (GeneralPorter) zoneGeneral);

        driver.start();
        luggage.start();

        // For each plane
        for (int i = 0; i < nAvioes; i++) {
            zoneGeneral.resetLog();

            // Set number of bags for each person
            int malasNoAviao = 0;
            int[] malas = new int[nPassageiros];
            Random r = new Random();
            for (int j = 0; j < nPassageiros; j++) {
                malas[j] = Math.abs(r.nextInt()) % (nMalas + 1);
                malasNoAviao += malas[j];
            }

            // Set FN and how many bags should be on the plane (some may still get lost)
            zoneGeneral.setMalas(i, malasNoAviao);

            // Create all passengers and set their bags/travel status
            Passenger[] people = new Passenger[nPassageiros];
            for (int j = 0; j < nPassageiros; j++) {

                boolean journeyOver = r.nextBoolean();

                for (int k = 0; k < malas[j]; k++) {
                    if (r.nextDouble() < 0.8 || !journeyOver) {
                        zoneA.addLuggage(new Luggage(j, !journeyOver));
                    }
                }

                zoneGeneral.setPassengerTravel(j, journeyOver);
                zoneGeneral.setPassengerMaxBags(j, malas[j]);

                people[j] = new Passenger(j, (ZoneArrivalPass) zoneA, (ZoneLuggagePass) zoneLug, (ZoneEntryExitPass) zoneEnEx, (ZoneTransferArrivalPass) zoneTA, (ZoneTransferDeparturePass) zoneTD, malas[j], journeyOver, (GeneralPass) zoneGeneral);
            }

            // Start all passengers
            for (int j = 0; j < nPassageiros; j++) {
                people[j].start();
            }

            // Wait for passengers to end
            zoneEnEx.resetPlane();
            // Wait for porter to be ready for a new plane
            zoneLug.resetPlane();

            // Reset FN
            zoneGeneral.setMalas(-1, 0);
            System.out.print(".");
        }

        // Wait for driver and porter threads to end
        try {
            driver.join();
            luggage.join();
        } catch (InterruptedException ex) {
        }

        // Save log file
        zoneGeneral.closeFile();

        System.out.println("\nSimulation has ended. Check the log file for more details.");
    }
}
