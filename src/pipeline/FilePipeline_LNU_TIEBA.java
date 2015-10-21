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
import java.util.Map.Entry;
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
 */
@ThreadSafe
public class FilePipeline_LNU_TIEBA extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * create a FilePipeline with default path"/data/webmagic/"
     */
    public FilePipeline_LNU_TIEBA() {
        setPath("F:\\Clawer\\LNU_TIEBA\\");
    }

    public FilePipeline_LNU_TIEBA(String path) {
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
    	String date = resultItems.get("date");
    	String title = resultItems.get("title");
    	Integer nextPage = resultItems.get("nextPage");

    	//如果有下页的，保存在multiplePage文件夹下，否则以时间分文件夹。文件名为url中的数字+标题
    	
    	String module = null;
    	if (nextPage != null && nextPage > 0) {
			module = "mutiplePage";
		} else if (date != null && date.contains(" ")) {
			module = date.trim().substring(0, date.lastIndexOf('-'));
		}

    	String fileNum = "";
    	Matcher matcher = Pattern.compile("\\d+").matcher(url);
    	if (matcher.find()) {
    		fileNum = matcher.group();
		}
    	//文件名中不允许出现“<>"|*?:/\”等特殊符号
		String fileName = title.replaceAll("[<>|\"*?:/]", "");

		try {
			String path = this.path + PATH_SEPERATOR + module + PATH_SEPERATOR + fileNum + fileName + ".txt";
			PrintWriter printWriter;
			
			printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getFile(path), true), "utf-8"));

			printWriter.println("url：" + url);
			for (Entry<String, Object> entry : resultItems.getAll().entrySet()) {
				printWriter.println(entry.getKey() + "： " + entry.getValue());
			}
			

			printWriter.close();
		} catch (IOException e) {
			logger.warn(url);
			logger.warn("write file error", e);
		}
        
    }
}
