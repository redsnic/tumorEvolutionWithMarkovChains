package Utils;

public class Quadruplet<A,B,C,D>{
	private final A first;
	private final B second;
	private final C third;
	private final D fourth;
	
	public Quadruplet(A fst, B snd, C thr, D four){
		this.first = fst;
		this.second = snd;
		this.third = thr;
		this.fourth = four;
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
	
	public D four(){
		return fourth;
	}
}
