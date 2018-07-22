package GenotypeGraph;

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
	
}
