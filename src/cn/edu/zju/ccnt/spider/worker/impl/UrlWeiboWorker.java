package cn.edu.zju.ccnt.spider.worker.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.apache.http.client.CookieStore;
import org.apache.log4j.Logger;

import sun.security.krb5.internal.LoginOptions;
import berkeleydb.MyCrawler;
import cn.edu.zju.ccnt.spider.fetcher.WeiboFetcher;
import cn.edu.zju.ccnt.spider.handler.NextUrlHandler;
import cn.edu.zju.ccnt.spider.parser.WeiboParser;
import cn.edu.zju.ccnt.spider.parser.bean.Account;
import cn.edu.zju.ccnt.spider.queue.AccountQueue;
import cn.edu.zju.ccnt.spider.queue.WeiboUrlQueue;
import cn.edu.zju.ccnt.spider.utils.Constants;
import cn.edu.zju.ccnt.spider.utils.Utils;
import cn.edu.zju.ccnt.spider.worker.BasicWorker;

/**
 * 从UrlQueue中取出url，下载页面，分析url，保存已访问rul
 * @author yuki
 *
 */
public class UrlWeiboWorker extends BasicWorker implements Runnable {
	
	public  int threadno;
	public static String sign="sign";
	private static final Logger Log = Logger.getLogger(UrlWeiboWorker.class.getName());
//	public static int threadsnum=10;
	public static int count=0;
	public static int i;
	public CookieStore cookie;
	public UrlWeiboWorker(int threadno,CookieStore cookie){
		this.threadno=threadno;
		this.cookie=cookie;
	}
	
	/**
	 * 下载对应页面并分析出页面对应URL，放置在未访问队列中
	 * @param url
	 * 
	 * 返回值：被封账号/系统繁忙/OK
	 * @throws Exception 
	 * 
	 */
	public String dataHandler(String url,CookieStore cookie) throws Exception{
		return NextUrlHandler.addNextWeiboUrl(WeiboFetcher.getContentFromUrl(url,cookie));
	}
	
	
	public void run() {
	
		synchronized(sign){
			// 登录成功
			if(cookie != null) {
				System.out.println("登陆成功");
							while(true) {
								try {
									if(WeiboUrlQueue.isEmpty()){
										
										try {
											Utils.initializeFollowUrl1();
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
						
									// 从队列中获取URL并处理
									try{
										
										String result = dataHandler(WeiboUrlQueue.outElement(),cookie);
								
									try {
										if(WeiboUrlQueue.isEmpty()){
											
											Utils.initializeFollowUrl1();
											if(WeiboUrlQueue.isEmpty()){
												break;
											}
										}
										
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									// 针对处理结果进行处理：OK, SYSTEM_BUSY, ACCOUNT_FORBIDDEN
									if(result.equals(Constants.OK)){	
										Thread.sleep(200);
									}
									// 系统繁忙
									else if(result.equals(Constants.SYSTEM_BUSY)){
										Log.info(">> System busy, retry after 5s...");
										Thread.sleep(5 * 1000);
									}
									// 账号被冻结，切换账号继续执行
									else if(result.equals(Constants.ACCOUNT_FORBIDDEN)){
										Log.info(">> " + (new Date()).toString() + ": " + username + " account has been frozen!");						
										// 切换账号
										cookie = switchAccountForCookie();
										while(cookie == null){
											// 队列中的所有账号当前均不可用，停顿5分钟，再试
											Thread.sleep(5 * 60 * 1000);
											cookie = switchAccountForCookie();
										}
									}
									}
									catch (IOException e) {
										Log.error(e);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
										
								}
						
							
							
							
	
				
			}
			else{
				Log.info(">> " + username + " login failed!");
			}

	
		
		// 关闭数据库连接
//		try {
//			WeiboParser.conn.close();
//			Utils.conn.close();
//		} 
//		catch (SQLException e) {
//			Log.error(e);
//		}
//		
		Log.info(threadno+"  spider stop...");
		
		}
	}

}
