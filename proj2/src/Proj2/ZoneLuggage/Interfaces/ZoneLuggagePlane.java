package Proj2.ZoneLuggage.Interfaces;

/**
 * Interface of the ZoneLuggage Monitor for the Plane Thread
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneLuggagePlane {
    
    /**
     * Plane waits until porter has finished all his luggage.
     */
    public void resetPlane();
}
