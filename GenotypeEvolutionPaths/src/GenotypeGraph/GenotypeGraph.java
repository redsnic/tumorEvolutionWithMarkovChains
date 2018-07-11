package GenotypeGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import Utils.Triplet;
import Utils.Utils;

/**
 * Base class to represent directed graphs about genotypes
 * It also implements a basic dot format output 
 * The implementation is with adjacency lists
 * @author rossi
 */
public class GenotypeGraph {

	String[] geneLabelsOrder;
	ArrayList<GenotypeNode> nodes = new ArrayList<GenotypeNode>();
	int id = 0; // unique id for nodes
	private int threshold = 0; /* 0 = no threshold on the number of genes */
	
	/**
	 * default constructor, creates a graph consisting
	 * of many genotype nodes
	 * @param labels    list associating each gene's HUGO symbol with a position 
	 * @param genotypes 
	 * note that a single genotype is a sequence of boolean
	 * stating if a gene was or wasn't mutated (in the order defined by labels)  
	 */
	public GenotypeGraph(String[] labels, ArrayList<boolean[]> genotypes){
		this.geneLabelsOrder = labels.clone();
		for(boolean[] g : genotypes){
			nodes.add(new GenotypeNode(g, this.id));
			this.id++;
		}
		prepareEdges();
	}
	
	/**
	 * Alternate constructor reading input from STDIN
	 */
	public GenotypeGraph(){
		this.readInput();
		prepareEdges();
	}
	
	/**
	 * Alternate constructor reading input from STDIN and 
	 * setting a threshold on the maximum number of genes
	 * @param thres number of genes to keep for the analysis 
	 * (it selects the most mutated genes)
	 */
	public GenotypeGraph(int thres){
		this.threshold  = thres;
		this.readInput();
		prepareEdges();
	}
	
	/**
	 * reads an input file in "BML" format from STDIN:
	 * #samples	#genes '\n'
	 * spaced list of all HUGO symbols for each gene '\n'
	 * sampleID and relative boolean array of mutation presence '\n' (repeated #samples times) 
	 */
	private void readInput() {
		Scanner in = new Scanner(System.in);
		int nSamples = in.nextInt();
		int nGenes = in.nextInt();
		
		this.geneLabelsOrder = new String[nGenes];
		for(int i=0; i<nGenes; i++){      // gene list
			this.geneLabelsOrder[i] = in.next();
		}
		ArrayList<boolean[]> tempSamples = new ArrayList<boolean[]>();  
		for(int i=0; i<nSamples; i++){    // sample list
			boolean[] temp = new boolean[nGenes];
			in.next(); // ignore sample label
			for(int j=0; j<nGenes; j++){ // mutations in sample 
				temp[j] = in.nextInt()==1?true:false;
			}
			tempSamples.add(temp);
		}
		in.close();
		ArrayList<Integer> geneMutations = computeGeneMutations(tempSamples);
		boolean[] toBeKept = computeElimiations(geneMutations);
		reduceGenes(toBeKept, tempSamples);
	}

	/**
	 * Applies the threshold set by the user to reduce the dataset
	 * @param toBeKept      list of genes to keep an to eliminate (in the first GeneLabelsOrder order)
	 * @param tempSamples   original unreduced samples
	 */
	private void reduceGenes(boolean[] toBeKept, ArrayList<boolean[]> tempSamples) {
		String[] newOrder = new String[(int) Utils.sumBool(toBeKept)];
		boolean[][] oldSamples = toBoolMatrix(tempSamples);
		boolean[][] newSamples = new boolean[oldSamples.length][(int) Utils.sumBool(toBeKept)];
		int j=0;
		for(int i=0; i<toBeKept.length; i++){
			if(toBeKept[i]){
				newOrder[j] = this.geneLabelsOrder[i];
				for(int k = 0; k<oldSamples.length; k++){
					newSamples[k][j] = oldSamples[k][i];
				}
				j++;
			}
		}
		/* update data set */
		this.geneLabelsOrder = newOrder;
		for(boolean[] g : newSamples){
			if(Utils.sumBool(g) > 0){ // exclude tumoral genotypes without observed mutations
				nodes.add(new GenotypeNode(g, this.id));
				this.id++;
			}
		}
		
	}

