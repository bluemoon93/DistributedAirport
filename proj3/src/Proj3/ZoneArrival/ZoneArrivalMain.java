package Proj3.ZoneArrival;

import Proj3.ConfigurationInterface;
import Proj3.Registry.Register;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class ZoneArrivalMain
 *
 * @author Tiago Soares, David Simoes
 */
public class ZoneArrivalMain {

    public static void main(String[] args) {
        System.setProperty("java.security.policy", "java.policy");
        
        String nameEntryConfig = "config";
        String nameEntryBase = "RegisterHandler";
        
        Registry registry = null;
        Register reg = null;
        ConfigurationInterface configInfo = null;
        
        String rmiRegHostName = "localhost";
        int rmiRegPortNumb = 40000;
        
        ZoneArrival monitor = null;
        ZoneArrivalInterface monitorStub = null;

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
        
        // Create monitor
        try {
            monitor = new ZoneArrival(configInfo.getnA(), configInfo.getnP());
        } catch (RemoteException e) {
            System.out.println("Error creating monitor: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        
        // Create stub for the monitor on given port 
        try {
            monitorStub = (ZoneArrivalInterface) UnicastRemoteObject.exportObject(monitor, configInfo.getzArrPort());
        } catch (RemoteException e) {
            System.out.println("Monitor stub generation exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Stub was generated!");

        // Register it with the general registry service 
        try {
            reg.bind(configInfo.getzArr(), monitorStub);
        } catch (RemoteException e) {
            System.out.println("Monitor registration exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("Monitor already bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Monitor object was registered!");
    }
}
