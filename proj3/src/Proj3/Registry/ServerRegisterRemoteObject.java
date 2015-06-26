package Proj3.Registry;

import Proj3.Configuration;
import Proj3.ConfigurationInterface;
import java.rmi.AlreadyBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * This data type instantiates and registers a remote object that enables the
 * registration of other remote objects located in the same or other processing
 * nodes in the local registry service. Communication is based in Java RMI.
 */
public class ServerRegisterRemoteObject {

    public static void main(String[] args) {
        int index=0, nAvioes=5, nPassageiros=6, nLugares=3, nMalas=2, rmiRegPortNumb = 40000; 
        String logName = "log.txt", rmiRegHostName = "localhost";
        System.setProperty("java.security.policy", "java.policy");
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
        
        System.out.println("Nome do nó de processamento onde está localizado o serviço de registo = " + rmiRegHostName);
        System.out.println("Número do port de escuta do serviço de registo = " + rmiRegPortNumb);
        System.out.println("Code base status = " + System.getProperty("java.rmi.server.useCodebaseOnly"));
        System.out.println("Code base = " + System.getProperty("java.rmi.server.codebase"));

        /* create and install the security manager */
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        System.out.println("Security manager was installed!");

        /* instantiate a registration remote object and generate a stub for it */
        RegisterRemoteObject regEngine = new RegisterRemoteObject(rmiRegHostName, rmiRegPortNumb);
        Register regEngineStub = null;
        int listeningPort = 22000;                            /* it should be set accordingly in each case */

        try {
            regEngineStub = (Register) UnicastRemoteObject.exportObject(regEngine, listeningPort);
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject stub generation exception: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Stub was generated!");

        /* register it with the local registry service */
        String nameEntry = "RegisterHandler";
        Registry registry = null;

        try {
            //registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            registry = LocateRegistry.createRegistry(rmiRegPortNumb);
        } catch (RemoteException e) {
            System.out.println("RMI registry creation exception: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("RMI registry was created!");

        try {
            registry.rebind(nameEntry, regEngineStub);
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject remote exception on registration: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("RegisterRemoteObject object was registered!");
        
        // Create configuration values
        Configuration conf = new Configuration(nAvioes, nPassageiros, nMalas, nLugares, logName);
        ConfigurationInterface confStub = null;
        
        try {
            confStub = (ConfigurationInterface) UnicastRemoteObject.exportObject(conf, 22006);
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject stub generation exception: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Stub was generated!");
        
        // Register it with the general registry service 
        try {
            registry.bind("config", confStub);
        } catch (RemoteException e) {
            System.out.println("Monitor registration exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("Monitor already bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Configuration object was registered!");
    }
}
