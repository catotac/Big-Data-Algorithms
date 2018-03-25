/**
* @author Rahul Singh
* @author Prachi Patel
*/

import java.util.BitSet;
import java.util.Random;

/**
 * This class contains a Bloomfilter using random
 * function
 */

public class BloomFilterRan {
	BitSet hashFunctions;
	int setSize, bitsperElement, numofHashFuncs, numOfElem;
	int sizeOffilter;
	int a, b;
	int netPrime;
		
	/**
	 * Constructor to define the object of BloomfilterRan class
	 */
	public BloomFilterRan(int setSize, int bitsPerElement) {
		sizeOffilter = setSize*bitsPerElement;
		numofHashFuncs = (int) Math.round(((double)sizeOffilter/(double)setSize) * Math.log(2.0));
		hashFunctions = new BitSet(sizeOffilter);
		Random rand = new Random();
		numOfElem = 0;
		netPrime = findPrime(sizeOffilter);
	
		/**
		 * Calculate a and b for the hash function
		 */
		a = rand.nextInt(findPrime(sizeOffilter));
		b = rand.nextInt(findPrime(sizeOffilter));
	}
	
	public void add(String s) {
		if(s.length() == 0) {
			return;
		}
		s = s.toLowerCase();
		long[] hashvalues = randomhashes(s);
		for(int i = 0; i < hashvalues.length; i++)
		{
			hashFunctions.set((int)(Math.abs(hashvalues[i])%sizeOffilter));
		}
		numOfElem++;
	}
	public boolean appears(String s) {
		s = s.toLowerCase();
		long[] hashvalues = randomhashes(s);
		for(int i = 0; i < hashvalues.length; i++)
		{
			if(!hashFunctions.get((int)(Math.abs(hashvalues[i])%sizeOffilter))){
				return false;
			}
		}
		return true;		
	}	
	/**
	 * 
	 * @param num
	 * @return The prime number greater than tN.
	 */
	private int findPrime(int num) {
		int i, j;
		for(i = num; i < 2*num; i++) {
			for(j = 2; j < i; j++) {
				if(i%j == 0) {
					break;
				}
			}
			if(j == i) {
				break;
			}			
		}
		return i;
	}
	/**
	 * 
	 * @param s is the current String
	 * @return Returns k hash functions of s using random hash function
	 */
	private long[] randomhashes(String s) {
		long[] khashes = new long[numofHashFuncs];
		int hasCode = s.hashCode();
		for(int i = 1; i < numofHashFuncs; i++) {
			khashes[i] = Math.abs(a*hasCode + b + i*hasCode)%netPrime;
		}
		return khashes;
	}
	
	public int filterSize() {
		return sizeOffilter;
	}
	
	public int dataSize() {
		return numOfElem;		
	}
	public int numHashes() {
		return numofHashFuncs;		
	}
		
}
	