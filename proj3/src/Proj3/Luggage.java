package Proj3;

import java.io.Serializable;

/**
 * Class Luggage implements Serializable
 *
 * @author Tiago Soares, David Simoes
 */
public class Luggage implements Serializable{

    private final int ownerId;
    private final boolean ownerTravelling;

    /**
     * Constructor of Luggage Class
     *
     * @param ownerId id of the luggage owner
     * @param ownerTravelling whether the owner is travelling or not
     */
    public Luggage(int ownerId, boolean ownerTravelling) {
        this.ownerId = ownerId;
        this.ownerTravelling = ownerTravelling;
    }

    /**
     * Get the ID of the luggage owner
     *
     * @return ownerID
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * Check if the owner is travelling or not
     *
     * @return ownerTravelling
     */
    public boolean isOwnerTravelling() {
        return ownerTravelling;
    }
}