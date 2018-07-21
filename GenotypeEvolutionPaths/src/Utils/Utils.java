package Utils;

import java.util.ArrayList;
import java.util.Random;

import GenotypeGraph.SquareMatrix;

public class Utils {
	
	public static Random rnd = new Random(System.currentTimeMillis());
	
	/**
	 * @param   arr 
	 * @return  sum of all arr's elements
	 */
	public static double sumDouble(ArrayList<Double> arr) {
		double sum = 0;
		for(double v: arr){
			sum+=v;
		}
		return sum;
	}
	
	/**
	 * @param   arr 
	 * @return  sum of all arr's elements 
	 */
	public static long sumLong(ArrayList<Long> arr) {
		long sum = 0;
		for(long v: arr){
			sum+=v;
		}
		return sum;
	}
	
	/**
	 * @param   arr 
	 * @return  sum of all arr's elements 
	 */
	public static long sumInt(ArrayList<Integer> arr) {
		int sum = 0;
		for(int v: arr){
			sum+=v;
		}
		return sum;
	}
	
	/**
	 * @param   arr 
	 * @return  sum of all arr's elements (considering true=1 and false=0) 
	 */
	public static int sumBool(boolean[] arr) {
		int sum = 0;
		for(boolean b: arr){
			sum+=b?1:0;
		}
		return sum;
	}

	/**
	 * Adds an array of boolean to an array of integer (considering true=1 and false=0) 
	 * REQUIRE |sum| = |arr|
	 * @param sum  
	 * @param arr
	 */
	public static void addBoolVec(int[] sum, boolean[] arr) {
		for(int i = 0; i<arr.length; i++){
			sum[i]+=arr[i]?1:0;
		}
	}

	/**
	 * Casts an integer (seen as a binary value) to an array of booleans of size size
	 * @param i     value to be casted
	 * @param size  maximum number of bit used
	 * @return      i encoded in binary in a boolean array
	 */
	public static boolean[] binaryToBoolArray(long i, int size) {
		int pos=size-1;
		boolean[] v = new boolean[size];
		while(i>0){
			v[pos] = i%2==1;
			i/=2;
			pos--;
		}
		return v;
	}
	
	/**
	 * Verifies if a and b are equal except for a single position
	 * @param a 
	 * @param b
	 * @return true if a and b are equal except for a single position
	 */
	public static boolean singleMismatch(boolean[] a, boolean[] b){
		if(a.length != b.length){
			return false;
		}
		boolean mismatch = false;
		for(int i = 0; i<a.length; i++){
			if(a[i]!=b[i] && !mismatch){
				mismatch=true;
			}else if(a[i]!=b[i]){
				return false;
			}
		}
		return mismatch;
		
	}

	/**
	 * bs |= bs2; for vectors of boolean
	 * @param bs  Modified with or  
	 * @param bs2 unmodified
	 */
	public static void or(boolean[] bs, boolean[] bs2) {
		assert(bs.length == bs2.length);
		for(int i=0; i<bs.length; i++){
			bs[i] = bs2[i] || bs[i]; 
		}
	}

	/**
	 * is += is2
	 * @param is
	 * @param is2
	 */
	public static void addToFrom(int[] is, int[] is2) {
		assert(is.length == is2.length);
		for(int i=0; i<is.length; i++){
			is[i] += is2[i]; 
		}
	}
	
	/**
	 * Creates a random number according to the random seed set in this class
	 * @return a pseudo-random number between 0 and 1
	 */
	public static double random(){
		return rnd.nextDouble();
	}
	
	/**
	 * Casts an ArrayList<boolean[]> to a boolean[][] 
	 * @param  list (of arrays)
	 * @return a matrix
	 */
	public static boolean[][] toBoolMatrix(ArrayList<boolean[]> list) {
		boolean[][] matrix = new boolean[list.size()][list.get(0).length];
		for(int i=0; i<matrix.length ;i++){
			for(int j=0; j<matrix[i].length; j++){
				matrix[i][j] = list.get(i)[j];
			}
		}
		return matrix;
	}

	/**
	 * @param matrix
	 * @param row
	 * @return sum of all elements on row of matrix
	 */
	public static Double sumDoubleRow(SquareMatrix<Double> matrix, int row) {
		Double ans = 0.;
		for(int col=0; col<matrix.getSize(); col++){
			ans+=matrix.get(row,col);
		}
		return ans;
	}
	
	/**
	 * @param matrix
	 * @param col
	 * @return sum of all elements on row of matrix
	 */
	public static Double sumDoubleCol(SquareMatrix<Double> matrix, int col) {
		Double ans = 0.;
		for(int row=0; row<matrix.getSize(); row++){
			ans+=matrix.get(row,col);
		}
		return ans;
	}
	
	/**
	 * @param a
	 * @param b
	 * @return the hammingDistance between the genotypes of a and b
	 */
	public static int hammingDistance(boolean[] a,boolean[] b){
		int count =0;
		for(int i = 0; i<a.length; i++){
			count += a[i]!=b[i]?1:0;
		}
		return count;
	}
	
}
