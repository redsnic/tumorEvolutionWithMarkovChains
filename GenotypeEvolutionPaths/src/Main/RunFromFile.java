package Main;

import Datasets.Dataset;
import GenotypeGraphsv2.GenotypeGraphAllowingMultipleMutations;

public class RunFromFile {

	public static void main (String[] args){
		Dataset D = new Dataset();
		D.read("/home/rossi/Scrivania/Esempio/dataset");
		D.compact();
		GenotypeGraphAllowingMultipleMutations gnt = new GenotypeGraphAllowingMultipleMutations(D);
		gnt.toDot(System.out);
	}
	
	
}
