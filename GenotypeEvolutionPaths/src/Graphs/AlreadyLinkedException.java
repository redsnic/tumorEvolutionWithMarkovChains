package Graphs;

public class AlreadyLinkedException extends RuntimeException {

	public AlreadyLinkedException(int i, int j){
		super();
		System.err.println(i + " and " + j + " were already linked");
	}
	
}
