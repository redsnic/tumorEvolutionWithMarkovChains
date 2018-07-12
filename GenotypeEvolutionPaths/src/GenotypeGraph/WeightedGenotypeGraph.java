package GenotypeGraph;

import java.util.ArrayList;

import MarkovChains.MarkovChain;
import Utils.Utils;

/**
 * A very simple method to add transition probabilities to a genotype graph 
 * @author rossi
 * (Actually this is also a Markov Chain, TODO toMC() method)
 * INVARIANT: the sum of the weight of each edge outcoming from any node is 1
 *            the graph is a DAG
 *            there is only a node with no parents and this node is labelled with the 'clonal' genotype 
 *            edges are set from node a to b if and only if : 
 *                  a.genotype.length + 1 = b.genotype.length and
 *                  exists a mutation x so that a.genotype = b.genotype\{x}  
 */
public class WeightedGenotypeGraph extends GenotypeGraphSimple {

	/**
	 * sum of weights of reachable nodes using direct edges
	 */
	ArrayList<ArrayList<Double>> upWeights;   
	/**
	 * sum of weights of reachable nodes using reverse edges
	 */
	ArrayList<ArrayList<Double>> downWeights;
	/**
	 * weight computed using this formula:
	 * W(<a,b>) = [Wdown(<a,b>)*(Wup(<a,b>)/Wup(b))]/[sum_{x \in a.Adj}Wdown(<a,x>)*(Wup(<a,x>)/Wup(x)]
	 */
	ArrayList<ArrayList<Double>> finalWeights;
	
	GenotypeNode root = null;
	
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
		addRoot();
		
		/* init */
		upWeights = setAtNull();
		downWeights = setAtNull();
		finalWeights = setAtNull();
		
		/* Weight computation */
		setDownWeights();
		setUpWeights();	
		setFinalWeights();
		
		/* compute emission probabilities for each node */ 
		computeEmissionProbability();
		
