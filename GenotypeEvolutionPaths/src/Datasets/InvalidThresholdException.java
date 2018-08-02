package Datasets;

public class InvalidThresholdException extends RuntimeException {
	
	public InvalidThresholdException(int n){
		System.err.println("Threshold on the number of genes should be a positive integer, used: " + n);
	}
	
}
