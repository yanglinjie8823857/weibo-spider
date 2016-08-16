package cn.edu.zju.ccnt.spider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.splitWord.analysis.ToAnalysis;

public class CE {

	public static void main(String[] args) {
		String a="今天真好！";
		a=ToAnalysis.parse(a).toString();
		Pattern p= Pattern.compile("/[^/]*(,|])");
		
		Matcher m=p.matcher(a);
		a=m.replaceAll(" ");
		a=a.substring(1);
		System.out.println(a);
	}

}
