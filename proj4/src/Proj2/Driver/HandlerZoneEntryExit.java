package Proj2.Driver;

import Proj2.ZoneEntryExit.Interfaces.ZoneEntryExitDriver;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerZoneEntryExit; Sends the appropriate messages to the ZoneEntryExitMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerZoneEntryExit implements ZoneEntryExitDriver {

    private final InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param ip IP address of the Monitor
     */
    public HandlerZoneEntryExit(InetSocketAddress ip) {
        this.ip = ip;
    }

    /**
     * Gets total people that have left the airport.
     *
     * @return number of total people
     */
    @Override
    public int peopleLeft() {
        System.out.println("PL;");
        int ret=0;
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("PL;\n");
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
