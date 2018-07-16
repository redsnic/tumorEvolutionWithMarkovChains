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
	/**
	 * unique id for the next node to be added
	 */
	int id = 0;
	/**
	 * reference to the node containing the clonal genotype
	 */
	GenotypeNode root = null;
	/**
	 * list of names used as labels for genes in this generator
	 */
	private String[] geneLabelsOrder;
	
	/**
	 * Getter for the list of names used as labels for genes in this generator
	 * @return labels
	 */
	public String[] getLabels(){
		return this.geneLabelsOrder;
	}
	
	/**
	 * Default constructor (empty graph)
	 */
	public GraphDatasetGenerator(String[] labels){	
		this.geneLabelsOrder = labels;
	}
	
	/**
	 * Alternate constructor (random graph with n genes)
	 * @param n number of artificial genes 
	 */
	public GraphDatasetGenerator(int n){
		
		assert(n<64): "too many genes (long limit)!";
		this.geneLabelsOrder = new String[n];
		for(int i=0; i<geneLabelsOrder.length; i++){
			this.geneLabelsOrder[i] = "G" + i;
		}
		
		boolean[] root = new boolean[n];
		GenotypeNode nroot = new GenotypeNode(root);
		this.add(nroot);
		this.setRoot(nroot);
		
		ArrayList<Integer> mutcounts = new ArrayList<Integer>();
		mutcounts.add(0);
		
		for(long i=1 ; i<Math.pow(2, this.geneLabelsOrder.length); i++){
			boolean[] genotype = Utils.binaryToBoolArray(i, geneLabelsOrder.length);
			int totals = Utils.sumBool(genotype);
			double selector = Math.random();
			
			if(selector < 1/(double)totals){
				this.add(new GenotypeNode(genotype));
				mutcounts.add(totals);
			}
		}
		
		for(GenotypeNode node : V){
			link(node,node,Math.random());
		}
		
		for(GenotypeNode a : V){
			for(GenotypeNode b : V){
				if(mutcounts.get((int) a.getId())+1==mutcounts.get((int) b.getId()) && Utils.singleMismatch(a.getGenotype(), b.getGenotype())){
					link(a,b,Math.random());
				}
			}
		}
		
		for(GenotypeNode a : V){
			if(a.getNumberOfParents()==1 && a != this.root){
				link(this.root,a, Math.random());
			}
		}

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
	
	/**
	 * print this graph in dot format
	 */
	public void toDot(){
		System.out.println("digraph G{");
		for(GenotypeNode n : V){
			this.toDotNode(n);
		}
		System.out.println("}");
	}

	/**
	 * Dot print of a single node
	 * @param node the node to be printed
	 */
	private void toDotNode(GenotypeNode node) {
		System.out.println(node.getId() + " [label=\"" + this.nodeLabel(node) + "\"]");
		double norm = 0.;
		for(GenotypeNode child : node.getAdj() ){
			norm+=this.getWeight(node, child);
		}
		for(GenotypeNode child : node.getAdj() ){
			System.out.println(node.getId() + " -> " + child.getId() + " [label=\"" +  String.format("%.3f", (this.getWeight(node, child)/norm)/(1-(this.getWeight(node, node)/norm))) + "\"]");
		}
	}
	
	/**
	 * Prints a node referring to mutated genes with their respective HUGO symbols
	 * @param node  the node of which the genotype should be printed REQUIRE not null
	 * @return      the sequence of HUGO symbols of genes that are mutated in a given genotype
	 */
	public String nodeLabel(GenotypeNode node){
		ArrayList<String> res = new ArrayList<String>();
		for(int i = 0; i<node.getGenotype().length; i++){
			if(node.getGenotype()[i]){
				res.add(this.geneLabelsOrder[i]);
			}
		}
		if(res.size()>0){
			return res.toString();
		}else{
			return "clonal";
		}
	}
	

}
