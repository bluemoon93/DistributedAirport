package Proj2.Plane.Passenger;

import Proj2.ZoneTA.Interfaces.ZoneTAPass;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerZoneTAP; Sends the appropriate messages to the
 ZoneTAMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerZoneTAP implements ZoneTAPass {

    final private InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param a IP address of the Monitor
     */
    public HandlerZoneTAP(InetSocketAddress a) {
        ip = a;
    }

    /**
     * Passenger enters the queue waiting for the bus; 
     * If enough people to fill the Bus are in the Zone, wakes up driver; Else, waits;
     * After the bus has opened it's doors, passenger waits until he is in front of the queue;
     * When he is, he leaves the queue and seats inside the bus;
     * If there is still room in the bus, alerts the next passenger in the queue; Else, tells the driver to leave.
     *
     * @param id of the passenger
     */
    @Override
    public void takeABus(int id) {
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("TAB;"+id+";\n");
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
