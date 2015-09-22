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

public class SearchKeyWord {

	
	public List<Integer> searchKeyWord(File file, String keyWord) {
		BufferedReader br = null;
		List<Integer> position = new ArrayList<Integer>();
		try {
			String line;
			StringBuffer sb = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
	        while ((line = br.readLine()) != null){
	        	sb.append(line);
	        } 
	        
	        String str = sb.toString();
	        position = searchWord(str, keyWord);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return position;
	}
	
	private List<Integer> searchWord(String content, String key){
		List<Integer> position = new ArrayList<Integer>();
		Pattern p = Pattern.compile(key);
		Matcher m = p.matcher(content);
		while(m.find()){
			position.add(m.start());
		}
		return position;
	}

}
