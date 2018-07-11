package GenotypeGraph;

public class Main {

	public static void main(String[] args) {
		WeightedGenotypeGraph g = new WeightedGenotypeGraph(2);
		g.toMC().toDot();
	}

}
