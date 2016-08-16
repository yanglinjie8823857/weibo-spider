package cn.edu.zju.ccnt.spider;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.ansj.splitWord.analysis.ToAnalysis;

import com.mysql.jdbc.PreparedStatement;

import cn.edu.zju.ccnt.spider.utils.Constants;
import cn.edu.zju.ccnt.spider.utils.DBConn;

public class Test {
	
	public static void main(String[] args) throws SQLException, IOException {
		initializeParams();
		Connection conn = DBConn.getConnection();
		String querySql = "SELECT * FROM WEIBO ";
		
		Statement st = null;
		ResultSet rs = null;
		conn.setAutoCommit(false); 
		conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			
			
			st = conn.createStatement();
			rs = st.executeQuery(querySql);		
			while(rs.next()){
				String str=rs.getString("content");
				
				System.out.println(ToAnalysis.parse(str));
			}
			rs.close();
			st.close();
			conn.commit();
	}
	private static void initializeParams() throws IOException{
		InputStream in;
	
			in = new BufferedInputStream(new FileInputStream("conf/spider.properties"));
			Properties properties = new Properties();
			properties.load(in);
			
			// 从配置文件中读取数据库连接参数
			DBConn.CONN_URL = properties.getProperty("DB.connUrl");
			DBConn.USERNAME = properties.getProperty("DB.username");
			DBConn.PASSWORD = properties.getProperty("DB.password");
			
			// 从配置文件中读取根目录，并设置相关文件地址
			Constants.ROOT_DISK = properties.getProperty("spider.rootDisk");
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
			
	}

}
