package GenotypeGraph;

import java.util.ArrayList;
import java.util.Collections;

import MarkovChains.MarkovChain;
import Utils.Pair;
import Utils.Utils;

/**
 * A very simple method to add transition probabilities to a genotype graph 
 * @author rossi
 * (Actually this is also a Markov Chain, TODO toMC() method)
 * INVARIANT: the sum of the weight of each edge out-coming from any node is 1
 *            the graph is a DAG
 *            there is only a node with no parents and this node is labeled with the 'clonal' genotype 
 *            edges are set from node a to b if and only if : 
 *                  a.genotype.length + 1 = b.genotype.length and
 *                  exists a mutation x so that a.genotype = b.genotype\{x}  
 */
public class WeightedGenotypeGraph extends GenotypeGraphSimple {

	/**
	 * sum of weights of reachable nodes using direct edges
	 */
	SquareMatrix<Double> upWeights = new SquareMatrix<Double>(0.);   
	/**
	 * sum of weights of reachable nodes using reverse edges
	 */
	SquareMatrix<Double> downWeights = new SquareMatrix<Double>(0.);
	/**
	 * final weights computed with this formula:
	 * W(<a,b>) = [Wdown(<a,b>)*(Wup(<a,b>)/Wup(b))]/[sum_{x \in a.Adj}Wdown(<a,x>)*(Wup(<a,x>)/Wup(x)]
	 */
	SquareMatrix<Double> W = new SquareMatrix<Double>(0.);
	
	int[] nParents;
	int[] nChildren;
	
	GenotypeNode root = null; // TODO int
	
	/**
	 * Default constructor, enriches the simple genotype graph with weights 
	 * @param labels     HUGO symbols for genes
	 * @param genotypes  
	 * note that a single genotype is a sequence of boolean
	 * stating if a gene was or wasn't mutated (in the order defined by labels)  
	 */
	public WeightedGenotypeGraph(String[] labels, ArrayList<boolean[]> genotypes) {
		super(labels, genotypes); /* prepare graph */
		prepareGraph();
	}
	
	/**
	 * Alternate constructor with a limitation on the number of gene usable in the analysis
	 * @param labels     HUGO symbols for genes
	 * @param genotypes  dataset
	 * @param thres      limit to the number of gene usable in the analysis
	 */
	public WeightedGenotypeGraph(String[] labels, ArrayList<boolean[]> genotypes, int thres) {
		super(labels, genotypes, thres);    /* prepare graph */
		prepareGraph();
	}
	
	/**
	 * Stdin constructor
	 */
	public WeightedGenotypeGraph(){
		super();
		prepareGraph();
	}
	
	/**
	 * Stdin constructor with a limitation on the number of gene usable in the analysis
	 * @param thres      limit to the number of gene usable in the analysis
	 */
	public WeightedGenotypeGraph(int thres){
		super(thres);
		prepareGraph();
	}
	
	/**
	 * Steps to construct this graph,
	 * the structure generated is a DAG with a single ROOT
	 */
	private void prepareGraph(){
		/* 
		 * compact dataset, count observeation frequencies and 
		 * compute observation probabilities for each genotype 
		 */
		setObservedProbabilities();  
		
		/* add clonal genotype */
		computeSumTables();
		addRoot();
		computeSumTables();
		
		initWeights();
		
		/* Weight computation */
		setUpWeights();	
		setDownWeightsSplitted();
		setFinalWeights();
		
		/* compute steady state */
		computeSteadyState();
	}
	
	/**
	 * Initialization of weight matrices
	 */
	private void initWeights() {
		for(int i=0; i<E.getSize(); i++){
			upWeights.enlarge();
			downWeights.enlarge();
			W.enlarge();
		}
	}

