package Proj2.Plane.Passenger;

import Proj2.General.Interfaces.GeneralPass;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerGeneralP; Sends the appropriate messages to the
 ZoneGeneralMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerGeneralP implements GeneralPass {

    final private InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param a IP address of the Monitor
     */
    public HandlerGeneralP(InetSocketAddress a) {
        ip = a;
    }

    /**
     * Sets the state of the passenger with ID=id
     *
     * @param st state of Passenger
     * @param id of the passenger
     */
    @Override
    public void setPassengerState(int id, int st) {
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("SPaS;"+id+";"+st+";\n");
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
