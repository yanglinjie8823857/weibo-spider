package cn.edu.zju.ccnt.spider.fetcher;

import java.util.ArrayList;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.zju.ccnt.spider.parser.WeiboParser;
import cn.edu.zju.ccnt.spider.parser.bean.Page;
import cn.edu.zju.ccnt.spider.queue.VisitedWeiboUrlQueue;
import cn.edu.zju.ccnt.spider.queue.WeiboUrlQueue;
import cn.edu.zju.ccnt.spider.utils.Constants;
import cn.edu.zju.ccnt.spider.utils.FetcherType;
import cn.edu.zju.ccnt.spider.utils.Utils;

public class WeiboFetcher {
	public static int weibonums=0;
	private static final Logger Log = Logger.getLogger(WeiboFetcher.class.getName());
	
	/**
	 * 根据url爬取网页内容
	 * @param url
	 * @return
	 */
	@SuppressWarnings({ "finally", "unused" })
	public static Page getContentFromUrl(String url,CookieStore cookie){
		String content = null;
		Document contentDoc = null;
		
		// 设置GET超时时间
		HttpParams params = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
	    HttpConnectionParams.setSoTimeout(params, 10 * 1000);	    
	    
		AbstractHttpClient httpClient = new DefaultHttpClient(params);
		httpClient.setCookieStore(cookie);
		
		HttpGet getHttp = new HttpGet(url);
		// 设置HTTP Header
		getHttp.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
		HttpResponse response;
		
		try{
			// 获得信息载体
			response = httpClient.execute(getHttp);
			HttpEntity entity = response.getEntity();	
			
			if(entity != null){
				// 转化为文本信息, 设置爬取网页的字符集，防止乱码
				content = EntityUtils.toString(entity, "UTF-8");
				Log.info(">> login NEW content: \n" + content);
		
					Log.info("222222222222");
					String returnMsg = Utils.checkContent(content, url, FetcherType.WEIBO);
				Log.info("111111111111");
				Log.info(returnMsg);
//					
//					}
//				}

				// 将content字符串转换成Document对象
				contentDoc = WeiboParser.getPageDocument(content);
		
				// 判断是否符合下载网页源代码到本地的条件
				List<Element> weiboItems = new ArrayList<Element>();
				
				Elements elements = contentDoc.getElementsByClass("c");
				for(Element el: elements){
					if(el.id().startsWith("M_")){
						weiboItems.add(el);
						weibonums++;
					}
				}
				
				// 微博数量超过限制，过滤掉，使其拿不到后续链接自动结束
				if(weiboItems == null){
					contentDoc = new Document("");
				}
				
				if(weiboItems != null && weiboItems.size() > 0){
					Log.info("444444");
					WeiboParser.createFile(weiboItems, url);
				}				
			}
		}
		catch(Exception e){
			Log.error(e);
			
			// 处理超时，和请求忙相同
			url = url.split("&gsid")[0];
			Log.info("lalallalalal");
			Log.info(">> Put back url: " + url);
			WeiboUrlQueue.addFirstElement(url);
			return new Page(Constants.SYSTEM_BUSY, null);
		}
		finally{
		VisitedWeiboUrlQueue.addElement(url);
		return new Page(content, contentDoc);}
	}
}
