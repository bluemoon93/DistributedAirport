package Proj3;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Configuration implements ConfigurationInterface {
    private int nA, nP, nM, nL;
    private String logName;
    private final String zGen="general", zArr="arrival", zEE="entryExit", zLug="luggage", zTA="TA", zTD="TD";
    private final int zGenPort=22007, zArrPort=22001, zEEPort=22002, zLugPort=22003, zTAPort=22004, zTDPort=22005;
    
    public Configuration(int nA, int nP, int nM, int nL, String logName) {
        this.restart(nA, nP, nM, nL, logName);
    }

    @Override
    public int getnA() {
        return nA;
    }
    
    @Override
    public String getLogName() {
        return logName;
    }

    @Override
    public int getnL() {
        return nL;
    }

    @Override
    public int getnM() {
        return nM;
    }

    @Override
    public int getnP() {
        return nP;
    }

    @Override
    public String getzArr() {
        return zArr;
    }

    @Override
    public int getzArrPort() {
        return zArrPort;
    }

    @Override
    public String getzEE() {
        return zEE;
    }

    @Override
    public int getzEEPort() {
        return zEEPort;
    }

    @Override
    public String getzGen() {
        return zGen;
    }

    @Override
    public int getzGenPort() {
        return zGenPort;
    }

    @Override
    public String getzLug() {
        return zLug;
    }

    @Override
    public int getzLugPort() {
        return zLugPort;
    }

    @Override
    public String getzTA() {
        return zTA;
    }

    @Override
    public int getzTAPort() {
        return zTAPort;
    }

    @Override
    public String getzTD() {
        return zTD;
    }

    @Override
    public int getzTDPort() {
        return zTDPort;
    }

    @Override
    public void unexport() throws RemoteException {
        UnicastRemoteObject.unexportObject(this, true);
    }

    @Override
    public final void restart(int nA, int nP, int nM, int nL, String logName){
        this.nA = nA;
        this.nP = nP;
        this.nM = nM;
        this.nL = nL;
        this.logName = logName;
    }
}
