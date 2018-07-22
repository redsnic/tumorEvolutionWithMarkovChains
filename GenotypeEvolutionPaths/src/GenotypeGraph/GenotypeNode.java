package GenotypeGraph;

import Utils.Utils;

/**
 * A base class for a single node from a genotype graph 
 * (based on adjacency matrix)
 * @author rossi
 * 
 * INVARIANT: numberOfMutations = #count of true in genotype
 * 			  probability, steadyStateProbability in [0,1]
 */
public class GenotypeNode implements Comparable<GenotypeNode> {
	/**
	 * the state of mutations relative of this node
	 */
	private boolean[] genotype = null;
	/**
	 * count of mutated genes in the genotype associated with this node 
	 */
	private long numberOfMutations = 0;
	/**
	 * unique id to be used for next node
	 */
	int id;
	/**
	 * Observed probability extracted from dataset
	 */
	double probability = 0.;
	/**
	 * Probability at the 'steady state' of this node
	 */
	double steadyStateProbability = 0;
	
	/**
	 * Getter for the genotype of this node
	 * @return the genotype associated with this node
	 */
	public boolean[] getGenotype(){
		return this.genotype;
	}
	
	/**
	 * Default constructor, creates a node labeled with a genotype
	 * @param genotype
	 * @param id REQUIRE must be unique in the context of usage
	 */
	public GenotypeNode(boolean[] genotype, int id){
		this.genotype = genotype;
		this.numberOfMutations = Utils.sumBool(genotype);
		this.id = id;
	}
	
	/**
	 * @return the number of mutations that are present
	 *  in the genotype associated with this node
	 */
	public long getNumberOfMutations(){
		return this.numberOfMutations;
	}
	
	/**
	 * @return the number of genes considered 
	 * in the genotype associated
	 * with this node 
	 */
	public long genotypeLength(){
		return this.genotype.length;
	}
	
	/**
	 * @return the unique id of this node
	 */
	public int getId(){
		return this.id;
	}

	/**
	 * sets this id to this node
	 * (do not create clashes)
	 * @param id
	 */
	public void setId(int id){
		this.id = id;
	}

	@Override
	/**
	 * Comparison based on number of mutations and 
	 * on the binary digit they represent (if the number of mutations is equal) 
	 */
	public int compareTo(GenotypeNode other) {
		if (other.getNumberOfMutations() < this.getNumberOfMutations()){
			return 1;
		} else if (other.getNumberOfMutations() > this.getNumberOfMutations()) {
			return -1;
		} else {
			return compareGenotype(other.genotype);
		}
	}
	
	/**
	 * Comparison of two genotypes A and B. (lexical order)
	 * A is considered bigger than B if the binary number it
	 * represents is bigger than that of B 
	 * @param other B's genotype
	 * @return 0 if A=B, 1 if A>B, -1 else
	 */
	private int compareGenotype(boolean[] other){
		for(int i=0; i<other.length; i++){
			if(other[i] && !this.genotype[i]){
				return -1;
			} else if(!other[i] && this.genotype[i]){
				return 1;
			}
		}
		return 0;
	}
	
	/**
	 * Checks if this node has a genotype containing one mutation more
	 * than the other node
	 * @param   other 
	 * @return  true if this node has a genotype containing one mutation more than the other node, false otherwise 
	 */
	public boolean containsAndOnlyOneMore(GenotypeNode other){
		if(this.numberOfMutations != other.getNumberOfMutations()+1){
			return false;
		}
		boolean firstMismatchFound = false;
		for (int i = 0; i < this.genotype.length; i++) {
			if(this.genotype[i] && !other.genotype[i] && !firstMismatchFound){
				firstMismatchFound = true;
			} else if (this.genotype[i] && !other.genotype[i] && firstMismatchFound){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if this node has a genotype containing more mutations
	 * than the other node 
	 * (there are no genes that are mutated in other but not in this)
	 * @param   other 
	 * @return  true if this node has a genotype containing more mutations than the other node, false otherwise 
	 */
	public boolean contains(GenotypeNode other){
		for (int i = 0; i < this.genotype.length; i++) {
			if(!this.genotype[i] && other.genotype[i]){
				return false;
			}
		}
		return true;
	}
	
	
}
