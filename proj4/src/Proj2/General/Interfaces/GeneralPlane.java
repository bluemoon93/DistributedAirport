package Proj2.General.Interfaces;

/**
 * Interface of the General Monitor for the Plane Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface GeneralPlane {
    /**
     * Sets the current plane's flight number and how many bags the plane was
     * supposed to be carrying
     *
     * @param fn flight number
     * @param ln number of luggages presently at the plane's hold
     */
    public void setMalas(int fn, int ln);
    
    /**
     * Prints header lines for the status, for easy reading and comprehension.
     *
     */
    public void resetLog();
    
    /**
     * Sets passenger travelling status
     *
     * @param over if is travelling or not
     * @param i of the passenger
     */
    public void setPassengerTravel(int i, boolean over);
    
    /**
     * Sets how many bags the passenger was meant to be carrying.
     *
     * @param bags number of pieces of luggage the passenger carried at the start
     * of his journey
     * @param i of the passenger
     */
    public void setPassengerMaxBags(int i, int bags);
}
