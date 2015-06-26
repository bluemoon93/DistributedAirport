package Proj2.Plane;

import Proj2.ZoneEntryExit.Interfaces.ZoneEntryExitPlane;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerZoneEntryExit; Sends the appropriate messages to the
 ZoneEntryExitMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerZoneEntryExit implements ZoneEntryExitPlane {

    private final InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param a IP address of the Monitor
     */
    public HandlerZoneEntryExit(InetSocketAddress a) {
        ip = a;
    }

    /**
     * Checks if everyone has left or not and waits while people are still moving around the airport.
     */
    @Override
    public void resetPlane() {
        //System.out.println("Plane: "+"RP;");
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("RP;\n");
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
