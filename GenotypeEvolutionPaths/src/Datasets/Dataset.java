package Datasets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.StringTokenizer;

import Utils.Pair;
import Utils.Quadruplet;
import Utils.Triplet;
import Utils.Utils;

/**
 * Class to manage the input data set for the analysis
 * Input format:
 * numberOfSamples  numberOfGenes
 * Gene_1 Gene_2 ... Gene_n
 * SampleName_1 ValueOf_1(Gene_1) ValueOf_1(Gene_2) ... ValueOf_1(Gene_n)
 * SampleName_2 ValueOf_2(Gene_1) ValueOf_2(Gene_2) ... ValueOf_2(Gene_n)
 * ...
 * SampleName_n ValueOf_n(Gene_1) ValueOf_n(Gene_2) ... ValueOf_n(Gene_n)
 *
 * @author rossi
 *
 */
public class Dataset {
	/**
	 * data set can read input only once
	 */
	private boolean initialized = false;
	/**
	 * if data set must be compacted (do not keep copies of the same genotype) or not
	 */
	private boolean compacted = false;
	/**
	 * number of genotypes present in the data set
	 */
	private int numberOfEntries = 0;
	/**
	 * Genes names
	 */
	private ArrayList<String> labels = new ArrayList<String>();
	/**
	 * genotypes present in the data set
	 */
	private ArrayList<boolean[]>  genotypes = new ArrayList<boolean[]>();
	/**
	 * Manages sample IDs
	 */
	private ArrayList<ArrayList<String>> samples = new ArrayList<ArrayList<String>>();
	/**
	 * number of times a given genotype is present in the dataset (always 1 if data set is not compacted)
	 */
	private ArrayList<Integer> frequencies =  new ArrayList<Integer>();
	
	public Dataset(){
	}
	
	/**
	 * read data set from STDIN
	 * @throws DatasetAlreadyInitializedException if the dataset was already initialized
	 */
	public void read() throws DatasetAlreadyInitializedException{
		if(initialized){
			throw new DatasetAlreadyInitializedException();
		}
		Scanner in = new Scanner(System.in);
		readData(in);
		in.close();
		initialized=true;
		filterClonalGenotypes();
	}
	
	/**
	 * read data set from a file
	 * @param path to input file
	 * @throws DatasetAlreadyInitializedException if the dataset was already initialized
	 */
	public void read(String path) throws DatasetAlreadyInitializedException{
		if(initialized){
			throw new DatasetAlreadyInitializedException();
		}
		try {
			Scanner in = new Scanner(new File(path));
			readData(in);
			in.close();
			initialized=true;
			filterClonalGenotypes();
		} catch (FileNotFoundException e) {
			System.err.println("Error in reading input file!");
			e.printStackTrace();
		}
	}
	
	/**
	 * read data set from STDIN in CAPRI format
	 * @throws DatasetAlreadyInitializedException if the dataset was already initialized
	 */
	public void readCAPRI() throws DatasetAlreadyInitializedException{
		if(initialized){
			throw new DatasetAlreadyInitializedException();
		}
		Scanner in = new Scanner(System.in);
		readDataCAPRI(in);
		in.close();
		initialized=true;
		filterClonalGenotypes();
	}
	
	/**
	 * read data set from a file in CAPRI format
	 * @param path to input file
	 * @throws DatasetAlreadyInitializedException if the dataset was already initialized
	 */
	public void readCAPRI(String path) throws DatasetAlreadyInitializedException{
		if(initialized){
			throw new DatasetAlreadyInitializedException();
		}
		try {
			Scanner in = new Scanner(new File(path));
			readDataCAPRI(in);
			in.close();
			initialized=true;
			filterClonalGenotypes();
		} catch (FileNotFoundException e) {
			System.err.println("Error in reading input file!");
			e.printStackTrace();
		}
	}
	
