package GenotypeGraph;

public class Main {
	public static void main(String[] args) {
		WeightedGenotypeGraphAllowingMultipleMutations g = new WeightedGenotypeGraphAllowingMultipleMutations(30);
		g.reducedToDot(2);
		//g.toDot();
	}
}
