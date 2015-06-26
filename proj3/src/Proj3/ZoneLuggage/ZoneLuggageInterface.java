/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Proj3.ZoneLuggage;

import Proj3.General.Interfaces.GeneralLuggage;
import Proj3.ZoneLuggage.Interfaces.ZoneLuggagePass;
import Proj3.ZoneLuggage.Interfaces.ZoneLuggagePlane;
import Proj3.ZoneLuggage.Interfaces.ZoneLuggagePorter;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author bluemoon
 */
public interface ZoneLuggageInterface extends  ZoneLuggagePass, ZoneLuggagePorter, ZoneLuggagePlane, Remote {
    public void unexport() throws RemoteException;
    public void restart(GeneralLuggage g, int pass) throws RemoteException;
}
