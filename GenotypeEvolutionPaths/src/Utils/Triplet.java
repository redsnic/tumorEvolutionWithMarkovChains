package Utils;

/**
 * Simple class to manage triplets easily
 * @author rossi
 *
 * @param <A> first type
 * @param <B> second type  
 * @param <C> third type
 */
public class Triplet<A,B,C>{
	
	private final A first;
	private final B second;
	private final C third;
	
	public Triplet(A fst, B snd, C thr){
		this.first = fst;
		this.second = snd;
		this.third = thr;
	}
	
	public A fst(){
		return first;
	}
	
	public B snd(){
		return second;
	}
	
	public C thr(){
		return third;
	}
}