package Proj2.Plane.Passenger;

import Proj2.ZoneEntryExit.Interfaces.ZoneEntryExitPass;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerZoneEntryExitP; Sends the appropriate messages to the
 ZoneEntryExitMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerZoneEntryExitP implements ZoneEntryExitPass {

    final private InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param a IP address of the Monitor
     */
    public HandlerZoneEntryExitP(InetSocketAddress a) {
        ip = a;
    }

    /**
     * Checks if caller is the last passenger leaving; If true, wakes up all other passengers; else, waits;
     * When everyone else has left, wakes up Plane thread for a new plane to come.
     *
     * @param id of the passenger
     */
    @Override
    public void goingHome(int id) {
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("GH;"+id+";\n");
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
