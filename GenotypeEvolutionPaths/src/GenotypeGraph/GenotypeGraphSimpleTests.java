package GenotypeGraph;

import java.util.ArrayList;

import org.junit.Test;

public class GenotypeGraphSimpleTests {

	@Test
	public void testToDot() {
		String[] labels = {"A","B","C"}; 
		ArrayList<boolean[]> gnts = new ArrayList<boolean[]>();
		boolean[] l1 = {true,false,false};
		boolean[] l2 = {true,true,false};
		boolean[] l3 = {true,true,true};
		gnts.add(l1); gnts.add(l1); gnts.add(l1); gnts.add(l1); gnts.add(l2); gnts.add(l2); gnts.add(l3);
		GenotypeGraphSimple g = new GenotypeGraphSimple(labels, gnts);
		g.toDot();
	}

}
