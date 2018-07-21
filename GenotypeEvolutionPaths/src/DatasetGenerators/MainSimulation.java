package DatasetGenerators;

import java.util.ArrayList;

import GenotypeGraph.GenotypeNode;
import GenotypeGraph.WeightedGenotypeGraph;
import GenotypeGraph.WeightedGenotypeGraphAllowingMultipleMutations;

public class MainSimulation {

	@SuppressWarnings("unused")
	private static GraphDatasetGeneratorAllowingMultipleMutations createGraphDatasetGenerator(){
		String[] labels = {"A","B","C","D"};
		GraphDatasetGeneratorAllowingMultipleMutations gen = new GraphDatasetGeneratorAllowingMultipleMutations(labels);
		
		boolean[] root = {false,false,false,false};
		boolean[] a = {true,false,false, false};
		boolean[] b = {false,true,false, false};
		boolean[] ac = {true,false,true, false};
		boolean[] ad = {true,false,false, true};
		boolean[] ab = {true,true,false, false};
		boolean[] bd = {false,true,false, true};
		boolean[] abd = {true,true,false, true};
		
		GenotypeNode nroot = gen.add(root);
		GenotypeNode na    = gen.add(a);
		GenotypeNode nb    = gen.add(b);
		GenotypeNode nac   = gen.add(ac);
		GenotypeNode nad   = gen.add(ad);
		GenotypeNode nab   = gen.add(ab);
		GenotypeNode nbd   = gen.add(bd);
		GenotypeNode nabd  = gen.add(abd);
		
		gen.link(nroot, nroot, 0.9);
		gen.link(nroot, nb, 0.05);
		gen.link(nroot, na, 0.05);
		gen.link(nb, nb, 0.8);
		gen.link(nb, nbd, 0.10);
		gen.link(nb, nab, 0.10);
		gen.link(na, na, 0.6);
		gen.link(na, nab, 0.10);
		gen.link(na, nad, 0.10);
		gen.link(na, nac, 0.2);
		gen.link(nbd, nbd, 0.99);
		gen.link(nbd, nabd, 0.01);
		gen.link(nab, nab, 0.9);
		gen.link(nab, nabd, 0.1);
		gen.link(nad, nad, 0.4);
		gen.link(nad, nabd, 0.6);
		gen.link(nac, nac, 1);
		gen.link(nabd, nabd, 1);
		
		gen.setRoot(nroot);
		
		return gen;
	}
	
	@SuppressWarnings("unused")
	private static GraphDatasetGeneratorAllowingMultipleMutations createGraphDatasetGeneratorWithEqualSelfLoopProbability(){
		String[] labels = {"A","B","C","D"};
		GraphDatasetGeneratorAllowingMultipleMutations gen = new GraphDatasetGeneratorAllowingMultipleMutations(labels);
		
		boolean[] root = {false,false,false,false};
		boolean[] a = {true,false,false, false};
		boolean[] b = {false,true,false, false};
		boolean[] ac = {true,false,true, false};
		boolean[] ad = {true,false,false, true};
		boolean[] ab = {true,true,false, false};
		boolean[] bd = {false,true,false, true};
		boolean[] abd = {true,true,false, true};
		
		GenotypeNode nroot = gen.add(root);
		GenotypeNode na    = gen.add(a);
		GenotypeNode nb    = gen.add(b);
		GenotypeNode nac   = gen.add(ac);
		GenotypeNode nad   = gen.add(ad);
		GenotypeNode nab   = gen.add(ab);
		GenotypeNode nbd   = gen.add(bd);
		GenotypeNode nabd  = gen.add(abd);
		
		gen.link(nroot, nroot, 0.9);
		gen.link(nroot, nb, 0.05);
		gen.link(nroot, na, 0.05);
		gen.link(nb, nb, 0.9);
		gen.link(nb, nbd, 0.075);
		gen.link(nb, nab, 0.025);
		gen.link(na, na, 0.9);
		gen.link(na, nab, 0.025);
		gen.link(na, nad, 0.025);
		gen.link(na, nac, 0.05);
		gen.link(nbd, nbd, 0.9);
		gen.link(nbd, nabd, 0.1);
		gen.link(nab, nab, 0.9);
		gen.link(nab, nabd, 0.1);
		gen.link(nad, nad, 0.9);
		gen.link(nad, nabd, 0.1);
		gen.link(nac, nac, 1);
		gen.link(nabd, nabd, 1);
		
		gen.setRoot(nroot);
		
		return gen;
	}
	
	/**
	 * Simulates the generator to create samples for an artificial data set
	 * @param gen         generator
	 * @param nSamples    number of samples to generate in the data set
	 * @param pathLength  length of path followed in the generator for the simulation
	 * @return            an artificial data set corresponding to the requests
	 */
	public static ArrayList<boolean[]> simulate(GraphDatasetGeneratorAllowingMultipleMutations gen, int nSamples, int pathLength){
		ArrayList<boolean[]> D = new ArrayList<boolean[]>();
		for(int i=0; i<nSamples; i++){
			D.add(gen.simulate(pathLength));
		}
		return D;
	}
	
	public static void main(String[] args) {

		//GraphDatasetGenerator gen = createGraphDatasetGeneratorWithEqualSelfLoopProbability();
		GraphDatasetGeneratorAllowingMultipleMutations gen = new GraphDatasetGeneratorAllowingMultipleMutations(6);
		System.out.println("Simulation graph: ");
		gen.toDot();
		
		String[] labels = gen.getLabels();
		 
		/* it is important to keep this version */
		WeightedGenotypeGraph sstate = new WeightedGenotypeGraph(labels, simulate(gen, 1000000, 100));
		ArrayList<boolean[]> D = new ArrayList<boolean[]>();
		for(int i=1; i<=1; i++){ /* modify here to set up data set */
			D.addAll(simulate(gen, 1000000, 6));
		}
		WeightedGenotypeGraphAllowingMultipleMutations grp = new WeightedGenotypeGraphAllowingMultipleMutations(labels, D);
		System.out.println("Generated DAG: ");
		grp.toDot();
		System.out.println("Simulated steady state from 'real' model");
		sstate.printSteadyState();
		System.out.println("Simulated steady state from generated model");
		grp.printSteadyState();
		
	}

}
