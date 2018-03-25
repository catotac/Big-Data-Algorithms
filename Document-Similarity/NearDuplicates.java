/**
 * @author Prachi Patel
 * @author Rahul Singh
 */


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

/**
 * Finds near duplicates of a document using MinHash and LSH
 */
public class NearDuplicates {
	
	
	/**
	 * Main class
	 */
	public static void main(String[] args){
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter folder directory: ");		//Enter path of document collection
		String folder = sc.nextLine();	
		System.out.println("Enter number of permutations: ");	//Enter number of Permutations
		int k = sc.nextInt();	
		System.out.println("Enter similarity threshold: ");	//Enter the similarity threshold value
		double s = sc.nextDouble();
		sc.nextLine();
		System.out.println("Enter the file name: ");	//Enter the file name for which near duplicates are to be found
		String filename = sc.nextLine();
		nearDuplicateDetector(folder, k, s, filename);
		
	}
	public static void nearDuplicateDetector(String folder, int k, double s, String filename){
		int falsePos = 0;
		try{
			System.out.println("Program Running");
			MinHash minHsh = new MinHash(folder, k);
			System.out.println("After MinHash");
			int[][] minHashMat = minHsh.minHashMatrix;
			System.out.println("After MinshAshMat");
			int b = calculateBands(k, s);
			LSH lsh = new LSH(minHashMat, minHsh.fileNames, b);
		
			/**
		 	* Similar documents using LSH before removing false positives
		 	*/
			System.out.println("After LSH");
			ArrayList<String> lstNearDuplicates = lsh.nearDuplicatesOf(filename);
			int i = 0;
		
			/**
			 * Removing False Positives
			 */
			List<String> filteredList = new ArrayList<String>();
			System.out.println("Similar documents before removing false positives");
			for(String str : lstNearDuplicates){
				System.out.println(i++ + " "+ str);
				if ( minHsh.exactJaccard(str, filename) >= s){
					filteredList.add(str);
				}
				else falsePos++;
			
			}
			/**
			 * Printing Similar documents after filtering false positives
			 */
			System.out.println("Documents similar to : " +filename);
			for(String str : filteredList){
				System.out.println(str);
			}
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println("Number of FalsePositives: "+falsePos);
	}
	
/**
 * 
 * @param numofper
 * @param thereshold
 * @return the optimized number of bands.
 */
	public static int calculateBands(int numofper, double thereshold) {
		double reverseofpow = 1/(Math.pow(thereshold, numofper));
		for(int band = 1;;band++) {
			if((Math.pow(band, band) < reverseofpow) && (Math.pow(band+1, band + 1) > reverseofpow))
				return band;
		}		
	}
}
