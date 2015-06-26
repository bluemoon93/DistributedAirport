package Proj2.Porter;

import Proj2.Luggage;
import Proj2.ZoneArrival.Interfaces.ZoneArrivalPorter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerZoneArrival; Sends the appropriate messages to the
 ZoneArrivalMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerZoneArrival implements ZoneArrivalPorter {

    final private InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param ip IP address of the Monitor
     */
    public HandlerZoneArrival(InetSocketAddress ip) {
        this.ip = ip;
    }

    /**
     * Porter returns true immediatly if all planes have arrived or waits all passengers from the new plane have landed
     *
     * @return true if all planes have arrived else false
     */
    @Override
    public boolean takeArest(int reqId) {
        System.out.println("TAR;"+reqId+";");
        boolean ret = false;
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            
            System.out.println("Connected to the monitor but didn't send anything");
            halfSecondSleep();
            halfSecondSleep();
            halfSecondSleep();
            System.out.println("\t... done!");  
            
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("TAR;"+reqId+";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
            ret = Boolean.parseBoolean(ok.split(";")[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    /**
     * Gets number of luggage at the Plane's Hold
     *
     * @return how many bags are still left in the plane's hold
     */
    @Override
    public int getLugNumber(int reqId) {
        System.out.println("GLN;"+reqId+";");
        int lug = 0;
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("GLN;"+reqId+";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
            lug = Integer.parseInt(ok.split(";")[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lug;
    }

    /**
     * Gets a random bag
     *
     * @return bag from the plane
     */
    @Override
    public Luggage getLug(int reqId) {
        System.out.println("GL;"+reqId+";");
        Luggage t = null;
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("GL;"+reqId+";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
            String[] fields = ok.split(";");
            t = new Luggage(Integer.parseInt(fields[1]), Boolean.parseBoolean(fields[2]));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return t;
    }
    
    private void halfSecondSleep(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
    }
}
