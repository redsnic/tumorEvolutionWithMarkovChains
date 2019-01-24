package Main;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import DatasetGeneratorsv2.GraphDatasetGeneratorAllowingMultipleMutations;

public class ExactProbabilities {
	
	/**
	 * Print plot data
	 * @param out    output stream
	 * @param k      size
	 * @param probs  list of probabilities
	 * @param names  list of genotypes
	 */
	private static void printer(PrintStream out, int k, ArrayList<Double> probs, ArrayList<String> names){
		out.println("> "+k);
		for(int i=0; i<names.size(); i++){
			out.println(names.get(i) + " " + probs.get(i));
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		/* output path */
		String path = "/home/redsnic/Scrivania/Test_ripetizioni/testEvoluzione111.txt";
		PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(path)), true);
		/* prepare generator */
		GraphDatasetGeneratorAllowingMultipleMutations gen = Main.SimulationsFromKnownGenerator.simpleGenerator();
		/* probabilities */
		ArrayList<Double> vect = new ArrayList<Double>();
		/* at step 0 each cell is in the empty genotype (healty) state*/
		vect.add(1.);
		/* each other state has zero  probability */
		for(int i  = 0; i<gen.getSize(); i++){
			vect.add(0.);
		}
		/* get genotypes labels */ 
		ArrayList<String> names = gen.getNames();
		/* print initial state */
		printer(out, 0, vect, names);
		/* start exact probabilities evolution computation */
		int numberOfSteps = 100;
		for(int j = 0; j<numberOfSteps; j++){
			vect = gen.computeNextStepProbabilities(vect);
			printer(out, j, vect, names);
		}
	}
}
