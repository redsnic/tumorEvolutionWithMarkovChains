package Main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import DatasetGeneratorsv2.GenotypeGen;
import DatasetGeneratorsv2.GraphDatasetGeneratorAllowingMultipleMutations;
import Datasets.Dataset;
import GenotypeGraphsv2.GenotypeGraphAllowingMultipleMutations;
import Graphs.Node;

public class SimulationsFromKnownGenerator {
	
	/**
	 * Whith different self loop probabilities
	 * @return
	 */
	private static GraphDatasetGeneratorAllowingMultipleMutations generator1(){
		String[] labels = {"A","B","C","D"};
		GraphDatasetGeneratorAllowingMultipleMutations gen = new GraphDatasetGeneratorAllowingMultipleMutations(labels);
		
		boolean[] a = {true,false,false, false};
		boolean[] b = {false,true,false, false};
		boolean[] ac = {true,false,true, false};
		boolean[] ad = {true,false,false, true};
		boolean[] ab = {true,true,false, false};
		boolean[] bd = {false,true,false, true};
		boolean[] abd = {true,true,false, true};
		
		Node<GenotypeGen> nroot = gen.getRoot();
		Node<GenotypeGen> na    = gen.add(a);
		Node<GenotypeGen> nb    = gen.add(b);
		Node<GenotypeGen> nac   = gen.add(ac);
		Node<GenotypeGen> nad   = gen.add(ad);
		Node<GenotypeGen> nab   = gen.add(ab);
		Node<GenotypeGen> nbd   = gen.add(bd);
		Node<GenotypeGen> nabd  = gen.add(abd);
		
		gen.link(nroot, nroot, 0.9);
		gen.link(nroot, nb, 0.05);
		gen.link(nroot, na, 0.05);
		gen.link(nb, nb, 0.8);
		gen.link(nb, nbd, 0.10);
		gen.link(nb, nab, 0.10);
		gen.link(na, na, 0.6);
		gen.link(na, nab, 0.10);
		gen.link(na, nad, 0.10);
		gen.link(na, nac, 0.2);
		gen.link(nbd, nbd, 0.99);
		gen.link(nbd, nabd, 0.01);
		gen.link(nab, nab, 0.9);
		gen.link(nab, nabd, 0.1);
		gen.link(nad, nad, 0.4);
		gen.link(nad, nabd, 0.6);
		gen.link(nac, nac, 1);
		gen.link(nabd, nabd, 1);
		
		return gen;
		
	}
	
	/**
	 * Whith different self loop probabilities
	 * @return
	 */
	private static GraphDatasetGeneratorAllowingMultipleMutations generatorTemp(){
		String[] labels = {"A","B","C","D"};
		GraphDatasetGeneratorAllowingMultipleMutations gen = new GraphDatasetGeneratorAllowingMultipleMutations(labels);
		
		boolean[] a    ={true,false, false, false};
		boolean[] b    ={false,true, false, false};
		boolean[] ab   ={true,true, false, false};
		boolean[] abd = {true,true, false, true};
		
		Node<GenotypeGen> nroot = gen.getRoot();
		Node<GenotypeGen> na    = gen.add(a);
		Node<GenotypeGen> nb    = gen.add(b);
		Node<GenotypeGen> nab   = gen.add(ab);
		Node<GenotypeGen> nabd  = gen.add(abd);
		
		gen.link(nroot, nroot, 0.729);
		gen.link(nroot, na, 0.104);
		gen.link(nroot, nb, 0.167);
		gen.link(na, nab, 0.167);
		gen.link(na, na, 0.83);
		gen.link(nb, nab, 0.5);
		gen.link(nb, nb, 0.5);
		gen.link(nab, nab, 0.75);
		gen.link(nab, nabd, 0.25);
		gen.link(nabd, nabd, 1);
		return gen;
		
	}
	
