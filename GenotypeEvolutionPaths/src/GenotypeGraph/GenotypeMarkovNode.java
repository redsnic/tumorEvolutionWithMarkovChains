package GenotypeGraph;

import java.util.ArrayList;

import MarkovChains.MarkovNode;
import Utils.Utils;

/**
 * TODO re-implement with adjacency matrix if needed
 * @author rossi
 *
 */
public class GenotypeMarkovNode implements MarkovNode {

	/**
	 * Unique id of this node
	 */
	long id = 0;
	/**
	 * adjacency list of this node
	 */
	ArrayList<GenotypeMarkovNode> nodes;
	/**
	 * weights associated with each outgoing edge of this node
	 */
	ArrayList<Double> weights;
	/**
	 * label used when printing this node
	 */
	String label;
	
	/**
	 * Default Constructor, creates a node of a Markov Chain with a given label
	 */
	public GenotypeMarkovNode(String label){
		nodes = new ArrayList<GenotypeMarkovNode>();
		weights = new ArrayList<Double>();
		this.label = label;
	}
	
	@Override
	public void toDot() {
		System.out.println(id + " [label=\"" + this.label + "\"]" );
		for(int i = 0; i<nodes.size(); i++){
			System.out.println(this.id + " -> " + nodes.get(i).id + " [label=\"" + String.format("%.3f", weights.get(i)) + "\"]" );
		}
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public void add(MarkovNode other, double weight) {
		assert (Utils.sumDouble(this.weights)+weight) <= 1.0001 : "value greater than 1 was " + (Utils.sumDouble(this.weights)+weight) + "\n"; 
		nodes.add((GenotypeMarkovNode) other);
		weights.add(weight);
	}
	
}   
