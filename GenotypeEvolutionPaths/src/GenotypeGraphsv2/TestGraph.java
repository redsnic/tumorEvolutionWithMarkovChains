package GenotypeGraphsv2;



import org.junit.Test;

import Datasets.Dataset;

public class TestGraph {

	@Test
	public void test() {
		Dataset D = new Dataset();
		D.read("/home/rossi/Scrivania/test_base.bml");
		D.compact();
		GenotypeGraphAllowingMultipleMutations grp = new GenotypeGraphAllowingMultipleMutations(D);
		grp.toDot(System.out);
	}

}
