package Proj2;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Class Config
 *
 * @author Tiago Soares, David Simoes
 */
public class Config {

    private static int nAvioes = 5, nPassageiros = 6, nMalas = 2, nLugares = 3;
    private static String logName = "log.txt", configName = "config.txt";
    private static boolean terminate = false;
    private static InetSocketAddress[] addresses = new InetSocketAddress[9];

    /**
     * Start-up class to contact every machine and configure them accordingly.
     *
     * @param args Usage: [[-nA nAvioes] [-nP nPassageiros] [-nM nMalas] [-nL nLugares] [-log logNameFile] [-con configNameFile]] [-ter]
     */
    public static void main(String[] args) {
        int index = 0;

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
                } else if (args[index].equals("-con")) {
                    configName = args[++index];
                } else if (args[index].equals("-ter")) {
                    terminate = true;
                } else {
                    throw new Exception();
                }

                index++;
            }
        } catch (Exception ex) {
            System.out.println("Usage: java -jar SD_T1_P1_G2.jar [-nA nAvioes] [-nP nPassageiros] [-nM nMalas] [-nL nLugares] [-log logNameFile] [-con configNameFile]");
            System.out.println("Usage: java -jar SD_T1_P1_G2.jar [-ter]");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar -log log.txt -con config.txt");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar -nA 5 -nP 6 -nM 2 -nL 3");
            System.out.println("\tEX: java -jar SD_T1_P1_G2.jar -ter all");
            System.exit(1);
        }

        try {
            if (new File(configName).canRead()) {
                Scanner fin = new Scanner(new File(configName));
                int i = 0;
                while (fin.hasNextLine() && i < 9) {
                    String[] fields = fin.nextLine().split(" ");
                    addresses[i] = new InetSocketAddress(fields[0], Integer.parseInt(fields[1]));
                    i++;
                }
            } else {
                throw new Exception();
            }
        } catch (Exception ex) {
            System.out.println("Configuration file \"" + configName + "\" corrupted!");
            ex.printStackTrace();
            System.exit(1);
        }

        if (!terminate) {
            System.out.println("Simulation started.");
            System.out.println("nAvioes = " + nAvioes);
            System.out.println("nPassageiros = " + nPassageiros);
            System.out.println("nMalas = " + nMalas);
            System.out.println("nLugares = " + nLugares);
            System.out.println("The log file is named \"" + logName + "\"\n");
            System.out.println("GeneralMonitor \t\t\t@ " + addresses[0]);
            System.out.println("ZoneArrivalMonitor \t\t@ " + addresses[1]);
            System.out.println("ZoneEntryExitMonitor \t\t@ " + addresses[2]);
            System.out.println("ZoneLuggageMonitor \t\t@ " + addresses[3]);
            System.out.println("ZoneTransferArrivalMonitor \t@ " + addresses[4]);
            System.out.println("ZoneTransferDepartureMonitor \t@ " + addresses[5] + "\n");
            System.out.println("Porter \t\t\t\t@ " + addresses[6]);
            System.out.println("Driver \t\t\t\t@ " + addresses[7]);
            System.out.println("Plane (Passengers) \t\t@ " + addresses[8]);

            try {
                startGeneralMonitor(nPassageiros, nLugares, logName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            try {
                startZoneAMonitor(nAvioes, nPassageiros);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            try {
                startZoneEnExMonitor(nPassageiros, addresses[0]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            try {
                startZoneLugMonitor(addresses[0],nPassageiros);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            try {
                startZoneTAMonitor(addresses[0], nLugares);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            try {
                startZoneTDMonitor(addresses[0], nPassageiros);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            try {

                startDriverThread(addresses[4], addresses[5], addresses[2], addresses[0], nAvioes, nPassageiros);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            try {
                startPorterThread(addresses[3], addresses[1], addresses[0]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            try {
                startPlaneThread(nAvioes, nPassageiros, nMalas, addresses[0], addresses[1], addresses[2], addresses[3], addresses[4], addresses[5]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Shuting down monitors.\n");
            System.out.println("GeneralMonitor \t\t\t@ " + addresses[0]);
            System.out.println("ZoneArrivalMonitor \t\t@ " + addresses[1]);
            System.out.println("ZoneEntryExitMonitor \t\t@ " + addresses[2]);
            System.out.println("ZoneLuggageMonitor \t\t@ " + addresses[3]);
            System.out.println("ZoneTransferArrivalMonitor \t@ " + addresses[4]);
            System.out.println("ZoneTransferDepartureMonitor \t@ " + addresses[5] + "\n");

            for (int i = 0; i < 6; i++) {
                try {
                    terminateMonitor(addresses[i]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void terminateMonitor(InetSocketAddress address) throws Exception {
        try (Socket tcpSock = new Socket(address.getAddress(), address.getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("EXIT;\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }
    }

    private static void startGeneralMonitor(int nPassageiros, int nLugares, String outFile) throws Exception {
        try (Socket tcpSock = new Socket(addresses[0].getAddress(), addresses[0].getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("CONF;" + nPassageiros + ";" + nLugares + ";" + outFile + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }
    }

    private static void startZoneAMonitor(int nAvioes, int nPassageiros) throws Exception {
        try (Socket tcpSock = new Socket(addresses[1].getAddress(), addresses[1].getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("CONF;" + nAvioes + ";" + nPassageiros + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }
    }

    private static void startZoneEnExMonitor(int nPassageiros, InetSocketAddress inetSocketAddress) throws Exception {
        try (Socket tcpSock = new Socket(addresses[2].getAddress(), addresses[2].getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("CONF;" + nPassageiros + ";" + inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort() + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }
    }

    private static void startZoneLugMonitor(InetSocketAddress inetSocketAddress, int pass) throws Exception {
        try (Socket tcpSock = new Socket(addresses[3].getAddress(), addresses[3].getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("CONF;" + inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort() + ";"+pass+";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }
    }

    private static void startZoneTAMonitor(InetSocketAddress inetSocketAddress, int nLugares) throws Exception {
        try (Socket tcpSock = new Socket(addresses[4].getAddress(), addresses[4].getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("CONF;" + inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort() + ";" + nLugares + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }
    }

    private static void startZoneTDMonitor(InetSocketAddress inetSocketAddress, int people) throws Exception {
        try (Socket tcpSock = new Socket(addresses[5].getAddress(), addresses[5].getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("CONF;" + inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort() + ";"+people+";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }
    }

    private static void startDriverThread(InetSocketAddress inetSocketAddress, InetSocketAddress inetSocketAddress0, InetSocketAddress inetSocketAddress1, InetSocketAddress inetSocketAddress2, int nAvioes, int nPassageiros) throws Exception {
        try (Socket tcpSock = new Socket(addresses[7].getAddress(), addresses[7].getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("CONF;" + inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort() + ";"
                    + inetSocketAddress0.getHostName() + ":" + inetSocketAddress0.getPort() + ";"
                    + inetSocketAddress1.getHostName() + ":" + inetSocketAddress1.getPort() + ";"
                    + inetSocketAddress2.getHostName() + ":" + inetSocketAddress2.getPort() + ";"
                    + nAvioes + ";" + nPassageiros + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }
    }

    private static void startPorterThread(InetSocketAddress inetSocketAddress, InetSocketAddress inetSocketAddress0, InetSocketAddress inetSocketAddress1) throws Exception {
        try (Socket tcpSock = new Socket(addresses[6].getAddress(), addresses[6].getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("CONF;" + inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort() + ";"
                    + inetSocketAddress0.getHostName() + ":" + inetSocketAddress0.getPort() + ";"
                    + inetSocketAddress1.getHostName() + ":" + inetSocketAddress1.getPort() + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }
    }

    private static void startPlaneThread(int nAvioes, int nPassageiros, int nMalas, InetSocketAddress inetSocketAddress, InetSocketAddress inetSocketAddress0, InetSocketAddress inetSocketAddress1, InetSocketAddress inetSocketAddress2, InetSocketAddress inetSocketAddress3, InetSocketAddress inetSocketAddress4) throws Exception {
        try (Socket tcpSock = new Socket(addresses[8].getAddress(), addresses[8].getPort())) {
            PrintWriter out = new PrintWriter(tcpSock.getOutputStream());
            out.write("CONF;" + nAvioes + ";" + nPassageiros + ";" + nMalas + ";"
                    + inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort() + ";"
                    + inetSocketAddress0.getHostName() + ":" + inetSocketAddress0.getPort() + ";"
                    + inetSocketAddress1.getHostName() + ":" + inetSocketAddress1.getPort() + ";"
                    + inetSocketAddress2.getHostName() + ":" + inetSocketAddress2.getPort() + ";"
                    + inetSocketAddress3.getHostName() + ":" + inetSocketAddress3.getPort() + ";"
                    + inetSocketAddress4.getHostName() + ":" + inetSocketAddress4.getPort() + ";\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            String ok = in.readLine();
        }
    }
}
