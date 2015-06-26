package Proj2.Mains;

import Proj2.General.Interfaces.GeneralTransferDeparture;
import Proj2.ZoneTD.HandlerGeneral;
import Proj2.ZoneTD.ZoneTD;
import Proj2.ZoneTD.ZoneTDProxy;
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
 * Class ZoneTDMain
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneTDMain {

    /**
     * Waits for a configure message and starts the Monitor; After that, it
     * receives requests and creates threads to handle them. If the monitor has
     * been resumed, then it will start from where it stopped
     *
     * @param args 0 to 1 arguments; Port number where the class will receive
     * connections
     */
    public static void main(String[] args) {
        ServerSocket serverSock = null;
        int port = 10006;
        ZoneTD monitor = null;

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
        Integer maxPass = 0;

        File mState = new File("MonitorState");
        File mStateOld = new File("MonitorState.old");

        // if monitor state exists
        if (mState.isFile() || mStateOld.isFile()) {
            System.out.println("Resuming monitor state....");
            try (Scanner fin = new Scanner(new File("MonitorFaultConfiguration"))) {
                String configValue = fin.nextLine();
                String[] configValues = configValue.split(";");
                if (configValues.length == 3 && configValues[0].equals("CONF")) {
                    String[] ipPort = configValues[1].split(":");
                    addrGeneral = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));
                    maxPass = Integer.parseInt(configValues[2]);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }

            GeneralTransferDeparture zoneGen = new HandlerGeneral(addrGeneral);

            //  if tempFile or no current monitor state, use .old for params
            boolean useOlds = !mState.isFile();

            //if (!useOlds) {
                File folder = new File(".");
                File[] listOfFiles = folder.listFiles();
                for (File f : listOfFiles) {
                    if (f.getName().contains("MonitorThreadState") && f.getName().contains(".temp")) {
                        System.out.println("Found temporary file.");
                        useOlds = true;
                        f.delete();
                        System.out.println(f.getName().substring(0, f.getName().length()-5));
                        new File(f.getName().substring(0, f.getName().length()-5)).delete();
                        mState.delete();
                    }
                }
           // }

            //  create monitor with special params
            if (!useOlds) {
                System.out.println("Using Current Monitor State");
                try {
                    Scanner fin = new Scanner(mState);
                    String[] fields = fin.nextLine().split(";");
                    monitor = new ZoneTD(Boolean.parseBoolean(fields[0]), Integer.parseInt(fields[1]), zoneGen, maxPass);
                    fin.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    useOlds = true;
                }
            }
            if (useOlds) {
                System.out.println("Using .OLD Monitor State");
                try {
                    Scanner fin = new Scanner(mStateOld);
                    String[] fields = fin.nextLine().split(";");
                    monitor = new ZoneTD(Boolean.parseBoolean(fields[0]), Integer.parseInt(fields[1]), zoneGen, maxPass);
                    fin.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }

            try {
                serverSock = new ServerSocket(port);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if(!mState.isFile() && !mStateOld.isFile()){
            System.out.println("Zone Transfer Departure waiting for configuration values");
            boolean configured = false;
            try {
                serverSock = new ServerSocket(port);
                while (!configured) {
                    try (Socket configSock = serverSock.accept()) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(configSock.getInputStream()));
                        String configValue = in.readLine();
                        String[] configValues = configValue.split(";");

                        if (configValues.length == 3 && configValues[0].equals("CONF")) {
                            String[] ipPort = configValues[1].split(":");
                            addrGeneral = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));
                            maxPass = Integer.parseInt(configValues[2]);
                            configured = true;
                        } else {
                            System.out.println("ERROR: " + configValues[0]);
                            throw new Exception();
                        }

                        System.out.println("Configured successfully.");
                        System.out.println("GeneralMonitor \t\t\t@ " + addrGeneral);

                        GeneralTransferDeparture zoneGen = new HandlerGeneral(addrGeneral);

                        // Create a current state
                        File confFile = new File("MonitorFaultConfiguration");

                        try (PrintWriter out = new PrintWriter(confFile)) {
                            out.println(configValue);
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        }

                        monitor = new ZoneTD(zoneGen, maxPass);

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
        }
        while (true) {
            try {
                ZoneTDProxy client = new ZoneTDProxy(serverSock.accept(), monitor, maxPass);
                client.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}
