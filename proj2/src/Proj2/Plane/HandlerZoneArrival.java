package Proj2.Plane;

import Proj2.Luggage;
import Proj2.ZoneArrival.Interfaces.ZoneArrivalPlane;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerZoneArrival; Sends the appropriate messages to the
 ZoneArrivalMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerZoneArrival implements ZoneArrivalPlane {

    private final InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param a IP address of the Monitor
     */
    public HandlerZoneArrival(InetSocketAddress a) {
        ip = a;
    }

    /**
     * Adds new luggage to plane's hold
     *
     * @param t luggage to add
     */
    @Override
    public void addLuggage(Luggage t) {
        //System.out.println("Plane: "+"AL;"+t.getOwnerId()+";"+t.isOwnerTravelling()+";");
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("AL;"+t.getOwnerId()+";"+t.isOwnerTravelling()+";\n");
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