	/**
	 * effectively reads a data set from a stream
	 * @param in input stream
	 */
	private void readData(Scanner in) {
		int nSamples = in.nextInt();
		int nGenes   = in.nextInt();
		
		for(int i = 0; i<nGenes; i++){
			this.labels.add(in.next());
		}
		
		for(int i = 0; i<nSamples; i++){
			ArrayList<String> sample = new ArrayList<String>();
			sample.add(in.next()); // sample name
			samples.add(sample);
			boolean[] genotype = new boolean[nGenes];
			for(int j=0; j<nGenes; j++){
				genotype[j] = (in.nextInt()==1)?true:false;
			}
			genotypes.add(genotype);
			frequencies.add(1);
		}
		
		numberOfEntries = genotypes.size();
	}
	/**
	
	 * effectively reads a data set from a stream
	 * @param in input stream
	 */ 
	private void readDataCAPRI(Scanner in) {
		
		StringTokenizer tok = new StringTokenizer(in.nextLine());
		tok.nextToken(); // first element is not important
		
		int nGenes   = 0;
		
		while(tok.hasMoreTokens()){
			this.labels.add(tok.nextToken());
			nGenes++;
		}
		
		
		
		while(in.hasNext()){
			
			StringTokenizer line = new StringTokenizer(in.nextLine());			
			ArrayList<String> sample = new ArrayList<String>();
			sample.add(line.nextToken()); // sample name
			samples.add(sample);
			boolean[] genotype = new boolean[nGenes];
			for(int j=0; j<nGenes; j++){
				genotype[j] = (Integer.parseInt(line.nextToken())==1)?true:false;
			}
			genotypes.add(genotype);
			frequencies.add(1);
		}
		
		numberOfEntries = genotypes.size();
	}

	/**
	 * Initialize this data set manually 
	 * @param labels for genes
	 * @param data   boolean matrix representing genotypes
	 * @throws DatasetAlreadyInitializedException if the dataset was already initialized
	 */
	public void read(String[] labels, boolean[][] data) throws DatasetAlreadyInitializedException{
		if(initialized){
			throw new DatasetAlreadyInitializedException();
		}
		for(String l : labels){
			this.labels.add(l);
		}
		int i = 0;
		for(boolean[] genotype : data){
			i++;
			genotypes.add(genotype);
			frequencies.add(1);
			ArrayList<String> s = new ArrayList<String>();
			s.add("s_" + i);
			samples.add(s);
		}
		numberOfEntries = genotypes.size();
		initialized = true;
		filterClonalGenotypes();
	}
	
	/**
	 * Reduces entries of the data set by grouping equal genotypes and
	 * memorizing their frequency of occurrence 
	 */
	public void compact() {

		ArrayList<Pair<boolean[], Integer>> mutationCounts = countMutations();
		/* genotype, mutationCounts, observed frequency, sample labels */
		ArrayList<Quadruplet<boolean[], Integer, Integer, ArrayList<String>>> mutationCountAndFrequency = new ArrayList<Quadruplet<boolean[], Integer, Integer,  ArrayList<String>>>();
		for (int i = 0; i < mutationCounts.size(); i++) {
			mutationCountAndFrequency.add(new Quadruplet<boolean[], Integer, Integer, ArrayList<String>>(mutationCounts.get(i).fst(),
					mutationCounts.get(i).snd(), this.frequencies.get(i),
					this.samples.get(i)));
		}
		Collections.sort(mutationCountAndFrequency, (a, b) -> Utils.genotypeBinaryValueCompare(a.fst(), b.fst()));
		Collections.sort(mutationCountAndFrequency, (a, b) -> (a.snd() > b.snd()) ? 1 : ((a.snd().equals(b.snd())) ? 0 : -1));
		collapse(mutationCountAndFrequency);

		compacted = true;

	}

	/**
	 * Eliminates non mutated genotypes present in the data set
	 */
	private void filterClonalGenotypes() {
		ArrayList<boolean[]> cleaned = new ArrayList<boolean[]>();
		ArrayList<Integer> cleanedFrequencies = new ArrayList<Integer>();
		
		for(int i=0; i<genotypes.size(); i++){
			if(Utils.sumBool(genotypes.get(i))!=0){
				cleaned.add(genotypes.get(i));
				cleanedFrequencies.add(frequencies.get(i));
			}else{
				this.numberOfEntries-=frequencies.get(i);
			}
		}
		
		this.genotypes=cleaned;
	}
	
