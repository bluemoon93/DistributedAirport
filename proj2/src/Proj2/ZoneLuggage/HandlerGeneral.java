package Proj2.ZoneLuggage;

import Proj2.General.Interfaces.GeneralLuggage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Handler class HandlerGeneral; Sends the appropriate messages to the
 * GeneralMonitor at a given IP and waits for a response.
 *
 * @author Tiago Soares, David Simoes
 */
public class HandlerGeneral implements GeneralLuggage {

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
     * Sets how many bags are currently in the Luggage Belt Conveyor at the
     * LuggageZone
     *
     * @param n number of luggages in luggage belt conveyor
     */
    @Override
    public void setLCB(int n) {
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("SLB;" + n + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sets how many bags are currently in the StoreRoom at the LuggageZone
     *
     * @param n number of luggages in storeRoom
     */
    @Override
    public void setST(int n) {
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("SSR;" + n + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sets how many bags the passenger currently has
     *
     * @param st number of pieces of luggage the passenger that he has presently
     * collected
     * @param id of the passenger
     */
    @Override
    public void setPassengerCurrBags(int id, int st) {
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("SPCB;" + id + ";" + st + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sets how many bags have been given as missing through the day
     *
     * @param b number of missing bags
     */
    @Override
    public void reportBagsMissing(int b) {
        try (Socket tcpSock = new Socket(ip.getAddress(), ip.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("RBM;" + b + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
