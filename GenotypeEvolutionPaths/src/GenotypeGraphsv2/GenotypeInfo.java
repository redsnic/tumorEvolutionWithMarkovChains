package GenotypeGraphsv2;

import java.util.ArrayList;

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
	private ArrayList<String> samples;
	private int numberOfMutations;
	private Dataset dataset;
	private int position;
	private double observedProbability;
	private boolean printSamples = true;
	private boolean printGenotypes = true;
	
	/**
	 * Constructor from dataset
	 * @param genotype
	 * @param dataset REQUIRE compacted
	 * @param position in the dataset
	 */
	public GenotypeInfo(boolean[] genotype, Dataset dataset, int position, ArrayList<String> samples){
		this.genotype = genotype;
		this.numberOfMutations = Utils.sumBool(genotype);
		this.dataset=dataset;
		this.position = position;
		this.observedProbability = dataset.normalizedFrequencyOf(position);
		this.samples = samples;
	}
	
	/**
	 * Manual constructor 
	 * @param genotype
	 * @param dataset (only used for the genotype translation) REQUIRE compacted
	 * @param observedProbability
	 * @param printSamples 
	 * @param printGenotypes 
	 */
	public GenotypeInfo(boolean[] genotype, Dataset dataset, double observedProbability, ArrayList<String> samples){
		this.genotype = genotype;
		this.numberOfMutations = Utils.sumBool(genotype);
		this.dataset=dataset;
		this.position = -1;
		this.observedProbability = observedProbability;
		this.samples = samples;
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
	
	/**
	 * Set printing information
	 * @param genotypes true to enable genotype printing (default)
	 * @param samples   true to enable samples printing  (default)
	 */
	public void setPrintPreferences(boolean genotypes, boolean samples){
		this.printGenotypes = genotypes;
		this.printSamples = samples;
	}
	
	@Override
	public String toString(){
		String s = "";
		if(printGenotypes){
			s += dataset.translate(genotype).toString() + ", " + String.format("%.3f", this.observedProbability) + "\n";
		}if(printSamples){
			s += "Samples: " + this.samples + "\n";
		}
		return s;
	}
	
	public String translate(){
		return dataset.translate(genotype).toString();
	}
	
}
