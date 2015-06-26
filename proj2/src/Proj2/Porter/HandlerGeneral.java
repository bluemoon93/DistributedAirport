package Proj2.Porter;

import Proj2.General.Interfaces.GeneralPorter;
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
public class HandlerGeneral implements GeneralPorter {

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
     * Sets state of Porter
     *
     * @param st state of Porter
     */
    @Override
    public void setPorterState(int st, int reqId) {
        System.out.println("SPoS;" + st + ";"+reqId+";");
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("SPoS;" + st + ";"+reqId+";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
