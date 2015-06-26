package Proj2.General;

import Proj2.General.Interfaces.*;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Class General wich implements GeneralDriver, GeneralPorter, GeneralPass,
 * GeneralLuggage and GeneralTransfer interfaces
 *
 * @author Tiago Soares, David Simoes
 */
public class General implements GeneralDriver, GeneralPorter, GeneralPass, GeneralLuggage, GeneralTransferArrival, GeneralTransferDeparture, GeneralPlane {

    private int nPassageiros, nLugares;
    private int dSt, pSt, FN, BN, CB, CR, bagsMissing;
    private int[] passSt, Q, S, NR, NA;
    private boolean[] Si;
    private String lastString;
    private final String[] passStates = {"ADZ", "LCP", "BRO", "EAT", "ATT", " TF", "DTT", "EDT"};
    private final String[] driverStates = {"PAT", " DF", "PDT", " DB"};
    private final String[] porterStates = {"WPL", "APH", "LBC", " SR"};
    BufferedWriter outFile;

    /**
     * Constructor of Class General
     *
     * @param nPass number of passengers per plane
     * @param nLug maximum number of bags per passenger
     * @param a BufferedWriter for the log file
     */
    public General(int nPass, int nLug, BufferedWriter a) {
        this.restart(nPass, nLug, a);
    }

    /**
     * Adds the passenger with ID = id to the waiting queue at the Arrival
     * Transfer Zone.
     *
     * @param id of passenger
     */
    @Override
    public synchronized void addWaitQueue(int id) {
        for (int i = 0; i < nPassageiros; i++) {
            if (Q[i] == -1) {
                Q[i] = id;
                break;
            }
        }

        reportLog();
    }

    /**
     * Removes the 1st passenger from the waiting queue at the Arrival Transfer
     * Zone.
     *
     */
    @Override
    public synchronized void leaveWaitQueue() {
        for (int i = 0; i < nPassageiros - 1; i++) {
            Q[i] = Q[i + 1];
        }
        Q[nPassageiros - 1] = -1;

        reportLog();
    }

    /**
     * Adds the passenger with ID = id to the bus queue
     *
     * @param id of passenger
     */
    @Override
    public synchronized void addBusQueue(int id) {
        for (int i = 0; i < nLugares; i++) {
            if (S[i] == -1) {
                S[i] = id;
                break;
            }
        }

        reportLog();
    }

    /**
     * Removes the passenger with ID = id to the bus queue
     *
     * @param id of passenger
     */
    @Override
    public synchronized void leaveBusQueue(int id) {
        for (int i = 0; i < nLugares; i++) {
            if (S[i] == id) {
                S[i] = -1;
                break;
            }
        }

        reportLog();
    }

    /**
     * Sets how many bags are currently in the Luggage Belt Conveyor at the
     * LuggageZone
     *
     * @param n number of luggages in luggage belt conveyor
     */
    @Override
    public synchronized void setLCB(int n) {
        CB = n;
        reportLog();
    }

    /**
     * Sets how many bags are currently in the StoreRoom at the LuggageZone
     *
     * @param n number of luggages in storeRoom
     */
    @Override
    public synchronized void setST(int n) {
        CR = n;
        reportLog();
    }

    /**
     * Sets the current plane's flight number and how many bags the plane was
     * supposed to be carrying
     *
     * @param fn flight number
     * @param ln number of luggages presently at the plane's hold
     */
    @Override
    public synchronized void setMalas(int fn, int ln) {
        FN = fn;
        BN = ln;

        reportLog();
    }

    /**
     * Sets driver state
     *
     * @param st state of driver
     */
    @Override
    public synchronized void setDriverState(int st) {
        dSt = st;
        reportLog();
    }

    /**
     * Sets porter state
     *
     * @param st state of porter
     */
    @Override
    public synchronized void setPorterState(int st, int reqId) {
        pSt = st;
        reportLog();
    }

    /**
     * Sets the state of the passenger with ID=id
     *
     * @param st state of passenger
     * @param id of the passenger
     */
    @Override
    public synchronized void setPassengerState(int id, int st) {
        passSt[id] = st;
        reportLog();
    }

    /**
     * Sets passenger travelling status
     *
     * @param st if is travelling or not
     * @param id of the passenger
     */
    @Override
    public synchronized void setPassengerTravel(int id, boolean st) {
        Si[id] = st;
    }

    /**
     * Sets how many bags the passenger was meant to be carrying
     *
     * @param st number of pieces of luggage the passenger carried at the start
     * of his journey
     * @param id of the passenger
     */
    @Override
    public synchronized void setPassengerMaxBags(int id, int st) {
        NR[id] = st;
        NA[id] = 0;
    }

    /**
     * Sets how many bags the passenger currently has
     *
     * @param st number of pieces of luggage the passenger that he has presently collected
     * @param id of the passenger
     */
    @Override
    public synchronized void setPassengerCurrBags(int id, int st) {
        NA[id] = st;
        reportLog();
    }

    /**
     * Sets how many bags have been given as missing through the day
     *
     * @param b number of missing bags
     */
    @Override
    public synchronized void reportBagsMissing(int b) {
        bagsMissing = b;
    }

