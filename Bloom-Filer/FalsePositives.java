
import java.util.Random;
import java.util.*;
import java.lang.*;
public class FalsePositives {
	public static void main(String[] args) {
		int sizeOfstring = 7;
		int setSize = 500000;
		int bitsPerElement = 16;
		String randomStr;
		HashSet<String> strArr = new HashSet<String>();
		int error_FNV = 0;
		int error_Murmur = 0;
		int error_Ran = 0;
		int error_Dynamic = 0;
		BloomFilterFNV bfFNV = new BloomFilterFNV(setSize, bitsPerElement);
		BloomFilterMurmer bfMurmur = new BloomFilterMurmer(setSize, bitsPerElement);
		BloomFilterRan bfRan = new BloomFilterRan(setSize, bitsPerElement);
		DynamicFilter bfDynamic = new DynamicFilter(bitsPerElement);
		/**
		 * Add strings to the object of each BloomFilter
		 *  		
		*/ 
		for(int i = 0; i < setSize; i++) {
			randomStr = randomString(sizeOfstring);
			strArr.add(randomStr);
			bfFNV.add(randomStr);
			bfMurmur.add(randomStr);
			bfRan.add(randomStr);
			bfDynamic.add(randomStr);
		}
		/**
		 * Check for false positives
		 */
		for(int i = 0; i < setSize; i++) {
			randomStr = randomString(sizeOfstring);
			if(!strArr.contains(randomStr))
				if(bfFNV.appears(randomStr)) {
					error_FNV = error_FNV + 1;
				}
				if(bfMurmur.appears(randomStr)) {
					error_Murmur = error_Murmur + 1;
				}
				if(bfRan.appears(randomStr)) {
					error_Ran = error_Ran + 1;
				}
				if(bfDynamic.appears(randomStr)) {
					error_Dynamic = error_Dynamic + 1;
				}
		}
		System.out.println("The accuracy of the BloomFilter for bitsperElement = :"+ bitsPerElement);
		System.out.println("1.) BloomFilterFNV -> " + (float)(error_FNV)/(float)(setSize));
		System.out.println("2.) BloomFilterMurmur -> " + (float)(error_Murmur)/(float)(setSize));
		System.out.println("3.) BloomFilterRandom -> " + (float)error_Ran/(float)(setSize));
		System.out.println("4.) DynamicFilter -> " + (float)error_Dynamic/(float)(setSize));

		
	}
	
	public static String randomString( int length) {
		  
	    int leftnumber = 97;
	    int randNumber;
	    int rightnumber = 122; 
	    Random rand = new Random();
	    StringBuilder buffer = new StringBuilder(length);
	    for (int i = 0; i < length; i++) {
	        randNumber = leftnumber + (int)(rand.nextFloat() * (leftnumber - rightnumber + 1));
	        buffer.append((char) randNumber);
	    }
	    String randString = buffer.toString();
	 
	    return randString;
	}
}

