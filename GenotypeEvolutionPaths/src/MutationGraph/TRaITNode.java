package MutationGraph;

import java.util.ArrayList;

/**
 * Class to represent a node from the TRaIT tool output
 * INVARIANT: nodes and weight are of the same length and 
 * whights.get(i) represents the weight of the edge going from 
 * this to nodes.get(i) 
 * 
 * @author rossi
 *
 */
public class TRaITNode {
	
	ArrayList<TRaITNode> nodes;
	ArrayList<Double> weights;
	String label;
	long id;
	long numberOfParents = 0;
	public boolean visited = false; 
	
	/**
	 * Default constructor, creates a node labeled with a
	 * name (that should be a HUGO name of a gene) 
	 * @param label
	 */
	public TRaITNode(String label){
		this.label = label;
		this.nodes = new ArrayList<TRaITNode>();
		this.weights = new ArrayList<Double>();
	}
	
	/**
	 * setter for dot id.
	 * Lets the graph object set it for printing
	 * @param id dot id
	 */
	void setId(long id){
		this.id = id;
	}
	
	/**
	 * Adds a node to the adjacency list of this.
	 * It also sets the edge's weight
	 * @param other   the other node
	 * @param weight  the weight of the edge
	 */
	public void add(TRaITNode other, Double weight){
		this.nodes.add(other);
		this.weights.add(weight);
		other.numberOfParents++;
	}
	   
	/**
	 * @return the label associated with this node
	 */
	public String getLabel(){
		return this.label;
	}
	
	/**
	 * prints this node and its adjacency list in dot format
	 */
	public void toDot(){
		System.out.println(id + " [label=\"" + this.label + "\"]" );
		for(int i = 0; i<nodes.size(); i++){
			System.out.println(this.id + " -> " + nodes.get(i).id + " [label=\"" + weights.get(i) + "\"]" );
		}
	}
	
	/**
	 * @return True if this has no parents
	 */
	public boolean isRoot(){
		return numberOfParents==0;
	}

}
