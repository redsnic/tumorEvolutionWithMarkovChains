package GenotypeGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import Utils.Triplet;
import Utils.Utils;

/**
 * Basic class to represent directed graphs about genotypes.
 * 
 * It also provides a basic dot format output 
 * The implementation is with adjacency matrix
 * @author rossi
 */
public class GenotypeGraph {

	/**
	 * Labels (possibly HUGO symbols) associated with genes considered in the analisys
	 */
	String[] geneLabelsOrder;
	/**
	 * nodes of this graph
	 */
	ArrayList<GenotypeNode> V = new ArrayList<GenotypeNode>();
	/**
	 * edges of this graph
	 */
	SquareMatrix<Boolean> E = new SquareMatrix<Boolean>(false);
	/**
	 * identifier used to give each node a unique number 
	 */
	protected int id = 0; // unique id for nodes
	/**
	 * limit on the number of genes to be considered
	 */
	private int threshold = 0; /* 0 = no threshold on the number of genes */
	
	/**
	 * @return the labels associated to genes 
	 */
	public String[] getLabels(){
		return this.geneLabelsOrder;
	}
	
	/**
	 * default constructor, creates a graph consisting
	 * of many genotype nodes
	 * @param labels    list associating each gene's HUGO symbol with a position 
	 * @param genotypes data set for the generation
	 * note that a single genotype is a sequence of boolean
	 * stating if a gene was or wasn't mutated (in the order defined by labels)  
	 */
	public GenotypeGraph(String[] labels, ArrayList<boolean[]> genotypes){
		this.geneLabelsOrder = labels.clone();
		ArrayList<Integer> geneMutations = computeGeneMutations(genotypes);
		boolean[] toBeKept = computeElimiations(geneMutations);
		reduceGenes(toBeKept, genotypes);
		prepareEdges();
	}
	
	/**
	 * Alternate constructor also setting a threshold on the maximum number of genes
	 * @param labels
	 * @param genotypes
	 */
	public GenotypeGraph(String[] labels, ArrayList<boolean[]> genotypes, int thres){
		this.threshold = thres;
		this.geneLabelsOrder = labels.clone();
		ArrayList<Integer> geneMutations = computeGeneMutations(genotypes);
		boolean[] toBeKept = computeElimiations(geneMutations);
		reduceGenes(toBeKept, genotypes);
		prepareEdges();
	}
	
	/**
	 * Alternate constructor reading input from STDIN
	 * TODO add file support
	 */
	public GenotypeGraph(){
		this.readInput();
		prepareEdges();
	}
	
	/**
	 * Alternate constructor reading input from STDIN and 
	 * setting a threshold on the maximum number of genes to be considered
	 * @param thres number of genes to keep for the analysis (n)
	 * (it selects the most mutated genes, in case of parity this number is extended to 
	 *  the first gene with a different number of mutation from the nth in the order)
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
		boolean[][] oldSamples = Utils.toBoolMatrix(tempSamples);
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
				V.add(new GenotypeNode(g, this.id));
				this.id++;
			}
		}
	}
	
	/**
	 * Initialize adjacency Matrix (note, use after genotype compression!)
	 */
	void setupAdjMatrix(){
		for(int i=0; i<V.size(); i++){
			E.enlarge();
		}
	}

	/**
	 * Finds the genes that should be considered according to the threshold set by the user
	 * @param geneMutations for each gene (in the GeneLabelsOrder order) how many times it is found as mutated in the data set
	 * @return              list of genes to keep an to eliminate (in the GeneLabelsOrder order)
	 */
	private boolean[] computeElimiations(ArrayList<Integer> geneMutations) {
		ArrayList<Triplet<String,Integer,Integer>> geneInformation = new ArrayList<Triplet<String,Integer,Integer>>();
		for(int i = 0; i<geneMutations.size(); i++){
			/* (HUGO symbol, #mutations, original position) */ 
			Triplet<String,Integer,Integer> temp = new Triplet<String,Integer,Integer>(this.geneLabelsOrder[i], geneMutations.get(i), i);
			geneInformation.add(temp);
		}
		Collections.sort(geneInformation, (a,b) -> a.snd()>b.snd()?-1:(a.snd()==b.snd()?0:1)); /* sort genes on second element (desc) */
		int thres = this.threshold!=0?this.threshold:geneInformation.size();                   /* 0 means no threshold */
		boolean res[] = new boolean[geneInformation.size()];
		int lastNumberOfMutations = -1;
		int i=0;
		for(; i<thres && i<geneInformation.size() ; i++){                                      /* keep the thres most mutated genes */
			if(geneInformation.get(i).snd()>0){                                /* exclude genes never seen as mutated in the dataset */
				res[geneInformation.get(i).thr()]=true;
				lastNumberOfMutations = geneInformation.get(i).snd();
			}
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
	 * @param  tempSamples input data set
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
		/*do nothing meant to be used by classes extending this*/
	}
	
	/**
	 * @return the number of nodes of the graph
	 */
	public long size(){
		return V.size();
	}
	
	/**
	 * print this graph in dot format
	 */
	public void toDot(){
		System.out.println("digraph G{");
		for(GenotypeNode n : V){
			this.toDotNode(n);
		}
		this.toDotEdges();
		System.out.println("}");
	}
	
	/**
	 * Prints all the edges of this graph in dot format
	 */
	void toDotEdges() {
		for(int i=0; i<E.getSize(); i++){
			for(int j=0; j<E.getSize(); j++){
				if(E.get(i, j)){
					System.out.println(i + " -> " + j);
				}
			}
		}
	}

	/**
	 * prints a node in dot format
	 * @param node the node to be printed REQUIRE not null
	 */
	public void toDotNode(GenotypeNode node){
		System.out.println(node.getId() + " [label=\"" + this.nodeLabel(node) + "\"]");
	}
	
	/**
	 * Prints a node referring to mutated genes with their respective labels
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
