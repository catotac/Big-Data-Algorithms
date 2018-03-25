import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class MyWikiRanker {
    public static void main(String[] args) {

        final int k=3;

        String graphFileName = "output.txt";// "MyWikiGraph.txt";

        System.out.println("Results with e=0.01");
        PageRank pageRank = new PageRank(graphFileName,0.01);
        MyWikiRanker wikiTennisRanker = new MyWikiRanker();

        String[] result = pageRank.topKPageRank(k);
        wikiTennisRanker.writeArrayToFile("top"+k+"ranks_01.txt", result);
        result = pageRank.topKInDegree(k);
        wikiTennisRanker.writeArrayToFile("top"+k+"InDegree_01.txt", result);
        result = pageRank.topKOutDegree(k);
        wikiTennisRanker.writeArrayToFile("top"+k+"OutDegree_01.txt", result);

        System.out.println(String.format("%-20s %-20s  %-20s" , "File1", "File2", "Exact Jaccard" ));
        System.out.println(String.format("%-20s %-20s  %f" , "Top"+k+"Ranks", "Top"+k+"InDegree", wikiTennisRanker.JaccardSimilarity("top"+k+"ranks_01.txt", "top"+k+"InDegree_01.txt") ));
        System.out.println(String.format("%-20s %-20s  %f" , "Top"+k+"Ranks", "Top"+k+"OutDegree", wikiTennisRanker.JaccardSimilarity("top"+k+"ranks_01.txt", "top"+k+"OutDegree_01.txt") ));
        System.out.println(String.format("%-20s %-20s  %f" , "Top"+k+"InDegree", "Top"+k+"OutDegree", wikiTennisRanker.JaccardSimilarity("top"+k+"InDegree_01.txt", "top"+k+"OutDegree_01.txt") ));

        System.out.println("\nResults with e=0.005");
        pageRank = new PageRank(graphFileName,0.005);
        result = pageRank.topKPageRank(k);
        wikiTennisRanker.writeArrayToFile("top"+k+"ranks_005.txt", result);
        result = pageRank.topKInDegree(k);
        wikiTennisRanker.writeArrayToFile("top"+k+"InDegree_005.txt", result);
        result = pageRank.topKOutDegree(k);
        wikiTennisRanker.writeArrayToFile("top"+k+"OutDegree_005.txt", result);

        System.out.println(String.format("%-20s %-20s  %-20s" , "File1", "File2", "Exact Jaccard" ));
        System.out.println(String.format("%-20s %-20s  %f" , "Top"+k+"Ranks", "Top"+k+"InDegree", wikiTennisRanker.JaccardSimilarity("top"+k+"ranks_005.txt", "top"+k+"InDegree_005.txt") ));
        System.out.println(String.format("%-20s %-20s  %f" , "Top"+k+"Ranks", "Top"+k+"OutDegree", wikiTennisRanker.JaccardSimilarity("top"+k+"ranks_005.txt", "top"+k+"OutDegree_005.txt") ));
        System.out.println(String.format("%-20s %-20s  %f" , "Top"+k+"InDegree", "Top"+k+"OutDegree", wikiTennisRanker.JaccardSimilarity("top"+k+"InDegree_005.txt", "top"+k+"OutDegree_005.txt") ));
    }

    public double JaccardSimilarity(String file1, String file2){
        return new JaccardSimilarity(file1, file2).exactJaccard();
    }

    public void writeArrayToFile(String fileName, String[] result) {
        File file = new File(fileName);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName, "UTF-8");

            for (String word : result) {
                writer.println(word);
            }
        }
        catch (UnsupportedEncodingException  | FileNotFoundException e) {
                e.printStackTrace();
        }
        finally{
            writer.close();
        }
    }

    public class JaccardSimilarity {
        String fileList[];

        public JaccardSimilarity(String file1, String file2) {
            fileList = new String[2];
            fileList[0] = file1;
            fileList[1] = file2;
        }

        /* Calculates the exactJaccard of the given two files
        * */
        public double exactJaccard(){
            HashSet<String> file1Terms = new HashSet<String>();
            HashSet<String> file2Terms = new HashSet<String>();

            file1Terms = getTerms(fileList[0]);
            file2Terms = getTerms(fileList[1]);

            double totalTerms = file1Terms.size() + file2Terms.size();
            file1Terms.retainAll(file2Terms); // removes non duplicates, results in A = A intesection B
            return ((double)file1Terms.size())/(totalTerms-file1Terms.size());
        }

        /**
         * This method get the terms form a file and returns them in a hashSet
         * */
        public HashSet<String> getTerms(String fileName){
            String currentLine, terms[];
            HashSet<String> fileTerms = new HashSet<String>();
            try {
                //Read file1
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                while ((currentLine = br.readLine()) != null) {
                    fileTerms.add(currentLine);
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return fileTerms;
        }
    }
}
