package Proj2.Mains;

import Proj2.General.Interfaces.GeneralLuggage;
import Proj2.PorterRequest;
import Proj2.ZoneLuggage.HandlerGeneral;
import Proj2.ZoneLuggage.ZoneLuggage;
import Proj2.ZoneLuggage.ZoneLuggageProxy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class ZoneLuggageMain
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneLuggageMain {
    
    /**
     * Waits for a configure message and starts the Monitor; 
     * After that, it receives requests and creates threads to handle them
     *
     * @param args 0 to 1 arguments; Port number where the class will receive connections
     */
    public static void main(String[] args) {

        ServerSocket serverSock = null;
        int port = 10004, pass = 0;
        ZoneLuggage monitor = null;

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

        InetSocketAddress addrGeneral = null;

        System.out.println("Zone Luggage waiting for configuration values");
        boolean configured = false;
        try {
            serverSock = new ServerSocket(port);
            while (!configured) {
                try (Socket configSock = serverSock.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(configSock.getInputStream()));
                    String[] configValues = in.readLine().split(";");

                    if (configValues.length == 3 && configValues[0].equals("CONF")) {
                        String[] ipPort = configValues[1].split(":");
                        addrGeneral = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));
                        pass = Integer.parseInt(configValues[2]);
                        configured = true;
                    } else {
                        System.out.println("ERROR: " + configValues[0]);
                        throw new Exception();
                    }

                    System.out.println("Configured successfully.");
                    System.out.println("nPassageiros = " + pass);
                    System.out.println("GeneralMonitor \t\t\t@ " + addrGeneral);

                    GeneralLuggage zoneGen = new HandlerGeneral(addrGeneral);
                    monitor = new ZoneLuggage(zoneGen, pass);

                    System.out.println("\nWaiting for requests.");

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

        while (true) {
            try {
                ZoneLuggageProxy client = new ZoneLuggageProxy(serverSock.accept(), monitor);
                client.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
