/**
 * @author Rahul Singh 
 * @author Prachi Patel
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.lang.*;

public class WikiCrawler {
	private static final String BASE_URL = "http://web.cs.iastate.edu/~pavan/";
	private String seedUrl, fileName, robotsTxt;
	private String[] keywords;
	private int max, reqCount;
	private PriorityQueue<Tuple> queueLinks;
	private Hashtable queueVisited, leftNodes, robotLinks;
	private HashSet<String> irrelevant = new HashSet<String>();
	private boolean isWeighted;
	private int timeStamp;
	
	
	public static void main(String[] args) {
		long t1 = System.currentTimeMillis();
		System.out.println("Crawling: \n");
		String[] topics = {"tennis"};
		Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of output file: ");         //Enter the name of output file.
        String filename = sc.nextLine();
        System.out.println("Enter max: ");   //Enter the value of max.
        int max = sc.nextInt();  
	
		WikiCrawler w = new WikiCrawler("/wiki/cs535_Seed1.html", topics, max, filename, true);
		w.crawl();
		long t2 = System.currentTimeMillis();
		System.out.println("Time: "+(t2-t1));

	}

	/**
	 * 
	 * @param seedUrl is the root address where crawling starts
	 * @param keywords list of target words
	 * @param max  is the maximum num of keywords
	 * @param fileName Name of the output file
	 * @param isWeighted Is it necessary to calculate weights.
	 */
	
	public WikiCrawler(String seedUrl, String[] keywords, int max, String fileName, boolean isWeighted){
		this.seedUrl = seedUrl;
		this.fileName = fileName;
		this.keywords = keywords;
		this.max = max;
		this.isWeighted = isWeighted;
		this.reqCount = 0;
		queueLinks = new PriorityQueue<Tuple>();
		queueVisited = new Hashtable();
		leftNodes = new Hashtable();
		robotLinks = new Hashtable();
		try {
			robotsTxt = getPageContent("/robots.txt", false);
			robotsTxt = robotsTxt.substring(robotsTxt.indexOf("User-agent: *")
					+"User-agent: *".length());
			robotsExclude();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage()+" : /robots.txt");
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage()+" : /robots.txt");
		} catch (IOException e) {
			System.out.println(e.getMessage()+" : /robots.txt");
		}
	}
	/**
	 * Calculates the weight of different webpages. 
	 * @param link - whose weight needs to be calculated
	 * @param pageContent - the content of the current link.
	 * @return
	 */

	public double getWeight(String link, String pageContent, String linkTitle) {
		double minweight = 0.0, weight = 0.0;
		if(isWeighted == false) 
			return weight;
		int index, linkindex, indexinsubst;
		String subString = "";
		linkindex = pageContent.indexOf(link);
		if((linkindex - 101) < 0) {
			if(linkindex + 100  < pageContent.length())
			{
				subString = pageContent.substring(0, linkindex + 100);
			}
			else
				subString = pageContent.substring(0, pageContent.length() - 1);
		}
		else 
		{
			if(linkindex + 100  < pageContent.length())
			{
				subString = pageContent.substring(linkindex - 100, linkindex + 100);
			}
			else
				subString = pageContent.substring(linkindex - 100, pageContent.length() - 1);
		}
		for(String key : keywords) {
				indexinsubst = subString.toLowerCase().indexOf(link.toLowerCase());
				if(link.toLowerCase().contains(key.toLowerCase()) || linkTitle.toLowerCase().contains(key.toLowerCase()))
					return 1.0;
				else
				{
					index = 0;
					while(subString.toLowerCase().indexOf(key.toLowerCase(), index + 1) != -1)
					{
						index = subString.toLowerCase().indexOf(key.toLowerCase(), index + 1);
						if(minweight == 0)
						{
							if(index >= indexinsubst)
								minweight = Math.abs(index - indexinsubst - link.length() - linkTitle.length());
							else 
								minweight = Math.abs(index - indexinsubst);
						}
						else
						{
							if(index >= indexinsubst)
								minweight = Math.min(Math.abs(index - indexinsubst - link.length() - linkTitle.length()), minweight);
							else 
								minweight = Math.min(Math.abs(index - indexinsubst), minweight);
						}
						
					}
				}
			}
			if(minweight == 0.0)
				return 0.0;
			else {
				return (1/(minweight + 2));
			}	
	}
	/**
	 * This method returns the URLs in a web page
	 * @param pageContent String containing the HTML content of a web page
	 * @return Set of URLs in the web page
	 */
	public LinkedHashSet<Tuple> getLinksInPage(String pageContent){
		LinkedHashSet<Tuple> linksInPage = new LinkedHashSet<Tuple> ();
		HashMap<Tuple, Double> linktoweight = new HashMap<Tuple, Double>();
		pageContent = pageContent.substring(pageContent.indexOf("<p>")+"<p>".length());
		double weight;
		Pattern p = Pattern.compile("<a href=\"(\\S+)\" title=\"(\\S+)\">(.+?)</a>");
		Matcher m = p.matcher(pageContent);
		while (m.find()){
			String linkGroup = m.group(1);
			String linkTitle = m.group(3);
			if(!linkGroup.contains("#") && !linkGroup.contains(":")
					&& (linkGroup.startsWith("/wiki/")) && !robotLinks.containsKey(linkGroup))
			{
				weight = getWeight(linkGroup, pageContent, linkTitle);
				Tuple linkinPage = new Tuple(linkGroup, weight, 0);
				if(linksInPage.contains(linkinPage))
				{
					if(weight < linktoweight.get(linkinPage)) {
						linksInPage.remove(linkinPage);
						linksInPage.add(linkinPage);
						linktoweight.put(linkinPage, weight);
					}
					
				}
				else
				{
					linksInPage.add(linkinPage);
					linktoweight.put(linkinPage, weight);
				}
				
			}
		}
		return linksInPage;
	}
    
	
	
	/**
	 * This method returns the content of a web page
	 * @param url The URL of the web page
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String getPageContent(String url, boolean raw) throws MalformedURLException, IOException{
		String absoluteUrl = null;
		String pageContent = null;

		if(raw){
			absoluteUrl = BASE_URL+"/w/index.php?title=" 
					+ url.substring(url.indexOf("/", 1)+1)
					+"&action=raw";
		}else{
			absoluteUrl = BASE_URL+url;
		}
		if(reqCount%20==0){
			try{
				Thread.sleep(2000);
			} catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
		}

		URL link = new URL(absoluteUrl);
		InputStream is = (InputStream) link.getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		String line = null;

		while((line = br.readLine()) != null){
			sb.append(line);
			sb.append(System.getProperty("line.separator"));
		}

		pageContent = sb.toString();
		reqCount++;
		return pageContent;
	}
	
	/**
	 * This method parses the robots.txt file and adds the disallowed URLs 
	 * to a list so that they are skipped during crawling.
	 */
	public void robotsExclude(){
		Pattern p = Pattern.compile("Disallow: (\\S+)");
		Matcher m = p.matcher(robotsTxt);

		while (m.find()){
			String linkGroup = m.group(1);
			if(!linkGroup.contains("#") && !linkGroup.contains(":")
					&& (linkGroup.startsWith("/wiki/")))
				robotLinks.put(linkGroup, new Integer(1));
		}
	}
	
	/**
	 * This method starts crawling the web pages from the seed URL. 
	 */
	public void crawl() 
	{
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName);
			writer.println(max);
		} catch (FileNotFoundException e) {
			System.out.println("File "+fileName+" not found.");
		}
		timeStamp = 1;
		Tuple seedTuple = new Tuple(seedUrl, 0.0, timeStamp);
		queueLinks.add(seedTuple);
		queueVisited.put(seedTuple.getDataItem(), new Integer(1));
		String currentUrl = null;
		String htmlContent = null;
		Tuple currTuple = seedTuple;
		while(queueVisited.size()<max){
			LinkedHashSet<Tuple> urlsInPage = null;
			currTuple = queueLinks.poll();
			currentUrl = currTuple.getDataItem(); 
			leftNodes.put(currentUrl, new Integer(1));
			try {
				htmlContent = getPageContent(currentUrl, false);
			} catch (FileNotFoundException e1) {
				System.out.println(e1.getMessage()+" : "+currentUrl);
			} catch (MalformedURLException e1) {
				System.out.println(e1.getMessage()+" : "+currentUrl);
			} catch (IOException e1) {
				System.out.println(e1.getMessage()+" : "+currentUrl);
			}
			urlsInPage = getLinksInPage(htmlContent);
			for(Tuple url : urlsInPage){
					if(!url.equals(currTuple) && !queueVisited.containsKey(url.getDataItem()))
					{
						timeStamp = timeStamp + 1;
						url.settimestamp(timeStamp);
						queueLinks.add(url);
						queueVisited.put(url.getDataItem(), new Integer(1));
						writer.println(currTuple.getDataItem()+" "+url.getDataItem());
					}
					if(queueVisited.size()>=max){
						break;
					}
			}
		}
		int iter = 0;
		while(queueLinks.size() != 0 ){
			LinkedHashSet<Tuple> urlsInPage = null;
			currTuple = queueLinks.poll();
			currentUrl = currTuple.getDataItem(); 
			leftNodes.put(currentUrl, new Integer(1));
			try {
				htmlContent = getPageContent(currentUrl, false);
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage()+" : "+currentUrl);
			} catch (MalformedURLException e) {
				System.out.println(e.getMessage()+" : "+currentUrl);
			} catch (IOException e) {
				System.out.println(e.getMessage()+" : "+currentUrl);
			}
			urlsInPage = getLinksInPage(htmlContent);
			for(Tuple url : urlsInPage){
				if(!url.equals(currTuple) && (leftNodes.containsKey(url.getDataItem()) || queueLinks.contains(url))){
					writer.println(currentUrl+"  "+url.getDataItem());
				}
			}
		}
		writer.close();
		System.out.println("Request Count: "+reqCount);
	}
}
/*
 * The tuple class carries the link the weight of the link
 */

class Tuple implements Comparable
{
	private String dataItem;
	private double weight;
	private int timestamp;
	
	//Default OCnstructor
	public Tuple(){}
	
	//Parameterized Constructor
	public Tuple(String dataItem, double weight, int timestamp){
		this.dataItem = dataItem;
		this.weight = weight;
		this.timestamp = timestamp;
	}
	
	public String getDataItem() {
		return dataItem;
	}

	public void setDataItem(String dataItem) {
		this.dataItem = dataItem;
	}

	public double getWeight() {
		return weight;
	}
	public int getTimestamp() {
		return timestamp;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void settimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	
	
	public int compareTo(Object o) {
		Tuple t = (Tuple) o;
		if(this.weight > t.weight)
			return -1;
		else 
		{
			if(this.weight < t.weight)
				return 1;
			else {
				if(this.timestamp > t.timestamp)
					return 1;
				else
					return -1;
			}
		}
    }
	@Override
	public int hashCode() {
		return this.dataItem.hashCode();
	}
	
	@Override
	public boolean equals(Object t) {
        boolean retVal = false;
        if (t instanceof Tuple){
            Tuple thisT = (Tuple) t;
            retVal = thisT.dataItem.equals(this.dataItem);
        }
     return retVal;
  }
}




