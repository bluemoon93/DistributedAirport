/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proj3;

import Proj3.General.GeneralInterface;
import Proj3.General.Interfaces.GeneralLuggage;
import Proj3.General.Interfaces.GeneralPass;
import Proj3.General.Interfaces.GeneralTransferArrival;
import Proj3.General.Interfaces.GeneralTransferDeparture;
import Proj3.Registry.Register;
import Proj3.ZoneArrival.ZoneArrivalInterface;
import Proj3.ZoneEntryExit.ZoneEntryExitInterface;
import Proj3.ZoneLuggage.ZoneLuggageInterface;
import Proj3.ZoneTA.ZoneTAInterface;
import Proj3.ZoneTD.ZoneTDInterface;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author asus
 */
public class Config {

    public static void main(String[] args) {
        System.setProperty("java.security.policy", "java.policy");

        int index = 0, nAvioes = 5, nPassageiros = 10, nLugares = 4, nMalas = 3, rmiRegPortNumb = 40000;
        String logName = "log.txt", rmiRegHostName = "localhost";
        boolean terminate = true;

        try {
            while (index < args.length) {
                if (args[index].equals("-nA")) {
                    nAvioes = Integer.parseInt(args[++index]);
                } else if (args[index].equals("-nP")) {
                    nPassageiros = Integer.parseInt(args[++index]);
                } else if (args[index].equals("-nM")) {
                    nMalas = Integer.parseInt(args[++index]);
                } else if (args[index].equals("-nL")) {
                    nLugares = Integer.parseInt(args[++index]);
                } else if (args[index].equals("-log")) {
                    logName = args[++index];
                } else if (args[index].equals("-rmi")) {
                    rmiRegHostName = args[++index];
                } else if (args[index].equals("-rmiP")) {
                    rmiRegPortNumb = Integer.parseInt(args[++index]);
                } else if (args[index].equals("-ter")) {
                    terminate = true;
                } else {
                    throw new Exception();
                }

                index++;
            }
        } catch (Exception ex) {
            System.out.println("Usage: java -jar SD_T1_P1_G2.jar [-nA nAvioes] [-nP nPassageiros] [-nM nMalas] [-nL nLugares] [-log logNameFile] [-rmi rmiIPaddress] [-rmiP rmiPort]");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar -log log.txt -con config.txt");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar -nA 5 -nP 6 -nM 2 -nL 3");
            System.exit(1);
        }

        if (terminate) {
            unregister(rmiRegHostName, rmiRegPortNumb);
        } else {
            reconfigure(nAvioes, nPassageiros, nLugares, nMalas, rmiRegPortNumb, logName, rmiRegHostName);
        }
    }

    private static void unregister(String rmiRegHostName, int rmiRegPortNumb) {
        String nameEntryConfig = "config";
        String nameEntryBase = "RegisterHandler";

        // Create and install the security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        System.out.println("Security manager was installed!");

        // Receive registry, register, config and all monitors
        Registry registry = null;
        Register reg = null;
        ConfigurationInterface configInfo = null;
        GeneralInterface zoneGeneralPass = null;
        ZoneArrivalInterface zoneAPass = null;
        ZoneEntryExitInterface zoneEnExPass = null;
        ZoneLuggageInterface zoneLugPass = null;
        ZoneTAInterface zoneTAPass = null;
        ZoneTDInterface zoneTDPass = null;
        try {
            registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        try {
            reg = (Register) registry.lookup(nameEntryBase);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        try {
            configInfo = (ConfigurationInterface) registry.lookup(nameEntryConfig);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        try {
            zoneGeneralPass = (GeneralInterface) registry.lookup(configInfo.getzGen());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        try {
            zoneAPass = (ZoneArrivalInterface) registry.lookup(configInfo.getzArr());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        try {
            zoneLugPass = (ZoneLuggageInterface) registry.lookup(configInfo.getzLug());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        try {
            zoneTAPass = (ZoneTAInterface) registry.lookup(configInfo.getzTA());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        try {
            zoneEnExPass = (ZoneEntryExitInterface) registry.lookup(configInfo.getzEE());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        try {
            zoneTDPass = (ZoneTDInterface) registry.lookup(configInfo.getzTD());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        // unexport and unbind all monitors
        try {
            zoneGeneralPass.unexport();
            registry.unbind(configInfo.getzGen());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        try {
            zoneAPass.unexport();
            registry.unbind(configInfo.getzArr());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        try {
            zoneLugPass.unexport();
            registry.unbind(configInfo.getzLug());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        try {
            zoneTAPass.unexport();
            registry.unbind(configInfo.getzTA());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        try {
            zoneEnExPass.unexport();
            registry.unbind(configInfo.getzEE());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        try {
            zoneTDPass.unexport();
            registry.unbind(configInfo.getzTD());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        try {
            configInfo.unexport();
            registry.unbind(nameEntryConfig);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        try {
            reg.unexport();
            //registry.unbind(nameEntryBase);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void reconfigure(int nAvioes, int nPassageiros, int nLugares, int nMalas, int rmiRegPortNumb, String logName, String rmiRegHostName) {
        Registry registry = null;
        Register reg = null;
        String nameEntryBase = "RegisterHandler";

        // Create and install the security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        System.out.println("Security manager was installed!");

        // Get Registry, to look up more objects!
        try {
            registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
        } catch (RemoteException e) {
            System.out.println("RMI registry creation exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("RMI registry was created!");

        // Get Register, to register objects
        try {
            reg = (Register) registry.lookup(nameEntryBase);
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("RegisterRemoteObject not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Register received");

        // Remove current config values (if they exist)
        ConfigurationInterface conf = null;
        try {
            conf = (ConfigurationInterface) registry.lookup("config");
            conf.restart(nAvioes, nPassageiros, nMalas, nLugares, logName);
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("RegisterRemoteObject not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Configuration object was reconfigured");

        // Receive all monitors
        GeneralInterface zoneGeneral = null;
        ZoneArrivalInterface zoneA = null;
        ZoneEntryExitInterface zoneEnEx = null;
        ZoneLuggageInterface zoneLug = null;
        ZoneTAInterface zoneTA = null;
        ZoneTDInterface zoneTD = null;
        try {
            zoneGeneral = (GeneralInterface) registry.lookup(conf.getzGen());
            zoneA = (ZoneArrivalInterface) registry.lookup(conf.getzArr());
            zoneLug = (ZoneLuggageInterface) registry.lookup(conf.getzLug());
            zoneTA = (ZoneTAInterface) registry.lookup(conf.getzTA());
            zoneEnEx = (ZoneEntryExitInterface) registry.lookup(conf.getzEE());
            zoneTD = (ZoneTDInterface) registry.lookup(conf.getzTD());
        } catch (RemoteException e) {
            System.out.println("Lookup/creation exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Monitor not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Got all monitors");

        // Restart monitors
        try {
            zoneGeneral.restart(conf.getnP(), conf.getnL(), conf.getLogName());
            zoneA.restart(conf.getnA(), conf.getnP());
            zoneEnEx.restart(conf.getnP(), (GeneralPass) zoneGeneral);
            zoneLug.restart((GeneralLuggage) zoneGeneral, conf.getnP());
            zoneTA.restart((GeneralTransferArrival) zoneGeneral, conf.getnL());
            zoneTD.restart((GeneralTransferDeparture) zoneGeneral);
        } catch (Exception ex) {
            System.out.println("Couldn't restart server: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        System.out.println("All monitors restarted");
    }
}
