package cn.edu.zju.ccnt.spider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.splitWord.analysis.ToAnalysis;



public class YY {
	public static void main(String[] args) throws IOException {
		  InputStreamReader in=new InputStreamReader(new FileInputStream(new File("stoplists/C.txt")));
	        BufferedReader bf = new BufferedReader(in);
	        File f=new File("H:/xinde.txt");
	        BufferedWriter ou=new BufferedWriter(new FileWriter(f));
	        String s=null;
	        while((s=bf.readLine())!=null){
	        	
	        	Pattern p= Pattern.compile("/[^/]*(,|])");
	    		
	    		Matcher m=p.matcher(ToAnalysis.parse(s).toString());
	    		String a=m.replaceAll(" ");
	    		a=a.substring(1);
	    		
	        	
	        	ou.write(a+"\r\n");
	        	ou.flush();
	        }
	        in.close();
	        bf.close();
	        ou.close();
	}
}
