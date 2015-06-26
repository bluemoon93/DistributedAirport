package Proj2.Driver;

import Proj2.ZoneTD.Interfaces.ZoneTDDriver;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerZoneTransferDeparture; Sends the appropriate messages to
 * the ZoneTDMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerZoneTransferDeparture implements ZoneTDDriver {

    private final InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param ip IP address of the Monitor
     */
    public HandlerZoneTransferDeparture(InetSocketAddress ip) {
        this.ip = ip;
    }

    /**
     * Parks bus in the transfer departure zone and sets how many people are
     * about to leave; Wakes all passengers on the bus and until they have left.
     *
     * @param potb people on the bus
     * @param reqId The ID of the Request
     */
    @Override
    public void park(int potb, int reqId) {
        System.out.println("Bus is parking with " + potb + " passengers on board");

        while (true) {
            try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
                tcpSock.setSoTimeout(30000);
                PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
                out.write("PRK;" + potb + ";"+reqId+";\n");
                out.flush();

                if(out.checkError())
                    throw new Exception();
                
                BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
                String ok = in.readLine();
                if(ok!=null)
                    break;
            } catch (Exception ex) {
                System.out.println("Monitor has crashed: "+ex.getMessage());
            }
        }
        System.out.println("Leaving park method!");
    }

    /**
     * Bus leaves this zone.
     * @param reqId The ID of the Request
     *
     */
    @Override
    public void depart(int reqId) {
        System.out.println("Bus is departing!");
        while (true) {
            try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
                tcpSock.setSoTimeout(30000);
                PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
                out.write("DEP;"+reqId+";\n");
                out.flush();
                
                if(out.checkError())
                    throw new Exception();

                BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
                String ok = in.readLine();
                if(ok!=null)
                    break;
            } catch (Exception ex) {
                System.out.println("Monitor has crashed: "+ex.getMessage());
            }
        }
    }
}
