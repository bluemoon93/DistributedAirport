/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Proj3.ZoneEntryExit;

import Proj3.General.Interfaces.GeneralPass;
import Proj3.ZoneEntryExit.Interfaces.ZoneEntryExitDriver;
import Proj3.ZoneEntryExit.Interfaces.ZoneEntryExitPass;
import Proj3.ZoneEntryExit.Interfaces.ZoneEntryExitPlane;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author bluemoon
 */
public interface ZoneEntryExitInterface extends ZoneEntryExitDriver, ZoneEntryExitPass, ZoneEntryExitPlane, Remote {
    public void unexport() throws RemoteException;
    public void restart(int nPass, GeneralPass g) throws RemoteException;
}
