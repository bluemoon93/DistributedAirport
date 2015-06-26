package Proj2.ZoneEntryExit;

import Proj2.General.Interfaces.GeneralPass;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Class ZoneEntryExitProxy
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneEntryExitProxy extends Thread {
    static Object lock = new Object();
    Socket sock;
    ZoneEntryExit zoneEnEx;

    /**
     * Constructor of the ZoneEntryExitProxy
     *
     * @param sock Socket where the communication streams are going to be established
     * @param zoneEnEx The monitor where the methods are invoked
     */
    public ZoneEntryExitProxy(Socket sock, ZoneEntryExit zoneEnEx) {
        this.sock = sock;
        this.zoneEnEx = zoneEnEx;
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
            if (command[0].equals("PL")) {
                ok += zoneEnEx.peopleLeft() + ";";
            } else if (command[0].equals("GH")) {
                zoneEnEx.goingHome(Integer.parseInt(command[1]));
            } else if (command[0].equals("RP")) {
                zoneEnEx.resetPlane();
            } else if (command[0].equals("CONF")) {
                int nPass = 0;
                InetSocketAddress addrGeneral;

                nPass = Integer.parseInt(command[1]);
                String[] ipPort = command[2].split(":");
                addrGeneral = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));

                System.out.println("Configured successfully.");
                System.out.println("nPassageiros = " + nPass);
                System.out.println("GeneralMonitor \t\t\t@ " + addrGeneral);

                GeneralPass zoneGen = new HandlerGeneral(addrGeneral);
                zoneEnEx.restart(nPass, zoneGen);
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
                System.out.println();
            }
            
            in.close();
            out.close();
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }
}
