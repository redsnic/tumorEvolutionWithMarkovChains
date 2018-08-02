package GenotypeGraph;

public class Main {
	public static void main(String[] args) {
		WeightedGenotypeGraphAllowingMultipleMutations g = new WeightedGenotypeGraphAllowingMultipleMutations(9);
		//g.reducedToDot(1);
		g.toDot();
	}
}
