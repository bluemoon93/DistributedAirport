package Proj2.General.Interfaces;

/**
 * Interface of the General Monitor for the Porter Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface GeneralPorter {

    /**
     * Sets state of Porter
     *
     * @param st state of Porter
     */
    public void setPorterState(int st, int reqId);
}
