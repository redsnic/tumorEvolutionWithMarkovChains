package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import DatasetGeneratorsv2.GraphDatasetGeneratorAllowingMultipleMutations;
import Datasets.Dataset;
import GenotypeGraphsv2.GenotypeGraphAllowingMultipleMutations;

/**
 * Main for command line use
 * 
 * @author rossi
 */
public class CommandLineInterface {

	public static void main(String[] args) throws FileNotFoundException {
		
		String input = null;
		String output = null;
		int shrink = 0;
		
		/*--- Read arguments ---*/
		
		for(int i = 0; i<args.length; i++){
			
			if(args[i].equals("-h") || args[i].equals("--help")){
				System.out.println("Program to extract tumor progression from a mutational matrix,");
				System.out.println("output is in dot format (https://en.wikipedia.org/wiki/DOT_(graph_description_language)),");
				System.out.println("for other information see: https://github.com/redsnic/tumorEvolutionWithMarkovChains");
				System.out.println("Usage: input output [flags]");
				System.out.println("Flags:");
				System.out.println("-h, --help : show this help message");
				System.out.println("-s n, --shrink n : reduces the number of considered genes to n");
				System.out.println("-i in, --input in: set in as input file, by default input is read from STDIN");
				System.out.println("-o out, --output out: set out as output file, by default output is written on STDOUT");
				return;
			}
			
			else if(args[i].equals("-s") || args[i].equals("--shrink")){
				i++;
				if (shrink == 0) {
					try {
						shrink = Integer.parseInt(args[i]);
					} catch (Exception e) {
						System.out.println("Error, shrink parameter n must be a positive number");
						System.out.println("use -h or --help for more information");
						return;
					}
					if (shrink <= 0) {
						System.out.println("Error, shrink parameter n must be a positive number");
						System.out.println("use -h or --help for more information");
						return;
					}
				} else{
					System.out.println("Error, number of considered genes set multiple times");
					System.out.println("use -h or --help for more information");
					return;
				}
			} else if(args[i].equals("-i") || args[i].equals("--input")){
				i++;
				if(input == null){
					input = args[i];
					File f = new File(input);
					if (!f.exists()) {
						System.out.println("Error, can not open input file: FILE NOT FOUND");
						System.out.println("use -h or --help for more information");
						return;
					} else if (f.isDirectory()) {
						System.out.println("Error, can not open input file: FILE IS A DIRECTORY");
						System.out.println("use -h or --help for more information");
						return;
					}
				}else{
					System.out.println("Error, input file set multiple times");
					System.out.println("use -h or --help for more information");
					return;
				}
			}else if(args[i].equals("-o") || args[i].equals("--output")){
				i++;
				if(output == null){
					output = args[i];
					File f = new File(input);
					if (f.isDirectory()) {
						System.out.println("Error, can not open output file: FILE IS A DIRECTORY");
						System.out.println("use -h or --help for more information");
						return;
					}
				}else{
					System.out.println("Error, output file set multiple times");
					System.out.println("use -h or --help for more information");
					return;
				}
			} else {
					System.out.println("Error, invalid argument: " + args[i]);
					System.out.println("use -h or --help for more information");
					return;	
			}
		}
		
		if(input == null){
			input = "STDIN";
		}
		
		if(output == null){
			output = "STDOUT";
		}
		
		/*-- read --*/
		
		Dataset D = new Dataset();
		if(input.toUpperCase() == "STDIN"){
			D.read();
		}else{
			D.read(input);
		}
		D.compact();
		
		/*-- execute --*/
		
		GenotypeGraphAllowingMultipleMutations grp;
		if(shrink == 0){
			grp = new GenotypeGraphAllowingMultipleMutations(D);
		}else{
			D.shrink(shrink);
			grp = new GenotypeGraphAllowingMultipleMutations(D);
		}
		
		/*-- output --*/
		
		if(output.toUpperCase() == "STDOUT"){
			grp.toDot(System.out);
		}else{
			PrintStream stream = new PrintStream(output);
			grp.toDot(stream);
			stream.close();
		}
		
	}

}
