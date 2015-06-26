package Proj2.Porter;

import Proj2.ZoneLuggage.Interfaces.ZoneLuggagePorter;
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
public class HandlerZoneLuggage implements ZoneLuggagePorter {
    
    final private InetSocketAddress ip;
    boolean wait;
    /**
     * Handler constructor
     *
     * @param ip IP address of the Monitor
     */
    public HandlerZoneLuggage(InetSocketAddress ip){
        this.ip=ip;
        wait=true;
    }

    /**
     * Puts a bag at the luggage belt conveyor and wakes waiting passengers
     *
     * @param ownerId identification of the luggage owner
     */
    @Override
    public void putABagLBC(int ownerId, int reqId) {
        System.out.println("PABLBC;" + ownerId + ";"+reqId+";");
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("PABLBC;" + ownerId + ";"+reqId+";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Announces that there are no more bags and wakes waiting passengers.
     */
    @Override
    public void noMoreBags(int reqId) {
        System.out.println("NMB;"+reqId+";");
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("NMB;"+reqId+";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Puts a bag in the storeroom.
     *
     */
    @Override
    public void putABagSR(int reqId) {
        System.out.println("PABSR;"+reqId+";");
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("PABSR;"+reqId+";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
