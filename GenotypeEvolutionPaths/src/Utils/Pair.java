package Utils;

/**
 * Easy class to manage pairs
 * @author rossi
 *
 * @param <F> type of first member
 * @param <S> type of second member
 */
public class Pair <F,S>{

	F fst;
	S snd;
	
	public Pair(F f, S s){
		fst = f;
		snd = s;
	}
	
	public F fst(){
		return fst;
	}
	
	public S snd(){
		return snd;
	}
	
	public String toString(){
		return "<" + fst.toString() + ", " + snd.toString() + ">"; 
	}
}
