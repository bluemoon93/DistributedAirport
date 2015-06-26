package Proj3.General;

import Proj3.General.Interfaces.GeneralDriver;
import Proj3.General.Interfaces.GeneralLuggage;
import Proj3.General.Interfaces.GeneralPass;
import Proj3.General.Interfaces.GeneralPlane;
import Proj3.General.Interfaces.GeneralPorter;
import Proj3.General.Interfaces.GeneralTransferArrival;
import Proj3.General.Interfaces.GeneralTransferDeparture;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GeneralInterface extends GeneralDriver, GeneralPorter, GeneralPass, GeneralLuggage, GeneralTransferArrival, GeneralTransferDeparture, GeneralPlane, Remote {
    public void unexport() throws RemoteException;

    public void restart(int nPass, int nLug, String a) throws RemoteException;
}
