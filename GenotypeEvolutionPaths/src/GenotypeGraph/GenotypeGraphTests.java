package GenotypeGraph;

import java.util.ArrayList;

import org.junit.Test;

public class GenotypeGraphTests {

	@Test
	public void shouldPrintGraphInDot() {
		String[] labels = {"A","B","C"}; 
		ArrayList<boolean[]> gnts = new ArrayList<boolean[]>();
		boolean[] l1 = {true,false,false};
		boolean[] l2 = {true,true,false};
		boolean[] l3 = {true,true,true};
		gnts.add(l1); gnts.add(l1); gnts.add(l1); gnts.add(l1); gnts.add(l2); gnts.add(l2); gnts.add(l3);
		GenotypeGraph g = new GenotypeGraph(labels, gnts){
			@Override
			protected void prepareEdges(){
				super.prepareEdges();
				for(int i = 0; i<this.nodes.size()-1; i++){
					this.nodes.get(i).add(this.nodes.get(i+1));
				}
			}
		};
		g.toDot();
	}

}
