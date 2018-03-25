/**
* @author Rahul Singh
* @author Prachi Patel
*/

import java.util.BitSet;
import java.nio.ByteBuffer;
/**
 * This class contains a Bloomfilter using 64-bit FNV
 * function
 */

public class BloomFilterFNV {
	BitSet hashFunctions;
	int setSize, bitsperElement, numofHashFuncs, numOfElem;
	int sizeOffilter;
	
	private static final long FNV_64_INIT = 0xcbf29ce484222325L;
	private static final long FNV_64_PRIME = 0x100000001b3L;
	
	/**
	 * Constructor to define the object of BloomfilterFNV class
	 */
	
	public BloomFilterFNV(int setSize, int bitsPerElement) {
		sizeOffilter = setSize*bitsPerElement;
		numofHashFuncs = (int) Math.round(((double)sizeOffilter/(double)setSize) * Math.log(2.0));
		hashFunctions = new BitSet(sizeOffilter);
		numOfElem = 0;		
	}
	
	public void add(String s) {
		byte[] strtobytearr;
		if(s.length() == 0) {
			return;
		}
		s = s.toLowerCase();
		strtobytearr = s.getBytes();
		long[] hashvalues = FNVhashes(strtobytearr);
		for(int i = 0; i < hashvalues.length; i++)
		{
			hashFunctions.set((int)(Math.abs(hashvalues[i])%sizeOffilter));
		}
		numOfElem++;
	}
	
	public boolean appears(String s) {
		s = s.toLowerCase();
		long[] hashvalues = FNVhashes(s.getBytes());
		for(int i = 0; i < hashvalues.length; i++)
		{
			if(!hashFunctions.get((int)(Math.abs(hashvalues[i])%sizeOffilter))){
				return false;
			}
		}
		return true;		
	}	
	
	/** 
	 * @param bytarr
	 * @return the k hash values obtained by successively hashing the 
	 * obtained hash numbers. 
	 */
	
	private long[] FNVhashes(byte[] bytarr) {
		long[] khashes = new long[numofHashFuncs];
		khashes[0] = FNVhash(bytarr);
		for(int i = 1; i < numofHashFuncs; i++) {
			khashes[i] = FNVhash(longToBytes(khashes[i - 1]));
		}
		return khashes;
	}
	private byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    return buffer.array();
	}
	/**
	 * Function returns the hashcode of a byte array
	 */
	private long FNVhash(byte[] bytarr) {
		long rv = FNV_64_INIT;
		for(int i = 0; i < bytarr.length; i++) {
			rv ^= bytarr[i];
			rv *= FNV_64_PRIME;
		}
		return rv;
		
		
	}
	/**
	 * Returns the size of the filter
	 */
	public int filterSize() {
		return sizeOffilter;
	}
	/**
	 * Returns the size of the elements
	 */
	public int dataSize() {
		return numOfElem;		
	}
	/**
	 * Returns the number of hash functions
	 */
	public int numHashes() {
		return numofHashFuncs;		
	}
		
}
	