	/**
	 * Equal self loop probability
	 * @return
	 */
	private static GraphDatasetGeneratorAllowingMultipleMutations generator2(){
		String[] labels = {"A","B","C","D"};
		GraphDatasetGeneratorAllowingMultipleMutations gen = new GraphDatasetGeneratorAllowingMultipleMutations(labels);
		
		boolean[] a = {true,false,false, false};
		boolean[] b = {false,true,false, false};
		boolean[] ac = {true,false,true, false};
		boolean[] ad = {true,false,false, true};
		boolean[] ab = {true,true,false, false};
		boolean[] bd = {false,true,false, true};
		boolean[] abd = {true,true,false, true};
		
		Node<GenotypeGen> nroot = gen.getRoot();
		Node<GenotypeGen> na    = gen.add(a);
		Node<GenotypeGen> nb    = gen.add(b);
		Node<GenotypeGen> nac   = gen.add(ac);
		Node<GenotypeGen> nad   = gen.add(ad);
		Node<GenotypeGen> nab   = gen.add(ab);
		Node<GenotypeGen> nbd   = gen.add(bd);
		Node<GenotypeGen> nabd  = gen.add(abd);
		
		gen.link(nroot, nroot, 0.9);
		gen.link(nroot, nb, 0.05);
		gen.link(nroot, na, 0.05);
		gen.link(nb, nb, 0.9);
		gen.link(nb, nbd, 0.075);
		gen.link(nb, nab, 0.025);
		gen.link(na, na, 0.9);
		gen.link(na, nab, 0.025);
		gen.link(na, nad, 0.025);
		gen.link(na, nac, 0.05);
		gen.link(nbd, nbd, 0.9);
		gen.link(nbd, nabd, 0.1);
		gen.link(nab, nab, 0.9);
		gen.link(nab, nabd, 0.1);
		gen.link(nad, nad, 0.9);
		gen.link(nad, nabd, 0.1);
		gen.link(nac, nac, 1);
		gen.link(nabd, nabd, 1);
		
		return gen;
		
	}
	
	/**
	 * Tree
	 */
	private static GraphDatasetGeneratorAllowingMultipleMutations generator3(){
		String[] labels = {"A","B","C","D","E","F","G"};
		GraphDatasetGeneratorAllowingMultipleMutations gen = new GraphDatasetGeneratorAllowingMultipleMutations(labels);
		
		boolean[] a =   {true, false,false, false, false, false, false};
		boolean[] ab =  {true,true,false, false, false, false, false};
		boolean[] abc = {true,true,true, false, false, false, false};
		boolean[] abd = {true,true,false, true, false, false, false};
		boolean[] ae =  {true, false,false, false, true, false, false};
		boolean[] aef =  {true, false,false, false, true, true, false};
		boolean[] aeg =  {true, false,false, false, true, false, true};
		
		Node<GenotypeGen> nroot = gen.getRoot();
		Node<GenotypeGen> na    = gen.add(a);
		Node<GenotypeGen> nab    = gen.add(ab);
		Node<GenotypeGen> nabc   = gen.add(abc);
		Node<GenotypeGen> nabd   = gen.add(abd);
		Node<GenotypeGen> nae   = gen.add(ae);
		Node<GenotypeGen> naef   = gen.add(aef);
		Node<GenotypeGen> naeg  = gen.add(aeg);
		
		gen.link(nroot, nroot, 0.9);
		gen.link(nroot, na, 0.1);
		gen.link(na, na, 0.1);
		gen.link(na, nab, 0.45);
		gen.link(na, nae, 0.45);
		gen.link(nab, nab, 0.9);
		gen.link(nae, nae, 0.9);
		gen.link(nab, nabc, 0.05);
		gen.link(nab, nabd, 0.05);
		gen.link(nae, naef, 0.05);
		gen.link(nae, naeg, 0.05);
		
		gen.link(nabc, nabc, 1);
		gen.link(nabd, nabd, 1);
		gen.link(naef, naef, 1);
		gen.link(naeg, naeg, 1);
		
		return gen;
		
	}
	
