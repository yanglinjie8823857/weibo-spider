package cn.edu.zju.ccnt.spider;

import java.io.BufferedInputStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;




import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Pattern;









import cn.edu.zju.ccnt.spider.utils.DBConn;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;


public class TopicModel {
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
	

    public static void main(String[] args) throws Exception {
    	initializeParams();
        // Begin by importing documents from text to feature sequences顺序，序列
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
 
        // Pipes: lowercase小写字母, tokenize切分词, remove stopwords停用词, map to features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );

        InstanceList instances = new InstanceList (new SerialPipes(pipeList));
      
        Reader fileReader = new InputStreamReader(new FileInputStream(new File("H:/xinde.txt")), "UTF-8");
        instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                                               3, 2, 1)); // data, label, name fields
        
        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01 两个参数，不用改
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
	//定义主题个数
        int numTopics = 10;   
	//训练模型
        ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
	//设置线程个数
        model.setNumThreads(2);

        // Run the model for 50 iterations and stop (this is for testing only, 
        //  for real applications, use 1000 to 2000 iterations)
	//设置迭代次数
        model.setNumIterations(50);
	//模型训练结束
        model.estimate();

        // Show the words and topics in the first instance

        // The data alphabet maps word IDs to strings输出第一个实例的每个单词
        Alphabet dataAlphabet = instances.getDataAlphabet();
        
        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;
        
        Formatter out = new Formatter(new StringBuilder(), Locale.US);
        for (int position = 0; position < tokens.getLength(); position++) {
            out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
        }
        System.out.println(out);
        
        // Estimate the topic distribution of the first instance, 
        //  given the current Gibbs state.输出第一个实例的主题分布
//        BufferedWriter bw= new BufferedWriter(new FileWriter(new File("H:/xinde1.txt")));
        int m=0;
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
//        while(true){
//        	if(m>100){break;}
//        double[] topicDistribution = model.getTopicProbabilities(m);
//
//        // Get an array of sorted sets of word ID/count pairs
//        
//        double a[] = new double[20];
//        Connection conn= DBConn.getConnection();
//        PreparedStatement st=conn.prepareStatement("INSERT INTO topic (id,topic) VALUES (?, ?)");
//        
//        // Show top 5 words in topics with proportions for the first document输出描述某个主题的前五个单词以及权重
//        for (int topic = 0; topic < numTopics; topic++) {
//            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();         
//           
//            int rank = 0;
//            String s="";
//            while (iterator.hasNext() && rank < 10) {
//                IDSorter idCountPair = iterator.next();
//                 s+=dataAlphabet.lookupObject(idCountPair.getID())+"("+idCountPair.getWeight()+")";
//                out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
//                rank++;
//            }
//            System.out.println(out);
//        }
//            st.setInt(1, topic);
//            st.setString(2, s);
//            st.execute();
//            a[topic]= topicDistribution[topic];
//           System.out.println(out);
//        }
//        st.close();
//       
//        double k;
//        
//        	int index = 0;
//        	k = a[0];
//        	for(int i=0; i<a.length; i++){
//        	if(a[i] > k){
//        		k = a[i];
//        		index = i;
//        		}
//        }

////        	System.out.println(instances.get(0).getData().toString());
//        // Create a new instance with high probability of topic 0  输出第一个记录属于所有topic的概率以及用来描述这个topic的部分单词
//        StringBuilder topicZeroText = new StringBuilder();
//        Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();
//
//        int rank = 0;
//        while (iterator.hasNext() && rank < 5) {
//            IDSorter idCountPair = iterator.next();
//            topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
//            rank++;
//        }

//         Create a new instance named "test instance" with empty target and source fields.创建一个新的实例，推断它的概率分布
        InstanceList testing = new InstanceList(instances.getPipe());
       testing.addThruPipe(new Instance("美国人民是值得尊重的，艰苦卓绝的独立斗争，建立民主自由的社会制度，但如果为了忍受了100多年落后和屈辱的中国尊严而战，我会选择为祖国付出一切！ 我在:唐山市滦县", null, "test instance", null));

        TopicInferencer inferencer = model.getInferencer();
        double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
        double k;
      
      	int index = 0;
      	k = testProbabilities[0];
      	for(int i=0; i<testProbabilities.length; i++){
      	if(testProbabilities[i] > k){
      		k = testProbabilities[i];
      		index = i;
      		}
      }
       
    
}
}