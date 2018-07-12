package GenotypeGraph;

import java.util.ArrayList;

import MarkovChains.MarkovNode;
import Utils.Utils;

public class GenotypeMarkovNode implements MarkovNode {

	long id = 0;
	ArrayList<GenotypeMarkovNode> nodes;
	ArrayList<Double> weights;
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
