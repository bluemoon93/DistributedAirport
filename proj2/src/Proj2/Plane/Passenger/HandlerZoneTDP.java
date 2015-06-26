package Proj2.Plane.Passenger;

import Proj2.ZoneTD.Interfaces.ZoneTDPass;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerZoneTDP; Sends the appropriate messages to the
 * ZoneTDMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerZoneTDP implements ZoneTDPass {

    final private InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param a IP address of the Monitor
     */
    public HandlerZoneTDP(InetSocketAddress a) {
        ip = a;
    }

    /**
     * Passengers wait until the bus arrives to this zone.
     * 
     */
    @Override
    public void finishBusTrip() {

        System.out.println("Passenger is waiting for the bus to finish his trip");
        while (true) {
            try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
                tcpSock.setSoTimeout(30000);
                PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
                out.write("FBT;\n");
                out.flush();

                if (out.checkError()) {
                    throw new Exception();
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
                String ok = in.readLine();
                if(ok!=null)
                    break;
            } catch (Exception ex) {
                System.out.println("Monitor has crashed: " + ex.getMessage());
            }
        }
    }

    /**
     * Passenger leaves the bus and wakes up driver if the bus is empty
     *
     * @param id of the passengers
     * @param reqId The ID of the Request
     */
    @Override
    public void leaveBus(int id, int reqId) {

        System.out.println("Passenger " + id + " is leaving bus!");
        while (true) {
            try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
                tcpSock.setSoTimeout(30000);
                PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
                out.write("LB;" + id + ";" + reqId + ";\n");
                out.flush();

                BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
                String ok = in.readLine();
                if(ok!=null)
                    break;
            } catch (Exception ex) {
                System.out.println("Monitor has crashed: " + ex.getMessage());
            }
        }
    }
}