	/**
	 * Keeps in the dataset only the top mutated genes
	 * @param thres number of genes to be kept (increased in case of parity of occurrences
	 * @throws InvalidThresholdException if thres is not a positive number 
	 */
	public void shrink(int thres) throws InvalidThresholdException{
		if(thres<=0){                     // invalid threshold
			throw new InvalidThresholdException(thres);
		}
		if(thres>=this.labels.size()){ // nothing to do
			return;
		}
		
		/* count genes frequency */
		
		int[] counts = new int[this.getNumberOfGenes()];
		for(int i=0; i<this.genotypes.size(); i++){   
			for(int j=0; j<this.genotypes.get(i).length; j++){
				counts[j]+=this.genotypes.get(i)[j]?1:0;
			}
		}
		
		/* <position, number of mutations, label> */
		ArrayList<Triplet<Integer,Integer,String>> genes = new ArrayList<Triplet<Integer,Integer,String>>();
		for(int i = 0; i<this.labels.size(); i++){
			genes.add(new Triplet<Integer,Integer,String>(i, counts[i], labels.get(i)));
		}
		
		/* sort genes (descending order by number of mutations) */
		
		Collections.sort(genes, (a,b) -> a.snd()>b.snd()?-1:a.snd()==b.snd()?0:1);
		
		/* keep thres genes + all the genes with the same amount of mutations of any of the kept ones */
		
		int trueThres = thres;
		int lastNoOfMutations = genes.get(trueThres-1).snd();
		for(int i = thres; i<genes.size(); i++){
			if(genes.get(i).snd() == lastNoOfMutations){
				trueThres++;
			}else{
				break;
			}
		}
		
		ArrayList<Triplet<Integer,Integer,String>> selectedGenes = new ArrayList<Triplet<Integer,Integer,String>>();
		
		for(int i = 0; i<trueThres; i++){
			selectedGenes.add(genes.get(i));
		}
		
		/* update data set */
		
		reconstructDataset(selectedGenes);
		
	}
	
	/**
	 * reconstruct data set after having reduced the number of considered genes 
	 * @param selectedGenes genotypes
	 */
	private void reconstructDataset(ArrayList<Triplet<Integer, Integer, String>> selectedGenes) {
		labels = new ArrayList<String>();
		ArrayList<boolean[]> newGenotypes = new ArrayList<boolean[]>();
		
		for(boolean[] g : this.genotypes){
			boolean[] newGenotype = new boolean[selectedGenes.size()];
			for(int i = 0; i<selectedGenes.size(); i++){
				newGenotype[i] = g[selectedGenes.get(i).fst()];
			}
			newGenotypes.add(newGenotype);
		}
		
		for(int i = 0; i<selectedGenes.size(); i++){
			labels.add(selectedGenes.get(i).thr());
		}
		
		this.genotypes = newGenotypes;
		
		filterClonalGenotypes();
		if(compacted){
			compact();
		}
	}
	
	/**
	 * Removes the copies of the same genotype and increases the frequency of that genotype accordingly
	 * @param mutationCountAndFrequency genotypes REQUIRE sorted by binary number and number of mutations
 	 */
	private void collapse(ArrayList<Quadruplet<boolean[], Integer, Integer, ArrayList<String>>> mutationCountAndFrequency) {
		/* compact: sum frequencies of equal genotypes and keep a single entry in the dataset */
		boolean[] last = null;
		ArrayList<Triplet<boolean[], Integer, ArrayList<String>>> compacted = new ArrayList<Triplet<boolean[], Integer, ArrayList<String>>>();
		ArrayList<String> sampleTemp = new ArrayList<String>();
		for(int i=0; i<mutationCountAndFrequency.size(); i++){
			if(last == null || Utils.genotypeBinaryValueCompare(mutationCountAndFrequency.get(i).fst(), last) != 0){
			   last = mutationCountAndFrequency.get(i).fst();
			   sampleTemp = new ArrayList<String>();
			   sampleTemp.addAll(mutationCountAndFrequency.get(i).four());
			   compacted.add(new Triplet<boolean[], Integer, ArrayList<String>>(last , mutationCountAndFrequency.get(i).thr(), sampleTemp));
			}else{
				compacted.get(compacted.size()-1).thr().addAll(mutationCountAndFrequency.get(i).four());
				compacted.set(compacted.size()-1, new Triplet<boolean[], Integer, ArrayList<String>>(last , compacted.get(compacted.size()-1).snd()+mutationCountAndFrequency.get(i).thr(),
						compacted.get(compacted.size()-1).thr()));
			}
		}
		/* apply changes */
		this.genotypes = new ArrayList<boolean[]>();
		this.frequencies = new ArrayList<Integer>();
		this.samples = new ArrayList<ArrayList<String>>();
		for(Triplet<boolean[], Integer, ArrayList<String>> p : compacted){
			this.genotypes.add(p.fst());
			this.frequencies.add(p.snd());
			this.samples.add(p.thr());
		}
	}

