package GenotypeGraphsv2;

import java.io.PrintStream;
import java.util.ArrayList;

import Datasets.Dataset;
import Graphs.Node;
import Utils.SquareMatrix;
import Utils.Utils;

/**
 * 
 * Implementation of the genotype graph allowing multiple mutations
 * and using a better design
 * 
 * @author rossi
 *
 */
public class GenotypeGraphAllowingMultipleMutations extends GenotypeGraph {
	
	ArrayList<Node<GenotypeInfo>> genotypes; 
	
	Node<GenotypeInfo> root;
	
	/**
	 * 
	 * @param dataset REQUIRE is compacted and its genotypes are sorted by number of mutations 
	 */
	public GenotypeGraphAllowingMultipleMutations(Dataset dataset) {
		super(dataset);
	}

	/**
	 * constructor with printing information
	 * @param dataset REQUIRE is compacted and its genotypes are sorted by number of mutations 
	 * @param printGenotypes
	 * @param printSamples
	 */
	public GenotypeGraphAllowingMultipleMutations(Dataset dataset, boolean printGenotypes, boolean printSamples) {
		super(dataset);
		/* set printing information */
		for(Node<GenotypeInfo> info : genotypes ){
			info.getContent().setPrintPreferences(printGenotypes, printSamples);
		}
	}

	@Override
	void addNodes(){
		genotypes = new ArrayList<Node<GenotypeInfo>>();
		/* add clonal genotype */
		ArrayList<String> clonal = new ArrayList<String>();
		clonal.add("clonal");
		genotypes.add(structure.add(new GenotypeInfo(new boolean[dataset.getNumberOfGenes()], dataset, 0., clonal)));
		root = genotypes.get(0);
		
		for(int i=0; i<this.dataset.getNumberOfDifferentGenotypes(); i++){
			GenotypeInfo gntInfo = new GenotypeInfo(this.dataset.get(i), this.dataset ,i, this.dataset.getSamples(i));
			genotypes.add(structure.add(gntInfo));
		}
	}

	@Override
	void addEdges() {
		/* link each node for which b is subset of a with maximum cardinality */
		for(int i = 0; i< genotypes.size(); i++){
			int minDist = Integer.MAX_VALUE;
			/* it uses the order of the genotypes to reduce computations */
			for(int j=i-1; j>=0; j--){
				if(!Utils.isSubseteq(genotypes.get(j).getContent().get(), genotypes.get(i).getContent().get())) continue;
				if(minDist < Utils.hammingDistance(genotypes.get(i).getContent().get(), genotypes.get(j).getContent().get())) break;
				/* link b to a*/
				this.structure.link(genotypes.get(j), genotypes.get(i));
				minDist =  Utils.hammingDistance(genotypes.get(i).getContent().get(), genotypes.get(j).getContent().get());	
			}	
		}
	}

	@Override
	void addWeights() {
		
		SquareMatrix<Double> upWeights = computeUpWeights();
		normalizeUp(upWeights);
		/* UNCOMMENT down here to print upWeights */
		//this.structure.setWeights(upWeights);
		//this.toDot(System.out);
		/* UNCOMMENT up here to print upWeights */
		SquareMatrix<Double> downWeights = computeDownWeights(upWeights);
		normalizeDown(downWeights);
		this.structure.setWeights(downWeights);
		
		
	}
	
	/**
	 * Compute upWeights using dynamic programming
	 * (step 1)
	 * @return the matrix of the upWeights
	 */
	private SquareMatrix<Double> computeUpWeights() {
		SquareMatrix<Double> upWeights = new SquareMatrix<Double>(0.);
		upWeights.enlarge(genotypes.size());
		
		int[] memoIn = new int[genotypes.size()];
		
		computeUpWeightsRec(root, memoIn, upWeights);
		
		return upWeights;
	}
	
