
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class MinHash {
int k;
String[] fileNames;
String folder;

HashMap<String,Integer> termIndexMap = new HashMap<String, Integer>();
HashMap<String,HashSet<Integer>> documentTermMap = new HashMap<String, HashSet<Integer>>();
int termIndex=0,prime;
int[] a,b;
long[][] hashValues;
int[][] minHashMatrix;
	
	public static void main(String[] args)
	{
		double exactJaccard, approxJaccard;
		MinHash minHash=new MinHash("space",800);
		String[] allFiles = minHash.allDocs("space");
		
		
		//	exactJaccard = minHash.exactJaccard(allFiles[0],allFiles[1]);
		//	System.out.println("Exact Jaccard Similarity between doc  0 and 1 is : "+exactJaccard);
			
		//	approxJaccard = minHash.approximateJaccard(allFiles[0],allFiles[1]);
		//	System.out.println("Approximate Jaccard Similarity between doc  0 and 1 is : "+approxJaccard);
			
	}

	public MinHash(String folder, int numPerm)
	{
		this.folder= folder;
		this.k=numPerm;
		fileNames = allDocs(folder);
		run(fileNames); //extracts terms from files, generates documentTermMap and termIndexMap
		generateHashFunctions(k);
		minHashMatrix(fileNames);
	}
	
	
	
	public String[] allDocs(String folderName)
	{
		File FolderName = new File(folderName);
		File[] listOfFiles = FolderName.listFiles();
		ArrayList<String> filelist = new ArrayList<String>();

		    for (int i = 0; i < listOfFiles.length; i++) {
		    	if(listOfFiles[i].getName().indexOf(".txt")!=-1){
		    		filelist.add( listOfFiles[i].getName());
		    	}
		    	
		    }
		return (filelist.toArray(new String[filelist.size()])) ;
	}
	private void run(String[] fileArray)
	{
		for (int i=0;i<fileArray.length;i++ )
		{
			ExtractTerms(fileArray[i]);
		//	minHashMatrix();
		}
	}
	
	private void ExtractTerms(String fileName)
	{
		String currentLine;
		//HashSet<String> terms=new HashSet<String>();
		HashSet<Integer> termID=new HashSet<Integer>();
        
        	int noTerms = 0;
          try {
            //Read file1
            BufferedReader br = new BufferedReader(new FileReader(folder+File.separator+fileName));
            while ((currentLine = br.readLine()) != null) {
                //Call getTerms for the currentLine
            	
            	String[] t = currentLine.toLowerCase().replaceAll("[.,:;'\"]", " ").split(" +");
            	for (String str : t) {
            		
            		 if (str.length() > 2 && !str.equals("the"))
            		 {	
            			 noTerms++;
            			 if(!termIndexMap.containsKey(str))
            			 {
                			termIndexMap.put(str, termIndex);
                			termID.add(termIndex);
                            termIndex++; 
            			 }
            			 else
            			 {
            				 termID.add(termIndexMap.get(str));
            			 }			
            		 } 
            		
                    
                }
            	
            	
            	/*StringTokenizer st = new StringTokenizer(currentLine, " .,:;'");
            	 while (st.hasMoreTokens()) {
            		 term = st.nextToken().trim();
            		 terms.add(term); 
            		 if (term.length() > 2 && !term.equals("the"))
            		 {	
            			 if(!termIndexMap.containsKey(term))
            			 {
                			termIndexMap.put(term, termIndex);
                			termID.add(termIndex);
                            termIndex++; 
            			 }
            			 else
            			 {
            				 termID.add(termIndexMap.get(term));
            			 }			
            		 }   
                 }*/
            }   
            if(!documentTermMap.containsKey(fileName))
            {
            		documentTermMap.put(fileName, termID);
            } 
            	//if(fileName.contains("space-98.txt"))
            	//System.out.println("Terms in thie file = "+noTerms);

            		br.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
	}
	
	private void generateHashFunctions(int k)
	{
		a=new int[k];
		b=new int[k];
		int aRand,bRand;
		prime = generatePrimeNumber(termIndexMap.size());
	    Random rn = new Random();
	    for (int i = 0; i < k; i++) {
            //Generate k random values a and b
            aRand = rn.nextInt(prime);
            bRand = rn.nextInt(prime);
            a[i]=aRand;
            b[i]=bRand;
            //(a[i]*x + b[i])%prime are the k hash functions.       
	    }	
	}
	
	   private int generatePrimeNumber(int setSize){
	        for(int num=setSize+1;;num++){
	            int i, numHalf = num/2;
	            for(i=2;i<=numHalf;i++){
	                if(num%i == 0){
	                    break;
	                }
	            }
	            if(i==numHalf+1) return num;
	        }
	    }
	
	public int[] minHashSig(String filename)
	{
		HashSet<Integer> termIDList ;//= new ArrayList<Integer>();
		termIDList = documentTermMap.get(filename);
		Integer min, current;
		int[] docSignature = new int[k];
		for(int i=0;i<k;i++)
		{		
			    min=Integer.MAX_VALUE;
			    
				for(int x: termIDList)
				{
					//(A+B)%m = (A%m + B%m) %m
					current = (int) (((a[i]*(double)x)%prime+b[i]%prime)%prime);
					min=Math.min(current, min);
					
					//current = (a[i]*x+b[i])%prime;
					//min=Math.min(current, min);
					
				}
			
				docSignature[i]=min;
				
		}
		
		return docSignature;
	}   
	   
	public int[][] minHashMatrix(String[] fileList)
	{
		minHashMatrix=new int[k][fileList.length]; 
		int[] docSign;
		for(int i=0;i<fileList.length;i++)
		{
			docSign= minHashSig(fileList[i]);
			for(int j=0;j<k;j++)
			{
				minHashMatrix[j][i]=(int) docSign[j];
				if(minHashMatrix[j][i]<0)
				{
					System.out.println("NEGATIVE VALUE IN MIN_HASH_MAT : "+minHashMatrix[j][i]);
					System.out.println("FOUND FOR DOCUMENT : "+fileList[i]);
				}
			}
		}
		return minHashMatrix;
	}
	
	public double exactJaccard(String file1, String file2)
	{
		HashSet<Integer> termsFile1 = new HashSet<Integer>(documentTermMap.get(file1));
		HashSet<Integer> termsFile2; 
		int totalTerms;
		double exactJacSim;
		//Collections.copy(termsFile1,documentTermMap.get(file1));
		termsFile2 = documentTermMap.get(file2);
		totalTerms = termsFile1.size() + termsFile2.size();
		termsFile1.retainAll(termsFile2);//termsFile1 will only have intersection terms of file1 and file2
		exactJacSim = (double)termsFile1.size()/(totalTerms-termsFile1.size());
		
		return exactJacSim ;
	}
	
	public double approximateJaccard(String file1, String file2)
	{
		int file1Index=0,file2Index=0,commonHashValue=0;
		double approxJacSim;
		for(int i=0;i<fileNames.length;i++)
		{
			if(fileNames[i].equals(file1))
			{
				file1Index=i;
			}	
			else if(fileNames[i].equals(file2))
			{
				file2Index=i;
			}
		}
		for(int i=0;i<k;i++)
		{
			if(minHashMatrix[i][file1Index]==minHashMatrix[i][file2Index])
			{
				commonHashValue++;
			}
		}
		approxJacSim = (double)commonHashValue/k ;
		return approxJacSim ;
	}
	
	
	
	public int numterms()
	{
		return termIndexMap.size();
	}
	
	public int numPermutations()
	{
		return k;
	}

}
