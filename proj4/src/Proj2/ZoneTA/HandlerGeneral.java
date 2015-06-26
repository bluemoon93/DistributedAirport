package Proj2.ZoneTA;

import Proj2.General.Interfaces.GeneralTransferArrival;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerGeneral; Sends the appropriate messages to the
 GeneralMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerGeneral implements GeneralTransferArrival {

    final private InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param ip IP address of the Monitor
     */
    public HandlerGeneral(InetSocketAddress ip) {
        this.ip = ip;
    }

    /**
     * Adds the passenger with ID = id to the waiting queue at the Arrival Transfer Zone.
     *
     * @param id of passenger
     */
    @Override
    public void addWaitQueue(int id) {
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("AWQ;" + id + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes the 1st passenger from the waiting queue at the Arrival Transfer Zone.
     */
    @Override
    public void leaveWaitQueue() {
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("LWQ;\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Adds the passenger with ID = id to the bus queue
     *
     * @param id of passenger
     */
    @Override
    public void addBusQueue(int id) {
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("ABQ;" + id + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
