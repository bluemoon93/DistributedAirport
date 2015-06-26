package Proj2.Mains;

import Proj2.General.General;
import Proj2.General.GeneralProxy;
import Proj2.PorterRequest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class GeneralMain
 *
 * @author Tiago Soares, David Simoes
 */
public class GeneralMain {

    /**
     * Waits for a configure message and starts the Monitor; 
     * After that, it receives requests and creates threads to handle them.
     *
     * @param args 0 to 1 arguments; Port number where the class will receive connections
     */
    public static void main(String[] args) {
        ServerSocket serverSock = null;
        int port = 10001;
        General monitor = null;

        try {
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);
            } else // DO NOTHING
            {
            }
        } catch (Exception ex) {
            System.out.println("Usage: java -jar SD_T1_P2_G2.jar [portNumber]");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar " + port);
        }

        int nPass = 0, nLug = 0;
        BufferedWriter bw;
        String logName = "";

        System.out.println("General monitor waiting for configuration values");
        boolean configured = false;
        try {
            serverSock = new ServerSocket(port);
            while (!configured) {
                try (Socket configSock = serverSock.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(configSock.getInputStream()));
                    String[] configValues = in.readLine().split(";");

                    if (configValues.length == 4 && configValues[0].equals("CONF")) {
                        nPass = Integer.parseInt(configValues[1]);
                        nLug = Integer.parseInt(configValues[2]);
                        logName = configValues[3];
                        configured = true;
                    } else {
                        System.out.println("ERROR: " + configValues[0]);
                        throw new Exception();
                    }
 
                    bw = new BufferedWriter(new FileWriter(logName), 32 * 1024);

                    System.out.println("Configured successfully.");
                    System.out.println("nPassageiros = " + nPass);
                    System.out.println("nLugares = " + nLug);
                    System.out.println("The log file is named \"" + logName + "\"\n");

                    monitor = new General(nPass, nLug, bw);

                    System.out.println("\nWaiting for requests.");

                    PrintWriter out = new PrintWriter(configSock.getOutputStream());
                    out.write("OK;\n");
                    out.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        while (true) {
            try {
                GeneralProxy client = new GeneralProxy(serverSock.accept(), monitor);
                client.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}