	/**
	 * Recursion for upWeights computation
	 * @param position  node reached in the visit
	 * @param memoIn    incoming edges considered for each node
	 * @param upWeights table of the upWeights
	 */
	private void computeUpWeightsRec(Node<GenotypeInfo> position, int[] memoIn, SquareMatrix<Double> upWeights) {
		
		/* refer to upWeights formula */
		double weight = 0.;
		for(Node<GenotypeInfo> parent : structure.par(position) ){
			weight += upWeights.get(parent.getId(), position.getId());
		}
		weight += position.getContent().getObservedProbability();
		if(weight>0){
			weight /= structure.getNumberOfChildren(position);
		}else {
			weight = 0.; // epsilon
		}
		
		for(Node<GenotypeInfo> child : structure.adj(position) ){
			upWeights.set(position.getId(), child.getId(), weight);
			memoIn[child.getId()]++;
			if(memoIn[child.getId()] == structure.getNumberOfParents(child)){
				computeUpWeightsRec(child, memoIn, upWeights);
			}
		}
		
	}

	/**
	 * Normalization step for up weights:
	 * for each node normalize the weights of its incoming edges
	 * (step 2)
	 * @param upWeights normalized upWeigts table
	 */
	private void normalizeUp(SquareMatrix<Double> upWeights) {		
		for(Node<GenotypeInfo> node : genotypes){
			double norm = 0.;
			ArrayList<Node<GenotypeInfo>> par = structure.par(node);
			for(Node<GenotypeInfo> parent : par){
				norm += upWeights.get(parent.getId(), node.getId());
			}
			if(norm == 0.){ /* if it is connected to the root directly */
				upWeights.set(root.getId(), node.getId(), 1.);
			}else{
				for(Node<GenotypeInfo> parent : par){
					upWeights.set(parent.getId(), node.getId(), upWeights.get(parent.getId(), node.getId())/(double) norm);
				}
			}
		}	
	}
	
	/**
	 * Compute upWeights using dynamic programming and upWeights information
	 * (step 3)
	 * @param upWeights
	 * @return the matrix of the upWeights
	 */
	private SquareMatrix<Double> computeDownWeights(SquareMatrix<Double> upWeights) {
		SquareMatrix<Double> downWeights = new SquareMatrix<Double>(0.);
		downWeights.enlarge(genotypes.size());
		
		int[] memoOut = new int[genotypes.size()];
		
		for(Node<GenotypeInfo> node : genotypes){
			if(this.structure.getNumberOfChildren(node) == 0){
				computeDownWeightsRec(node, memoOut, downWeights, upWeights);
			}
		}
		
		return downWeights;
	}
	
	/**
	 * 
	 * Recursion for upWeights computation
	 * @param position  node reached in the visit
	 * @param memoOut   out-coming edges considered for each node
	 * @param downWeights table of the downWeights
	 * @param upWeights table of the upWeights
	 */
	private void computeDownWeightsRec(Node<GenotypeInfo> position, int[] memoOut, SquareMatrix<Double> downWeights, SquareMatrix<Double> upWeights) {
		/* refer to downWeights formula */
		double weight = 0.;
		for(Node<GenotypeInfo> child : structure.adj(position) ){
			weight += downWeights.get(position.getId(), child.getId());
		}
		weight += position.getContent().getObservedProbability();
		
		for(Node<GenotypeInfo> parent : structure.par(position) ){
			downWeights.set(parent.getId(), position.getId() , weight*upWeights.get(parent.getId(), position.getId()));
			memoOut[parent.getId()]++;
			if(memoOut[parent.getId()] == structure.getNumberOfChildren(parent)){
				computeDownWeightsRec(parent, memoOut, downWeights ,upWeights);
			}
		}
		
	}

	/**
	 * Normalization step for down weights:
	 * for each node normalize the weights of its out-coming edges
	 * (step 4, last)
	 * @param downWeights table of the downWeights
	 */
	private void normalizeDown(SquareMatrix<Double> downWeights) {
		for (Node<GenotypeInfo> node : genotypes) {
			double norm = 0.;
			ArrayList<Node<GenotypeInfo>> adj = structure.adj(node);
			for (Node<GenotypeInfo> child : adj) {
				norm += downWeights.get(node.getId(), child.getId());
			}
			for (Node<GenotypeInfo> child : adj) {
				downWeights.set(node.getId(), child.getId(),
						downWeights.get(node.getId(), child.getId()) / (double) norm);
			}
		}
	}

	/**
	 * Print probabilities in python script format for plotting
	 * @param out
	 */
	public void plotData(PrintStream out) {
		for(Node<GenotypeInfo> node : genotypes){
			out.println(node.getContent().translate() + " " + node.getContent().getObservedProbability());
		}
	}
	


}
