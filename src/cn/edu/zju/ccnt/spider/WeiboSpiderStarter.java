package cn.edu.zju.ccnt.spider;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.http.client.CookieStore;
import org.apache.log4j.Logger;

import berkeleydb.MyCrawler;
import cn.edu.zju.ccnt.spider.parser.bean.Account;
import cn.edu.zju.ccnt.spider.queue.AccountQueue;
import cn.edu.zju.ccnt.spider.queue.CommentUrlQueue;
import cn.edu.zju.ccnt.spider.queue.FollowUrlQueue;
import cn.edu.zju.ccnt.spider.queue.WeiboUrlQueue;
import cn.edu.zju.ccnt.spider.utils.Constants;
import cn.edu.zju.ccnt.spider.utils.DBConn;
import cn.edu.zju.ccnt.spider.utils.Utils;
import cn.edu.zju.ccnt.spider.worker.BasicWorker;
import cn.edu.zju.ccnt.spider.worker.LoginWeibo;
import cn.edu.zju.ccnt.spider.worker.impl.UrlAbnormalWeiboWorker;
import cn.edu.zju.ccnt.spider.worker.impl.UrlCommentWorker;
import cn.edu.zju.ccnt.spider.worker.impl.UrlFollowWorker;
import cn.edu.zju.ccnt.spider.worker.impl.UrlRepostWorker;
import cn.edu.zju.ccnt.spider.worker.impl.UrlWeiboWorker;

public class WeiboSpiderStarter {
	private static final Logger Log = Logger.getLogger(WeiboSpiderStarter.class.getName());
	private static int WORKER_NUM = 1;
	private static String TYPE;
	
	
	public static void main(String[] args) throws Exception{
		// 初始化配置参数
		initializeParams();
		
		// 根据type判断爬虫任务类型
		if(TYPE.equals("weibo")){
			fetchWeibo();
		}
		else if(TYPE.equals("comment")){
			fetchComment();
		}
		else if(TYPE.equals("repost")){
			fetchRepost();
		}
		else if(TYPE.equals("abnormal")){
			fetchAbnormalWeibo();
		}
		else if(TYPE.equals("follow")){
			fetchFollowee();
		}
		else{
			Log.error("Unknown crawl type: " + TYPE + ".\n Exit...");
		}
	}
	
	/**
	 * 从配置文件中读取配置信息：数据库连接、相关文件根目录、爬虫任务类型
	 */
	private static void initializeParams(){
		InputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream("conf/spider.properties"));
			Properties properties = new Properties();
			properties.load(in);
			
			// 从配置文件中读取数据库连接参数
			DBConn.CONN_URL = properties.getProperty("DB.connUrl");
			DBConn.USERNAME = properties.getProperty("DB.username");
			DBConn.PASSWORD = properties.getProperty("DB.password");
			
			
			Constants.REPOST_LOG_PATH = Constants.ROOT_DISK + "repost_log.txt";
			Constants.COMMENT_LOG_PATH = Constants.ROOT_DISK + "comment_log.txt";
			Constants.SWITCH_ACCOUNT_LOG_PATH = Constants.ROOT_DISK + "switch_account_log.txt";
			Constants.ACCOUNT_PATH = Constants.ROOT_DISK + "account.txt";
			Constants.ACCOUNT_RESULT_PATH = Constants.ROOT_DISK + "account_result.txt";
			Constants.LOGIN_ACCOUNT_PATH = Constants.ROOT_DISK + "login_account.txt";
			Constants.ABNORMAL_ACCOUNT_PATH = Constants.ROOT_DISK + "abnormal_account.txt";
			Constants.ABNORMAL_WEIBO_PATH = Constants.ROOT_DISK + "abnormal_weibo.txt";
			Constants.ABNORMAL_WEIBO_CLEANED_PATH = Constants.ROOT_DISK + "abnormal_weibo_cleaned.txt";
			// 从配置文件中读取爬虫任务类型
			WeiboSpiderStarter.TYPE = properties.getProperty("spider.type");
			
			// 从配置文件中读取follow爬取相关参数
			
			
			// 从配置文件中读取微博相关参数
			Constants.CHECK_WEIBO_NUM = Boolean.parseBoolean(properties.getProperty("weibo.checkWeiboNum", "false"));
			if(Constants.CHECK_WEIBO_NUM){
				Constants.WEIBO_NO_MORE_THAN = Integer.parseInt(properties.getProperty("weibo.maxWeiboNum"));
			}
			
