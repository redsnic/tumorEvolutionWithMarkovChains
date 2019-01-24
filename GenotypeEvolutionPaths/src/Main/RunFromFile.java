package Main;

import Datasets.Dataset;
import GenotypeGraphsv2.GenotypeGraphAllowingMultipleMutations;

public class RunFromFile {

	public static void main (String[] args){
		Dataset D = new Dataset();
		D.readCAPRI("/home/redsnic/Scrivania/test.CAPRI");
		D.compact();
		GenotypeGraphAllowingMultipleMutations gnt = new GenotypeGraphAllowingMultipleMutations(D);
		gnt.toDot(System.out);
	}
	
	
}
