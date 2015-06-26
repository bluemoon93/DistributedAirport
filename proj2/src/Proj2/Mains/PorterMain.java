package Proj2.Mains;

import Proj2.General.Interfaces.GeneralPorter;
import Proj2.Porter.HandlerGeneral;
import Proj2.Porter.HandlerZoneArrival;
import Proj2.Porter.HandlerZoneLuggage;
import Proj2.Porter.Porter;
import Proj2.ZoneArrival.Interfaces.ZoneArrivalPorter;
import Proj2.ZoneLuggage.Interfaces.ZoneLuggagePorter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Class PorterMain
 *
 * @author Tiago Soares, David Simoes
 */
public class PorterMain {

    /**
     * Waits for a configure message and starts the Thread.
     *
     * @param args 0 to 1 arguments; Port number where the class will receive
     * connections
     */
    public static void main(String[] args) {

        ServerSocket serverSock = null;
        int port = 10007;

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

        InetSocketAddress addrGeneral = null, addrLug = null, addrA = null;
        String configValue;

        System.out.println("Porter waiting for configuration values");
        boolean configured = false;
        try {
            serverSock = new ServerSocket(port);
            while (!configured) {
                if (!new File("PorterState").isFile() && !new File("PorterState.old").isFile()) {
                    try (Socket configSock = serverSock.accept()) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(configSock.getInputStream()));
                        configValue = in.readLine();

                        startTheThread(configValue);
                        configured = true;

                        // Create a current state
                        File confFile = new File("PorterFaultConfiguration");
                        try {
                            PrintWriter out = new PrintWriter(confFile);
                            out.println(configValue);
                            out.close();
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        }

                        System.out.println("\nThread started");

                        PrintWriter out = new PrintWriter(configSock.getOutputStream());
                        out.write("OK;\n");
                        out.flush();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    try {
                        Scanner fin = new Scanner(new File("PorterFaultConfiguration"));
                        String val = fin.nextLine();
                        startTheThread(val);
                        configured=true;
                        fin.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static void startTheThread(String configValue) throws Exception {
        InetSocketAddress addrGeneral = null, addrLug = null, addrA = null;
        String[] configValues = configValue.split(";");

        if (configValues.length == 4 && configValues[0].equals("CONF")) {
            String[] ipPort = configValues[1].split(":");
            addrLug = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));

            ipPort = configValues[2].split(":");
            addrA = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));

            ipPort = configValues[3].split(":");
            addrGeneral = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));
        } else {
            System.out.println("ERROR: " + configValues[0].equals("CONF"));
            throw new Exception();
        }

        System.out.println("Configured successfully.");
        System.out.println("ZoneLuggageMonitor \t@ " + addrLug);
        System.out.println("ZoneArrivalMonitor \t@ " + addrA);
        System.out.println("GeneralMonitor \t\t@ " + addrGeneral + "\n");

        GeneralPorter zoneGeneral = new HandlerGeneral(addrGeneral);
        ZoneLuggagePorter zoneLug = new HandlerZoneLuggage(addrLug);
        ZoneArrivalPorter zoneA = new HandlerZoneArrival(addrA);

        Porter thread = new Porter(zoneLug, zoneA, zoneGeneral);
        thread.start();
    }
}
