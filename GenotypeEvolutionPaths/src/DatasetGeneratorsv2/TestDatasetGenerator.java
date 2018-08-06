package DatasetGeneratorsv2;



import org.junit.Test;

import Datasets.Dataset;
import GenotypeGraphsv2.GenotypeGraphAllowingMultipleMutations;

public class TestDatasetGenerator {

	@Test
	public void test() {
		GraphDatasetGeneratorAllowingMultipleMutations gen = new GraphDatasetGeneratorAllowingMultipleMutations(4);
		gen.toDot(System.out);
		Dataset data = gen.generate(1000, 2);
		data.compact();
		data.printBMLFormat(System.out);
		GenotypeGraphAllowingMultipleMutations grf = new GenotypeGraphAllowingMultipleMutations(data);
		grf.toDot(System.out);
	}

}
