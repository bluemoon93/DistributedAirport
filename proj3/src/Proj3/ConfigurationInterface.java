package Proj3;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConfigurationInterface extends Remote {
    public void unexport() throws RemoteException;
    
    public int getnA() throws RemoteException;

    public String getLogName() throws RemoteException;
    
    public int getnL() throws RemoteException;

    public int getnM() throws RemoteException;

    public int getnP() throws RemoteException;

    public String getzArr() throws RemoteException;

    public int getzArrPort() throws RemoteException;

    public String getzEE() throws RemoteException;

    public int getzEEPort() throws RemoteException;

    public String getzGen() throws RemoteException;

    public int getzGenPort() throws RemoteException;

    public String getzLug() throws RemoteException;

    public int getzLugPort() throws RemoteException;

    public String getzTA() throws RemoteException;

    public int getzTAPort() throws RemoteException;

    public String getzTD() throws RemoteException;

    public int getzTDPort() throws RemoteException;
    
    public void restart(int nA, int nP, int nM, int nL, String logName) throws RemoteException;
}
