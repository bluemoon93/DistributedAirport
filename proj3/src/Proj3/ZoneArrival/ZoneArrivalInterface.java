/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Proj3.ZoneArrival;

import Proj3.ZoneArrival.Interfaces.ZoneArrivalPass;
import Proj3.ZoneArrival.Interfaces.ZoneArrivalPlane;
import Proj3.ZoneArrival.Interfaces.ZoneArrivalPorter;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author bluemoon
 */
public interface ZoneArrivalInterface extends ZoneArrivalPass, ZoneArrivalPorter, ZoneArrivalPlane, Remote {
    public void unexport() throws RemoteException;
    public void restart(int nAv, int nPass) throws RemoteException;
}
