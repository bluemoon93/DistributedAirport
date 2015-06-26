package Proj2.Plane;

import Proj2.General.Interfaces.GeneralPlane;
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
public class HandlerGeneral implements GeneralPlane {

    private final InetSocketAddress ip;

    /**
     * Handler constructor
     *
     * @param a IP address of the Monitor
     */
    public HandlerGeneral(InetSocketAddress a) {
        ip = a;
    }

    /**
     * Sets the current plane's flight number and how many bags the plane was
     * supposed to be carrying.
     *
     * @param i flight number
     * @param malas number of luggages presently at the plane's hold
     */
    @Override
    public void setMalas(int i, int malas) {
        //System.out.println("Plane: "+"SMA;"+i+";"+malas+";");
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("SMA;"+i+";"+malas+";\n");
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Prints header lines for the status, for easy reading and comprehension.
     *
     */
    @Override
    public void resetLog() {
        //System.out.println("Plane: "+"ReL;");
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("ReL;\n");
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Sets passenger travelling status
     *
     * @param over if is travelling or not
     * @param i of the passenger
     */
    @Override
    public void setPassengerTravel(int i, boolean over) {
        //System.out.println("Plane: "+"SPT;"+i+";"+over+";");
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("SPT;"+i+";"+over+";\n");
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Sets how many bags the passenger was meant to be carrying
     *
     * @param bags number of pieces of luggage the passenger carried at the start of his journey
     * @param i of the passenger
     */
    @Override
    public void setPassengerMaxBags(int i, int bags) {
        //System.out.println("Plane: "+"SPMB;"+i+";"+bags+";");
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("SPMB;"+i+";"+bags+";\n");
            out.flush();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
