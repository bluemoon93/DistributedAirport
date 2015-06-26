package Proj2.Plane;

import Proj2.ZoneLuggage.Interfaces.ZoneLuggagePlane;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerZoneLuggage; Sends the appropriate messages to the
 ZoneLuggageMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerZoneLuggage implements ZoneLuggagePlane {

    private final InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param a IP address of the Monitor
     */
    public HandlerZoneLuggage(InetSocketAddress a) {
        ip = a;
    }

    /**
     * Plane waits until porter has finished all his luggage.
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
