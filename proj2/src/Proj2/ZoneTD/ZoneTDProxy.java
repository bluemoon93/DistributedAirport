package Proj2.ZoneTD;

import Proj2.General.Interfaces.GeneralTransferDeparture;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
//import java.util.Scanner;

/**
 * Class ZoneTDProxy
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneTDProxy extends Thread {

    private static final Object lock = new Object();
    private final Socket sock;
    private final ZoneTD zoneTD;
    private Integer maxPass;
    //private final static int waitingForPeopleToLeave = 0, departed = 1, leftBus = 2;

    /**
     * Constructor of the ZoneTDProxy
     *
     * @param sock Socket where the communication streams are going to be
     * established
     * @param gen The monitor where the methods are invoked
     * @param pass Maximum number of passengers
     */
    public ZoneTDProxy(Socket sock, ZoneTD gen, Integer pass) {
        this.sock = sock;
        this.zoneTD = gen;
        this.maxPass = pass;
    }

    /**
     * Processes a message from someone, calls the appropriate method on the
     * monitor and returns an OK message with the method result attached (if
     * any).
     *
     */
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String configValue = in.readLine();
            String[] command = configValue.split(";");

            synchronized (lock) {
                for (String command1 : command) {
                    System.out.print(command1 + ";");
                }
                System.out.println();
            }
            
            // For the methods depart(), leaveBus() and park(), the Monitor State is evaluated!
            // If the monitor is meant to resume this method, it will read the appropriate file and
            //  start from where it left off.
            // Since the methods are all very simple and don't have more than 1 state, no actual reading
            //  has to be performed. Simply knowing the file exists is enough

            if (command[0].equals("DEP")) {
                
                System.out.println("Got a departure request");
            halfSecondSleep(1000);
            System.out.println("\t... done!");
                
                File tState = new File("MonitorThreadState" + maxPass + ";" + command[1]);
                File tStateTemp = new File("MonitorThreadState" + maxPass + ";" + command[1] + ".temp");

                if (tState.isFile() || tStateTemp.isFile()) {
                    System.out.print("\tResuming thread state...");

                    if (tStateTemp.isFile()) {
                        System.out.println(" From the beginning.");
                        zoneTD.depart(Integer.parseInt(command[1]));
                    } else {
                        System.out.println(" Method had been complete before!");
                    }
                } else {
                    zoneTD.depart(Integer.parseInt(command[1]));
                }
            } else if (command[0].equals("LB")) {
                File tState = new File("MonitorThreadState" + command[1] + ";" + command[2]);
                File tStateTemp = new File("MonitorThreadState" + command[1] + ";" + command[2] + ".temp");

                if (tState.isFile() || tStateTemp.isFile()) {
                    System.out.print("\tResuming thread state...");
                    if (tStateTemp.isFile()) {
                        System.out.println(" From the beginning.");
                        zoneTD.leaveBus(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    } else {
                        System.out.println(" Method had been complete before!");
                    }
                } else {
                    zoneTD.leaveBus(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                }
            } else if (command[0].equals("FBT")) {
                zoneTD.finishBusTrip();
            } else if (command[0].equals("PRK")) {
                File tState = new File("MonitorThreadState" + maxPass + ";" + command[2]);
                File tStateTemp = new File("MonitorThreadState" + maxPass + ";" + command[2] + ".temp");

                if (tState.isFile() || tStateTemp.isFile()) {
                    System.out.print("\tResuming thread state...");

                    if (tStateTemp.isFile()) {
                        System.out.println(" From the beginning.");
                        zoneTD.park(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                    } else {
                        System.out.println(" From the special park onwards.");
                        zoneTD.specialPark();
                    }
                } else {
                    zoneTD.park(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                }
            } else if (command[0].equals("CONF")) {
                deleteStates();

                InetSocketAddress addrGeneral;

                String[] ipPort = command[1].split(":");
                addrGeneral = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));
                maxPass = Integer.parseInt(command[2]);

                System.out.println("Configured successfully.");
                System.out.println("GeneralMonitor \t\t\t@ " + addrGeneral);

                // Create a current state
                File confFile = new File("MonitorFaultConfiguration");
                try (PrintWriter out = new PrintWriter(confFile)) {
                    out.println(configValue);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }

                GeneralTransferDeparture zoneGen = new HandlerGeneral(addrGeneral);
                zoneTD.restart(zoneGen, maxPass);
            } else if (command[0].equals("EXIT")) {
                deleteStates();
                PrintWriter out = new PrintWriter(sock.getOutputStream());
                out.write("OK;\n");
                out.flush();
                System.exit(0);
            } else {
                System.out.println("UNKNOWN : " + command[0]);
            }

            PrintWriter out = new PrintWriter(sock.getOutputStream());
            out.write("OK;\n");
            out.flush();
            
            System.out.println("Sent the OK and going to delete the files now");
            halfSecondSleep(1000);
            System.out.println("\t... done!");

            // delete thread state
            System.out.println("\tDeleting file for " + command[0]);
            if (command[0].equals("DEP")) {
                File tState = new File("MonitorThreadState" + maxPass + ";" + command[1]);
                while (tState.isFile()) {
                    tState.delete();
                }
            } else if (command[0].equals("PRK")) {
                File tState = new File("MonitorThreadState" + maxPass + ";" + command[2]);
                while (tState.isFile()) {
                    tState.delete();
                }
            } else if (command[0].equals("LB")) {
                File tState = new File("MonitorThreadState" + command[1] + ";" + command[2]);
                while (tState.isFile()) {
                    tState.delete();
                }
            }

            synchronized (lock) {
                System.out.print("\tANS: ");
                for (String command1 : command) {
                    System.out.print(command1 + ";");
                }
                System.out.println();
            }

            in.close();
            out.close();
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }
    
    private void halfSecondSleep(int n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException ex) {
        }
    }

    private void deleteStates() {
        File oldFile = new File("MonitorState.old");
        File newFile = new File("MonitorState");
        File confFile = new File("MonitorFaultConfiguration");

        // Delete all thread states that have remained
        File folder = new File(".");
        File[] listOfFiles = folder.listFiles();
        for (File f : listOfFiles) {
            if (f.getName().contains("MonitorThreadState")) {
                while (f.isFile()) {
                    f.delete();
                }
            }
        }

        // Delete MonitorStates (Current and Old)
        while (oldFile.isFile()) {
            oldFile.delete();
        }
        while (newFile.isFile()) {
            newFile.delete();
        }

        // Delete config file
        while (confFile.isFile()) {
            confFile.delete();
        }
    }
}
