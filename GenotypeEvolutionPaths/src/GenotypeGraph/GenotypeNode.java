package GenotypeGraph;

import java.util.ArrayList;

/**
 * A base class for a single node from a genotype graph
 * @author rossi
 * 
 * INVARIANT: numberOfMutations = #count of true in genotype
 * 			  probability, emissionProbability , steadyStateProbability in [0,1]
 */
public class GenotypeNode implements Comparable<GenotypeNode> {
	/**
	 * the state of mutations relative of this node
	 */
	boolean[] genotype = null;
	/**
	 * count of mutated genes in the genotype associated with this node 
	 */
	long numberOfMutations = 0;
	/**
	 * Adjacency list of this node
	 */
	ArrayList<GenotypeNode> adj = new ArrayList<GenotypeNode>();
	/**
	 * parents of this node (nodes that have and edge to this node)
	 */
	ArrayList<GenotypeNode> parents = new ArrayList<GenotypeNode>();
	/**
	 * unique id to be used for next node
	 */
	int id;
	/**
	 * Observed probability extracted from dataset
	 */
	double probability = 0.;

	/**
	 * Probability at the steady state of this node
	 */
	double steadyStateProbability = 0;

	//TODO 
	double emissionProbability = 0; 
	int passFrequency = 0;
	
	/**
	 * Getter for the adj list of this node
	 * @return the adj list
	 */
	public ArrayList<GenotypeNode> getAdj(){
		return this.adj;
	}
	
	/**
	 * Getter for the genotype of this node
	 * @return the genotype associated with this node
	 */
	public boolean[] getGenotype(){
		return this.genotype;
	}
	
	/**
	 * updates the Id of this node to a new one
	 * @param id
	 */
	public void setId(int id){
		this.id = id;
	}
	
	/**
	 * Default constructor, creates a node labeled with a genotype
	 * and with an empty 
	 * @param genotype
	 * @param id REQUIRE must be unique 
	 */
	public GenotypeNode(boolean[] genotype, int id){
		this.genotype = genotype;
		this.numberOfMutations = countMutations();
		this.id = id;
		assert (this.adj.size() == 0);
	}
	
	/**
	 * Default constructor, creates a node labeled with a genotype
	 * and with an empty 
	 * @param genotype
	 * REQUIRE id must be set to be unique afterwards 
	 */
	public GenotypeNode(boolean[] genotype){
		this.genotype = genotype;
		this.numberOfMutations = countMutations();
		this.id = -1;
		assert (this.adj.size() == 0);
	}
	
	/**
	 * Counts the number of mutations that are present in the genotype
	 * @return the number of mutations
	 */
	private long countMutations(){
		long count = 0;
		for(boolean mut : this.genotype){  /* count number of mutations */
			if(mut){
				count++;
			}
		}
		return count;
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
	 * Adds a node to the adjacency list of this node
	 * NOTE: there is no control on the meaning of the values of the genotype (eg. to which gene they are associated with)
	 * @param other a node REQUIRE not null, with compatible genotype
	 */
	public void add(GenotypeNode other){
		assert (other != null);
		assert (other.genotypeLength() == this.genotypeLength());  
		this.adj.add(other);
		other.addParent(this);
	}
	
	/**
	 * Adds a node to the parent list of this node
	 * NOTE: there is no control on the meaning of the values of the genotype (eg. to which gene they are associated with)
	 * @param other a node REQUIRE not null, with compatible genotype
	 */
	protected void addParent(GenotypeNode other){
		assert (other != null);
		assert (other.genotypeLength() == this.genotypeLength());  
		this.parents.add(other);
	}
	
	/**
	 * @return the unique id of this node
	 */
	public long getId(){
		return this.id;
	}
	
	/**
	 * @return number of nodes that are reachable from this node by passing a single edge
	 */
	public long getNumberOfChildren(){
		return this.adj.size();
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
	 * Comparison of two genotypes A and B.
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
	public boolean contains(GenotypeNode other){
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
	 * Linear search among parents to find which one has parId id 
	 * @param  parId id to find
	 * @return position of parent in the adj list of this (-1 if not found)
	 */
	public int findParentFromId(int parId) {
		for( int i = 0; i<this.parents.size() ;i++){
			if(this.parents.get(i).id==parId) return i;
		}
		assert(false): "Parent with id " + parId + " not found in node " + this.id;
		return -1;
	}

	public int findChildFromId(int childID) {
		for( int i = 0; i<this.adj.size() ;i++){
			if(this.adj.get(i).id==childID) return i;
		}
		assert(false): "Child with id " + childID + " not found in node " + this.id;
		return -1;
	}

	public int getNumberOfParents() {
		return this.parents.size();
	}
	
}
