package Proj3.Driver;

import Proj3.ConfigurationInterface;
import Proj3.General.Interfaces.GeneralDriver;
import Proj3.Registry.Register;
import Proj3.ZoneEntryExit.Interfaces.ZoneEntryExitDriver;
import Proj3.ZoneTA.Interfaces.ZoneTADriver;
import Proj3.ZoneTD.Interfaces.ZoneTDDriver;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Class DriverMain
 *
 * @author Tiago Soares, David Simoes
 */
public class DriverMain {

    public static void main(String[] args) {
        System.setProperty("java.security.policy", "java.policy");
        
        String nameEntryConfig = "config";
        String nameEntryBase = "RegisterHandler";

        Registry registry = null;
        Register reg = null;
        ConfigurationInterface configInfo = null;

        String rmiRegHostName = "localhost";
        int rmiRegPortNumb = 40000;

        Driver thr = null;

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
        ZoneTADriver a = null;
        ZoneTDDriver b = null;
        ZoneEntryExitDriver c = null;
        GeneralDriver g = null;

        try {
            a = (ZoneTADriver) registry.lookup(configInfo.getzTA());
            b = (ZoneTDDriver) registry.lookup(configInfo.getzTD());
            c = (ZoneEntryExitDriver) registry.lookup(configInfo.getzEE());
            g = (GeneralDriver) registry.lookup(configInfo.getzGen());
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
            thr = new Driver(a, b, c, g, configInfo.getnA(), configInfo.getnP());
            thr.start();
        } catch (RemoteException e) {
            System.out.println("Error starting thread: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
