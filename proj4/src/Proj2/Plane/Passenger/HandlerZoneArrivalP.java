package Proj2.Plane.Passenger;

import Proj2.ZoneArrival.Interfaces.ZoneArrivalPass;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerZoneArrivalP; Sends the appropriate messages to the
 ZoneArrivalMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerZoneArrivalP implements ZoneArrivalPass {

    final private InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param a IP address of the Monitor
     */
    public HandlerZoneArrivalP(InetSocketAddress a) {
        ip = a;
    }

    /**
     * Increases number of passengers in the zone and wakes up porter if there are enough people.
     */
    @Override
    public void iGotHere() {
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("IGH;\n");
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
