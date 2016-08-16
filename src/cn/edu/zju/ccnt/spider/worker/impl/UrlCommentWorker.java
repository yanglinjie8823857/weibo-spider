package cn.edu.zju.ccnt.spider.worker.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.apache.http.client.CookieStore;
import org.apache.log4j.Logger;

import cn.edu.zju.ccnt.spider.fetcher.CommentFetcher;
import cn.edu.zju.ccnt.spider.handler.NextUrlHandler;
import cn.edu.zju.ccnt.spider.parser.CommentParser;
import cn.edu.zju.ccnt.spider.parser.bean.Account;
import cn.edu.zju.ccnt.spider.queue.AccountQueue;
import cn.edu.zju.ccnt.spider.queue.CommentUrlQueue;
import cn.edu.zju.ccnt.spider.queue.WeiboUrlQueue;
import cn.edu.zju.ccnt.spider.utils.Constants;
import cn.edu.zju.ccnt.spider.utils.Utils;
import cn.edu.zju.ccnt.spider.worker.BasicWorker;

/**
 * 从UrlQueue中取出url，下载页面，分析url，保存已访问rul
 * @author yuki
 *
 */
public class UrlCommentWorker extends BasicWorker implements Runnable {
	private static final Logger Log = Logger.getLogger(UrlCommentWorker.class.getName());
	protected String username = null;
	protected String password = null;
	public CookieStore cookie;
	public static String sign="sign";
	public UrlCommentWorker(CookieStore cookie){
		this.cookie=cookie;
	}
	/**
	 * 下载对应页面并分析出页面对应URL，放置在未访问队列中
	 * @param url
	 * 
	 * 返回值：被封账号/系统繁忙/OK
	 */

	@Override
	public void run() {
		// 首先获取账号并登录
			
		synchronized(sign){
			// 登录成功
			if(cookie != null) {
							while(true) {
								try {
									if(CommentUrlQueue.isEmpty()){
										
										try {
											Utils.initializeCommentUrl();
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
										
										String result = dataHandler(CommentUrlQueue.outElement(),cookie);
								
									try {
										if(CommentUrlQueue.isEmpty()){
											
											Utils.initializeCommentUrl();
											if(CommentUrlQueue.isEmpty()){
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
		Log.info("spider stop...");
		
		}
		}
	@Override
	public String dataHandler(String url, CookieStore cookie)
			throws IOException {
		// TODO Auto-generated method stub
		return NextUrlHandler.addNextCommentUrl(CommentFetcher.getContentFromUrl(url,cookie));
	}
}
