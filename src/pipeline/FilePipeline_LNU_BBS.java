package pipeline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.Soundbank;
import javax.swing.border.TitledBorder;

import org.apache.http.annotation.ThreadSafe;
import org.jboss.netty.handler.queue.BufferedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import selector.PlainText;
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

/*	@Override
	public void process(ResultItems resultItems, Task task) {
		
	}*/

	@Override
	public void process(Task task, Page page) {
		
	}

    @Override
    public void process(ResultItems resultItems, Task task) {
    	String url = resultItems.getRequest().getUrl();
    	String module = resultItems.get("module");
    	String user = resultItems.get("user");
    	String date = resultItems.get("date");
    	String title = resultItems.get("title");
    	String click = resultItems.get("click");
    	String reply = resultItems.get("reply");
    	String content = resultItems.get("content");

    	
    	//文件名为发帖人+标题格式
    	String fileName = user + "-" + title;
    	
	        try {
	            String path = this.path + PATH_SEPERATOR + module + PATH_SEPERATOR + fileName +".txt";
	            PrintWriter printWriter;
	            
	            File file = new File(path);
	            //如果文件已经存在，则说明这是该帖子的非第一页，只存内容即可
	            if (file.exists()) {
	            	printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
	            			file, true),"utf-8"));
		            printWriter.println(content);
	            }else {
	            	printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
	            			getFile(path), true),"utf-8"));
	            	printWriter.println("url： " + url);
	            	printWriter.println("标题： " + title);
					printWriter.println("发帖人：  " + user);
	            	printWriter.println("发帖时间：  " + date);
	            	printWriter.println("查看人数  " + click);
	            	printWriter.println("回复人数：  " + reply);
		            printWriter.println("正文：\r\n" + content);
	            }

	            printWriter.close();
	        } catch (IOException e) {
	            logger.warn("write file error", e);
	        }
        
    }
}