    /**
     * Prints a line with the current status of the airport.
     *
     */
    public synchronized void reportLog() {
        char[] situationPsg = new char[nPassageiros],
                charNR = new char[nPassageiros], charNA = new char[nPassageiros];

        String charStateD, charStateP, charFN;
        String[] passSt2 = new String[nPassageiros], waitS = new String[nLugares], waitQ = new String[nPassageiros];

        for (int i = 0; i < nLugares; i++) {
            if (S[i] >= 0) {
                waitS[i] = Integer.toString(S[i]);
            } else {
                waitS[i] = "-";
            }
        }

        if (FN >= 0) {
            charFN = Integer.toString(FN);
        } else {
            charFN = " -";
        }

        if (dSt >= 0) {
            charStateD = driverStates[dSt];
        } else {
            charStateD = " - ";
        }

        if (pSt >= 0) {
            charStateP = porterStates[pSt];
        } else {
            charStateP = " - ";
        }

        for (int i = 0; i < nPassageiros; i++) {
            if (NR[i] >= 0) {
                charNR[i] = Integer.toString(NR[i]).charAt(0);
            } else {
                charNR[i] = '-';
            }

            if (NA[i] >= 0) {
                charNA[i] = Integer.toString(NA[i]).charAt(0);
            } else {
                charNA[i] = '-';
            }

            if (Si[i] == true) {
                situationPsg[i] = 'T';
            } else {
                situationPsg[i] = 'F';
            }

            if (Q[i] >= 0) {
                waitQ[i] = Integer.toString(Q[i]);
            } else {
                waitQ[i] = "-";
            }

            if (passSt[i] >= 0) {
                passSt2[i] = passStates[passSt[i]];
            } else {
                passSt2[i] = " - ";
                situationPsg[i] = '-';
                charNA[i] = '-';
                charNR[i] = '-';
            }
        }

        String out = String.format("%4s %3d    %4s %2d %4d   %4s ",
                charFN, BN, charStateP, CB, CR, charStateD);

        for (int i = 0; i < nPassageiros; i++) {
            String temp = String.format(" %2s ", waitQ[i]);
            out += temp;
        }
        out += " ";
        for (int i = 0; i < nLugares; i++) {
            String temp = String.format(" %2s", waitS[i]);
            out += temp;
        }
        out += "  ";
        for (int i = 0; i < nPassageiros; i++) {
            String temp = String.format(" |  %s   %c    %c    %c ", passSt2[i], situationPsg[i], charNR[i], charNA[i]);
            out += temp;
        }
        out += '\n';

        if (!out.equals(lastString)) {
            writeFile(out);

            if (!lastString.equals("") && checkStates() == true) {
                closeFile();
            }

            lastString = out;
        }
    }

    private boolean checkStates() {
        if (dSt != -1) {
            return false;
        }
        if (pSt != -1) {
            return false;
        }
        for (int i = 0; i < passSt.length; i++) {
            if (passSt[i] != -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Prints header lines for the status, for easy reading and comprehension.
     *
     */
    @Override
    public synchronized void resetLog() {
        writeFile("-------------------------------");
        for (int i = 0; i < nPassageiros; i++) {
            writeFile("----");
        }
        writeFile("--");
        for (int i = 0; i < nLugares; i++) {
            writeFile("----");
        }
        for (int i = 0; i < nPassageiros; i++) {
            writeFile("----------------------");
        }
        writeFile("\n");

        writeFile("  PLANE     PORTER             DRIVER                                                       PASSENGERS\n");
        writeFile("  FN  BN    Stat CB   SR   Stat");
        for (int i = 0; i < nPassageiros; i++) {
            String t = String.format(" Q%02d", i);
            writeFile(t);
        }
        writeFile("  ");
        for (int i = 0; i < nLugares; i++) {
            String t = String.format(" S%d", i);
            writeFile(t);
        }
        writeFile("  ");
        for (int i = 0; i < nPassageiros; i++) {
            String t = String.format(" | St%02d Si%02d NR%02d NA%02d", i, i, i, i);
            writeFile(t);
        }
        writeFile("\n");
    }

    private synchronized void closeFile() {
        writeFile("Day has ended. " + bagsMissing + " bags were lost.");
        try {
            outFile.flush();
            outFile.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private synchronized void writeFile(String x) {
        //System.out.print(x);
        try {
            outFile.write(x);
            //outFile.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /** Re-initialized all the variables in the monitor
     *
     * @param nPass number of passengers per plane
     * @param nLug maximum number of bags per passenger
     * @param a BufferedWriter for the log file
     */
    protected final synchronized void restart(int nPass, int nLug, BufferedWriter a) {
        nPassageiros = nPass;
        nLugares = nLug;
        outFile = a;

        passSt = new int[nPassageiros];
        Q = new int[nPassageiros];
        S = new int[nLugares];
        NR = new int[nPassageiros];
        NA = new int[nPassageiros];
        Si = new boolean[nPassageiros];

        lastString = "";
        bagsMissing = BN = CB = CR = 0;
        dSt = pSt = FN = -1;

        for (int i = 0; i < nPassageiros; i++) {
            NA[i] = 0;
            passSt[i] = NR[i] = Q[i] = -1;
        }

        for (int i = 0; i < nLugares; i++) {
            S[i] = -1;
        }

        writeFile("     AIRPORT RHAPSODY - Description of the internal state of the problem\n");
        resetLog();
        reportLog();
    }
}
