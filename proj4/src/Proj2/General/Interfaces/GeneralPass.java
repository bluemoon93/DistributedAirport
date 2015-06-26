package Proj2.General.Interfaces;

/**
 * Interface of the General Monitor for the Passenger Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface GeneralPass {

    /**
     * Sets the state of the passenger with ID=id
     *
     * @param st state of Passenger
     * @param id of the passenger
     */
    public void setPassengerState(int id, int st);
}
