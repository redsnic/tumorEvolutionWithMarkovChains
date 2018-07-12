package DatasetGenerators;

import java.util.ArrayList;

import GenotypeGraph.GenotypeNode;
import GenotypeGraph.WeightedGenotypeGraph;

public class MainSimulation {

	private static GraphDatasetGenerator createGraphDatasetGenerator(){
		GraphDatasetGenerator gen = new GraphDatasetGenerator();
		
		boolean[] root = {false,false,false,false};
		boolean[] a = {true,false,false, false};
		boolean[] b = {false,true,false, false};
		boolean[] ac = {true,false,true, false};
		boolean[] ad = {true,false,false, true};
		boolean[] ab = {true,true,false, false};
		boolean[] bd = {false,true,false, true};
		boolean[] abd = {true,true,false, true};
		
		GenotypeNode nroot= new GenotypeNode(root);
		GenotypeNode na   = new GenotypeNode(a);
		GenotypeNode nb   = new GenotypeNode(b);
		GenotypeNode nac  = new GenotypeNode(ac);
		GenotypeNode nad  = new GenotypeNode(ad);
		GenotypeNode nab  = new GenotypeNode(ab);
		GenotypeNode nbd  = new GenotypeNode(bd);
		GenotypeNode nabd = new GenotypeNode(abd);
		
		gen.add(nroot);
		gen.add(na);
		gen.add(nb);
		gen.add(nac);
		gen.add(nad);
		gen.add(nab);
		gen.add(nbd);
		gen.add(nabd);
		
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
	
	private static GraphDatasetGenerator createGraphDatasetGeneratorWithEqualSelfLoopProbability(){
		GraphDatasetGenerator gen = new GraphDatasetGenerator();
		
		boolean[] root = {false,false,false,false};
		boolean[] a = {true,false,false, false};
		boolean[] b = {false,true,false, false};
		boolean[] ac = {true,false,true, false};
		boolean[] ad = {true,false,false, true};
		boolean[] ab = {true,true,false, false};
		boolean[] bd = {false,true,false, true};
		boolean[] abd = {true,true,false, true};
		
		GenotypeNode nroot= new GenotypeNode(root);
		GenotypeNode na   = new GenotypeNode(a);
		GenotypeNode nb   = new GenotypeNode(b);
		GenotypeNode nac  = new GenotypeNode(ac);
		GenotypeNode nad  = new GenotypeNode(ad);
		GenotypeNode nab  = new GenotypeNode(ab);
		GenotypeNode nbd  = new GenotypeNode(bd);
		GenotypeNode nabd = new GenotypeNode(abd);
		
		gen.add(nroot);
		gen.add(na);
		gen.add(nb);
		gen.add(nac);
		gen.add(nad);
		gen.add(nab);
		gen.add(nbd);
		gen.add(nabd);
		
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
	
	public static WeightedGenotypeGraph simulate(GraphDatasetGenerator gen, int nSamples, int pathLength, String[] labels){
		ArrayList<boolean[]> D = new ArrayList<boolean[]>();
		for(int i=0; i<nSamples; i++){
			D.add(gen.simulate(pathLength));
		}
		return new WeightedGenotypeGraph(labels, D);
	}
	
	public static void main(String[] args) {
		
		GraphDatasetGenerator gen = createGraphDatasetGeneratorWithEqualSelfLoopProbability();
		
		String[] labels = {"A","B","C","D"};
		
		WeightedGenotypeGraph sstate = simulate(gen, 100000, 1000, labels);
		WeightedGenotypeGraph grp = simulate(gen, 100000, 10, labels);
		System.out.println("Generated DAG: ");
		grp.toDot();
		System.out.println("Simulated steady state from 'real' model");
		sstate.printSteadyState();
		System.out.println("Simulated steady state from generated model");
		grp.printSteadyState();
		
	}

}
