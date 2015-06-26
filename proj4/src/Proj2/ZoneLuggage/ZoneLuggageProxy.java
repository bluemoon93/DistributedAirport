package Proj2.ZoneLuggage;

import Proj2.General.Interfaces.GeneralLuggage;
import Proj2.PorterRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Class ZoneLuggageProxy
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneLuggageProxy extends Thread {

    private static Object lock = new Object();
    private Socket sock;
    private ZoneLuggage zoneLug;
    private static PorterRequest pr=new PorterRequest();

    /**
     * Constructor of the ZoneLuggageProxy
     *
     * @param sock Socket where the communication streams are going to be
     * established
     * @param zoneLug The monitor where the methods are invoked
     */
    public ZoneLuggageProxy(Socket sock, ZoneLuggage zoneLug) {
        this.sock = sock;
        this.zoneLug = zoneLug;
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

            if (command[0].equals("PABLBC")) {
                if (Integer.parseInt(command[2]) > pr.getId()) {
                    pr.setId(Integer.parseInt(command[2]));
                    zoneLug.putABagLBC(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                }
            } else if (command[0].equals("NMB")) {
                if (Integer.parseInt(command[1]) > pr.getId()) {
                    pr.setId(Integer.parseInt(command[1]));
                    zoneLug.noMoreBags(Integer.parseInt(command[1]));
                }
            } else if (command[0].equals("PABSR")) {
                if (Integer.parseInt(command[1]) > pr.getId()) {
                    pr.setId(Integer.parseInt(command[1]));
                    zoneLug.putABagSR(Integer.parseInt(command[1]));
                }
            } else if (command[0].equals("RLM")) {
                zoneLug.reportLuggageMissing(Integer.parseInt(command[1]));
            } else if (command[0].equals("GL")) {
                ok += zoneLug.getLuggage(Integer.parseInt(command[1]), Integer.parseInt(command[2])) + ";";
            } else if (command[0].equals("RP")) {
                zoneLug.resetPlane();
            } else if (command[0].equals("CONF")) {
                InetSocketAddress addrGeneral;

                String[] ipPort = command[1].split(":");
                addrGeneral = new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));
                int pass = Integer.parseInt(command[2]);

                System.out.println("Configured successfully.");
                System.out.println("nPassageiros = " + pass);
                System.out.println("GeneralMonitor \t\t\t@ " + addrGeneral);
                pr=new PorterRequest();
                GeneralLuggage zoneGen = new HandlerGeneral(addrGeneral);
                zoneLug.restart(zoneGen, pass);
            } else if (command[0].equals("EXIT")) {
                PrintWriter out = new PrintWriter(sock.getOutputStream());
                out.write("OK;\n");
                out.flush();
                System.exit(0);
            } else {
                System.out.println("UNKNOWN : " + command[0]);
                throw new IOException();
            }

            PrintWriter out = new PrintWriter(sock.getOutputStream());
            out.print(ok + "\n");
            out.flush();

            if (out.checkError()) // If thread crashed
            {
                throw new IOException();
            }

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
        } catch (Exception ex) {
            System.out.println("THE PORTER HAS CRASHED!!");
            ex.printStackTrace();
        }
    }
}
