package Graphs;

import java.util.ArrayList;
import java.util.List;

import Utils.SquareMatrix;

/**
 * Implementation with adj matrix
 * INVARIANT : IDs of nodes are their position in V
 *             V.size() = E.getSize();
 * @author rossi
 *
 * @param <T>
 */
public class DirectedGraphMatrix<T> implements Graph<T> 
{

	ArrayList<Node<T>> V = new ArrayList<Node<T>>();
	SquareMatrix<Boolean> E = new SquareMatrix<Boolean>(false);
	ArrayList<Integer> numberOfParents = new ArrayList<Integer>();
	ArrayList<Integer> numberOfChildren = new ArrayList<Integer>();
	
	@Override
	public int getNumberOfNodes() {
		return V.size();
	}

	@Override
	public List<Node<T>> getAdjacencyList(Node<T> current) throws InvalidNodeException {
		/* scan adjacency matrix on the line associated with current  */
		ArrayList<Node<T>> ret = new ArrayList<Node<T>>();
		for(int j=0; j<E.getSize(); j++){
			if(E.get(current.getId(), j)){
				ret.add(V.get(j));
			}
		}
		return ret;
	}

	@Override
	public List<Node<T>> getNodes() {
		return V;
	}

	@Override
	public void link(Node<T> a, Node<T> b) throws AlreadyLinkedException {
		if(E.get(a.getId(), b.getId())){
			throw new AlreadyLinkedException(a.getId(), b.getId());
		}
		E.set(a.getId(), b.getId(), true);
		numberOfChildren.set(a.getId(), numberOfChildren.get(a.getId())+1);
		numberOfParents.set(b.getId(), numberOfParents.get(b.getId())+1);
	}

	@Override
	public void unlink(Node<T> a, Node<T> b) throws NoEdgeFoundException {
		if(!E.get(a.getId(), b.getId())){
			throw new NoEdgeFoundException(a.getId(), b.getId());
		}
		E.set(a.getId(), b.getId(), false);
		numberOfChildren.set(a.getId(), numberOfChildren.get(a.getId())-1);
		numberOfParents.set(b.getId(), numberOfParents.get(b.getId())-1);
	}

	@Override
	public Node<T> add(T content) {
		Node<T> newNode = new SimpleNode<T>(V.size(), content);
		V.add(newNode);
		E.enlarge();
		numberOfChildren.add(0);
		numberOfParents.add(0);
		return newNode;
	}

	@Override
	public void toDot() {
		System.out.println("digraph g{");
		for(Node<T> v: V){
			v.toDot();
		}
		for(int i=0; i<E.getSize(); i++){
			for (int j = 0; j < E.getSize(); j++) {
				if(E.get(i, j)){
					System.out.println(i + " -> " + j);
				}
			}
		}
		System.out.println("}");
	}
	
	/**
	 * @param position
	 * @return the adjacency list of position O(V)
	 */
	public ArrayList<Node<T>> adj(Node<T> position){
		ArrayList<Node<T>> ret = new ArrayList<Node<T>>();
		for(int i = 0; i<E.getSize(); i++){
			if(E.get(position.getId(), i)){
				ret.add(this.V.get(i));
			}
		}
		return ret;
	}
	
	/**
	 * @param position
	 * @return the list of parents of position O(V)
	 */
	public ArrayList<Node<T>> par(Node<T> position){
		ArrayList<Node<T>> ret = new ArrayList<Node<T>>();
		for(int i = 0; i<E.getSize(); i++){
			if(E.get(i, position.getId())){
				ret.add(this.V.get(i));
			}
		}
		return ret;
	}
	
	/**
	 * @param position
	 * @return the number of parents of position (O(1))
	 */
	public int getNumberOfParents(Node<T> position){
		return  this.numberOfParents.get(position.getId());
	}
	
	/**
	 * @param position
	 * @return the number of children of position
	 */
	public int getNumberOfChildren(Node<T> position){
		return  this.numberOfChildren.get(position.getId());
	}
	
}
