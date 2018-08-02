package Graphs;

import java.util.List;

public interface Graph <T>{
	
	/**
	 * @return the number of nodes in the graph
	 */
	public int getNumberOfNodes();
	
	/**
	 * @param  current
	 * @return returns the adjacency list of current
	 * @throws InvalidNodeException if that node is not in this graph
	 */
	public List<Node<T>> getAdjacencyList(Node<T> current) throws InvalidNodeException; 
	
	/**
	 * @return the list of nodes of this graph
	 */
	public List<Node<T>> getNodes();
	
	/**
	 * Adds an edge from a to b
	 * @param a
	 * @param b
	 * @throws AlreadyLinkedException if a and b were already linked
	 */
	public void link(Node<T> a, Node<T> b) throws AlreadyLinkedException;
	
	/**
	 * Removes an edge from a to b 
	 * @param a
	 * @param b
	 * @throws NoEdgeFoundException if the edge from a to b does not exist
	 */
	public void unlink(Node<T> a, Node<T> b) throws NoEdgeFoundException;
	
	/**
	 * Adds to the graph a node containing 'content' 
	 * @param  content
	 * @return the newly created node
	 */
	public Node<T> add(T content);
	
	/**
	 * Prints this graph in dot format
	 */
	public void toDot();
	
}