	/**
	 * bigger
	 */
	private static GraphDatasetGeneratorAllowingMultipleMutations generator4(){
		String[] labels = {"A","B","C","D","E","F","G", "H"};
		GraphDatasetGeneratorAllowingMultipleMutations gen = new GraphDatasetGeneratorAllowingMultipleMutations(labels);
		
		boolean[] _a      =  {true,  false, false, false, false, false, false, false};
		boolean[] _b      =  {false, true,  false, false, false, false, false, false};
		boolean[] _ab     =  {true,  true,  false, false, false, false, false, false};
		boolean[] _abe    =  {true,  true,  false, false, true,  false, false, false};
		boolean[] _abcd   =  {true,  true,  true,  true,  false, false, false, false};
		boolean[] _abcde  =  {true,  true,  true,  true,  true,  false, false, false};
		boolean[] _abcdf  =  {true,  true,  true,  true,  false, true,  false, false};
		boolean[] _abcdef =  {true,  true,  true,  true,  true, true,  false, false};
		boolean[] _abce   =  {true,  true,  true,  false, true,  false, false, false};
		boolean[] _abcef  =  {true,  true,  true,  false, true,  true,  false, false};
		boolean[] _abcefh =  {true,  true,  true,  false, true,  true,  false, true };
		boolean[] _abcefg =  {true,  true,  true,  false, true,  true,  true,  false};
		
		Node<GenotypeGen> root = gen.getRoot();
		Node<GenotypeGen> a      = gen.add(_a);
		Node<GenotypeGen> b      = gen.add(_b);
		Node<GenotypeGen> ab     = gen.add(_ab);
		Node<GenotypeGen> abe    = gen.add(_abe);
		Node<GenotypeGen> abcd   = gen.add(_abcd);
		Node<GenotypeGen> abcde  = gen.add(_abcde);
		Node<GenotypeGen> abcdf  = gen.add(_abcdf);
		Node<GenotypeGen> abcdef = gen.add(_abcdef);
		Node<GenotypeGen> abce   = gen.add(_abce);
		Node<GenotypeGen> abcef  = gen.add(_abcef);
		Node<GenotypeGen> abcefh = gen.add(_abcefh);
		Node<GenotypeGen> abcefg = gen.add(_abcefg);
		
		
		
		gen.link(root, root, 0.9);
		gen.link(root, a, 0.05);
		gen.link(root, b, 0.05);
		gen.link(a, a, 0.9);
		gen.link(a, ab, 0.1);
		gen.link(b, b, 0.9);
		gen.link(b, ab, 0.1);
		gen.link(ab, ab, 0.9);
		gen.link(ab, abe, 0.05);
		gen.link(ab, abcd, 0.5);
		gen.link(abe, abe, 0.9);
		gen.link(abe, abce, 0.1);
		gen.link(abce, abce, 0.9);
		gen.link(abce, abcde, 0.05);
		gen.link(abce, abcef, 0.05);
		gen.link(abcd, abcd, 0.9);
		gen.link(abcd, abcde, 0.05);
		gen.link(abcd, abcdf, 0.05);
		gen.link(abcde, abcde, 0.9);
		gen.link(abcde, abcdef, 0.1);
		gen.link(abcdef, abcdef, 0.9);
		gen.link(abcef, abcef, 0.9);
		gen.link(abcef, abcefh, 0.033);
		gen.link(abcef, abcefg, 0.033);
		gen.link(abcdf, abcdef, 0.1);
		gen.link(abcdf, abcdf, 0.9);
		gen.link(abcefg, abcefg, 1);
		gen.link(abcefh, abcefh, 1);
		gen.link(abcef, abcdef, 0.033);
		return gen;
		
	}
	
