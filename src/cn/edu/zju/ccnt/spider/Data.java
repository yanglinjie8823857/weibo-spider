package cn.edu.zju.ccnt.spider;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.splitWord.analysis.ToAnalysis;

import cn.edu.zju.ccnt.spider.utils.DBConn;

public class Data {
	private static void initializeParams() throws IOException{
		InputStream in;
		
			in = new BufferedInputStream(new FileInputStream("conf/spider.properties"));
			Properties properties = new Properties();
			properties.load(in);
			
			// 从配置文件中读取数据库连接参数
			DBConn.CONN_URL = properties.getProperty("DB.connUrl");
			DBConn.USERNAME = properties.getProperty("DB.username");
			DBConn.PASSWORD = properties.getProperty("DB.password");
			}
	public static void main(String[] args) throws SQLException, IOException {
		initializeParams();
		Connection conn = DBConn.getConnection();
		PreparedStatement ps = null;
		PreparedStatement pp=null;
		Statement st = null;
		ResultSet rs = null;
		String accountID = null;
		String fenci=null;
		int n=0;
		while(true){
			if(n>180000){break;}
		String querySql = "SELECT weiboID,content FROM weibo WHERE isclassified = 0 and accountID='2109370565' LIMIT 1";

	
		
			
			st = conn.createStatement();
			rs = st.executeQuery(querySql);		
			if(rs.next()){
				fenci=rs.getString("content");
				
				Pattern p= Pattern.compile("/[^/]*(,|])");
				
				Matcher m=p.matcher(ToAnalysis.parse(fenci).toString());
				fenci=m.replaceAll(" ");
				fenci=fenci.substring(1);
//				System.out.println(fenci);
				accountID = rs.getString("weiboID");
				pp=conn.prepareStatement("update weibo set isclassified=1 where weiboID=?");
				pp.setString(1, accountID);
				
				pp.execute();
				pp.close();
				ps = conn.prepareStatement("insert into fenci (weiboID,fenci) values(?,?)");
				ps.setString(1, accountID);
				ps.setString(2, fenci);
				ps.execute();
				ps.close();
			}
			rs.close();
			st.close();
			n++;
		}
			
	}
}


