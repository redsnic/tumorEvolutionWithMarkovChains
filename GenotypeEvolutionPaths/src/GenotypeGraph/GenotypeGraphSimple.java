package GenotypeGraph;
/* TODO resolve problem with getNodes() */
import java.util.ArrayList;
import java.util.Collections;

/**
 * Generates a graph connecting all nodes with equal genotypes 
 * but for a single extra mutation in the receiver of the edge
 * @author rossi
 *
 */
public class GenotypeGraphSimple extends GenotypeGraph{

	/**
	 * List of counts for each different genotype observed in the data set
	 * (usable to compute observed probabilities)
	 */
	ArrayList<Long> counts;
	/**
	 * Default constructor
	 * @param labels     HUGO symobl for each gene (in dataset order)
	 * @param genotypes  dataset
	 */
	public GenotypeGraphSimple(String[] labels, ArrayList<boolean[]> genotypes) {
		super(labels, genotypes); 
	}
	/**
	 * Alternate consuctor with limited number of considered genes
	 * @param labels     HUGO symobl for each gene (in dataset order)
	 * @param genotypes  dataset
	 * @param thres      limit on considered genes
	 */
	
	public GenotypeGraphSimple(String[] labels, ArrayList<boolean[]> genotypes, int thres) {
		super(labels, genotypes, thres); 
	}
	
	/**
	 * Default constuctor reading dataset from STDIN
	 */
	public GenotypeGraphSimple() { 
		super();
	}
	
	/**
	 * Alternate consuctor with limited number of considered genes reading dataset from STDIN
	 * @param thres    limit on considered genes
	 */
	public GenotypeGraphSimple(int thres) {
		super(thres);
	}
	
	@Override 
	protected void prepareEdges(){
		Collections.sort(this.V);
		countAndCompress();
		setupAdjMatrix();
		linkNodes(); 
	}
	
	/**
	 * Counts all redundant genotypes and 
	 * reduces the repetitions to a single node
	 * REQUIRE this.nodes is sorted
	 */
	private void countAndCompress(){
		this.counts = new ArrayList<Long>();
		ArrayList<GenotypeNode> compressed = new ArrayList<GenotypeNode>();
		
		if (this.size() == 0){ /* there are no nodes */
			return;
		}
			
		long count = 1;        /* count nodes */
		compressed.add(this.V.get(0));
		for(int i = 1; i<this.size() ;i++){
			if(compressed.get(compressed.size()-1).compareTo(this.V.get(i)) != 0){
				compressed.add(this.V.get(i));
				this.counts.add(count);
				count=1;
			} else {
				count++;
			}
		}
		
		this.V = compressed;
		this.counts.add(count);
		this.reset();
	}
	
	/**
	 * Creates a node from A to B only if A has one mutation less than B and 
	 * and all other mutations are the same
	 */
	protected void linkNodes(){
		/* for each node check the following nodes and add an edge to each other compatible node */
		for(int i = 0; i<this.V.size() ; i++){
			for (int j = i+1; j<this.V.size() && this.V.get(i).getNumberOfMutations()+2 > this.V.get(j).getNumberOfMutations() ; j++) {
				if( this.V.get(j).contains(this.V.get(i))){
					E.set(i, j, true);
				}
			}
		}
	}
	
	/**
	 * Resets node IDs and adjacency matrix
	 */
	private void reset(){
		this.id = 0;
		for(GenotypeNode n : V){
			n.id = this.id;
			this.id++;
		}
		this.E = new SquareMatrix<Boolean>(false);
		for(int i=0; i<V.size(); i++){
			E.enlarge();
		}
	}
	
	/**
	 * @param n  a node of the graph
	 * @return   the list of the parents of n
	 */
	ArrayList<GenotypeNode> getParentsOf(GenotypeNode n){
		ArrayList<GenotypeNode>  pList = new ArrayList<GenotypeNode>();
		for(int i=0; i<E.getSize(); i++){
			if(E.get(i, n.id)){
				pList.add(V.get(i));
			}
		}
		return pList;
	}
	
	/**
	 * @param n  a node of the graph
	 * @return   the list of the children of n
	 */
	ArrayList<GenotypeNode> getChildrenOf(GenotypeNode n){
		ArrayList<GenotypeNode>  cList = new ArrayList<GenotypeNode>();
		for(int j=0; j<E.getSize(); j++){
			if(E.get(n.id, j)){
				cList.add(V.get(j));
			}
		}
		return cList;
	}
	
	
	

}
