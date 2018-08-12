package Main;

import Datasets.Dataset;
import GenotypeGraphsv2.GenotypeGraphAllowingMultipleMutations;

public class RunFromSTDIN {
	
	
	public static void main(String[] args){
		Dataset D = new Dataset();
		D.read();
		D.compact();
		D.shrink(10);
		GenotypeGraphAllowingMultipleMutations grp = new GenotypeGraphAllowingMultipleMutations(D);
		grp.toDot(System.out);
	}

}