		/* compute steady state */
		computeSteadyState();
	}
	
	/**
	 * Set up weights using this formula:
	 * W(<a,b>) = [Wdown(<a,b>)*(Wup(<a,b>)/Wup(b))]/[sum_{x \in a.Adj}Wdown(<a,x>)*(Wup(<a,x>)/Wup(x)]
	 * TODO this formula has a bias, it gives more weight to nodes with many confluences
	 */
	private void setFinalWeights() {
		/* compute [Wdown(<a,b>)*(Wup(<a,b>)/Wup(b))] */
		for(int i = 0; i<nodes.size(); i++){
			ArrayList<Double> temp = new ArrayList<Double>();
			for(int j = 0; j<nodes.get(i).adj.size(); j++){ 
				/* TODO check if access to a from b can be improved avoiding a linear search on b's parent */
				temp.add(downWeights.get(i).get(j)*(upWeights.get(nodes.get(i).adj.get(j).id).get(nodes.get(i).adj.get(j).findParentFromId(i))/Utils.sumDouble(upWeights.get(nodes.get(i).adj.get(j).id))));
			}
			finalWeights.set(i, temp);
		}
		/* normalize by [sum_{x \in a.Adj}Wdown(<a,x>)*(Wup(<a,x>)/Wup(x)] */
		for(int i = 0; i<finalWeights.size(); i++){
			Double normFactor = Utils.sumDouble(finalWeights.get(i));
			for(int j = 0; j<finalWeights.get(i).size(); j++){
				finalWeights.get(i).set(j, finalWeights.get(i).get(j)/normFactor );
			}
		}
	}

	/** 
	 * Adds the clonal genotype with edges to each node without any parent as root of the graph
	 */
	private void addRoot() {
		GenotypeNode root = new GenotypeNode(new boolean[this.geneLabelsOrder.length] , this.nodes.size());
		for(GenotypeNode n : this.nodes){ 
			if(n.parents.size()==0){
				root.add(n);
			}
		}
		root.probability = 1; /* TODO check if it is better to use 0 or 1 */
		this.nodes.add(root);
		this.root = root;
	}

	/**
	 * DP procedure to efficiently compute the sum of
	 * all the weights of nodes reachable (excluding itself) by any node 
	 */
	private void setDownWeights() {
		for( GenotypeNode node : this.nodes ){
			assert(node!=null);
			setDownWeightsRec(node);
		}
	}
	
	/**
	 * DP procedure to efficiently compute the sum of
	 * all the weights of nodes reachable by a specified node (excluding itself)  
	 * @param node  starting point
	 * @return      sum of weights of nodes reachable by node (including itself) 
	 */
	private Double setDownWeightsRec(GenotypeNode node) {
		assert(node!=null);
		if(downWeights.get(node.id) != null) {
			return Utils.sumDouble(downWeights.get(node.id)) + node.probability;  // Dynamic Programming
		}
		ArrayList<Double> lineWeights = new ArrayList<Double>();
		for(int i = 0; i<node.adj.size(); i++){
			lineWeights.add(setDownWeightsRec(node.adj.get(i)));
		}
		downWeights.set(node.id, lineWeights);
		return Utils.sumDouble(lineWeights)+node.probability;
	}

	/**
	 * DP procedure to efficiently compute the sum of
	 * all the weights of nodes reachable (excluding itself) by any node 
	 * following reverse edges
	 */
	private void setUpWeights() {
		for( GenotypeNode node : this.nodes ){
			assert(node!=null);
			setUpWeightsRec(node);
		}
	}
	
	
	/**
	 * DP procedure to efficiently compute the sum of
	 * all the weights of nodes reachable by a specified node (excluding itself) following reverse edges
	 * @param node  starting point
	 * @return      sum of weights of nodes reachable by node (including itself) following reverse edges
	 */
	private Double setUpWeightsRec(GenotypeNode node) {
		assert(node!=null);
		if(upWeights.get(node.id) != null){ 
			return Utils.sumDouble(upWeights.get(node.id)) + node.probability;  // Dynamic Programming
		}
		ArrayList<Double> lineWeights = new ArrayList<Double>();
		for(int i = 0; i<node.parents.size(); i++){
			lineWeights.add(setUpWeightsRec(node.parents.get(i)));
		}
		upWeights.set(node.id, lineWeights);
		
		return Utils.sumDouble(lineWeights)+node.probability;
		
	}	
	
	/**
	 * Sets observation probabilities up for each node
	 * (P(genotype) = #count(genotype in dataset)/#sizeof(dataset))
	 * Note: resets nodes IDs (needed for fast access to weights'lists)
	 */
	private void setObservedProbabilities() {
		double normFactor = Utils.sumLong(counts);
		for(int i = 0; i< this.nodes.size(); i++){
			this.nodes.get(i).setId(i);  
			this.nodes.get(i).probability = this.counts.get(i)/normFactor; 
		}
	}
	
	/**
	 * Initialization for weight lists
	 * (also used for the DP algorithms)
	 * @return a list containing a null for each node 
	 */
	private ArrayList<ArrayList<Double>> setAtNull(){
		ArrayList<ArrayList<Double>> weightList = new ArrayList<ArrayList<Double>>();
		for( @SuppressWarnings("unused") GenotypeNode n : this.nodes ){
			weightList.add(null); /* just add null */
		}
		for( GenotypeNode n : this.nodes ){
			assert(weightList.get(n.id) == null);
		}
		return weightList;
	}

	 
	/* adds edges'weigths */
	@Override
	public void toDotNode(GenotypeNode node){
		System.out.println(node.getId() + " [label=\"<" + this.nodeLabel(node) + ", " + String.format("%.3f", node.probability) + ">\"]");
		int j=0;
		for(GenotypeNode child : node.adj ){
			System.out.println(node.getId() + " -> " + child.getId() + " [label=\"" + String.format("%.3f", this.finalWeights.get(node.id).get(j)) +
					"\nDown: "  + String.format("%.3f", this.downWeights.get(node.id).get(j)) + 
					"\nUp: "  + String.format("%.3f", this.upWeights.get(child.id).get(child.findParentFromId(node.id))) +  "\"]");
			j++;
		}
	}
	
	/**
	 * Creates a MarkovChain object based on this graph 
	 * (in fact this graph is already a Markov Chain)
	 * @return the newly created Markov Chain
	 */
	public MarkovChain toMC(){
		MarkovChain mc = new MarkovChain();
		
		ArrayList<GenotypeMarkovNode> gNodeList = new ArrayList<GenotypeMarkovNode>(); 
		for(GenotypeNode n : this.nodes){
			GenotypeMarkovNode newNode = new GenotypeMarkovNode(this.nodeLabel(n));
			gNodeList.add(newNode);
		}

		for(int i = 0; i<this.nodes.size(); i++){
			int j=0;
			for(GenotypeNode child: this.nodes.get(i).adj ){
				gNodeList.get(i).add(gNodeList.get(child.id), finalWeights.get(i).get(j));
				j++;
			}
		}
		
		for(GenotypeMarkovNode n : gNodeList){
			mc.add(n);
		}
		
		return mc;
	}
	
	/**
	 * Simulates the graph to produce an artificial observed genotype
	 * @return an artificial genotype
	 * TODO
	 */
	public boolean[] generateData(){
		GenotypeNode position = this.root;
		return generateDataNext(position);
	}

	/**
	 * TODO
	 */
	private void computeEmissionProbability() {
		
		int[] memoIn = new int[this.nodes.size()];
		/* no emission probability for clonal genotype */
		computeEmissionProbabilityRec(this.root, memoIn);
		
	}

	/**
	 * Computes the sum of the weights (observed probabilities) 
	 * of the reachable nodes from position (maintaining "splitting" information) TODO
	 * @param position the starting point of the visit
	 * @param memoIn TODO
	 * TODO check if efficiency can be improved
	 */
	private void computeEmissionProbabilityRec(GenotypeNode position, int[] memoIn) {
		
		double remaining = position.emissionProbability;
		if(position == root){
			remaining = 1;
		}
		
		if(position.emissionProbability!=0){
			position.emissionProbability = position.probability/remaining;
		} else{
			position.emissionProbability = 0;
		}
		
		if(position.adj.size()==0){
			position.emissionProbability=1;
		}
		
		double remainingProbability = remaining*(1-position.emissionProbability);
		
		//TODO
		for(int i=0; i<position.adj.size(); i++){
			position.adj.get(i).emissionProbability += remainingProbability*finalWeights.get(position.id).get(i);
			memoIn[position.adj.get(i).id]++;
			if(memoIn[position.adj.get(i).id] == position.adj.get(i).parents.size()){
				computeEmissionProbabilityRec(position.adj.get(i), memoIn);
			}
		}
		
	}

	/**
	 * recursive function for synthetic data generation 
	 * @param position last node reached in the visit
	 * @return an artificial genotype
	 * TODO
	 */
	private boolean[] generateDataNext(GenotypeNode position) {
		position.passFrequency++;
		
		double chooseRoute = Math.random();
		
		double stop = Math.random();
		if(stop < (position.emissionProbability)){
			return position.genotype; 
		}
		
		//if(position.adj.size() == 0) {
		//	return position.genotype;
		//}
		
		int i=0;
		for(GenotypeNode next : position.adj){
			if(finalWeights.get(position.id).get(i)>chooseRoute){
				/* TODO normalize output probability using down weights */
				return generateDataNext(next);
			} else {
				chooseRoute -= finalWeights.get(position.id).get(i);
				i++;
			}
		}
		//assert(false); // unreachable
		return new boolean[position.genotype.length];
	}
	
	/**
	 * computes the steady state of this graph seen 
	 * as a Markov Chain
	 * NOTE: this graph is a DAG with a single 'root'
	 */
	private void computeSteadyState(){
		int[] memoIn = new int[this.nodes.size()];
		this.root.steadyStateProbability = 1.; /* starting situation */
		computeSteadyStateRec(this.root, memoIn);
	}

	/**
	 * Visit on the graph to compute the steady state
	 * @param position active node in visit
	 * @param memoIn   table having for each node the number of edges yet considered in the computation
	 */
	private void computeSteadyStateRec(GenotypeNode position, int[] memoIn) {
		
		if(position.adj.size()>0){
			for(int i=0; i<position.adj.size(); i++){
				position.adj.get(i).steadyStateProbability += (position.steadyStateProbability)*finalWeights.get(position.id).get(i);
				memoIn[position.adj.get(i).id]++;
				if(memoIn[position.adj.get(i).id] == position.adj.get(i).parents.size()){ 
					/* when all edges entering in a node are considered recurr in that node */
					computeSteadyStateRec(position.adj.get(i), memoIn);
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
		for(GenotypeNode node:nodes){
			if(node.steadyStateProbability>0){
				System.out.println(this.nodeLabel(node) + " " + node.steadyStateProbability);
			}
		}
	}
	
	
}
