package Graphs;

public class NoEdgeFoundException extends RuntimeException {

	public NoEdgeFoundException(int i, int j){
		super();
		System.err.println(i + " and " + j + " were NOT already linked");
	}
	
}
