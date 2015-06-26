package Proj3.Plane;

import Proj3.ConfigurationInterface;
import Proj3.General.Interfaces.GeneralPass;
import Proj3.General.Interfaces.GeneralPlane;
import Proj3.Registry.Register;
import Proj3.ZoneArrival.Interfaces.ZoneArrivalPass;
import Proj3.ZoneArrival.Interfaces.ZoneArrivalPlane;
import Proj3.ZoneEntryExit.Interfaces.ZoneEntryExitPass;
import Proj3.ZoneEntryExit.Interfaces.ZoneEntryExitPlane;
import Proj3.ZoneLuggage.Interfaces.ZoneLuggagePass;
import Proj3.ZoneLuggage.Interfaces.ZoneLuggagePlane;
import Proj3.ZoneTA.Interfaces.ZoneTAPass;
import Proj3.ZoneTD.Interfaces.ZoneTDPass;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Class PlaneMain
 *
 * @author Tiago Soares, David Simoes
 */
public class PlaneMain {

    /**
     * Waits for a configure message and starts the Thread;
     *
     * @param args 0 to 1 arguments; Port number where the class will receive
     * connections
     */
    public static void main(String[] args) {
        System.setProperty("java.security.policy", "java.policy");
        
        String nameEntryConfig = "config";
        String nameEntryBase = "RegisterHandler";

        Registry registry = null;
        Register reg = null;
        ConfigurationInterface configInfo = null;

        String rmiRegHostName = "localhost";
        int rmiRegPortNumb = 40000;

        Plane thr = null;

        // Process args
        if (args.length == 2) {
            rmiRegHostName = args[0];
            rmiRegPortNumb = Integer.parseInt(args[1]);
        }

        System.out.println("Nome do nó de processamento onde está localizado o serviço de registo = " + rmiRegHostName);
        System.out.println("Número do port de escuta do serviço de registo = " + rmiRegPortNumb);
        System.out.println("Code base status = " + System.getProperty("java.rmi.server.useCodebaseOnly"));
        System.out.println("Code base = " + System.getProperty("java.rmi.server.codebase"));

        // Create and install the security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        System.out.println("Security manager was installed!");

        // Get Registry, to loop up more objects!
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

        // Get Configuration info, to have all the info regarding the monitors
        try {
            configInfo = (ConfigurationInterface) registry.lookup(nameEntryConfig);
        } catch (RemoteException e) {
            System.out.println("Configuration Info look up exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Configuration Info not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Configuration Info received");

        // Receive all monitors
        GeneralPlane zoneGeneralPlane = null;
        ZoneArrivalPlane zoneAPlane = null;
        ZoneEntryExitPlane zoneEnExPlane = null;
        ZoneLuggagePlane zoneLug = null;
        GeneralPass zoneGeneralPass = null;
        ZoneArrivalPass zoneAPass = null;
        ZoneEntryExitPass zoneEnExPass = null;
        ZoneLuggagePass zoneLugPass = null;
        ZoneTAPass zoneTAPass = null;
        ZoneTDPass zoneTDPass = null;

        try {
            zoneGeneralPlane = (GeneralPlane) registry.lookup(configInfo.getzGen());
            zoneAPlane = (ZoneArrivalPlane) registry.lookup(configInfo.getzArr());
            zoneEnExPlane = (ZoneEntryExitPlane) registry.lookup(configInfo.getzEE());
            zoneLug = (ZoneLuggagePlane) registry.lookup(configInfo.getzLug());
            zoneGeneralPass = (GeneralPass) registry.lookup(configInfo.getzGen());
            zoneAPass = (ZoneArrivalPass) registry.lookup(configInfo.getzArr());
            zoneLugPass = (ZoneLuggagePass) registry.lookup(configInfo.getzLug());
            zoneTAPass = (ZoneTAPass) registry.lookup(configInfo.getzTA());
            zoneEnExPass = (ZoneEntryExitPass) registry.lookup(configInfo.getzEE());
            zoneTDPass = (ZoneTDPass) registry.lookup(configInfo.getzTD());
        } catch (RemoteException e) {
            System.out.println("Monitors look up exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Monitors not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // Create and start thread
        try {
            thr = new Plane(configInfo.getnA(), configInfo.getnP(), configInfo.getnM(), zoneGeneralPlane, zoneAPlane, zoneEnExPlane, zoneLug,
                    zoneGeneralPass, zoneAPass, zoneEnExPass, zoneLugPass, zoneTAPass, zoneTDPass);
            thr.start();
        } catch (RemoteException e) {
            System.out.println("Error starting thread: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
