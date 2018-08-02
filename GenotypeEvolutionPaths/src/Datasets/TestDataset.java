package Datasets;



import org.junit.Test;

public class TestDataset {

//	@Test
//	public void testSTDINRead() {
//		Dataset D = new Dataset();
//		D.read();
//		D.compact();
//		D.shrink(1);
//		System.out.println(D.toString());
//	}

	@Test
	public void testFILERead() {
		Dataset D = new Dataset();
		D.read("/home/rossi/Scrivania/Nuovo documento 5");
		D.compact();
		D.shrink(33);
		System.out.println(D.toString());
	}
	
}
