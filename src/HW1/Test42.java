package HW1;
public class Test42 {

	// test the method stats
	static private void testStats(int nbVals){
		System.out.println("Card game with "+nbVals+" values");
		Battle.stats(nbVals, 1000);
		System.out.println("");
	}
	
	public static void main(String[] args) {
		testStats(11);
		testStats(12);
		testStats(13);
	}

}
