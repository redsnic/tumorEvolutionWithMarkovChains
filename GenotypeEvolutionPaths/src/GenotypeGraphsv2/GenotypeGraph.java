package GenotypeGraphsv2;

import java.io.PrintStream;

import Datasets.Dataset;
import Graphs.DirectedWeightedGraphMatrix;
import Graphs.SimpleNode;

public abstract class GenotypeGraph {

	DirectedWeightedGraphMatrix<GenotypeInfo> structure = new  DirectedWeightedGraphMatrix<GenotypeInfo>();
	Dataset dataset;
	
	public GenotypeGraph(Dataset dataset){
		this.dataset = dataset;
		addNodes();
		addEdges();
		addWeights();
	}
	
	abstract void addNodes();
	
	abstract void addEdges();
	
	abstract void addWeights();
	
	public void toDot(PrintStream out){
		structure.toDot(out);
	}
	
}
