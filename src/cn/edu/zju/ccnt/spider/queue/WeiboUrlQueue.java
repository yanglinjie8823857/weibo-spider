package cn.edu.zju.ccnt.spider.queue;

import java.util.LinkedList;

import berkeleydb.MyCrawler;

/**
 * 未访问的url队列
 * @author yuki
 *
 */
public class WeiboUrlQueue {
	// 超链接队列
	public static LinkedList<String> urlQueue = new LinkedList<String>();
	
//	public static MyCrawler mycrawler = new MyCrawler();
	// 队列中对应最多的超链接数量
	public static final int MAX_SIZE = 10000;
	
//	public static String crawling() throws Exception{
//		return mycrawler.crawling();
//	}
	
//	public synchronized static  void initCrawlerWithSeeds(String []seeds){
//		mycrawler.initCrawlerWithSeeds(seeds);
//	}
//	
//	@SuppressWarnings("static-access")
//	public synchronized static void add(String url) throws Exception{
//		mycrawler.add(url);
//	}
//	
	public synchronized static void addFirstElement(String url){
		urlQueue.addFirst(url);
	}
	
	public synchronized static String outElement(){
		return urlQueue.removeFirst();
	}
	
	public synchronized static boolean isEmpty(){
		return urlQueue.isEmpty();
	}
	
	public static int size(){
		return urlQueue.size();
	}
	
	public static boolean isContains(String url){
		return urlQueue.contains(url);
	}
//	public synchronized static String geturl() throws Exception{
//		return mycrawler.unvisitedFrontier.getNext().getOriUrl();
//	} 
//	public synchronized static boolean isurlempty() throws Exception{
//		return mycrawler.unvisitedFrontier.isempty();
//	}
	
}
