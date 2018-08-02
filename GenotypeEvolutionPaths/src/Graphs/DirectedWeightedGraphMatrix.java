package Graphs;

import Utils.SquareMatrix;

/**
 * Class to manage weighted directed graphs 
 * with adj matrix
 * 
 * @author rossi
 *
 * @param <T>
 */
public class DirectedWeightedGraphMatrix<T> extends DirectedGraphMatrix<T>{

	SquareMatrix<Double> W = new SquareMatrix<Double>(0.);
	
	public DirectedWeightedGraphMatrix(){
		super();
	}
	
	/**
	 * Set weight of edge from a to b
	 * @param a
	 * @param b
	 * @throws NoEdgeFoundException when a and b are not linked
	 */
	public void setWeight(Node<T> a, Node<T> b, double weight) throws NoEdgeFoundException{
		if(!E.get(a.getId(), b.getId())){
			throw new NoEdgeFoundException(a.getId(), b.getId());
		}
		W.set(a.getId(), b.getId(), weight);
	}
	
	@Override
	public Node<T> add(T content) {
		W.enlarge();
		return super.add(content);
	}
	
	/**
	 * creates an edge from a to b with weight 'weight'
	 * @param a
	 * @param b
	 * @param weight
	 */
	public void link(Node<T> a, Node<T> b, double weight){
		super.link(a, b);
		setWeight(a,b, weight);
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
					System.out.println(i + " -> " + j + "[label=\"" + String.format("%.3f", W.get(i, j)) + "\"]");
				}
			}
		}
		System.out.println("}");
	}

	/**
	 * Changes this weight matrix with another one
	 * @param matrix
	 */
	public void setWeights(SquareMatrix<Double> matrix) {
		assert(matrix.getSize() == E.getSize());
		W = matrix;	
	}
	
	/**
	 * @param a
	 * @param b
	 * @return weight of edge from a to b
	 */
	public double getWeight(Node<T> a ,Node<T> b){
		if(!E.get(a.getId(), b.getId())) return 0.;
		return W.get(a.getId(), b.getId());
	}
	
}
