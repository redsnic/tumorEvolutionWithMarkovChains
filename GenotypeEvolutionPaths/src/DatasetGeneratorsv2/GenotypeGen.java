package DatasetGeneratorsv2;

import Utils.Utils;

public class GenotypeGen {

	private boolean[] genotype;
	private int numberOfMutations;

	public GenotypeGen(boolean[] genotype){
		this.genotype = genotype;
		this.numberOfMutations = Utils.sumBool(genotype);
	}
	
	public boolean[] get(){
		return this.genotype;
	}
	
	public int getNumberOfMutations(){
		return this.numberOfMutations;
	}
	
	@Override
	public String toString(){
		return Utils.BoolVecToString(this.genotype);
	}
	
}
