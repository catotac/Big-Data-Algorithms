
import java.util.BitSet;
import java.nio.ByteBuffer;
/**
 * This class contains a Bloomfilter using 64-bit Murmur
 * function
 */

public class BloomFilterMurmer {
	BitSet hashFunctions;
	int setSize, bitsperElement, numofHashFuncs, numOfElem;
	int sizeOffilter;
		
	/**
	 * Constructor to define the object of BloomfilterMurmur class
	 */
	public BloomFilterMurmer(int setSize, int bitsPerElement) {
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
		long[] hashvalues = Murmurhashes(strtobytearr);
		for(int i = 0; i < hashvalues.length; i++)
		{
			hashFunctions.set((int)(Math.abs(hashvalues[i])%sizeOffilter));
		}
		numOfElem++;
	}
	public boolean appears(String s) {
		s = s.toLowerCase();
		long[] hashvalues = Murmurhashes(s.getBytes());
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
	 * @param bytarr
	 * @return the k hash values obtained by successively hashing the 
	 * obtained hash numbers. 
	 */
	private long[] Murmurhashes(byte[] bytarr) {
		long[] khashes = new long[numofHashFuncs];
		khashes[0] = Murmurhash(bytarr);
		for(int i = 1; i < numofHashFuncs; i++) {
			khashes[i] = Murmurhash(longToBytes(khashes[i - 1]));
		}
		return khashes;
	}
	private byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    return buffer.array();
	}	
	/**
	 * Function returns the hashcode of a byte array using Murmur hash functions
	 */
	private long Murmurhash(byte[] bytarr) {
		int length = bytarr.length;
		final long seed = 0x9747b28c;
		final long m = 0xc6a4a7935bd1e995L;
		final int r = 47;
		long h = (seed&0xffffffffl)^(length*m);
		int length8 = length/8;
		for (int i=0; i<length8; i++) {
			final int i8 = i*8;
		    long k =  ((long)bytarr[i8+0]&0xff) + (((long)bytarr[i8+1]&0xff)<<8) 
		    		+ (((long)bytarr[i8+2]&0xff)<<16) +(((long)bytarr[i8+3]&0xff)<<24)
		        +(((long)bytarr[i8+4]&0xff)<<32) +(((long)bytarr[i8+5]&0xff)<<40)
		        +(((long)bytarr[i8+6]&0xff)<<48) +(((long)bytarr[i8+7]&0xff)<<56);
		            
		    k *= m;
		    	k ^= k >>> r;
		    k *= m;        
		    h ^= k;
		    h *= m; 
		}        
       switch (length%8) {
       		case 7: h ^= (long)(bytarr[(length&~7)+6]&0xff) << 48;
		    case 6: h ^= (long)(bytarr[(length&~7)+5]&0xff) << 40;
		    case 5: h ^= (long)(bytarr[(length&~7)+4]&0xff) << 32;
		    case 4: h ^= (long)(bytarr[(length&~7)+3]&0xff) << 24;
		    case 3: h ^= (long)(bytarr[(length&~7)+2]&0xff) << 16;
		    case 2: h ^= (long)(bytarr[(length&~7)+1]&0xff) << 8;
		    case 1: h ^= (long)(bytarr[length&~7]&0xff);
		    h *= m;
       };
		     
       h ^= h >>> r;
	   h *= m;
	   h ^= h >>> r;
	   return h;
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
	
