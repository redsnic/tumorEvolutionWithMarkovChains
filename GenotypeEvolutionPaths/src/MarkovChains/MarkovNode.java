package MarkovChains;

/**
 * A node of a MarkovChain 
 * REQUIRE: sum of weights must be 1 for each node
 * @author rossi
 *
 */
public interface MarkovNode {
		
	public void toDot();
	public void setId(long id);
	
	/**
	 * Adds a node to this adjacency list
	 * @param other
	 * @param weight 
	 */
	public void add(MarkovNode other, double weight);

}
