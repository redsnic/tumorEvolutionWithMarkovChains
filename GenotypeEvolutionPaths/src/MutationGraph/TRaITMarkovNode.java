package MutationGraph;

import java.util.ArrayList;

import MarkovChains.MarkovNode;
import Utils.Utils;

/**
 * @author redsnic
 * TODO wrong interpretation of weights on edges
 */
public class TRaITMarkovNode implements MarkovNode {

	long id = 0;
	ArrayList<TRaITMarkovNode> nodes;
	ArrayList<Double> weights;
	ArrayList<String> labels;
	
	public TRaITMarkovNode(){
		nodes = new ArrayList<TRaITMarkovNode>();
		weights = new ArrayList<Double>();
		labels = new ArrayList<String>();
	}
	
	@Override
	public void toDot() {
		System.out.println(id + " [label=\"" + this.labels + "\"]" );
		for(int i = 0; i<nodes.size(); i++){
			System.out.println(this.id + " -> " + nodes.get(i).id + " [label=\"" + weights.get(i) + "\"]" );
		}
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public void add(MarkovNode other, double weight) {
		assert (Utils.sumDouble(this.weights)+weight) <= 1; 
		nodes.add((TRaITMarkovNode) other);
		weights.add(weight);
	}
	
	public void addLabel(String label){
		labels.add(label);
	}



}
