package cn.edu.zju.ccnt.spider;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;

public class AS {

	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		String a="评论[1209]";
//		String b=a.substring(a.indexOf("[")+1, a.indexOf("]"));
//		int c=Integer.parseInt(b);
//		System.out.println(c);
		String a="我是一个好人！";
		
		Pattern p= Pattern.compile("/[^/]*(,|])");
		
		Matcher m=p.matcher(ToAnalysis.parse(a).toString());
		String s=m.replaceAll(" ");
		s=s.substring(1);
		System.out.println(s);
	}

}
