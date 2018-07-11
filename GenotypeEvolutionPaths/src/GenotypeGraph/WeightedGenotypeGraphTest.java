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

}
