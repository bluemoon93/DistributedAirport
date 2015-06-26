package Proj1.ZoneLuggage;

/**
 * Interface ZoneLuggagePass
 *
 * @author Tiago Soares, David Simoes
 */
public interface ZoneLuggagePass {

    /**
     * Reports more missing bags
     *
     * @param bags number of bags missing from this passenger
     */
    public void reportLuggageMissing(int id, int bags);

    /**
     * Gets all the luggage from passenger with ID=id; Waits until passenger has all his bags or until there are no more bags
     *
     * @param id of the passenger
     * @param lugTotal total number of bags he has to catch
     * @return how much luggage this passenger caught
     */
    public int getLuggage(int id, int lugTotal);
}
