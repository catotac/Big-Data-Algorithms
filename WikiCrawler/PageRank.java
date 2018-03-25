import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class PageRank {
	double beta=0.85, epsilon;
	int noOfNodes, numOfEdges;
    double pageRankVector[];
    
    HashMap<String, Vertex> webGraph = new HashMap<String, Vertex>();

    public static void main(String[] args) {
        /**
         * Sample run to print top k nodes.
         */
    		PageRank p = new PageRank("output.txt",0.01);
        String[] result = p.topKPageRank(10);
        System.out.println("\nTop 10 page ranks");
        for (int i = 0; i < 10; i++) {
            System.out.println(result[i] + ", Rank: "+ p.pageRankOf(result[i]));
        }
        result = p.topKInDegree(10);
        System.out.println("\nTop 10 in degree ");
        for (int i = 0; i < 10; i++) {
            System.out.println(result[i]);
        }
        result = p.topKOutDegree(10);
        System.out.println("\nTop 10 out degree");
        for (int i = 0; i < 10; i++) {
            System.out.println(result[i]);
        }
    }

    public String[] topKInDegree(int k){
        Object[] neighborsArray = webGraph.values().toArray();
        Comparator inDegreeComparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {  //this comparator sort the array in descending order. That why -1 and 1 are switched
                if(!o1.getClass().equals(Vertex.class) && !o2.getClass().equals(Vertex.class)){
                    return 1; // throw an exception here ideally
                }
                if(((Vertex)o1).inDegree > ((Vertex)o2).inDegree){
                    return -1;
                }
                else if(((Vertex)o1).inDegree < ((Vertex)o2).inDegree){
                    return 1;
                }
                else if(((Vertex)o1).inDegree == ((Vertex)o2).inDegree){
                    return 0;
                }
                return 1;
            }
        };
        Arrays.sort(neighborsArray,inDegreeComparator);
        String[] topKInDegree = new String[k];
        for (int i = 0; i < k; i++) {
            topKInDegree[i] = ((Vertex)neighborsArray[i]).name;
        }
        return topKInDegree;
    }

    public String[] topKOutDegree(int k){
        Object[] neighborsSet = webGraph.values().toArray();
        Comparator outDegreeComparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {  //this comparator sort the array in descending order. That why -1 and 1 are switched
                if(!o1.getClass().equals(Vertex.class) && !o2.getClass().equals(Vertex.class)){
                    return 1; // throw an exception here ideally
                }
                if(((Vertex)o1).outDegree > ((Vertex)o2).outDegree){
                    return -1;
                }
                else if(((Vertex)o1).outDegree < ((Vertex)o2).outDegree){
                    return 1;
                }
                else if(((Vertex)o1).outDegree == ((Vertex)o2).outDegree){
                    return 0;
                }
                return 1;
            }
        };
        Arrays.sort(neighborsSet,outDegreeComparator);
        String[] topKOutDegree = new String[k];
        for (int i = 0; i < k; i++) {
            topKOutDegree[i] = ((Vertex)neighborsSet[i]).name;
        }
        return topKOutDegree;
    }

    public String[] topKPageRank(int k){
        Object[] neighborsSet = webGraph.values().toArray();
        Comparator rankComparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {  //this comparator sort the array in descending order. That why -1 and 1 are switched
                if(!o1.getClass().equals(Vertex.class) && !o2.getClass().equals(Vertex.class)){
                    return 1; // throw an exception here ideally
                }
                int vertex1Index = ((Vertex)o1).index;
                int vertex2Index = ((Vertex)o2).index;
                if(pageRankVector[vertex1Index] > pageRankVector[vertex2Index]){
                    return -1;
                }
                else if(pageRankVector[vertex1Index] < pageRankVector[vertex2Index]){
                    return 1;
                }
                else if(pageRankVector[vertex1Index] == pageRankVector[vertex2Index]){
                    return 0;
                }
                return 1;
            }
        };
        Arrays.sort(neighborsSet,rankComparator);
        String[] topKRankedPages = new String[k];
        for (int i = 0; i < k; i++) {
            topKRankedPages[i] = ((Vertex)neighborsSet[i]).name;
        }
        return topKRankedPages;
    }
    
    public PageRank(String fileName, double approxmiation){
        //Read the file
        String currentLine;
        this.epsilon = approxmiation;
        //read firstLine as number of neighborsSet
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            noOfNodes =Integer.valueOf( br.readLine());
            pageRankVector = new double[noOfNodes];
            while ((currentLine = br.readLine()) != null) {
                //split the line into two and create the vertex objects
                String nodes[]  = currentLine.trim().split(" +");
                Vertex source = webGraph.get(nodes[0].trim());
                Vertex destination = webGraph.get(nodes[1].trim());
                if(source==null){
                    source = new Vertex(nodes[0].trim(),webGraph.size());
                    webGraph.put(nodes[0].trim(), source);
                }
                if(destination==null){
                    destination = new Vertex(nodes[1].trim(),webGraph.size());
                    webGraph.put(nodes[1].trim(), destination);
                }
                if(source.neighborsSet.add(destination)){ // increment degree counts only if the add succeeded
                    source.outDegree++;
                    destination.inDegree++;
                }
                numOfEdges++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        computePageRank();
    }

    private void computePageRank() {
        //make all ranks to 1/N
        double pageRankVector_NPlus1[], pageRankVector_N[] = new double[noOfNodes];
        int t = 0;
        boolean convergerd= false;
        for (int i = 0; i < noOfNodes; i++) {
            pageRankVector_N[i] = 1/(double)noOfNodes;
        }
        while(!convergerd){
            t++;
            pageRankVector_NPlus1 = oneStepRandomWalk(pageRankVector_N);
            double norm=0, nPlus1_sum=0;
            for (int i = 0; i < noOfNodes; i++) {
                norm += Math.abs(pageRankVector_N[i]-pageRankVector_NPlus1[i]);
                //nPlus1_sum += pageRankVector_NPlus1[i];
            }
            pageRankVector_N = pageRankVector_NPlus1;
            if(Math.abs(norm)<= epsilon){
                convergerd = true;
                pageRankVector = pageRankVector_N;
                System.out.println("Iterations used for epsilon: " + t);
            }
        }
    }

    private double[] oneStepRandomWalk(double[] pageRankVector_n) {
        double pageRankVector_NPlus1[] = new double[noOfNodes];
        double initialValue = (1-beta)/noOfNodes;
        for (int i = 0; i < noOfNodes; i++) {
            pageRankVector_NPlus1[i] = initialValue;
        }
        for(String node: webGraph.keySet()){
            Vertex vertex = webGraph.get(node);
            if(vertex.outDegree!=0){
                for(Vertex outDegreeVertex: vertex.neighborsSet){
                    pageRankVector_NPlus1[outDegreeVertex.index] =  pageRankVector_NPlus1[outDegreeVertex.index] +
                            (beta*pageRankVector_n[vertex.index]/vertex.neighborsSet.size());
                }
            }
            else{
                for(Vertex outDegreeVertex: webGraph.values()) {
                    pageRankVector_NPlus1[outDegreeVertex.index] = pageRankVector_NPlus1[outDegreeVertex.index] +
                            (beta * pageRankVector_n[vertex.index] / noOfNodes);
                }
            }
        }
        return pageRankVector_NPlus1;
    }
    public double outDegreeOf(String node){
        return webGraph.get(node).outDegree;
    }

    public double inDegreeOf(String node){
        return webGraph.get(node).inDegree;
    }

    public double pageRankOf(String node){
        return pageRankVector[webGraph.get(node).index];
    }
    
    public int numOfEdges(){
        return numOfEdges;
    }

}

