package berkeleydb;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.ConnectTimeoutException;

public class MyCrawler {
	  public static BDBFrontier visitedFrontier;
	  public static BDBFrontier unvisitedFrontier;
	  private static int num = 0;  
	  public MyCrawler() {
	    try{
	      if(visitedFrontier == null){
	        visitedFrontier = new BDBFrontier("d:\\cache\\visited");	  //采用Nosql数据库存储访问地址方式
	        visitedFrontier.clearAll();
	      }
	      if(unvisitedFrontier == null) {
	        unvisitedFrontier = new BDBFrontier("d:\\cache\\unvisited");
	        unvisitedFrontier.clearAll();
	      }
	    }catch(Exception e) {
	      e.printStackTrace();
	    }
	  }
	   
	  public void initCrawlerWithSeeds(String[] seeds) {
	    synchronized (this) {
	      try {
	        for(int i = 0;i<seeds.length;i++){
	          CrawlUrl url = new CrawlUrl();			//采用berkeleyDB形式
	          url.setOriUrl(seeds[i]);
	          unvisitedFrontier.putUrl(url);
	        }
	      } catch(Exception e) {
	        e.printStackTrace();
	      }
	    }
	  }
	  public  String crawling() throws Exception {
	 
	     String visitedUrl=null;
//	      initCrawlerWithSeeds(seeds);
	      //采用berkeleyDB方式存储
	      CrawlUrl visitedCrawlUrl = (CrawlUrl)unvisitedFrontier.getNext();
	      //visitedFrontier.putUrl(visitedCrawlUrl);
	      do{
	        
	        if(visitedCrawlUrl == null) {
	          continue;
	        }
	         visitedUrl = visitedCrawlUrl.getOriUrl();
	        if(visitedFrontier.contains(visitedUrl)) {			//同步数据
	          visitedCrawlUrl = (CrawlUrl)unvisitedFrontier.getNext();
	          continue;
	        }
	        visitedFrontier.putUrl(visitedCrawlUrl);
	        if(null == visitedUrl || "".equals(visitedUrl.trim())) {   //抓取的地址为空
	          visitedFrontier.putUrl(visitedCrawlUrl);
	          visitedCrawlUrl = (CrawlUrl)unvisitedFrontier.getNext();
	          continue;
	        }
	        
	       
//	        try{
//	          RetrievePage.downloadPage(visitedUrl);					//下载页面
//	          Set<String> links = HtmlParserTool.extractLinks(visitedUrl, filter);
//	          for(String link :links) {
//	            if(!visitedFrontier.contains(link)
//	              &&!unvisitedFrontier.contains(link)	)
//	            {
//	              CrawlUrl unvisitedCrawlUrl = new CrawlUrl();
//	              unvisitedCrawlUrl.setOriUrl(link);
//	              unvisitedFrontier.putUrl(unvisitedCrawlUrl);
//	            }
//	          }
//	        }catch(ConnectTimeoutException e) {							//超时继续读下一个地址
//	          visitedFrontier.putUrl(visitedCrawlUrl);
//	          visitedCrawlUrl = (CrawlUrl)unvisitedFrontier.getNext();
//	          num ++;
//	          e.printStackTrace();
//	          continue;
//	        }catch(SocketTimeoutException e) {
//	          visitedFrontier.putUrl(visitedCrawlUrl);
//	          visitedCrawlUrl = (CrawlUrl)unvisitedFrontier.getNext();
//	          num ++;
//	          e.printStackTrace();
//	          continue;
//	        }
	        visitedCrawlUrl = (CrawlUrl)unvisitedFrontier.getNext();
	        num ++;
	      }while(BDBFrontier.threads >0 && num < 1000);
	      return visitedUrl;
	 
	  }
	  public synchronized static void add(String url) throws Exception{
     	 if(!visitedFrontier.contains(url)
	              &&!unvisitedFrontier.contains(url)	)
	            {
	              CrawlUrl unvisitedCrawlUrl = new CrawlUrl();
	              unvisitedCrawlUrl.setOriUrl(url);
	              unvisitedFrontier.putUrl(unvisitedCrawlUrl);
	            }
 	}
	}