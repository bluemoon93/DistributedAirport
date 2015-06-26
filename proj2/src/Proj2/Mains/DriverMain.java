package Proj2.Mains;

import Proj2.Driver.Driver;
import Proj2.Driver.HandlerGeneral;
import Proj2.Driver.HandlerZoneEntryExit;
import Proj2.Driver.HandlerZoneTransferArrival;
import Proj2.Driver.HandlerZoneTransferDeparture;
import Proj2.General.Interfaces.GeneralDriver;
import Proj2.ZoneEntryExit.Interfaces.ZoneEntryExitDriver;
import Proj2.ZoneTA.Interfaces.ZoneTADriver;
import Proj2.ZoneTD.Interfaces.ZoneTDDriver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class DriverMain
 *
 * @author Tiago Soares, David Simoes
 */
public class DriverMain {

    /**
     * Waits for a configure message and starts the Thread; 
     *
     * @param args 0 to 1 arguments; Port number where the class will receive connections
     */
    public static void main(String[] args) {
        ServerSocket serverSock;

        int port = 10008;

        try {
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);
            } else 
            {
                // DO NOTHING
            }
        } catch (Exception ex) { 
            System.out.println("Usage: java -jar SD_T1_P2_G2.jar [portNumber]");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar " + port);
        }

        int nPass = 0, nAv = 0;
        InetSocketAddress addrGeneral = null, addrEnEx = null, addrTA = null, addrTD = null;

        System.out.println("Driver waiting for configuration values");
        boolean configured = false;
        try {
            serverSock = new ServerSocket(port);
            while (!configured) {
                try (Socket configSock = serverSock.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(configSock.getInputStream()));
                    String[] configValues = in.readLine().split(";");

                    if (configValues.length == 7 && configValues[0].equals("CONF")) {
                        String[] ipPort = configValues[1].split(":");
                        addrTA = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));

                        ipPort = configValues[2].split(":");
                        addrTD = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));

                        ipPort = configValues[3].split(":");
                        addrEnEx = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));

                        ipPort = configValues[4].split(":");
                        addrGeneral = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));

                        nAv = Integer.parseInt(configValues[5]);
                        nPass = Integer.parseInt(configValues[6]);
                        configured = true;
                    } else {
                        System.out.println("ERROR: " + configValues[0].equals("CONF"));
                        throw new Exception();
                    }

                    System.out.println("Configured successfully.");
                    System.out.println("nAvioes = " + nAv);
                    System.out.println("nPassageiros = " + nPass);

                    System.out.println("GeneralMonitor \t\t\t@ " + addrGeneral);
                    System.out.println("ZoneEntryExitMonitor \t\t@ " + addrEnEx);
                    System.out.println("ZoneTransferArrivalMonitor \t@ " + addrTA);
                    System.out.println("ZoneTransferDepartureMonitor \t@ " + addrTD + "\n");

                    GeneralDriver zoneGeneral = new HandlerGeneral(addrGeneral);
                    ZoneEntryExitDriver zoneEnEx = new HandlerZoneEntryExit(addrEnEx);
                    ZoneTADriver zoneTA = new HandlerZoneTransferArrival(addrTA);
                    ZoneTDDriver zoneTD = new HandlerZoneTransferDeparture(addrTD);

                    Driver thread = new Driver(zoneTA, zoneTD, zoneEnEx, zoneGeneral, nAv, nPass);
                    thread.start();

                    System.out.println("\nThread started");

                    PrintWriter out = new PrintWriter(configSock.getOutputStream());
                    out.write("OK;\n");
                    out.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
