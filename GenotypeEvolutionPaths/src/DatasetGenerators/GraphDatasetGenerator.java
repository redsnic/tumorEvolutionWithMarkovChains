package DatasetGenerators;

import java.util.ArrayList;

import GenotypeGraph.GenotypeNode;
import Utils.Utils;

/**
 * Class for creating a generic weighted graph of genotypes
 * from which to generate data sets to be used for 
 * the validation of tumor evolution inference methods
 * NOTE: might be interesting to use this with other tools
 * TODO add output on STDOUT 
 * @author redsnic
 */
public class GraphDatasetGenerator {
	
	ArrayList<GenotypeNode> V = new ArrayList<GenotypeNode>();
	/**
	 * Weights are managed in a matrix
	 */
	ArrayList<ArrayList<Double>> W = new ArrayList<ArrayList<Double>>();
	int id = 0;
	GenotypeNode root = null;
	
	/**
	 * Default constructor (empty graph)
	 */
	public GraphDatasetGenerator(){	
	}
	
	/** 
	 * Adds a node to the graph,
	 * it also manages the modification of the adj matrix
	 * @param n node to be added
	 */
	public void add(GenotypeNode n){
		V.add(n);
		n.setId(id);
		W.add(new ArrayList<Double>());
		for(int i = 0; i<id; i++){
			W.get(id).add(0.);
		}
		for(int i = 0; i<=id; i++){
			W.get(i).add(0.);
		}
		id++;
	}
	
	/**
	 * Adds an edge from a to b
	 * @param a source
	 * @param b destination
	 * @param weight of the edge
	 */
	public void link(GenotypeNode a, GenotypeNode b, double weight){
		a.add(b);
		W.get((int) a.getId()).set((int) b.getId(), weight);
	}
	
	/**
	 * Shortcut to get the weight of the edge from a to b
	 * @param a
	 * @param b
	 * @return the weight of the edge from a to b
	 */
	private double getWeight(GenotypeNode a, GenotypeNode b){
		return W.get((int) a.getId()).get((int) b.getId());
	}
	
	/**
	 * @param a a node
	 * @return the sum of the weights of the outcoming edges of node a 
	 */
	private double totalWeight(GenotypeNode a){
		return Utils.sumDouble(W.get((int) a.getId()));
	}
	
	/**
	 * Sets which node is the clonal genotype
	 * @param root a node 
	 */
	public void setRoot(GenotypeNode root){
		this.root = root;
	}
	
	/**
	 * Simulates a path of limit length from
	 * the clonal genotype
	 * @param limit length of the path
	 * @return genotype of the last node visited following the path
	 */
	public boolean[] simulate(int limit){
		assert(root!=null);
		GenotypeNode position = this.root;
		for(int i=0; i<limit; i++){
			double chooseRoute = Math.random()*totalWeight(position);
			for(GenotypeNode next : position.getAdj()){
				if(chooseRoute < getWeight(position, next)){
					position = next;
					break;
				} else {
					chooseRoute-=getWeight(position, next);
				}
			}
		}
		return position.getGenotype();		
	}

}
