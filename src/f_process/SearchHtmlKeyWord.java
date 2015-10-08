package f_process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchHtmlKeyWord {
	
	/**
	 * 查找关键字，返回包含关键字所在句子的List
	 * @param content 要查找的字符串
	 * @param key 关键字
	 * @return 返回包含关键字所在句子的List
	 */
	public List<String> searchKeyWordReturnSentence(File file, String keyWord) {
		List<String> sentence = new ArrayList<String>();
		String content = readFile(file);
		sentence = searchWordInSentence(file, content, keyWord);
			
		return sentence;
	}
	
	/**
	 * 查找关键字位置，返回包含位置的List
	 * @param content 要查找的字符串
	 * @param key 关键字
	 * @return 位置List
	 */
	public List<Integer> searchKeyWordReturnPosition(File file, String keyWord) {
		List<Integer> position = new ArrayList<Integer>();
		String content = readFile(file);
		position = searchWordPosition(content, keyWord);
		
		return position;
	}
	
	/**
	 * 查找关键字位置
	 * @param content 要查找的字符串
	 * @param key 关键字
	 * @return 位置List
	 */
	private List<Integer> searchWordPosition(String content, String key){
		List<Integer> position = new ArrayList<Integer>();
		Pattern p = Pattern.compile(key);
		Matcher m = p.matcher(content);
		while(m.find()){
			position.add(m.start());
		}
		return position;
	}
	
	/**
	 * 查找关键字所在的句子
	 * @param content	要查找的字符串
	 * @param key	关键字
	 * @return 关键字所在句子的集合
	 */
	private List<String> searchWordInSentence(File file, String content, String key){
		List<String> sentence = new ArrayList<String>();
		Pattern pattern = Pattern.compile(key);
		Matcher matcher = pattern.matcher(content);
		int position = 0;
		int start, end;
		int length = content.length();
		sentence.add(file.getAbsolutePath());
		while(matcher.find()){
			position = matcher.start();
			//最多从当前位置的前200个字符位置开始查找上一个标点符号所在的位置
			start = position >= 200 ? position - 200 : 0;
			end = position < length ? position + 1 : position;
			
			String subString = content.substring(start, end);
			
			//查找中英文的句号，感叹号，问号，分号，冒号，破折号和空白
			//Pattern p = Pattern.compile("[。！？；：——.?:;!-\\s]");
			Pattern p = Pattern.compile("[。！？；\\?\\s]");
			Matcher m = p.matcher(subString);
			//初始化一下lastPubctuation，因为可能有如果找不到会返回-1的情况，所以为了安全，初始值设为了-2
			int lastPunctuation = -2;
			while (m.find()) {
				//记录最后一个匹配的位置
				lastPunctuation = m.start();
			}
			if (lastPunctuation >= 0) {
				//如果找到了，此时的lastPunctuation是subString中的位置，改成content中的位置。
				//+1是因为它此时指向的是标点符号
				lastPunctuation = position - (subString.length() - lastPunctuation) + 1;
			}
			if (lastPunctuation < 0 ) {
				//如果没有找到符合要求的位置，就设为当前位置的前100个或者0
				lastPunctuation = position >= 100 ? position - 100 : 0;
			}

			end = (length - position) >= 200 ? position + 200 : length;
			m = p.matcher(content.substring(position, end));
			int nextPunctuation = -2;
			if (m.lookingAt()) {
				nextPunctuation = m.start();
			}
			if (nextPunctuation >= 0) {
				nextPunctuation = nextPunctuation + position;
			}
			if (nextPunctuation < 0 ) {
				//如果没有找到符合要求的位置，就设为当前位置的后100个或者到结尾
				//-1是因为String.substring的第二个参数如果跟length一样大，会指标越界
				nextPunctuation = (length - position) > 100 ? position + 100 : length - 1;
			}

			sentence.add(content.substring(lastPunctuation + 1,nextPunctuation-1));
		}
		return sentence;
	}
	
	
	//去掉所有的html标签
	private String getContent(String content){
		if (content.indexOf("content：") >= 0) {
			content = content.substring(content.indexOf("content："));
		}
		
		return content;
	}
	
	private String readFile(File file){
		BufferedReader br = null;
		String str = null;
		try {
			String line = null;
			StringBuffer sb = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
	        while ((line = br.readLine()) != null){
	        	sb.append(line);
	        } 
	        
	        str = sb.toString();
	        str = getContent(str);	
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		return str;
	}

}
