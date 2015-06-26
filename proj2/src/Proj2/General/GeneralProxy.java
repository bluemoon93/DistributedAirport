package Proj2.General;

import Proj2.PorterRequest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class GeneralProxy
 *
 * @author Tiago Soares, David Simoes
 */
public class GeneralProxy extends Thread {
    private static Object lock = new Object();
    private Socket sock;
    private General zoneGeneral;
    private static PorterRequest pr=new PorterRequest();

    /**
     * Constructor of the GeneralProxy
     *
     * @param sock Socket where the communication streams are going to be established
     * @param gen The monitor where the methods are invoked
     */
    public GeneralProxy(Socket sock, General gen) {
        this.sock = sock;
        this.zoneGeneral = gen;
    }

    /**
     * Processes a message from someone, calls the appropriate method on the monitor
     * and returns an OK message with the method result attached (if any).
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

            if (command[0].equals("RL")) {
                zoneGeneral.reportLog();
            } else if (command[0].equals("ReL")) {
                zoneGeneral.resetLog();
            } else if (command[0].equals("SDS")) {
                zoneGeneral.setDriverState(Integer.parseInt(command[1]));
            } else if (command[0].equals("SPoS")) {
                if (Integer.parseInt(command[2]) > pr.getId()) {
                    pr.setId(Integer.parseInt(command[2]));
                    zoneGeneral.setPorterState(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                }
            } else if (command[0].equals("SPaS")) {
                zoneGeneral.setPassengerState(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
            } else if (command[0].equals("AWQ")) {
                zoneGeneral.addWaitQueue(Integer.parseInt(command[1]));
            } else if (command[0].equals("ABQ")) {
                zoneGeneral.addBusQueue(Integer.parseInt(command[1]));
            } else if (command[0].equals("LWQ")) {
                zoneGeneral.leaveWaitQueue();
            } else if (command[0].equals("LBQ")) {
                zoneGeneral.leaveBusQueue(Integer.parseInt(command[1]));
            } else if (command[0].equals("SLB")) {
                zoneGeneral.setLCB(Integer.parseInt(command[1]));
            } else if (command[0].equals("SSR")) {
                zoneGeneral.setST(Integer.parseInt(command[1]));
            } else if (command[0].equals("SMA")) {
                zoneGeneral.setMalas(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
            } else if (command[0].equals("SPT")) {
                zoneGeneral.setPassengerTravel(Integer.parseInt(command[1]), Boolean.parseBoolean(command[2]));
            } else if (command[0].equals("SPMB")) {
                zoneGeneral.setPassengerMaxBags(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
            } else if (command[0].equals("SPCB")) {
                zoneGeneral.setPassengerCurrBags(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
            } else if (command[0].equals("RBM")) {
                zoneGeneral.reportBagsMissing(Integer.parseInt(command[1]));
            } else if (command[0].equals("CONF")) {
                int nPass = 0, nLug = 0;
                BufferedWriter bw;
                String logName;

                nPass = Integer.parseInt(command[1]);
                nLug = Integer.parseInt(command[2]);
                logName = command[3];

                bw = new BufferedWriter(new FileWriter(logName));

                System.out.println("Configured successfully.");
                System.out.println("nPassageiros = " + nPass);
                System.out.println("nLugares = " + nLug);
                System.out.println("The log file is named \"" + logName + "\"\n");
                pr=new PorterRequest();
                zoneGeneral.restart(nPass, nLug, bw);
            } else if (command[0].equals("EXIT")) {
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
            
            if (out.checkError()) // If thread crashed
            {
                throw new IOException();
            }

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
            System.out.println("The Porter / TDMonitor has crashed! "+ex.getMessage());
            //ex.printStackTrace();
        }
    }
}
