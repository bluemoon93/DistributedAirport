package Proj2.Mains;

import Proj2.General.Interfaces.GeneralPass;
import Proj2.General.Interfaces.GeneralPlane;
import Proj2.Plane.HandlerGeneral;
import Proj2.Plane.HandlerZoneArrival;
import Proj2.Plane.HandlerZoneEntryExit;
import Proj2.Plane.HandlerZoneLuggage;
import Proj2.Plane.Passenger.*;
import Proj2.Plane.Plane;
import Proj2.ZoneArrival.Interfaces.ZoneArrivalPass;
import Proj2.ZoneArrival.Interfaces.ZoneArrivalPlane;
import Proj2.ZoneEntryExit.Interfaces.ZoneEntryExitPass;
import Proj2.ZoneEntryExit.Interfaces.ZoneEntryExitPlane;
import Proj2.ZoneLuggage.Interfaces.ZoneLuggagePass;
import Proj2.ZoneLuggage.Interfaces.ZoneLuggagePlane;
import Proj2.ZoneTA.Interfaces.ZoneTAPass;
import Proj2.ZoneTD.Interfaces.ZoneTDPass;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class PlaneMain
 *
 * @author Tiago Soares, David Simoes
 */
public class PlaneMain {

    /**
     * Waits for a configure message and starts the Thread; 
     *
     * @param args 0 to 1 arguments; Port number where the class will receive connections
     */
    public static void main(String[] args) {
        ServerSocket serverSock = null;
        int port = 10009;

        try {
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);
            } else // DO NOTHING
            {
            }
        } catch (Exception ex) {
            System.out.println("Usage: java -jar SD_T1_P2_G2.jar [portNumber]");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar " + port);
        }

        int nPass = 0, nAv = 0, nMalas = 0;
        InetSocketAddress addrGeneral = null, addrEnEx = null, addrLug = null, addrArr = null, addrTA = null, addrTD = null;

        System.out.println("Plane waiting for configuration values");
        boolean configured = false;
        try {
            serverSock = new ServerSocket(port);
            while (!configured) {
                try (Socket configSock = serverSock.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(configSock.getInputStream()));
                    String[] configValues = in.readLine().split(";");

                    if (configValues.length == 10 && configValues[0].equals("CONF")) {
                        nAv = Integer.parseInt(configValues[1]);
                        nPass = Integer.parseInt(configValues[2]);
                        nMalas = Integer.parseInt(configValues[3]);

                        String[] ipPort = configValues[4].split(":");
                        addrGeneral = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));

                        ipPort = configValues[5].split(":");
                        addrArr = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));

                        ipPort = configValues[6].split(":");
                        addrEnEx = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));

                        ipPort = configValues[7].split(":");
                        addrLug = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));

                        ipPort = configValues[8].split(":");
                        addrTA = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));

                        ipPort = configValues[9].split(":");
                        addrTD = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));
                        configured = true;
                    } else {
                        System.out.println("ERROR: " + configValues[0].equals("CONF"));
                        throw new Exception();
                    }

                    System.out.println("Configured successfully.");
                    System.out.println("nAvioes = " + nAv);
                    System.out.println("nPassageiros = " + nPass);
                    System.out.println("nMalas = " + nMalas + "\n");

                    System.out.println("GeneralMonitor \t\t\t@ " + addrGeneral);
                    System.out.println("ZoneArrivalMonitor \t\t@ " + addrArr);
                    System.out.println("ZoneEntryExitMonitor \t\t@ " + addrEnEx);
                    System.out.println("ZoneLuggageMonitor \t\t@ " + addrLug);
                    System.out.println("ZoneTransferArrivalMonitor \t@ " + addrTA);
                    System.out.println("ZoneTransferDepartureMonitor \t@ " + addrTD + "\n");

                    GeneralPlane zoneGeneral = new HandlerGeneral(addrGeneral);
                    ZoneEntryExitPlane zoneEnEx = new HandlerZoneEntryExit(addrEnEx);
                    ZoneLuggagePlane zoneLug = new HandlerZoneLuggage(addrLug);
                    ZoneArrivalPlane zoneA = new HandlerZoneArrival(addrArr);

                    GeneralPass zoneGeneralP = new HandlerGeneralP(addrGeneral);
                    ZoneEntryExitPass zoneEnExP = new HandlerZoneEntryExitP(addrEnEx);
                    ZoneLuggagePass zoneLugP = new HandlerZoneLuggageP(addrLug);
                    ZoneArrivalPass zoneAP = new HandlerZoneArrivalP(addrArr);
                    ZoneTAPass zoneTAP = new HandlerZoneTAP(addrTA);
                    ZoneTDPass zoneTDP = new HandlerZoneTDP(addrTD);

                    Plane planeThread = new Plane(nAv, nPass, nMalas, zoneGeneral, zoneA, zoneEnEx, zoneLug, zoneGeneralP, zoneAP, zoneEnExP, zoneLugP, zoneTAP, zoneTDP);
                    planeThread.start();

                    System.out.println("\nThread started");

                    PrintWriter out = new PrintWriter(configSock.getOutputStream());
                    out.write("OK;\n");
                    out.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
