package DatasetGenerators;

import java.util.ArrayList;

import GenotypeGraph.GenotypeNode;
import Utils.SquareMatrix;
import Utils.Utils;

/**
 * Class for creating a generic weighted graph of genotypes
 * from which to generate data sets to be used for 
 * the validation of tumor evolution inference methods
 * NOTE: might be interesting to use this as input for other tools
 * TODO add output on STDOUT 
 * @author redsnic
 */
public class GraphDatasetGenerator {
	
	/**
	 * table to avoid multiple number of parents computations
	 */
	int[] nParents;
	/**
	 * table to avoid multiple number of parents computations
	 */
	int[] nChildren;
	/**
	 * nodes of the graph
	 */
	ArrayList<GenotypeNode> V = new ArrayList<GenotypeNode>();
	/**
	 * Weights are managed in a matrix
	 */
	SquareMatrix<Double> W = new SquareMatrix<Double>(0.);
	/**
	 * adjacency matrix
	 */
	SquareMatrix<Boolean> E = new SquareMatrix<Boolean>(false);
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
	 * Populates the lookup tables for the number
	 * of parents and children
	 * MODIFY: nParents, nChildren
	 */
	protected void computeSumTables() {
		nParents = new int[E.getSize()];
		nChildren = new int[E.getSize()];
		for(int i=0; i<E.getSize(); i++){
			for(int j=0; j<E.getSize(); j++){
				int found = E.get(i,j)?1:0;
				nParents[j] += found;
				nChildren[i] += found;
			}
		}
	}
	
	/**
	 * Default constructor (empty graph, manual construction)
	 * @param labels "synthetic genes" names
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
		this.setRoot(this.add(root));
		
		for(long i=1 ; i<Math.pow(2, this.geneLabelsOrder.length); i++){
			boolean[] genotype = Utils.binaryToBoolArray(i, geneLabelsOrder.length);
			int totals = Utils.sumBool(genotype);
			double selector = Utils.random();
			
			if(selector < 1/(double)totals){
				this.add(genotype);
			}
		}
		  
		prepareLinks();
		
		computeSumTables();
	}
	
	/**
	 * Adds edges to dataset generator graph
	 */
	protected void prepareLinks() {
		
		prepareSelfLoops();
		
		for(GenotypeNode a : V){
			/* link all other nodes with an additional mutation */
			for(GenotypeNode b : V){
				if(Utils.sumBool(a.getGenotype())+1==Utils.sumBool(b.getGenotype()) && Utils.singleMismatch(a.getGenotype(), b.getGenotype())){
					link(a,b,Utils.random());
				}
			}
		}
		
		computeSumTables();
		
		/* link orphans */
		for(GenotypeNode a : V){
			if(nParents[a.getId()]==1 && a != this.root){
				link(this.root,a, Utils.random());
			}
		}
	}
	

	/**
	 * Adds self loops with random probability
	 */
	protected void prepareSelfLoops() {
		for(GenotypeNode node : V){
			link(node,node,Utils.random());
		}
	}

	/** 
	 * Adds a node to the graph,
	 * it also manages the modification of the adjacency matrix
	 * @param n node to be added
	 */
	public GenotypeNode add(boolean[] n){
		GenotypeNode newNode = new GenotypeNode(n, this.id);
		V.add(newNode);
		this.id++;
		E.enlarge();
		W.enlarge();
		return newNode;
	}
	
	/**
	 * Adds an edge from a to b
	 * @param a source
	 * @param b destination
	 * @param weight of the edge
	 */
	public void link(GenotypeNode a, GenotypeNode b, double weight){
		E.set(a.getId(), b.getId(), true);
		W.set(a.getId(), b.getId(), weight);
	}
	
	/**
	 * @param a a node
	 * @return the sum of the weights of the outcoming edges of node a 
	 */
	private double totalWeight(GenotypeNode a){
		return Utils.sumDoubleRow(W, a.getId());
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
			double chooseRoute = Utils.random()*totalWeight(position);
			for(int j=0; j<E.getSize(); j++){
				if(!E.get(position.getId(), j)) continue;
				if(chooseRoute < W.get(position.getId(), j)){
					position = V.get(j);
					break;
				} else {
					chooseRoute-=W.get(position.getId(), j);
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
		for(int j=0; j<E.getSize(); j++){
			if(!E.get(node.getId(), j)) continue;
			norm+=this.W.get(node.getId(), j);
		}
		for(int j=0; j<E.getSize(); j++){
			if(!E.get(node.getId(), j)) continue;
			if(node.getId() != j){
				System.out.println(node.getId() + " -> " + j + " [label=\"" +  String.format("%.3f", (this.W.get(node.getId(), j)/norm)/(1-(this.W.get(node.getId(), node.getId())/norm))) + "\"]");
			} else {
				System.out.println(node.getId() + " -> " + j + " [label=\"" +  String.format("%.3f", (this.W.get(node.getId(), j)/norm)) + "\"]");
			}
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
	

	/**
	 * Prints a data set on STDOUT generated by this generator using BML format
	 * @param numberOfSamples   Number of samples to be generated
	 * @param timeIntervals     Maximum length of the path in simulation
	 */
	public void printDatasetBMLFormat(int numberOfSamples, int timeIntervals){
		int numberOfGenes = this.geneLabelsOrder.length;
		
		System.out.println(numberOfSamples + " " + numberOfGenes); // header
		
		for(String gene : this.geneLabelsOrder ){                  // genes
			System.out.print(gene + " ");
		}
		System.out.println();
		
		for(int i = 0; i<numberOfSamples; i++){                    // genotypes
			System.out.print("Sample#" + i + " ");
			boolean[] genotype = this.simulate(timeIntervals);
			for(boolean status : genotype){
				System.out.print((status?1:0) + " ");
			}
			System.out.println();
		}
		
		
	}
	
	/**
	 * Prints a data set on STDOUT generated by this generator using TRONCO format
	 * @param numberOfSamples   Number of samples to be generated
	 * @param timeIntervals     Maximum length of the path in simulation
	 */
	public void printDatasetTRONCOFormat(int numberOfSamples, int timeIntervals){
		
		System.out.print("samples\\genes "); // header
		
		for(String gene : this.geneLabelsOrder ){                  // genes
			System.out.print(gene + " ");
		}
		System.out.println();
		
		for(int i = 0; i<numberOfSamples; i++){                    // genotypes
			System.out.print("Sample_" + i + " ");
			boolean[] genotype = this.simulate(timeIntervals);
			for(boolean status : genotype){
				System.out.print((status?1:0) + " ");
			}
			System.out.println();
		}
		
		
	}
	

	

}
