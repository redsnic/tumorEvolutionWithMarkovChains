package DatasetGenerators;

import java.util.ArrayList;
import java.util.Collections;

import GenotypeGraph.GenotypeNode;
import Utils.Pair;
import Utils.Utils;


/**
 * Extension of GraphDatasetGenerator that produces genotype graph where it
 * is possible to have multiple acquisitions of mutations as a single event 
 * (when this happen is impossible to acquire part of them alone)
 * @author rossi
 *
 */
public class GraphDatasetGeneratorAllowingMultipleMutations extends GraphDatasetGenerator {

	public GraphDatasetGeneratorAllowingMultipleMutations(int n) {
		super(n);
	}
	
	public GraphDatasetGeneratorAllowingMultipleMutations(String[] labels){	
		super(labels);
	}
	
	@Override
	protected void prepareLinks(){
		
		prepareSelfLoops();
		
		ArrayList<Pair<GenotypeNode, Integer>> genotypes = new ArrayList<Pair<GenotypeNode, Integer>>();
		
		for(GenotypeNode n : V){
			genotypes.add(new Pair<GenotypeNode, Integer>(n, Utils.sumBool(n.getGenotype())));
		}
		
		/* sorting genotypes by number of mutations */
		Collections.sort(genotypes, (a, b) -> a.snd()>b.snd()?1:a.snd()==b.snd()?0:-1);
		
		V = new ArrayList<GenotypeNode>();
		
		this.id = 0;
		
		for(Pair<GenotypeNode, Integer> el : genotypes){
			V.add(el.fst());
			V.get(V.size()-1).setId(this.id);
			this.id++;
		}
		
		linkNodes();
		
		computeSumTables();
		
	}

	/**
	 * links each node with all the nodes with less mutations (and with no different additional mutation) and minimal Hamming distance to this node 
	 */
	private void linkNodes(){
		
		for(int i = this.V.size()-1; i>=0 ; i--){
			int distance = Integer.MAX_VALUE;
			
			ArrayList<Integer> parents = new ArrayList<Integer>();
			
			for (int j = i-1; j>=0 ; j--) {
				if(V.get(i).getNumberOfMutations() == V.get(j).getNumberOfMutations()) continue;
				if(V.get(i).getNumberOfMutations() - V.get(j).getNumberOfMutations() > distance) break;
				if(!V.get(i).contains(V.get(j))) continue;
				
				int distij = Utils.hammingDistance(V.get(i).getGenotype(),V.get(j).getGenotype());
				
				if( distij<distance){
					distance=distij;
					parents = new ArrayList<Integer>();
					parents.add(j);
				}
				if( distij==distance ){
					parents.add(j);
				}
				
			}
			
			for(int par : parents){
				E.set(par, i, true);
				W.set(par, i, Utils.random());
			}
			
		}
		
	}

	
	

}
