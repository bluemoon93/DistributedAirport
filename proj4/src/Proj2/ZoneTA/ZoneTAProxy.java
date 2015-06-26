package Proj2.ZoneTA;

import Proj2.General.Interfaces.GeneralTransferArrival;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Class ZoneTAProxy
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneTAProxy extends Thread {
    static Object lock = new Object();
    Socket sock;
    ZoneTA zoneTA;

    /**
     * Constructor of the ZoneTAProxy
     *
     * @param sock Socket where the communication streams are going to be established
     * @param gen The monitor where the methods are invoked
     */
    public ZoneTAProxy(Socket sock, ZoneTA gen) {
        this.sock = sock;
        this.zoneTA = gen;
    }

    /**
     * Processes a message from someone, calls the appropriate method on the monitor
     * and returns an OK message with the method result attached (if any).
     *
     */
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String[] command = in.readLine().split(";");

            synchronized (lock) {
                for (String command1 : command) {
                    System.out.print(command1 + ";");
                }
                System.out.println();
            }

            String ok = "OK;";
            if (command[0].equals("DEP")) {
                zoneTA.depart();
            } else if (command[0].equals("ABB")) {
                ok += zoneTA.announceBusBoarding() + ";";
            } else if (command[0].equals("TAB")) {
                zoneTA.takeABus(Integer.parseInt(command[1]));
            } else if (command[0].equals("CONF")) {
                int nLug = 0;
                InetSocketAddress addrGeneral;

                String[] ipPort = command[1].split(":");
                addrGeneral = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));
                nLug = Integer.parseInt(command[2]);

                System.out.println("Configured successfully.");
                System.out.println("nLugares = " + nLug);
                System.out.println("GeneralMonitor \t\t\t@ " + addrGeneral);

                GeneralTransferArrival zoneGen = new HandlerGeneral(addrGeneral);
                zoneTA.restart(zoneGen, nLug);
            } else if (command[0].equals("EXIT")) {
                PrintWriter out = new PrintWriter(sock.getOutputStream());
                out.write("OK;\n");
                out.flush();
                System.exit(0);
            } else {
                System.out.println("UNKNOWN : " + command[0]);
            }

            PrintWriter out = new PrintWriter(sock.getOutputStream());
            out.write(ok + "\n");
            out.flush();

            synchronized (lock) {
                System.out.print("ANS: ");
                for (String command1 : command) {
                    System.out.print(command1 + ";");
                }
                System.out.print(" with " + ok);
                System.out.println();
            }
            
            in.close();
            out.close();
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }
}
