package pipeline;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.annotation.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.FilePersistentBase;
import clawer.Page;
import clawer.ResultItems;
import clawer.Task;

/**
 * Store results in files.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@ThreadSafe
public class FilePipeline_LNU_BBS extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());


    public FilePipeline_LNU_BBS() {
        setPath("F:\\Clawer\\LNU_BBS\\");
    }

    public FilePipeline_LNU_BBS(String path) {
        setPath(path);
    }

	@Override
	public void process(Task task, Page page) {
		
	}

    @Override
    public void process(ResultItems resultItems, Task task) {
    	String url = resultItems.getRequest().getUrl();
    	String module = resultItems.get("module");
    	String title = resultItems.get("title");
    	
    
//    	得到url中的数字
    	String fileNum = "";
    	Matcher matcher = Pattern.compile("\\d+").matcher(url);
    	if (matcher.find()) {
			fileNum = matcher.group();
		}
//		去掉标题中的特殊符号    	
    	title = title.replaceAll("[<>|\"*?:/]", "");
//    	文件名为url中数字+标题格式
    	String fileName = fileNum + "-" + title;
    	PrintWriter	printWriter = null;
	        try {
	            String path = this.path + PATH_SEPERATOR + module + PATH_SEPERATOR + fileName +".txt";

	            printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
	            			getFile(path), true),"utf-8"));
	            printWriter.println("url：" + url);
	            for(Entry<String, Object> result : resultItems.getAll().entrySet()){
	            	printWriter.println(result.getKey() + "：" + result.getValue());
	            }

	        } catch (IOException e) {
	            logger.warn("write file error", e);
	        } finally{
	        	printWriter.flush();
	        	printWriter.close();
	        }
        
    }


}
