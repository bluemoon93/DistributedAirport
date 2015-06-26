package Proj2.Plane.Passenger;

import Proj2.ZoneLuggage.Interfaces.ZoneLuggagePass;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerZoneLuggageP; Sends the appropriate messages to the
 ZoneLuggageMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerZoneLuggageP implements ZoneLuggagePass {

    final private InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param a IP address of the Monitor
     */
    public HandlerZoneLuggageP(InetSocketAddress a) {
        ip = a;
    }

    /**
     * Reports more missing bags
     *
     * @param bags number of bags missing from this passenger
     */
    @Override
    public void reportLuggageMissing(int bags) {
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("RLM;"+bags+";\n");
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Gets all the luggage from passenger with ID=id; Waits until passenger has all his bags or until there are no more bags
     *
     * @param id of the passenger
     * @param lugTotal total number of bags he has to catch
     * @return how much luggage this passenger caught
     */
    @Override
    public int getLuggage(int id, int lugTotal) {
        int ret = 0;
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("GL;"+id+";"+lugTotal+";\n");
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