	/**
	 * Adds shortcuts to reduce computations of the number 
	 * of parents and children of any node
	 */
	private void computeSumTables() {
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
	 * Set up weights using this formula:
	 * W(<a,b>) = [Wdown(<a,b>)*(Wup(<a,b>)/Wup(b))]/[sum_{x \in a.Adj}Wdown(<a,x>)*(Wup(<a,x>)/Wup(x)]
	 * TODO this formula has a bias, it gives more weight to nodes with many confluences
	 */
	private void setFinalWeights() {
		/* compute [Wdown(<a,b>)*(Wup(<a,b>)/Wup(b))] */
		for(int i = 0; i<V.size(); i++){
			for(int j = 0; j<E.getSize(); j++){
				if(E.get(i, j) == false) continue;
				W.set(i, j, downWeights.get(i, j)*(upWeights.get(i, j)/Utils.sumDoubleCol(upWeights, j)));
			}
		}
		/* normalize by [sum_{x \in a.Adj}Wdown(<a,x>)*(Wup(<a,x>)/Wup(x)] */
		for(int i = 0; i<W.getSize(); i++){
			Double normFactor = Utils.sumDoubleRow(W, i);
			for(int j = 0; j<W.getSize(); j++){
				W.set(i, j, W.get(i, j)/normFactor);
			}
		}
	}

	/** 
	 * Adds the clonal genotype with edges to each node without any parent as root of the graph
	 */
	private void addRoot() {
		GenotypeNode root = new GenotypeNode(new boolean[this.geneLabelsOrder.length] , this.id);
		this.id++;
		E.enlarge();
		for(GenotypeNode n : this.V){ 
			if(nParents[n.id]==0){
				E.set(root.id, n.id, true);
			}
		}
		root.probability = 1; /* TODO check if it is better to use 0 or 1 */
		this.V.add(root);
		this.root = root;
	}
	
	/**
	 * Use up weight information to split nodes and compute down weights
	 */
	private void setDownWeightsSplitted() {
		double[] weightReachableFromNode = new double[this.V.size()];
		int[] memoVisit = new int[this.V.size()];
		/* start from all 'leaf' nodes (no children)*/
		for( GenotypeNode node : this.V ){
			assert(node!=null);
			if(nChildren[node.id]== 0){
				setDownWeightsSplittedRec(node, weightReachableFromNode, memoVisit);
			}
		}
	}

	/**
	 * DP procedure to compute the sum of all the weights of the nodes
	 * reachable by 'node' (excluding itself) and using the information
	 * of the upWeights to split the observed probability among its 
	 * incoming edges
	 * @param node                      the considered node
	 * @param weightReachableFromNode   memoization array that has the information of the sum of all the 'splitted' weights that can be reached from this node
	 * @param memoVisit                 memoization array that has, for each node, the number of times it was considered in the visit
	 */
	private void setDownWeightsSplittedRec(GenotypeNode node, double[] weightReachableFromNode, int[] memoVisit) {
		
		weightReachableFromNode[node.id]+=node.probability;
		
		double norm = Utils.sumDoubleCol(upWeights, node.id);
		
		for(GenotypeNode parent : getParentsOf(node)){
			weightReachableFromNode[parent.id]+=weightReachableFromNode[node.id]*upWeights.get(parent.id,node.id)/norm;
			memoVisit[parent.id]++;
			if(memoVisit[parent.id] == nChildren[parent.id]){
				setDownWeightsSplittedRec(parent,weightReachableFromNode,memoVisit);
			}
		}
			
		for(GenotypeNode child : getChildrenOf(node)){
			downWeights.set(node.id, child.id, weightReachableFromNode[child.id]);
		}

	}

	/**
	 * DP procedure to efficiently compute the sum of
	 * all the weights of nodes reachable (excluding itself) by any node 
	 * following reverse edges
	 */
	private void setUpWeights() {
		int[] memoVisit = new int[this.V.size()];
		double[] memoValues = new double[this.V.size()];
		computeUpWeightsRec(this.root, memoVisit, memoValues);	
	}
	
	/**
	 * Recursive procedure to compute for each node its upWeights
	 * @param node          the node in analysis
	 * @param memoVisit     table counting the number of visits of each node to control the visit itself
	 * @param memoValues    table to memorize temporarily the results of the computation to improve efficiency
	 */
	private void computeUpWeightsRec(GenotypeNode node, int[] memoVisit, double[] memoValues){
		memoValues[node.id]+=node.probability;
		
		ArrayList<GenotypeNode> adj = getChildrenOf(node);
		
		for(GenotypeNode child : adj){
			memoValues[child.id]+=memoValues[node.id]/adj.size();
			upWeights.set(node.id, child.id, memoValues[node.id]/adj.size());
			memoVisit[child.id]++;
			if(memoVisit[child.id]==nParents[child.id]){
				computeUpWeightsRec(child, memoVisit, memoValues);
			}
		}
	
	}
	
	/**
	 * Sets observation probabilities up for each node
	 * (P(genotype) = #count(genotype in dataset)/#sizeof(dataset))
	 */
	private void setObservedProbabilities() {
		double normFactor = Utils.sumLong(counts);
		for(int i = 0; i< this.V.size(); i++){  
			this.V.get(i).probability = this.counts.get(i)/normFactor; 
		}
	}

	 
	/* adds edges weights */
	@Override
	public void toDotNode(GenotypeNode node){
		System.out.println(node.getId() + " [label=\"<" + this.nodeLabel(node) + ", " + String.format("%.3f", node.probability) + ">\"]");
	}
	
	@Override
	void toDotEdges(){
		for(int i=0; i<E.getSize(); i++ ){
			for(int j=0; j<E.getSize(); j++){
				if(!(E.get(i, j))) continue;
				System.out.println(i + " -> " + j + " [label=\"" + String.format("%.3f", this.W.get(i, j)) +
						"\nDown: "  + String.format("%.3f", this.downWeights.get(i, j)) + 
						"\nUp: "  + String.format("%.3f", this.upWeights.get(i,j)) +  "\"]");
			}
		}
	}
	
	/**
	 * reduces the number of edges of each node to a maximum of n
	 * keeping the most probable edges
	 * @param n limit on the maximum number of edges to show for each node
	 */
	public void reducedToDot(int n){
		int[] inEdges = new int[V.size()];
		boolean[] touched = new boolean[V.size()];
		touched[this.root.id] = true; 
		System.out.println("digraph G{");
		reducedToDotRec(this.root, n, inEdges, touched);
		System.out.println("}");
	}
	
	/**
	 * Recursive visit for the limited print
	 * @param position   position in the visit
	 * @param limit      maximum number of edges accepted for each node
	 * @param inEdges    memo array to guide the visit
	 * @param touched    element that must be printed (visited by the edges printed by the limited print)
	 */
	private void reducedToDotRec(GenotypeNode position, int limit, int[] inEdges, boolean[] touched) {
		//System.out.println(position.id);
		if(touched[position.id]){
			toDotNode(position);
		}
		ArrayList<Integer> bestEdges = getBestEdges(position.getId(), limit, inEdges);
		if(touched[position.id]){
			double norm = 0.;
			for(int j: bestEdges){
				norm += W.get(position.id, j);
			}
			for(int j: bestEdges){
				touched[j] = true;
				System.out.println(position.getId() + " -> " + j + " [label=\"" + String.format("%.3f", this.W.get(position.getId(), j)/norm) + "\"]");			
			}
		}
		
		for(int i = 0; i<E.getSize(); i++){
			if(!E.get(position.id,i)) continue;
			if(inEdges[i] == nParents[i]){
				//System.out.print(position.id + " -"+ inEdges[i] +"- ");
				reducedToDotRec(V.get(i),limit,inEdges, touched);
			}
		}
	}

	/**
	 * Given a node, returns a list of size less or equal than limit
	 * of its edges with maximum weight
	 * It also updates visit information for the reached nodes
	 * @param pos     node considered
	 * @param limit   maximum length of output list
	 * @param inEdges memo array for visit control 
	 * @return the maximum weight edges
	 */
	private ArrayList<Integer> getBestEdges(int pos, int limit, int[] inEdges) {
		ArrayList<Pair<Integer, Double>> weights = new ArrayList<Pair<Integer, Double>>();
		for(int i=0; i<E.getSize(); i++){
			if(!E.get(pos, i)) continue;
			weights.add(new Pair<Integer, Double>(i, W.get(pos,i)));
			//System.out.println(i + "++ from " + pos);
			inEdges[i]++;
		}
		weights.sort((a,b) -> -a.snd().compareTo(b.snd()));
		ArrayList<Integer> res = new ArrayList<Integer>();
		for(int i=0; i<limit && i<weights.size(); i++){
			res.add(weights.get(i).fst());
		}
		return res;
	}

	/**
	 * Creates a MarkovChain object based on this graph 
	 * (in fact this graph is already a Markov Chain)
	 * @return the newly created Markov Chain
	 */
	public MarkovChain toMC(){
		MarkovChain mc = new MarkovChain();
		
		ArrayList<GenotypeMarkovNode> gNodeList = new ArrayList<GenotypeMarkovNode>(); 
		for(GenotypeNode n : this.V){
			GenotypeMarkovNode newNode = new GenotypeMarkovNode(this.nodeLabel(n));
			gNodeList.add(newNode);
		}

		for(int i = 0; i<this.V.size(); i++){
			int j=0;
			for(GenotypeNode child: getChildrenOf(this.V.get(i)) ){
				gNodeList.get(i).add(gNodeList.get(child.id), W.get(i,j));
				j++;
			}
		}
		
		for(GenotypeMarkovNode n : gNodeList){
			mc.add(n);
		}
		
		return mc;
	}
	
	/**
	 * computes the 'steady state' of this graph seen 
	 * as a Markov Chain
	 * NOTE: this graph is a DAG with a single 'root'
	 */
	private void computeSteadyState(){
		int[] memoIn = new int[this.V.size()];
		this.root.steadyStateProbability = 1.; /* starting situation */
		computeSteadyStateRec(this.root, memoIn);
	}

	/**
	 * Visit on the graph to compute the steady state
	 * @param position active node in visit
	 * @param memoIn   table having for each node the number of edges yet considered in the computation
	 */
	private void computeSteadyStateRec(GenotypeNode position, int[] memoIn) {
		ArrayList<GenotypeNode> adj = getChildrenOf(position); 
		if(adj.size()>0){
			for(GenotypeNode child : adj){
				child.steadyStateProbability += (position.steadyStateProbability)*W.get(position.id, child.id);
				memoIn[child.id]++;
				if(memoIn[child.id] == nParents[child.id]){ 
					/* when all edges entering in a node are considered recur in that node */
					computeSteadyStateRec(child, memoIn);
				}
			}
			position.steadyStateProbability = 0;
		}
	}
	
	/**
	 * Prints the steady state computed for this graph seen as a Markov Chain
	 * NOTE: this graph is a DAG with a single 'root'
	 */
	public void printSteadyState(){
		System.out.println("Steady state: ");
		for(GenotypeNode node:V){
			if(node.steadyStateProbability>0){
				System.out.println(this.nodeLabel(node) + " " + node.steadyStateProbability);
			}
		}
	}
	

	
	
}
