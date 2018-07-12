package GenotypeGraph;

import java.util.ArrayList;
import java.util.Stack;

import MarkovChains.MarkovChain;
import Utils.Utils;

/**
 * A very simple method to add transition probabilities to a genotype graph 
 * @author rossi
 * (Actually this is also a Markov Chain, TODO toMC() method)
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
	
	public WeightedGenotypeGraph(String[] labels, ArrayList<boolean[]> genotypes, int thres) {
		super(labels, genotypes, thres);    /* prepare graph */
		prepareGraph();
	}
	
	
	public WeightedGenotypeGraph(){
		super();
		prepareGraph();
	}
	
	public WeightedGenotypeGraph(int thres){
		super(thres);
		prepareGraph();
	}
	
	private void prepareGraph(){
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
	}
	
	/**
	 * Set up weights using this formula:
	 * W(<a,b>) = [Wdown(<a,b>)*(Wup(<a,b>)/Wup(b))]/[sum_{x \in a.Adj}Wdown(<a,x>)*(Wup(<a,x>)/Wup(x)]
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
			if(n.parents.size()==0 && n.numberOfMutations==1){
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
		if(downWeights.get(node.id) != null) 
			return Utils.sumDouble(downWeights.get(node.id)) + node.probability;  // Dynamic Programming
		
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
		if(upWeights.get(node.id) != null) 
			return Utils.sumDouble(upWeights.get(node.id)) + node.probability;  // Dynamic Programming
		
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
			System.out.println(node.getId() + " -> " + child.getId() + " [label=\"" + String.format("%.3f", this.finalWeights.get(node.id).get(j)) + "\"]");
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
	 */
	public boolean[] generateData(){
		GenotypeNode position = this.root;
		return generateDataNext(position);
	}

	private void computeEmissionProbability() {
		computeEmissionProbabilityRec(this.root);
		this.root.emissionProbability = 0.; /* no emission probability for clonal genotype */
		double sum = 0;
		for(GenotypeNode next : nodes){
			sum += next.probability*next.emissionProbability; 
		}
		System.out.println("---> " + sum);
	}

	/**
	 * Computes the sum of the weights (observed probabilities) 
	 * of the reachable nodes from position (maintaining "splitting" information) TODO
	 * @param position the starting point of the visit
	 * TODO check if efficiency can be improved
	 */
	private double computeEmissionProbabilityRec(GenotypeNode position) {
		
		if(position.emissionProbability != -1){
			return position.emissionProbability;
		}
		
		double num = position.probability;
		double denom = num;
		//TODO
		for(GenotypeNode next : position.adj){
			denom += computeEmissionProbabilityRec(next) + next.probability * (upWeights.get(next.id).get(next.findParentFromId(position.id))/Utils.sumDouble(upWeights.get(next.id)));
		}
		
		position.emissionProbability = (num/denom);
		return denom-num;
		
	}

	/**
	 * recursive function for synthetic data generation 
	 * @param position last node reached in the visit
	 * @return an artificial genotype
	 */
	private boolean[] generateDataNext(GenotypeNode position) {
		double chooseRoute = Math.random();
		int i=0;
		if(position.adj.size() == 0) {
			return position.genotype;
		}
		for(GenotypeNode next : position.adj){
			if(finalWeights.get(position.id).get(i)>chooseRoute){
				double stop = Math.random();
				/* TODO normalize output probability using down weights */
				if(stop < (next.emissionProbability)){
					return next.genotype; 
				} else {
					return generateDataNext(next);
				}
			} else {
				chooseRoute -= finalWeights.get(position.id).get(i);
				i++;
			}
		}
		assert(false); // unreachable
		return null;
	}
	
	
}
