package Utils;

import java.util.ArrayList;

/**
 * Class to manage square matrices of a generic type
 * @author rossi
 *
 * @param <T>
 */
public class SquareMatrix <T>{

	private ArrayList<ArrayList<T>> matrix = new ArrayList<ArrayList<T>>();
	private int size = 0;
	private T initializzationValue;
	
	/**
	 * Default constructor
	 * @param init value with which new cells are initializzated
	 */
	public SquareMatrix(T init){
			this.initializzationValue = init;
	}
	
	
	/**
	 * copy constructor
	 * @param other
	 */
	public SquareMatrix(SquareMatrix<T> other) {
		this.initializzationValue = other.initializzationValue;
		this.enlarge(other.size);
		for(int i = 0; i<other.size; i++){
			for(int j=0; j<other.size; j++){
				this.set(i, j, other.get(i, j));
			}
		}
		
	}

	/**
	 * Makes this matrix a row and a column larger
	 */
	public void enlarge(){
		for(ArrayList<T> line : matrix){
			line.add(initializzationValue);
		}
		size++;
		matrix.add(new ArrayList<T>());
		for(int i=0; i<size; i++){
			matrix.get(matrix.size()-1).add(initializzationValue);
		}
	}
	
	/**
	 * Makes this matrix n row and n column larger
	 */
	public void enlarge(int n){
		for(int i = 0; i<n; i++){
			this.enlarge();
		}
	}
	
	/**
	 * Sets the value of a cell 
	 * NOTE: always use set(i,j, modifiedCopy) to modify a cell
	 * @param i        row
	 * @param j        column
	 * @param element  value
	 */
	public void set(int i, int j, T element){
		assert (i<size && j<size): "Address out of bounds in matrix: i: " + i + " j: " + j + " size: " + size;
		matrix.get(i).set(j, element);
	}
	
	/**
	 * Gets the value of a cell
	 * @param i   row 
	 * @param j   column 
	 * @return    value
	 */
	public T get(int i, int j){
		assert (i<size && j<size): "Address out of bounds in matrix: i: " + i + " j: " + j + " size: " + size;
		return matrix.get(i).get(j);
	}
	
	/**
	 * @return the length of a side of this matrix
	 */
	public int getSize(){
		return size;
	}
	
	/**
	 * matricial product
	 * @param other
	 * @return
	 */
	public SquareMatrix<Double> multiply(SquareMatrix<T> other){
		assert this.size == other.getSize() : "Matrices must be of the same dimension for multiplication!";
		SquareMatrix<Double> out = new SquareMatrix<Double>(0.);
		out.enlarge(this.size);
		/* check bounds */
		for(int i = 0; i< this.size; i++){
			for(int j=0; j<this.size; j++){
				Double cell = 0.;
				for(int k = 0; k<this.size; k++){
					cell += (Double) this.get(i,k) * (Double) other.get(k, j);
				}
				out.set(i, j, cell);
			}
		}
		return out;
	}
	
	/**
	 * premultiply row vector
	 * @param vect
	 * @return
	 */
	public ArrayList<Double> vecProduct(ArrayList<Double> vect){
		ArrayList<Double> out = new ArrayList<Double>();
		for(int i = 0; i<this.size; i++){
			Double cell = 0.;
			for(int k=0; k<this.size; k++){
				cell += vect.get(k) * (Double) this.get(k,i);
			}
			out.add(cell);
		}
		return out;
	}
	
}
