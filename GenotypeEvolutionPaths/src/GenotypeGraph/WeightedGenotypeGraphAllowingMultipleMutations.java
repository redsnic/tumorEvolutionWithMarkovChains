package GenotypeGraph;

import java.util.ArrayList;

import Utils.Utils;

/**
 * Extension of WeightedGenotypeGraph that allows the reconstruction of genotype graphs 
 * with the acquisition of multiple mutation in a single transition when the are not better 
 * explanations. 
 * @author rossi
 */
public class WeightedGenotypeGraphAllowingMultipleMutations extends WeightedGenotypeGraph {

	/**
	 * Default constructor
	 * @param labels     HUGO symbols for genes
	 * @param genotypes  
	 * note that a single genotype is a sequence of boolean
	 * stating if a gene was or wasn't mutated (in the order defined by labels)  
	 */
	public WeightedGenotypeGraphAllowingMultipleMutations(String[] labels, ArrayList<boolean[]> genotypes) {
		super(labels, genotypes); /* prepare graph */
	}
	
	/**
	 * Alternate constructor with a limitation on the number of gene usable in the analysis
	 * @param labels     HUGO symbols for genes
	 * @param genotypes  dataset
	 * @param thres      limit to the number of gene usable in the analysis
	 */
	public WeightedGenotypeGraphAllowingMultipleMutations(String[] labels, ArrayList<boolean[]> genotypes, int thres) {
		super(labels, genotypes, thres);    /* prepare graph */
	}
	
	/**
	 * Stdin constructor
	 */
	public WeightedGenotypeGraphAllowingMultipleMutations(){
		super();
	}
	
	/**
	 * Stdin constructor with a limitation on the number of gene usable in the analysis
	 * @param thres      limit to the number of gene usable in the analysis
	 */
	public WeightedGenotypeGraphAllowingMultipleMutations(int thres){
		super(thres);
	}
	
	
	/* note: elements of V are sorted by number of mutations */
	/**
	 * links each node with all the nodes with less mutations (and with no different additional mutation) and minimal Hamming distance to this node
	 */
	@Override
	protected void linkNodes(){
		
		for(int i = this.V.size()-1; i>=0 ; i--){
			int distance = Integer.MAX_VALUE;
			
			ArrayList<Integer> parents = new ArrayList<Integer>();
			
			for (int j = i-1; j>=0 ; j--) {
				if(V.get(i).getNumberOfMutations() == V.get(j).getNumberOfMutations()) continue;
				if(V.get(i).getNumberOfMutations() - V.get(j).getNumberOfMutations() > distance) break;
				if(!V.get(i).contains(V.get(j))) continue;
				
				int distij = Utils.hammingDistance(V.get(i).getGenotype(),V.get(j).getGenotype());
				
				if( distij<distance){
					distance=distij;
					parents = new ArrayList<Integer>();
					parents.add(j);
				}
				if( distij==distance ){
					parents.add(j);
				}
				
			}
			
			for(int par : parents){
				E.set(par, i, true);
			}
			
		}
		
	}
	
}
