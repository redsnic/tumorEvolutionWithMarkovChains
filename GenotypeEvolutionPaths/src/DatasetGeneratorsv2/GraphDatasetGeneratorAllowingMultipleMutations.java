package DatasetGeneratorsv2;

import java.util.ArrayList;
import java.util.List;

import Datasets.Dataset;
import Graphs.DirectedWeightedGraphMatrix;
import Graphs.Node;
import Utils.Utils;

public class GraphDatasetGeneratorAllowingMultipleMutations {
	
	DirectedWeightedGraphMatrix<GenotypeGen> structure = new DirectedWeightedGraphMatrix<GenotypeGen>();
	String[] labels;
	Node<GenotypeGen> root;
	
	/**
	 * Manual constructor
	 * @param labels of genes to be used
	 */
	public GraphDatasetGeneratorAllowingMultipleMutations(String[] labels){
		this.labels = labels;
		this.root = structure.add(new GenotypeGen(new boolean[labels.length]));
	}
	
	/**
	 * Automatic constructor
	 * @param numberOfGenes to generate genotypes with
	 */
	public GraphDatasetGeneratorAllowingMultipleMutations(int numberOfGenes){
		assert(numberOfGenes<20): "too many genes for random generation"; 
		this.labels = new String[numberOfGenes];
		for(int i = 0; i< numberOfGenes ; i++){
			this.labels[i] = "G" + i;
		}
		this.root = structure.add(new GenotypeGen(new boolean[labels.length]));
		this.randomGenerate();
	}
	
	/**
	 * Generate randomly a graph of genotypes where the probability of 
	 * including a genotype is 1/nOfMutations(genotype)
	 */
	private void randomGenerate(){
		ArrayList<Node<GenotypeGen>> nodes = new ArrayList<Node<GenotypeGen>>();
		nodes.add(this.root);
		for(int i = 1; i<Math.pow(2, this.labels.length); i++ ){
			double choose = Utils.random();
			boolean[] arr = Utils.binaryToBoolArray(i, this.labels.length);
			if(choose < 1./Utils.sumBool(arr)){
				nodes.add(structure.add(new GenotypeGen(arr)));
			}
		}
		linkAndWeightNodes(nodes);
		structure.toDot();
	}
	
	/**
	 * Adds links accordingly to the rules (maximum subsets) and 
	 * edges with random weight 
	 * @param nodes
	 */
	private void linkAndWeightNodes(ArrayList<Node<GenotypeGen>> nodes) {
		
		for(Node<GenotypeGen> a : nodes){
			ArrayList<Node<GenotypeGen>> toLink = new ArrayList<Node<GenotypeGen>>();
			int minDist = Integer.MAX_VALUE;
			for(Node<GenotypeGen> b : nodes){
				if(a==b) continue;
				if(Utils.isSubseteq(a.getContent().get(), b.getContent().get()) && minDist>=Utils.hammingDistance(a.getContent().get(), b.getContent().get())){
					if(minDist==Utils.hammingDistance(a.getContent().get(), b.getContent().get())){
						toLink.add(b);
					}else{
						toLink = new ArrayList<Node<GenotypeGen>>();
						toLink.add(b);
						minDist = Utils.hammingDistance(a.getContent().get(), b.getContent().get());
					}
				}
			}
			this.link(a, a, Utils.random());
			for(Node<GenotypeGen> b : toLink){
				this.link(a, b, Utils.random());
			}
			
		}
	}

	/**
	 * Adds a node to the generator graph
	 * @param gnt  genotype to be added
	 * @return     the newly created node
	 */
	public Node<GenotypeGen> add(boolean[] gnt){
		assert(Utils.sumBool(gnt)!=0): "do not manually insert clonal genotype (use getRoot())";
		return structure.add(new GenotypeGen(gnt));
	}
	
	/**
	 * links a and b with weight 'weight'
	 * @param a
	 * @param b
	 * @param weight
	 */
	public void link(Node<GenotypeGen> a, Node<GenotypeGen> b, double weight){
		structure.link(a, b, weight);
	}
	
	/**
	 * @return the clonal genotype of this
	 */
	public Node<GenotypeGen> getRoot(){
		return this.root;
	}
	
	/**
	 * Generate a dataset of numberOfSamples samples 
	 * obtained with a random walk on the generator graph of pathLength steps
	 * @param numberOfSamples   
	 * @param pathLength      
	 * @return the generated dataset
	 */
	public Dataset generate(int numberOfSamples, int pathLength){
		boolean[][] data = new boolean[numberOfSamples][];
		for(int i = 0; i<numberOfSamples ; i++){
			data[i] = this.simulate(pathLength, this.root);
		}
		Dataset newDataset = new Dataset();
		newDataset.read(this.labels, data);
		newDataset.compact();
		return newDataset;
	}

	/**
	 * random walk on generator graph of length 'pathLength' from node 'position' 
	 * @param pathLength  
	 * @param position 
	 * @return reached genotype
	 */
	private boolean[] simulate(int pathLength, Node<GenotypeGen> position) {
		if(pathLength == 0 || structure.getNumberOfChildren(position) == 0) return position.getContent().get();
		List<Node<GenotypeGen>> adj = structure.getAdjacencyList(position);
		
		/* normalize out-coming edges to get transition probabilities */
		double norm = 0.;
		for(int i=0; i<adj.size(); i++){
			norm += structure.getWeight(position, adj.get(i));
		}
		double choose = Utils.random();
		
		int i = 0;
		while(choose > structure.getWeight(position, adj.get(i))/norm){
			choose -= structure.getWeight(position, adj.get(i))/norm;
			i++;
		}
		
		return simulate(pathLength-1, adj.get(i));
		
	}
	
	/**
	 * Prints this graph in dot format
	 */
	public void toDot(){
		this.structure.toDot();
	}
	
	

}
