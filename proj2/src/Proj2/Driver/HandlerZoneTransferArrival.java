package Proj2.Driver;

import Proj2.ZoneTA.Interfaces.ZoneTADriver;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerZoneTransferArrival; Sends the appropriate messages to the ZoneTAMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerZoneTransferArrival implements ZoneTADriver {

    private final InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param ip IP address of the Monitor
     */
    public HandlerZoneTransferArrival(InetSocketAddress ip) {
        this.ip = ip;
    }

    /**
     * Bus leaves this zone.
     *
     */
    @Override
    public void depart() {
        System.out.println("DEP;");
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("DEP;\n");
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Bus waits for 2seconds until enough people have arrived.
     * After the 2 seconds, if zone is still empty, returns 0. 
     * Else, wakes passengers and waits for them to fill the bus or until there are no more passengers.
     *
     * @return how many people entered the bus
     */
    @Override
    public int announceBusBoarding() {
        System.out.println("ABB;");
        int ret=0;
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("ABB;\n");
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
            ret = Integer.parseInt(ok.split(";")[1]);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return ret;
    }
}
