package GenotypeGraph;

import java.util.ArrayList;

import org.junit.Test;

public class WeightedGenotypeGraphTest {

	@Test
	public void testToDot() {
		String[] labels = {"A","B","C","D"}; 
		ArrayList<boolean[]> gnts = new ArrayList<boolean[]>();
		boolean[] a = {true,false,false, false};
		boolean[] b = {false,true,false, false};
		boolean[] ac = {true,false,true, false};
		boolean[] ad = {true,false,false, true};
		boolean[] ab = {true,true,false, false};
		boolean[] bd = {false,true,false, true};
		boolean[] abd = {true,true,false, true};
		
		gnts.add(a); gnts.add(a);
		gnts.add(b); gnts.add(b); gnts.add(b);
		gnts.add(ac);
		gnts.add(ad);
		gnts.add(ab);
		gnts.add(bd);
		gnts.add(abd);
		
		WeightedGenotypeGraph g = new WeightedGenotypeGraph(labels, gnts);
		g.toDot();
	}
	
	@Test
	public void testToDotAndtoMC() {
		String[] labels = {"A","B","C","D"}; 
		ArrayList<boolean[]> gnts = new ArrayList<boolean[]>();
		boolean[] a = {true,false,false, false};
		boolean[] b = {false,true,false, false};
		boolean[] ac = {true,false,true, false};
		boolean[] ad = {true,false,false, true};
		boolean[] ab = {true,true,false, false};
		boolean[] bd = {false,true,false, true};
		boolean[] abd = {true,true,false, true};
		
		gnts.add(a); gnts.add(a);
		gnts.add(b); gnts.add(b); gnts.add(b);
		gnts.add(ac);
		gnts.add(ad);
		gnts.add(ab);
		gnts.add(bd);
		gnts.add(abd);
		
		WeightedGenotypeGraph g = new WeightedGenotypeGraph(labels, gnts);
		System.out.println("MC test");
		g.toMC().toDot();
	}
	
	@Test 
	public void testDatasetGeneration(){
		String[] labels = {"A","B","C","D"}; 
		ArrayList<boolean[]> gnts = new ArrayList<boolean[]>();
		boolean[] a = {true,false,false, false};
		boolean[] b = {false,true,false, false};
		boolean[] ac = {true,false,true, false};
		boolean[] ad = {true,false,false, true};
		boolean[] ab = {true,true,false, false};
		boolean[] bd = {false,true,false, true};
		boolean[] abd = {true,true,false, true};
		
		gnts.add(a); gnts.add(a);
		gnts.add(b); gnts.add(b); gnts.add(b);
		gnts.add(ac);
		gnts.add(ad);
		gnts.add(ab);
		gnts.add(bd);
		gnts.add(abd);
		
		WeightedGenotypeGraph g = new WeightedGenotypeGraph(labels, gnts);
		System.out.println("Multiple iterations test (starting point)");
		
		g.toDot();
		
		int nRep = 1000000;
		
		for(int i = 0; i<100 ; i++){
			System.out.println("--------   Test number: " + (i+1));
			ArrayList<boolean[]> temp = new ArrayList<boolean[]>();
			for(int j = 0; j<nRep; j++){
				temp.add(g.generateData());
			}
			System.out.println("probability of random passage:");
			for(GenotypeNode n : g.nodes){
				System.out.println(  g.nodeLabel(n) + ": " + n.passFrequency/(double) nRep );
			}
			System.out.println("Estimated emission probabilities");
			for(GenotypeNode n : g.nodes){
				System.out.println(  g.nodeLabel(n) + ": " + n.emissionProbability );
			}
			labels=g.getLabels();
			g = new WeightedGenotypeGraph(labels, temp);
			g.toDot();
		}
		
	}

}
