package Proj2.ZoneArrival;

import Proj2.Luggage;
import Proj2.PorterRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class ZoneArrivalProxy
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneArrivalProxy extends Thread {

    private static Object lock = new Object();
    private Socket sock;
    private ZoneArrival zoneA;
    private static PorterRequest pr = new PorterRequest();

    /**
     * Constructor of the ZoneArrivalProxy
     *
     * @param sock Socket where the communication streams are going to be
     * established
     * @param zoneA The monitor where the methods are invoked
     */
    public ZoneArrivalProxy(Socket sock, ZoneArrival zoneA) {
        this.sock = sock;
        this.zoneA = zoneA;
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
            this.sock.setSoTimeout(3000);
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String[] command = in.readLine().split(";");

            synchronized (lock) {
                for (String command1 : command) {
                    System.out.print(command1 + ";");
                }
                System.out.println();
            }

            String ok = "OK;";
            if (command[0].equals("TAR")) {
                if (Integer.parseInt(command[1]) > pr.getId()) {
                    pr.setId(Integer.parseInt(command[1]));
                    ok += zoneA.takeArest(Integer.parseInt(command[1])) + ";";
                    pr.setAnswer(ok);
                } else {
                    ok = pr.getAnswer();
                }
            } else if (command[0].equals("IGH")) {
                zoneA.iGotHere();
            } else if (command[0].equals("GLN")) {
                if (Integer.parseInt(command[1]) > pr.getId()) {
                    pr.setId(Integer.parseInt(command[1]));
                    ok += zoneA.getLugNumber(Integer.parseInt(command[1])) + ";";
                    pr.setAnswer(ok);
                } else {
                    ok = pr.getAnswer();
                }
            } else if (command[0].equals("GL")) {
                if (Integer.parseInt(command[1]) > pr.getId()) {
                    pr.setId(Integer.parseInt(command[1]));
                    Luggage t = zoneA.getLug(Integer.parseInt(command[1]));
                    ok += t.getOwnerId() + ";" + t.isOwnerTravelling() + ";";
                    pr.setAnswer(ok);
                } else {
                    ok = pr.getAnswer();
                }
            } else if (command[0].equals("AL")) {
                zoneA.addLuggage(new Luggage(Integer.parseInt(command[1]), Boolean.parseBoolean(command[2])));
            } else if (command[0].equals("CONF")) {
                int nAv = 0, nPass = 0;

                nAv = Integer.parseInt(command[1]);
                nPass = Integer.parseInt(command[2]);

                System.out.println("Configured successfully.");
                System.out.println("nAvioes = " + nAv);
                System.out.println("nPassageiros = " + nPass);
                pr = new PorterRequest();
                zoneA.restart(nAv, nPass);
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

            if (out.checkError()) // If thread crashed
            {
                throw new IOException();
            }

            synchronized (lock) {
                System.out.print("\tANS: ");
                for (String command1 : command) {
                    System.out.print(command1 + ";");
                }
                System.out.print(" with " + ok);
                System.out.println();
            }

            in.close();
            out.close();
        } catch (IOException | NumberFormatException ex) {
            System.out.println("THE PORTER HAS CRASHED!!");
            ex.printStackTrace();
        }
    }
}
