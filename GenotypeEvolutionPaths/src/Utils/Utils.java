package Utils;

import java.util.ArrayList;

public class Utils {
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
	
}