	/**
	 * Casts an ArrayList<boolean[]> to a boolean[][] 
	 * @param  list (of arrays)
	 * @return a matrix
	 */
	private boolean[][] toBoolMatrix(ArrayList<boolean[]> list) {
		boolean[][] matrix = new boolean[list.size()][list.get(0).length];
		for(int i=0; i<matrix.length ;i++){
			for(int j=0; j<matrix[i].length; j++){
				matrix[i][j] = list.get(i)[j];
			}
		}
		return matrix;
	}

	/**
	 * Finds the genes that should be considered according to the threshold set by the user
	 * @param geneMutations for each gene (in the GeneLabelsOrder order) how many times it is found as mutated in the data set
	 * @return              list of genes to keep an to eliminate (in the GeneLabelsOrder order)
	 */
	private boolean[] computeElimiations(ArrayList<Integer> geneMutations) {
		ArrayList<Triplet<String,Integer,Integer>> geneInformation = new ArrayList<Triplet<String,Integer,Integer>>();
		for(int i = 0; i<this.geneLabelsOrder.length; i++){
			/* (HUGO symbol, #mutations, original position */ 
			Triplet<String,Integer,Integer> temp = new Triplet<String,Integer,Integer>(this.geneLabelsOrder[i], geneMutations.get(i), i);
			geneInformation.add(temp);
		}
		Collections.sort(geneInformation, (a,b) -> a.snd()>b.snd()?1:(a.snd()==b.snd()?0:-1)); /* sort on second element */
		int thres = this.threshold!=0?this.threshold:geneInformation.size();                   /* 0 means no threshold */
		boolean res[] = new boolean[geneInformation.size()];
		int lastNumberOfMutations = 0;
		int i=0;
		for(i=0; i<thres && i<geneInformation.size() ; i++){ /* keep the thres most mutated genes */
			res[geneInformation.get(i).thr()]=true;
			lastNumberOfMutations = geneInformation.get(i).snd();
		}
		/* consider also every gene with the same number of mutations of the least mutated yet considered gene */
		for(; i<geneInformation.size() && lastNumberOfMutations==geneInformation.get(i).snd() ; i++){ 
			res[geneInformation.get(i).thr()]=true;
		}
		
		return res;
	}
	
	/**
	 * Computes the array of the counts for each mutation
	 * (i.e. how many times every mutation was seen in any sample)
	 * @param  tempSamples input dataset
	 * @return array of the counts for each mutation
	 */
	private ArrayList<Integer> computeGeneMutations(ArrayList<boolean[]> tempSamples) {
		int[] sums = new int[tempSamples.get(0).length];
		for(boolean[] v : tempSamples){
			Utils.addBoolVec(sums, v);
		}
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for(int k : sums){
			ret.add(k);
		}
		return ret; 
	}

	/**
	 * prepares the edges of the graph
	 */
	protected void prepareEdges(){
		/* do nothing */
	}
	
	/**
	 * @return the number of nodes of the graph
	 */
	public long size(){
		return nodes.size();
	}
	
	/**
	 * print this graph in dot format
	 */
	public void toDot(){
		System.out.println("digraph G{");
		for(GenotypeNode n : nodes){
			this.toDotNode(n);
		}
		System.out.println("}");
	}
	
	/**
	 * prints a node in dot format
	 * @param node the node to be printed REQUIRE not null
	 */
	public void toDotNode(GenotypeNode node){
		System.out.println(node.getId() + " [label=\"" + this.nodeLabel(node) + "\"]");
		for(GenotypeNode child : node.adj ){
			System.out.println(node.getId() + " -> " + child.getId());
		}
	}
	
	/**
	 * @param node  the node of which the genotype should be printed REQUIRE not null
	 * @return      the sequence of HUGO symbols of genes that are mutated in a given genotype
	 */
	public String nodeLabel(GenotypeNode node){
		ArrayList<String> res = new ArrayList<String>();
		for(int i = 0; i<node.genotype.length; i++){
			if(node.genotype[i]){
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
	 * updates the node set to a new one
	 * (also dot ids are recomputed)
	 * @param newNodes new node set 
	 */
	protected void setNodes(ArrayList<GenotypeNode> newNodes){
		this.nodes = newNodes;
		for(int i = 0; i<this.nodes.size() ;i++){
			this.nodes.get(i).setId(i);
		}
	}
	
	
}
