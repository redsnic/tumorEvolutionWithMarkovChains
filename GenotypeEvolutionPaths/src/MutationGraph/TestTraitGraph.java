package MutationGraph;

import org.junit.Test;

public class TestTraitGraph {

	@Test
	public void testToDot() {
		TRaITGraph tg = new TRaITGraph();
		TRaITNode root = new TRaITNode("");
		TRaITNode a = new TRaITNode("A");
		TRaITNode b = new TRaITNode("B");
		TRaITNode c = new TRaITNode("C");
		TRaITNode d = new TRaITNode("D");
		TRaITNode e = new TRaITNode("E");
		TRaITNode f = new TRaITNode("F");
		TRaITNode g = new TRaITNode("G");
		tg.add(root);
		tg.add(a);
		tg.add(b);
		tg.add(c);
		tg.add(d);
		tg.add(e);
		tg.add(f);
		tg.add(g);
		root.add(a, .80);
		root.add(f, .30);
		a.add(b, .70);
		b.add(c, .30);
		c.add(d, .70);
		d.add(e, .50);
		d.add(g, .50);
		f.add(c, .20);
		tg.toDot();
	}
	
	@Test
	public void testToDotAndToMCComplex() {
		TRaITGraph tg = new TRaITGraph();
		TRaITNode root = new TRaITNode("");
		TRaITNode a = new TRaITNode("A");
		TRaITNode b = new TRaITNode("B");
		TRaITNode c = new TRaITNode("C");
		TRaITNode d = new TRaITNode("D");
		TRaITNode e = new TRaITNode("E");
		TRaITNode f = new TRaITNode("F");
		TRaITNode g = new TRaITNode("G");
		TRaITNode h = new TRaITNode("H");
		TRaITNode i = new TRaITNode("I");
		TRaITNode j = new TRaITNode("J");
		tg.add(root);
		tg.add(a);
		tg.add(b);
		tg.add(c);
		tg.add(d);
		tg.add(e);
		tg.add(f);
		tg.add(g);
		tg.add(h);
		tg.add(i);
		tg.add(j);
		root.add(a, .80);
		root.add(f, .30);
		a.add(b, .70);
		b.add(c, .30);
		c.add(d, .70);
		d.add(e, .50);
		d.add(g, .50);
		f.add(c, .20);
		b.add(h, .30);
		h.add(i, .10);
		h.add(j, .30);
		j.add(e, .15);
		i.add(e, .40);
		tg.toDot();
		tg.toMC().toDot();
	}
	
	@Test
	public void testToDotAndToMC() {
		TRaITGraph tg = new TRaITGraph();
		TRaITNode root = new TRaITNode("");
		TRaITNode a = new TRaITNode("A");
		TRaITNode b = new TRaITNode("B");
		TRaITNode c = new TRaITNode("C");
		TRaITNode d = new TRaITNode("D");
		TRaITNode e = new TRaITNode("E");
		TRaITNode f = new TRaITNode("F");
		TRaITNode g = new TRaITNode("G");
		tg.add(root);
		tg.add(a);
		tg.add(b);
		tg.add(c);
		tg.add(d);
		tg.add(e);
		tg.add(f);
		tg.add(g);
		root.add(a, .80);
		root.add(f, .30);
		a.add(b, .70);
		b.add(c, .30);
		c.add(d, .70);
		d.add(e, .50);
		d.add(g, .50);
		f.add(c, .20);
		tg.toMC().toDot();
		
	}
	
	@Test
	public void testToDotAndToMCSimple() {
		TRaITGraph tg = new TRaITGraph();
		TRaITNode root = new TRaITNode("");
		TRaITNode a = new TRaITNode("A");
		TRaITNode f = new TRaITNode("F");
		tg.add(root);
		tg.add(a);
		tg.add(f);
		root.add(a, .80);
		root.add(f, .30);
		tg.toMC().toDot();
	}
	
	@Test
	public void testToDotAndToMCWorstCase() {
		TRaITGraph tg = new TRaITGraph();
		TRaITNode root = new TRaITNode("");
		TRaITNode a = new TRaITNode("A");
		TRaITNode b = new TRaITNode("B");
		TRaITNode c = new TRaITNode("C");
		TRaITNode d = new TRaITNode("D");
		TRaITNode e = new TRaITNode("E");
		TRaITNode f = new TRaITNode("F");
		TRaITNode g = new TRaITNode("G");
		TRaITNode h = new TRaITNode("H");
		TRaITNode i = new TRaITNode("I");
		tg.add(root);
		tg.add(a);
		tg.add(b);
		tg.add(c);
		tg.add(d);
		tg.add(e);
		tg.add(f);
		tg.add(g);
		tg.add(h);
		tg.add(i);
		root.add(a, 1.);
		root.add(b, 1.);
		a.add(c, 1.);
		b.add(c, 1.);
		c.add(d, 1.);
		c.add(e, 1.);
		d.add(f,  1.);;
		e.add(f, 1.);
		f.add(g, 1.);
		f.add(h, 1.);
		g.add(i, 1.);
		h.add(i, 1.);
		tg.toDot();
		tg.toMC().toDot();
		
	}

}
