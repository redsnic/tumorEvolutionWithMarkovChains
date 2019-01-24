package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

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
		boolean capri = false;
		int shrink = 0;
		boolean printSamples = true;
		boolean printGenotypes = true;
		
		/*--- Read arguments ---*/
		
		for(int i = 0; i<args.length; i++){
			
			if(args[i].equals("-h") || args[i].equals("--help")){
				System.out.println("Program to extract tumor progression from a mutational matrix,");
				System.out.println("output is in dot format (https://en.wikipedia.org/wiki/DOT_(graph_description_language)),");
				System.out.println("for other information see: https://github.com/redsnic/tumorEvolutionWithMarkovChains");
				System.out.println("Usage: -i input -o output [flags]");
				System.out.println("Flags:");
				System.out.println("-h, --help : show this help message");
				System.out.println("-s n, --shrink n : reduces the number of considered genes to n");
				System.out.println("-i in, --input in: set in as input file, by default input is read from STDIN");
				System.out.println("-o out, --output out: set out as output file, by default output is written on STDOUT");
				System.out.println("-c, --capri use CAPRI format for input (default BML format)");
				System.out.println("--no-genotypes, do not print extended genotypes");
				System.out.println("--no-samples, do not print samples names");
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
					File f = new File(output);
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
			}else if(args[i].equals("-c") || args[i].equals("--capri")){
				capri = true;
			}else if(args[i].equals("--no-genotypes")){
				printGenotypes = false;
			}else if(args[i].equals("--no-samples")){
				printSamples = false;
			}else {
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
		if(input.toUpperCase() == "STDIN" && !capri){
			D.read();
		}else if(!capri){
			D.read(input);
		}else if(input.toUpperCase() == "STDIN"){
			D.readCAPRI();
		}else{
			D.readCAPRI(input);
		}
		D.compact();
		
		/*-- execute --*/
		
		GenotypeGraphAllowingMultipleMutations grp;
		if(shrink == 0){
			grp = new GenotypeGraphAllowingMultipleMutations(D, printGenotypes, printSamples);
		}else{
			D.shrink(shrink);
			grp = new GenotypeGraphAllowingMultipleMutations(D, printGenotypes, printSamples);
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
