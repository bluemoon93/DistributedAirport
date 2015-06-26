package Proj2.ZoneTD;

import Proj2.General.Interfaces.GeneralTransferDeparture;
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
public class HandlerGeneral implements GeneralTransferDeparture {

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
     * Removes the passenger with ID = id to the bus queue
     *
     * @param id of passenger
     */
    @Override
    public void leaveBusQueue(int id) {
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("LBQ;" + id + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
