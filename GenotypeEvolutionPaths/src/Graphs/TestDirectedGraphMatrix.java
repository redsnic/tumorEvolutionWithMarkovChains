package Graphs;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestDirectedGraphMatrix {

	@Test
	public void test() {
		
		DirectedGraphMatrix<String> tst = new DirectedGraphMatrix<String>();
		
		Node<String> root = tst.add("clonal");
		Node<String> a = tst.add("A");
		Node<String> b = tst.add("B");
		Node<String> ab = tst.add("AB");
		Node<String> ac = tst.add("AC");
		Node<String> ad = tst.add("AD");
		Node<String> bd = tst.add("BD");
		Node<String> abd = tst.add("ABD");
		
		tst.link(root,a);
		tst.link(root,b);
		tst.link(a,ad);
		tst.link(a,ac);
		tst.link(a,ab);
		tst.link(b,ab);
		tst.link(b,bd);
		tst.link(ad, abd);
		tst.link(ab, abd);
		tst.link(bd, abd);
		
		tst.toDot();
		
	}

	@Test
	public void test2() {
		
		DirectedWeightedGraphMatrix<String> tst = new DirectedWeightedGraphMatrix<String>();
		
		Node<String> root = tst.add("clonal");
		Node<String> a = tst.add("A");
		Node<String> b = tst.add("B");
		Node<String> ab = tst.add("AB");
		Node<String> ac = tst.add("AC");
		Node<String> ad = tst.add("AD");
		Node<String> bd = tst.add("BD");
		Node<String> abd = tst.add("ABD");
		
		tst.link(root,a);
		tst.link(root,b);
		tst.link(a,ad);
		tst.link(a,ac);
		tst.link(a,ab);
		tst.link(b,ab);
		tst.link(b,bd);
		tst.link(ad, abd);
		tst.link(ab, abd);
		tst.link(bd, abd);
		
		tst.toDot();
		
	}
	
	
}