			in.close();
		} 
		catch (FileNotFoundException e) {
			Log.error(e);
		} 
		catch (IOException e) {
			Log.error(e);
		}
	}
	
	private static  void  fetchWeibo() throws Exception{
		Log.info("\n\n\n===========================\n     Fetch Weibo\n===========================\n");
		// 初始化微博页面链接
		//Utils.initializeWeiboUrl();
		Utils.initializeFollowUrl1();
//		WeiboUrlQueue.addFirstElement("http://weibo.cn/gongjibing");
		// 启动爬虫worker线程
		try {	
		      ArrayList<Thread> threadList = new ArrayList<Thread>(WORKER_NUM);
		      for(int i = 0 ; i <WORKER_NUM; i++) {
		    	 Utils.readAccountFromFile();
		    	 Account account = AccountQueue.outElement();
					
				String username = account.getUsername();
				String password = account.getPassword();
				
					// 使用账号登录
				CookieStore cookie=new LoginWeibo().loginAndGetCookie(username, password);
		    	UrlWeiboWorker weiboworker = new UrlWeiboWorker(i,cookie);
		        Thread t = new Thread(weiboworker);
		        threadList.add(t);
		        
		      }
		      long startTime=System.currentTimeMillis(); 
		      System.out.println("采集开始");
		      for(int i=0;i<threadList.size();i++){
		    	  threadList.get(i).start();
		      }
		      while(threadList.size() > 0) {
		        Thread child = (Thread) threadList.remove(0);
		        child.join();
		        
		      }
		      System.out.println("采集结束");
		      long endTime=System.currentTimeMillis(); 
		      System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
		    } catch(Exception e) {
		      e.printStackTrace();
		    }
		  }
	
	
	private static void fetchAbnormalWeibo() throws SQLException{
		Log.info("\n\n\n===========================\n     Abnormal Weibo\n===========================\n");
		// 初始化账号队列
		Utils.readAccountFromFile();
				
		// 初始化微博页面链接
		Utils.initializeAbnormalWeiboUrl();
		
		// 启动爬虫worker线程
		for(int i = 0; i < WORKER_NUM; i++){
			new Thread(new UrlAbnormalWeiboWorker()).start();
		}
	}
	
	private static void fetchComment() throws SQLException, InterruptedException{
		Log.info("\n\n\n===========================\n     Fetch Comment\n===========================\n");
		
		
		
		// 初始化评论页面链接
		Utils.initializeCommentUrl();
		

	     
	      ArrayList<Thread> threadList = new ArrayList<Thread>(WORKER_NUM);
	      for(int i = 0 ; i <WORKER_NUM; i++) {
	    	 Utils.readAccountFromFile();
	    	 Account account = AccountQueue.outElement();
			String username = account.getUsername();
			String password = account.getPassword();
				
				// 使用账号登录
			CookieStore cookie=new LoginWeibo().loginAndGetCookie(username, password);
	    	UrlCommentWorker commentworker = new UrlCommentWorker(cookie);
	        Thread t = new Thread(commentworker);
	        threadList.add(t);
	        
	      }
	      long startTime=System.currentTimeMillis(); 
	      System.out.println("采集开始");
	      for(int i=0;i<threadList.size();i++){
	    	  threadList.get(i).start();
	      }
	      while(threadList.size() > 0) {
	        Thread child = (Thread) threadList.remove(0);
	        child.join();
	        
	      }
	      System.out.println("采集结束");
	      long endTime=System.currentTimeMillis(); 
	      System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
	      
	}
	
	private static void fetchRepost() throws SQLException{
		Log.info("\n\n\n===========================\n     Fetch Repost\n===========================\n");
		// 初始化账号队列
		Utils.readAccountFromFile();
		
		// 初始化转发页面链接
		Utils.initializeRepostUrl();
		
		// 启动爬虫worker线程
		for(int i = 0; i < WORKER_NUM; i++){
			new Thread(new UrlRepostWorker()).start();
		}
	}
	
	private static void fetchFollowee() throws SQLException{
		Log.info("\n\n\n===========================\n     Fetch Followee\n===========================\n");
		// 初始化账号队列
		Utils.readAccountFromFile();
		
		// 初始化关注页面链接
		FollowUrlQueue.addFirstElement("http://weibo.cn/1239246050/follow");
				
		// 启动爬虫worker线程
		for(int i = 0; i < WORKER_NUM; i++){
			new Thread(new UrlFollowWorker()).start();
		}
	}
}
