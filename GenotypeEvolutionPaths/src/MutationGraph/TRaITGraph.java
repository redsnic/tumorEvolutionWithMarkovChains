package MutationGraph;

import java.util.ArrayList;

import MarkovChains.MarkovChain;
import Utils.Utils;

/**
 * Class to represent the output of TRaIT and to convert it in
 * a Markov chain. 
 * @author rossi
 *
 */
public class TRaITGraph {
	
	ArrayList<TRaITNode> nodes;
	
	/**
	 * Default constructor, creates an empty graph
	 */
	public TRaITGraph(){
		this.nodes = new ArrayList<TRaITNode>(); 
	}
	
	/**
	 * Adds a node to this graph, edges are managed at node level
	 * @param n
	 */
	public void add(TRaITNode n){
		nodes.add(n);
	}
	
	/**
	 * Adds an edge from a node to another with a give weight
	 * @param a  
	 * @param b
	 * @param weight
	 */
	public void add(TRaITNode a, TRaITNode b, double weight){
		a.add(b, weight);
	}
	
	/**
	 * Dot format print
	 */
	public void toDot(){
		long id = 0; /* dot ids setup */
		for(TRaITNode n : this.nodes){
			n.setId(id);
			id++;
		}
		System.out.println("digraph G{");
		for(TRaITNode n : this.nodes){
			n.toDot();
		}
		System.out.println("}");
	}
	
	/**
	 * Transforms this TRaIT graph in a Markov chain
	 * @return the newly created Markov chain
	 */
	public MarkovChain toMC(){
		
		int nRoots = 0; /* look for root */
		for(TRaITNode tNode : this.nodes ){
			if(!tNode.isRoot()) continue;  /* NOTE: there must be one and only one root (TRaIT specification)! */
			nRoots++;
			assert(nRoots<=1);
			return createMarkovChainFrom(tNode);
		}
		assert(false); /* ERROR, this code should not be reached*/
		return null;
	}

	/**
	 * Creates the Markov chain starting from a given node
	 * @param  tNode node of TRaIT graph from which to start REQUIRE: is the root (only node with no parent) 
	 * @return the newly created Markov Chain
	 */
	private MarkovChain createMarkovChainFrom(TRaITNode tNode) {
		
		MarkovChain mc = new MarkovChain();
		ArrayList<String> currentLabels = new ArrayList<String>();
		
		TRaITMarkovNode root = new TRaITMarkovNode();
		root.addLabel("clonal");
		recADD(tNode, root, mc, currentLabels);
		
		return mc;
		
	}

	/**
	 * recursion for the Markov chain creation, it operates following all the possible paths
	 * within the input graph. 
	 * @param tNode             TRaIT node now considered
	 * @param mcNode            Last Markov chain node REQUIRE: not yet in the final MC
	 * @param mc                The partial MC
	 * @param currentLabels     Stack for labels managing
	 * NOTE: this procedure operates MODIFYING mc, mcNode and currentLabels parameters 
	 */
	private void recADD(TRaITNode tNode, TRaITMarkovNode mcNode, MarkovChain mc, ArrayList<String> currentLabels) {
		mc.add(mcNode);                                           /* add node to Markov chain */
		double normFactor = Utils.sumDouble(tNode.weights);
		int i = 0;
		for(TRaITNode node : tNode.nodes){
			TRaITMarkovNode newNode = new TRaITMarkovNode();
			for(String s : currentLabels){
				newNode.addLabel(s);
			}
			newNode.addLabel(node.getLabel());                     /* create label */
			mcNode.add(newNode, tNode.weights.get(i)/normFactor);  /* add child in Markov Chain and compute probability */
			currentLabels.add(node.label);                         /* update current labels for recursion */
			recADD(node, newNode, mc, currentLabels);              /* continue recursion */
			i++;
		}
		if(currentLabels.size()>0){
			currentLabels.remove(currentLabels.size()-1);          /* remove last label */
		}
	}
	
	
	
	
	
	

}
