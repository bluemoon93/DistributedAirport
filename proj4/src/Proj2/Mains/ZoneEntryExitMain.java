package Proj2.Mains;

import Proj2.General.Interfaces.GeneralPass;
import Proj2.ZoneEntryExit.HandlerGeneral;
import Proj2.ZoneEntryExit.ZoneEntryExit;
import Proj2.ZoneEntryExit.ZoneEntryExitProxy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class ZoneEntryExitMain
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneEntryExitMain {

    /**
     * Waits for a configure message and starts the Monitor; 
     * After that, it receives requests and creates threads to handle them
     *
     * @param args 0 to 1 arguments; Port number where the class will receive connections
     */
    public static void main(String[] args) {

        ServerSocket serverSock = null;
        int port = 10003;
        ZoneEntryExit monitor = null;

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

        int nPass = 0;
        InetSocketAddress addrGeneral = null;

        System.out.println("Zone Entry Exit waiting for configuration values");
        boolean configured = false;
        try {
            serverSock = new ServerSocket(port);
            while (!configured) {
                try (Socket configSock = serverSock.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(configSock.getInputStream()));
                    String[] configValues = in.readLine().split(";");

                    if (configValues.length == 3 && configValues[0].equals("CONF")) {
                        nPass = Integer.parseInt(configValues[1]);
                        String[] ipPort = configValues[2].split(":");
                        addrGeneral = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));
                        configured = true;
                    } else {
                        System.out.println("ERROR: " + configValues[0]);
                        throw new Exception();
                    }

                    System.out.println("Configured successfully.");
                    System.out.println("nPassageiros = " + nPass);
                    System.out.println("GeneralMonitor \t\t\t@ " + addrGeneral);

                    GeneralPass zoneGen = new HandlerGeneral(addrGeneral);
                    monitor = new ZoneEntryExit(nPass, zoneGen);

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
                ZoneEntryExitProxy client = new ZoneEntryExitProxy(serverSock.accept(), monitor);
                client.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
