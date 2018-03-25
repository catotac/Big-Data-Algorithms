
import java.util.BitSet;
import java.util.Random;
import java.util.*;

/**
 * This class contains a Dynamic Bloomfilter 
 */

public class DynamicFilter {
	BitSet hashFunctions;
	int setSize, bitsperelement, numofHashFuncs, numOfElem;
	int sizeOffilter;
	int a, b, numSets;
	int numofsets, primeNum;
	ArrayList<BitSet> old_hashes = new ArrayList<BitSet>(); // Stores the old BitSets as an Array List.
	ArrayList<Integer> netPrime = new ArrayList<Integer>();	
	/**
	 * Constructor to define the object of BloomfilterDynamic class
	 */
	public DynamicFilter(int bitsPerElement) {
		bitsperelement = bitsPerElement;
		numofsets = 1;
		numOfElem = 0;
		setSize = numofsets*1000;
		sizeOffilter = setSize*bitsPerElement;
		numofHashFuncs = (int) Math.round(((double)sizeOffilter/(double)setSize) * Math.log(2.0));
		hashFunctions = new BitSet(sizeOffilter);
		Random rand = new Random();		
		/**
		 * Calculate a and b for the random hash function
		 */
		a = rand.nextInt(findPrime(sizeOffilter));
		b = rand.nextInt(findPrime(sizeOffilter));
		
		/**
		 * Calculate the next Random Number
		 */
		primeNum = findPrime(sizeOffilter);
		netPrime.add(primeNum);
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
		if(numOfElem > (1000*numofsets - 1)) {
			numofsets = 2*numofsets;
			numSets = numofsets;
			setSize = 1000*numSets;
			sizeOffilter = setSize*bitsperelement;
			BitSet temphash = new BitSet(sizeOffilter);
			old_hashes.add(hashFunctions);
			hashFunctions = temphash;
			numofHashFuncs = (int) Math.round(((double)sizeOffilter/(double)setSize) * Math.log(2.0));	
//			Random rand = new Random();
			/**
			 * Calculate a and b
			 */
			//a = rand.nextInt(sizeOffilter);
			//b = rand.nextInt(sizeOffilter);
			primeNum = findPrime(sizeOffilter);
			netPrime.add(primeNum);		
		}
	}
	/**
	 * Function creates new bloom filter with a larger size
	 * Stores the old filter in a list
	 * @
	 * @param numSets - Number to keep track if the number of elements 
	 */
/*	private void getTheSizeOfFilter(int numSets) {
		setSize = 1000*numSets;
		sizeOffilter = setSize*bitsperelement;
		BitSet temphash = new BitSet(sizeOffilter);
		old_hashes.add(hashFunctions);
		hashFunctions = temphash;
		numofHashFuncs = (int) Math.round(((double)sizeOffilter/(double)setSize) * Math.log(2.0));	
//		Random rand = new Random();
		/**
		 * Calculate a and b
		 */
		//a = rand.nextInt(sizeOffilter);
		//b = rand.nextInt(sizeOffilter);
//		netPrime = findPrime(sizeOffilter);
		
//	}
//*/
	public boolean appears(String s) {
		s = s.toLowerCase();
		int i, j;
		long[] hashvalues = randomhashes(s);
		for(i = 0; i < old_hashes.size(); i++)
		{	
			int oldsizeOffilter = old_hashes.get(i).length();
			long[] oldhashvalues = oldhashes(s, i);
			for(j = 0; j < oldhashvalues.length; j++) {
				if(!old_hashes.get(i).get((int)(Math.abs(oldhashvalues[j])%oldsizeOffilter))){
					break;
					
				}
			}
			if(j == oldhashvalues.length) {
				return true;				
			}		
		}
		for(i = 0; i < hashvalues.length; i++) {
			if(!hashFunctions.get((int)(Math.abs(hashvalues[i])%sizeOffilter))){
				return false;
			}
		}
		return true;		
	}	
	
	/** 
	 * @param bytarr
	 * @return the k hash values for the old BitSets
	 */
	private long[] oldhashes(String s, int num) {
		long[] khashes = new long[numofHashFuncs];
		int hasCode = s.hashCode();
		int numofoldHashFuncs = (int) Math.round(((double)bitsperelement) * Math.log(2.0));
		for(int i = 1; i < numofoldHashFuncs; i++) {
			khashes[i] = Math.abs(a*hasCode + b + i*hasCode)%netPrime.get(num);
		}
		return khashes;
	}
	
	/**
	 * 
	 * @param bytarr
	 * @return the k hash values using random hash function 
	 */
		
	private long[] randomhashes(String s) {
		long[] khashes = new long[numofHashFuncs];
		int hasCode = s.hashCode();
		for(int i = 1; i < numofHashFuncs; i++) {
			khashes[i] = Math.abs(a*hasCode + b + i*hasCode)%primeNum;
		}
		return khashes;
	}
	/**
	 * @param num
	 * @return The prime number greater than tN.
	 */
	private int findPrime(int num) {
		int i, j;
		for(i = num; i < 2*num; i++) {
			if(i%2 == 0)
				continue;
			for(j = 3; j < i; j = j+2) {
				if(i%j == 0) {
					return i;
				}
			}			
		}
		return i;
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
	
