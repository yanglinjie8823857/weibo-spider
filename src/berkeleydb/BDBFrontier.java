package berkeleydb;

import java.io.FileNotFoundException;

import java.util.Map.Entry;
import java.util.Set;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.DatabaseException;

public class BDBFrontier extends AbstractFrontier implements Frontier{
	  private StoredMap pendingUrisDB = null;
	  public static int threads = 5;
	  /**
	   * Creates a new instance of BDBFrontier.
	   *
	   * @param homeDirectory
	   * @throws DatabaseException
	   * @throws FileNotFoundException
	   */
	  public BDBFrontier(String homeDirectory) throws DatabaseException,
	      FileNotFoundException {
	    super(homeDirectory);
	    EntryBinding keyBinding = new SerialBinding(javaCatalog, String.class);
	    EntryBinding valueBinding = new SerialBinding(javaCatalog, CrawlUrl.class);
	    pendingUrisDB = new StoredMap(database, keyBinding, valueBinding, true);
	  }
	  /**
	   * 
	   * clearAll:
	   * 清除数据库
	   *
	   * @param	 参数
	   * @return void	返回值
	   * @throws 
	   *
	   */
	  public void clearAll() {
	    if(!pendingUrisDB.isEmpty())
	      pendingUrisDB.clear();
	  }
	  /**
	   * 获得下一条记录
	   * @see com.fc.frontier.Frontier#getNext()
	   */
	  @Override
	  public synchronized CrawlUrl getNext() throws Exception {
	    CrawlUrl result = null;
	    while(true) {
	      if(!pendingUrisDB.isEmpty()) {
	        Set entrys = pendingUrisDB.entrySet();
	        @SuppressWarnings("unchecked")
			Entry<String, CrawlUrl> entry = (Entry<String, CrawlUrl>) pendingUrisDB.entrySet().iterator().next();
	        result = entry.getValue();		//下一条记录
	        delete(entry.getKey());			//删除当前记录
	        System.out.println("get:" + homeDirectory + entrys);
	        return result;
	      }
	      else {
	        threads --;
	        if(threads > 0) {
	          wait();
	          threads ++;
	        }
	        else {
	          notifyAll();
	          return null;
	        }
	      }
	    }
	  }
	  /**
	   * 存入url
	   * @see com.fc.frontier.Frontier#putUrl(com.fc.CrawlUrl)
	   */
	  @Override
	  public synchronized boolean putUrl(CrawlUrl url) throws Exception {
	    if(url.getOriUrl() != null && !url.getOriUrl().equals("") 
	        && !pendingUrisDB.containsKey(url.getOriUrl())) 
	    {
	      @SuppressWarnings("rawtypes")
		Set entrys = pendingUrisDB.entrySet();
	      put(url.getOriUrl(), url);
	      notifyAll();
	      System.out.println("put:" + homeDirectory + entrys);
	      return true;
	    }
	    return false;
	  }
	  public boolean contains(Object key) {
	    if(pendingUrisDB.containsKey(key))
	      return true;
	    return false;
	  }
	  /**
	   * 存入数据库
	   * @see com.fc.frontier.AbstractFrontier#put(java.lang.Object, java.lang.Object)
	   */
	  @Override
	  protected synchronized void put(Object key, Object value) {
	    pendingUrisDB.put(key, value);
	  }
	  /**
	   * 从数据库取出
	   * @see com.fc.frontier.AbstractFrontier#get(java.lang.Object)
	   */
	  @Override
	  protected synchronized Object get(Object key) {
	    return pendingUrisDB.get(key);
	  }
	  public boolean isempty(){
		  return pendingUrisDB.isEmpty();
	  }
	  /**
	   * 删除
	   * @see com.fc.frontier.AbstractFrontier#delete(java.lang.Object)
	   */
	  @Override
	  protected synchronized Object delete(Object key) {
	    return pendingUrisDB.remove(key);
	  }
	  /**
	   * 
	   * calculateUrl:
	   * 对Url进行计算，可以用压缩算法
	   *
	   * @param	 参数
	   * @return String	返回值
	   * @throws 
	   *
	   */
	  private String calculateUrl(String url) {
	    return url;
	  }
//	  public static void main(String[] strs) {
//	    try {
//	      BDBFrontier bdbFrontier = new BDBFrontier("d:\\cache");
//	      CrawlUrl url = new CrawlUrl();
//	      url.setOriUrl("http://www.163.com");
//	      bdbFrontier.putUrl(url);
//	      System.out.println(((CrawlUrl)bdbFrontier.getNext()).getOriUrl());
//	      bdbFrontier.close();
//	    }catch(Exception e) {
//	      e.printStackTrace();
//	    }
//	  }
	}