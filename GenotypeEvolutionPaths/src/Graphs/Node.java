package Graphs;

import java.io.PrintStream;

public interface Node <T>{
	
	/**
	 * @return the unique id associated with this node
	 */
	public int getId();
	
	/**
	 * sets the unique id associated with this node
	 * @param id 
	 */
	public void setId(int id);
	
	/**
	 * @return the content of this node
	 */
	public T getContent();
	
	/**
	 * sets the content of this node
	 * @param newContent
	 */
	public void setContent(T newContent);
	
	/**
	 * prints this node in dot format
	 * @param out 
	 */
	public void toDot(PrintStream out);
	
}
