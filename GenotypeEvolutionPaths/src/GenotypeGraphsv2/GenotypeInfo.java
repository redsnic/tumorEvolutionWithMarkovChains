package GenotypeGraphsv2;

import Datasets.Dataset;
import Utils.Utils;

/**
 * Class to represent the information associated with a genotype
 * 
 * @author rossi
 *
 */
public class GenotypeInfo {

	private boolean[] genotype;
	private int numberOfMutations;
	private Dataset dataset;
	private int position;
	private double observedProbability;
	
	/**
	 * Constructor from dataset
	 * @param genotype
	 * @param dataset REQUIRE compacted
	 * @param position in the dataset
	 */
	public GenotypeInfo(boolean[] genotype, Dataset dataset, int position){
		this.genotype = genotype;
		this.numberOfMutations = Utils.sumBool(genotype);
		this.dataset=dataset;
		this.position = position;
		this.observedProbability = dataset.normalizedFrequencyOf(position);
	}
	
	/**
	 * Manual constructor 
	 * @param genotype
	 * @param dataset (only used for the genotype translation) REQUIRE compacted
	 * @param observedProbability
	 */
	public GenotypeInfo(boolean[] genotype, Dataset dataset, double observedProbability){
		this.genotype = genotype;
		this.numberOfMutations = Utils.sumBool(genotype);
		this.dataset=dataset;
		this.position = -1;
		this.observedProbability = observedProbability;
	}
	
	public double getObservedProbability(){
		return this.observedProbability;
	}
	
	public boolean[] get(){
		return this.genotype;
	}
	
	public int getNumberOfMutations(){
		return this.numberOfMutations;
	}
	
	@Override
	public String toString(){
		return dataset.translate(genotype).toString() + ", " + String.format("%.3f", this.observedProbability);
	}
	
}
