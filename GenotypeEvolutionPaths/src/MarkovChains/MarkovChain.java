package MarkovChains;

import java.util.ArrayList;

public class MarkovChain {

ArrayList<MarkovNode> nodes;
	
	/**
	 * Default constructor, creates an empty graph
	 */
	public MarkovChain(){
		this.nodes = new ArrayList<MarkovNode>(); 
	}
	
	/**
	 * Adds a node to this graph, edges are managed at node level
	 * @param n
	 */
	public void add(MarkovNode n){
		nodes.add(n);
	}
	
	/**
	 * Dot format print
	 */
	public void toDot(){
		long id = 0; /* dot ids setup */
		for(MarkovNode n : this.nodes){
			n.setId(id);
			id++;
		}
		System.out.println("digraph G{");
		for(MarkovNode n : this.nodes){
			n.toDot();
		}
		System.out.println("}");
	}
	
}