	/**
	 * counts number of mutations for each genotype
	 * @return genotypes associated with the number of mutations they contain
	 */
	private ArrayList<Pair<boolean[], Integer>> countMutations() {
		ArrayList<Pair<boolean[], Integer>> ret = new ArrayList<Pair<boolean[], Integer>>();
		for( boolean[] genotype : genotypes ){
			ret.add(new Pair<boolean[], Integer>(genotype, Utils.sumBool(genotype)));
		}
		return ret;
	}

	/**
	 * @param  i genotype position 
	 * @return observed probability of that genotype
	 * @throws NotAlreadyCompactedException when this is not compacted 
	 */
	public double normalizedFrequencyOf(int i) throws NotAlreadyCompactedException{
		if(!compacted){
			throw new NotAlreadyCompactedException();
		}
		return frequencies.get(i)/(double) numberOfEntries;
	}
	
	/**
	 * @param  i genotype position 
	 * @return observed frequency of that genotype
	 * @throws NotAlreadyCompactedException when this is not compacted 
	 */
	public int frequencyOf(int i){
		if(!compacted){
			throw new NotAlreadyCompactedException();
		}
		return frequencies.get(i);
	}
	
	/**
	 * @param i position
	 * @return genotype at position 'i'
	 */
	public boolean[] get(int i){
		return genotypes.get(i);
	}
	
	/**
	 * @return the number of genotypes in this dataset
	 */
	public int getNumberOfGenotypes(){
		return (int) Utils.sumInt(this.frequencies);
	}
	
	@Override
	public String toString(){
		StringBuilder ret = new StringBuilder();
		ret.append(labels.toString() + "\n");
		for(int i=0; i<genotypes.size(); i++){
			if(!compacted){
				ret.append(Utils.BoolVecToString(genotypes.get(i)) + "\n");
			}else{
				ret.append("[ " + Utils.BoolVecToString(genotypes.get(i)) + "] " + frequencyOf(i) + " (" + normalizedFrequencyOf(i) + ") " +"\n");
			}
		}
		return ret.toString();
	}
	
	/**
	 * Translates a genotype in its relative set of genes labels
	 * @param genotype
	 * @return a set of labels
	 */
	public ArrayList<String> translate(boolean[] genotype){
		assert (genotype.length==labels.size()): "Incompatible genotype";
		ArrayList<String> ret = new ArrayList<String>();
		for(int i=0; i<labels.size(); i++){
			if(genotype[i]){
				ret.add(labels.get(i));
			}
		}
		return ret;
	}

	/**
	 * @return the number of genes considered in this dataset
	 */
	public int getNumberOfGenes() {
		return this.labels.size();
	}
	
	/**
	 * Print this data set in BML format
	 */
	public void printBMLFormat(PrintStream out){
		out.println(this.getNumberOfGenotypes() + " " + this.getNumberOfGenes());
		for(String s : labels){
			out.print(s + " ");
		}
		out.println();
		for(int i = 0; i<this.genotypes.size(); i++){
			for(int j = 0 ; j<this.frequencyOf(i); j++){
				out.println(samples.get(i).get(j) + " " + Utils.BoolVecToString(this.genotypes.get(i)));
			}
		}
	}
	
	/**
	 * Print this data set in TRONCO format
	 */
	public void printTRONCOFormat(PrintStream out){
		out.print("s\\g ");
		for(String s : labels){
			out.print(s + " ");
		}
		out.println();
		for(int i = 0; i<this.genotypes.size(); i++){
			for(int j = 0 ; j<this.frequencyOf(i); j++){
				out.println(samples.get(i).get(j) + " " + Utils.BoolVecToString(this.genotypes.get(i)));
			}
		}
	}

	/**
	 * @return number of different genotypes after compacting 
	 */
	public int getNumberOfDifferentGenotypes() {
		return this.genotypes.size();
	}

	/**
	 * @param i position
	 * @return the samples names of the i-th elements of the dataset  
	 */
	public ArrayList<String> getSamples(int i) {
		return samples.get(i);
	}
	
	



}
