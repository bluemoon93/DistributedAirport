/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Proj3.ZoneTD;

import Proj3.General.Interfaces.GeneralTransferDeparture;
import Proj3.ZoneTD.Interfaces.ZoneTDDriver;
import Proj3.ZoneTD.Interfaces.ZoneTDPass;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author bluemoon
 */
public interface ZoneTDInterface extends ZoneTDPass, ZoneTDDriver, Remote {
    public void unexport() throws RemoteException;
    public void restart(GeneralTransferDeparture a) throws RemoteException;
}
