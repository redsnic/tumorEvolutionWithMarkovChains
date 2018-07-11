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
	public static long sumBool(boolean[] arr) {
		long sum = 0;
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
	
}
