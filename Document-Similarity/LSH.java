

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Collections;

/**
 * 
 * Local sensitive hashing of documents.
 *
 */
public class LSH {
	private int n;
	static int rows, numBands;
	private int[][] minHashMat;
	private String[] docNames;
	
	int p;
	int a;
	int b;
	String name;
	/**
	 * hashTables - Stores the buckets of minHash values of documents. 
	 */
	public static List<HashMap<Integer, ArrayList<String>>> hashTables = new ArrayList<HashMap<Integer, ArrayList<String>>>();
	
	public static void main(String[]  args)
	{
		MinHash minHash=new MinHash("./", 800);
		System.out.println("After MinHash");
		int[][] minHashMat = minHash.minHashMatrix;
		System.out.println("After MinshAshMat");
		LSH lsh = new LSH(minHashMat, minHash.fileNames, 50);
		System.out.println("After LSH");
		ArrayList<String> lstNearDuplicates = lsh.nearDuplicatesOf("baseball0.txt");
	}
	/**
	 * 
	 * @param minHashMatrix
	 * @param docNames
	 * @param bands
	 * Default constructor for the LSH class
	 */
	public LSH(int[][] minHashMatrix, String[] docNames, int bands)
	{
		n = docNames.length;
		hashTables = new ArrayList<HashMap<Integer, ArrayList<String>>>();
		Random r = new Random();
		rows = minHashMatrix.length / bands;
		this.minHashMat = minHashMatrix;
		this.docNames = docNames;
		this.numBands = bands;
		
		p = nextPrime(10 * n);
		a = r.nextInt(p);
		b = r.nextInt(p);
		HashMap<Integer, ArrayList<String>> tmpTable = new HashMap<Integer, ArrayList<String>>();
		ArrayList<String>names = new ArrayList<String>();
		
		int bandNum = 0;
		int hashValue = 1;
		for(int i = 0; i < minHashMatrix[0].length ; i++) 
		{ 
			for(int j = 1; j < minHashMatrix.length; j++) 
			{ 
				bandNum = j / rows;
				if(bandNum >= bands)
					bandNum = bandNum - 1;
				hashValue = hashValue + (a * minHashMatrix[j][i] + b);
				hashValue = hashValue % p;  // Using a*x + b%p to calculate the hash value
				//hashing for each band 
				if((j + 1) % rows == 0 || (j + 1) == n) {
					if(i == 0)
						tmpTable = new HashMap<Integer, ArrayList<String>>();
					else
						tmpTable = hashTables.get(bandNum);					
					if(tmpTable.isEmpty()  != true)
					{
						names = tmpTable.get(hashValue);
						if(names == null)
						{
								names = new ArrayList<String>();
								names.add(docNames[i]);
								tmpTable.put(hashValue, names);
								hashTables.set(bandNum, tmpTable);
						}
						else {
							names.add(docNames[i]);
							tmpTable.put(hashValue, names);
							hashTables.set(bandNum, tmpTable);
						}
					}
					else {
				
							names.add(docNames[i]);
							tmpTable.put(hashValue, names);
							hashTables.add(tmpTable);
					}					
					hashValue = 1;
				}
			}
		}
	}
	/**
	 * @param docName
	 * @return nearDuplicates of a document "docname"
	 */
	public ArrayList<String> nearDuplicatesOf(String docName) {
		int bandNum = 0;
		int hashValue = 1;
		HashMap<Integer, ArrayList<String>> tmpTable = new HashMap<Integer, ArrayList<String>>();
		ArrayList<String> duplicates = new ArrayList<String>();
		Set<String> similarString = new HashSet<String>();
		int docIndex = 0;
		for(int i = 0; i < n; i++) {
			if(docNames[i].equals(docName)) {
				docIndex = i;
			}
		}
		for(int i = 1; i < minHashMat.length; i++) {
			bandNum = i / rows;
			if(bandNum >= numBands)
				bandNum = bandNum - 1;
			hashValue = hashValue + (a * minHashMat[i][docIndex] + b);
			hashValue = hashValue % p;
			
			if((i + 1) % rows == 0 || (i + 1) == minHashMat.length) {
				tmpTable = hashTables.get(bandNum);
				ArrayList<String> names = tmpTable.get(hashValue);
				if(names != null) {
					similarString.addAll(names);
				}
				hashValue = 1;
			}	
		}	
		duplicates.addAll(similarString);
		return(duplicates);
	}
	
	
	public boolean isPrime(int n) {
		if(n == 1) return(false);
		else if(n == 2 || n == 3) return(true);
		else if(n % 2 == 0 || n % 3 == 0) return(false);
		else {
			for(int i = 5; i*i < n + 1; i += 6) {
				if(n % i == 0 || n % (i + 2) == 0) {
					return(false);
				}
			}
			return(true);
		}
	}
	
	/**
	 * Finds the next prime number larger than a starting integer.
	 * @param n Starting integer.
	 * @return The next prime number larger than starting integer.
	 */
	public int nextPrime(int n) {
		boolean isPrime = false;
		
		int m = n;
		while(!isPrime) {
			isPrime = isPrime(++m);
		}	
		return(m);
	}	
}