	/**
	 * bigger (not all equal)
	 */
	private static GraphDatasetGeneratorAllowingMultipleMutations generator5(){
		String[] labels = {"A","B","C","D","E","F","G", "H"};
		GraphDatasetGeneratorAllowingMultipleMutations gen = new GraphDatasetGeneratorAllowingMultipleMutations(labels);
		
		boolean[] _a      =  {true,  false, false, false, false, false, false, false};
		boolean[] _b      =  {false, true,  false, false, false, false, false, false};
		boolean[] _ab     =  {true,  true,  false, false, false, false, false, false};
		boolean[] _abe    =  {true,  true,  false, false, true,  false, false, false};
		boolean[] _abcd   =  {true,  true,  true,  true,  false, false, false, false};
		boolean[] _abcde  =  {true,  true,  true,  true,  true,  false, false, false};
		boolean[] _abcdf  =  {true,  true,  true,  true,  false, true,  false, false};
		boolean[] _abcdef =  {true,  true,  true,  true,  true, true,  false, false};
		boolean[] _abce   =  {true,  true,  true,  false, true,  false, false, false};
		boolean[] _abcef  =  {true,  true,  true,  false, true,  true,  false, false};
		boolean[] _abcefh =  {true,  true,  true,  false, true,  true,  false, true };
		boolean[] _abcefg =  {true,  true,  true,  false, true,  true,  true,  false};
		
		Node<GenotypeGen> root = gen.getRoot();
		Node<GenotypeGen> a      = gen.add(_a);
		Node<GenotypeGen> b      = gen.add(_b);
		Node<GenotypeGen> ab     = gen.add(_ab);
		Node<GenotypeGen> abe    = gen.add(_abe);
		Node<GenotypeGen> abcd   = gen.add(_abcd);
		Node<GenotypeGen> abcde  = gen.add(_abcde);
		Node<GenotypeGen> abcdf  = gen.add(_abcdf);
		Node<GenotypeGen> abcdef = gen.add(_abcdef);
		Node<GenotypeGen> abce   = gen.add(_abce);
		Node<GenotypeGen> abcef  = gen.add(_abcef);
		Node<GenotypeGen> abcefh = gen.add(_abcefh);
		Node<GenotypeGen> abcefg = gen.add(_abcefg);
		
		
		
		gen.link(root, root, 0.4);
		gen.link(root, a, 0.26);
		gen.link(root, b, 0.10);
		gen.link(a, a, 0.3);
		gen.link(a, ab, 0.3);
		gen.link(b, b, 0.3);
		gen.link(b, ab, 0.24);
		gen.link(ab, ab, 0.34);
		gen.link(ab, abe, 0.10);
		gen.link(ab, abcd, 0.33);
		gen.link(abe, abe, 0.32);
		gen.link(abe, abce, 0.7);
		gen.link(abce, abce, 0.2);
		gen.link(abce, abcde, 0.1);
		gen.link(abce, abcef, 0.134);
		gen.link(abcd, abcd, 0.2252);
		gen.link(abcd, abcde, 0.46);
		gen.link(abcd, abcdf, 0.23);
		gen.link(abcde, abcde, 0.36);
		gen.link(abcde, abcdef, 0.6);
		gen.link(abcdef, abcdef, 0.4);
		gen.link(abcef, abcef, 0.4);
		gen.link(abcef, abcefh, 0.23);
		gen.link(abcef, abcefg, 0.523);
		gen.link(abcdf, abcdef, 0.23);
		gen.link(abcdf, abcdf, 0.25);
		gen.link(abcefg, abcefg, 1);
		gen.link(abcefh, abcefh, 1);
		gen.link(abcef, abcdef, 0.04);
		return gen;
		
	}
	
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		GraphDatasetGeneratorAllowingMultipleMutations gen = generatorTemp();//new GraphDatasetGeneratorAllowingMultipleMutations(12);//generator3();

		String path = "/home/rossi/Scrivania/Test_ripetizioni/biggerNotEqualSelfLoops/";
		new File(path).mkdirs();
		String datasetPath = path+"dataset/";
		new File(datasetPath).mkdirs();
		
	
		System.out.println("Generator");
		PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(path + "generator.dot")), true);
		gen.toDot(out);
		out.close();
		for (int j = 8; j <=28 ; j++) {
			for (int i = 10; i <= 100000; i *= 10) {
				out = new PrintStream(new BufferedOutputStream(new FileOutputStream(path + "graph_"+i+"_"+j+".dot")), true);
				PrintStream outDataset = new PrintStream(new BufferedOutputStream(new FileOutputStream(datasetPath + "dataset_"+i+"_"+j+".txt")), true);
				System.out.println("Graph with " + i + " samples and path length " + j);
				Dataset data = gen.generate(i, j); 
				data.printTRONCOFormat(outDataset);
				GenotypeGraphAllowingMultipleMutations grp = new GenotypeGraphAllowingMultipleMutations(data);
				grp.toDot(out);
				out.close();
				outDataset.close();
			}
		}
		
		
	}

}
