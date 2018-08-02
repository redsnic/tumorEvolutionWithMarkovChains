package Graphs;

public class InvalidNodeException extends RuntimeException {

	public InvalidNodeException(int nodeId){
		super();
		System.err.println("Cannot find node with id " + nodeId);
	}
	
}
