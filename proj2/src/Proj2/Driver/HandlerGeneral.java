package Proj2.Driver;

import Proj2.General.Interfaces.GeneralDriver;
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
public class HandlerGeneral implements GeneralDriver {

    private final InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param ip IP address of the Monitor
     */
    public HandlerGeneral(InetSocketAddress ip) {
        this.ip = ip;
    }

    /**
     * Sets state of Driver
     *
     * @param st state of Driver
     */
    @Override
    public void setDriverState(int st) {
        System.out.println("SDS;" + st + ";");
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("SDS;" + st + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
