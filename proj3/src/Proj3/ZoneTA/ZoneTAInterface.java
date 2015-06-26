/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Proj3.ZoneTA;

import Proj3.General.Interfaces.GeneralTransferArrival;
import Proj3.ZoneTA.Interfaces.ZoneTADriver;
import Proj3.ZoneTA.Interfaces.ZoneTAPass;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author bluemoon
 */
public interface ZoneTAInterface extends ZoneTADriver, ZoneTAPass, Remote {
    public void unexport() throws RemoteException;
    public void restart(GeneralTransferArrival g, int nLug) throws RemoteException;
}
