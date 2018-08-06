package Graphs;

import java.io.PrintStream;

/**
 * Class for a node in a graph that is associated to an item 
 * @author rossi
 *
 * @param <T>
 */
public class SimpleNode <T> implements Node<T> {

	private int id;
	private T content;
	
	public SimpleNode(int id, T content){
		this.id=id;
		this.content=content;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id=id;
	}

	@Override
	public T getContent() {
		return this.content;
	}

	@Override
	public void setContent(T newContent) {
		this.content=newContent;
	}
	
	@Override
	public void toDot(PrintStream out){
		out.println(this.id + " [label=\""+ this.content.toString() +"\"]");
	}


}
