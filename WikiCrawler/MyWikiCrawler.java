
public class MyWikiCrawler {

    public static void main(String[] args) {
        String[] keywords = {"cricket", "world cup", "sport", "pitch", "innings", "score", "batsman"};
        WikiCrawler wikiCrawler = new WikiCrawler("/wiki/Cricket",keywords,100,"MyWikiGraph.txt",true);
        wikiCrawler.crawl();
    }
}