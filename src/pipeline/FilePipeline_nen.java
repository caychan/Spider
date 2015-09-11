package pipeline;

import java.io.BufferedWriter;
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
public class FilePipeline_nen extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * create a FilePipeline with default path"/data/webmagic/"
     */
    public FilePipeline_nen() {
        setPath("/data/webmagic/");
    }

    public FilePipeline_nen(String path) {
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
    	String source = "";
    	String date = "";
        boolean flag = true;
        String content = resultItems.get("content").toString();
        
        //如果内容长度小于20，则认为给定的xpath没有能抓到该页的内容，把该页写入content.txt里
		if (content.length() < 20) {
			flag = false;
            try {
				PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
						getFile("F:\\Clawer\\nen\\content.txt"),true),"utf-8"));
				printWriter.println(url);
				printWriter.close();
			} catch (Exception e) {
	            logger.warn("write file error", e);
			}
            return ;
		}
		
        //如果标题长度小于8，则认为给定的xpath没有能抓到该页的标题，把该页写入title.txt里
		String title = resultItems.get("title").toString();
		if (title.length() < 8) {
			flag = false;
			try {
				PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
						getFile("F:\\Clawer\\nen\\title.txt"), true),"utf-8"));
				printWriter.println(url);
				printWriter.close();
			} catch (Exception e) {
				logger.warn("write file error", e);
			}
			return ;
		}
    	
    	String mark = "";
    	String file = "";
    	//一般情况的url
    	if (new PlainText(url).regex("http://\\w+\\.nen\\.com\\.cn/system/[\\d/_]+\\.shtml").match()) {
    		source = resultItems.get("source").toString();
    		if (url.substring(0, url.indexOf(".")).contains("3")) {
    			url = url.substring(url.indexOf("."), url.length());
    		}
        	Pattern p = Pattern.compile("\\d+"); 
    		Matcher m = p.matcher(url); 
    		String rm = "";
    		/* 如果该新闻有分页情况，把各页内容写进同一个txt中
        	比如：http://news.nen.com.cn/system/2013/12/24/011648274.shtml和
        	http://news.nen.com.cn/system/2013/12/24/011648274_01.shtml
        	rm中记下01，然后再time中删掉”
    		 */
    		while (m.find()) {
    			if (url.contains("_")) {
    				rm = m.group();
    				mark += rm;
    			} else {
    				mark += m.group();
    			}
    		} 
    		if (! rm.equals("")) {
    			mark = mark.substring(0, mark.length() - rm.length());
    		}
    		file = mark.substring(0, 6);
		} else if (new PlainText(url).regex("http://it\\.nen\\.com\\.cn/[\\w/]+\\.shtml").match()) {//IT部分的url
			date = resultItems.get("date").toString(); 
			if (date != null) {
				if (date.contains("1") || date.contains("2")) {//时间要么是1***年，要么是2***年。
					mark = date.substring(0, 10);
					mark = mark.replaceAll("-", "");
					mark += url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."));
					file = mark.substring(0, 6);
				}
				if (date.contains("出处")) {
					source = date.substring(date.indexOf("出处"));
					if (source.contains("责编")) {
						source = source.substring(3, source.indexOf("责编"));
					}
				}
			} 
			
		} else if (new PlainText(url).regex("http://house\\.nen\\.com\\.cn/fangchan/web/html/[\\d/_]+\\.shtml").match()) {//房产方面的url
			source = resultItems.get("source").toString();
			date = resultItems.get("date").toString();
			if (date != null) {
				date.trim();
				mark = date.replaceAll("-", "").substring(0, 8);
				mark += url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."));
				file = mark.substring(0, 6);
			}
		}
    	
        if (flag) {
	        try {
	            String path = this.path + PATH_SEPERATOR + file + PATH_SEPERATOR + mark +".txt";
	            PrintWriter printWriter;
	            if (url.contains("_")) {
	            	printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
	            			getFile(path), true),"utf-8"));
	            	//如果该页是第n页，则只写正文
		            printWriter.println(content);
	            }else {
	            	printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
	            			getFile(path), true),"utf-8"));
	            	printWriter.println("url： " + resultItems.getRequest().getUrl());
	            	printWriter.println("日期： " + mark.substring(0, 4) + "-" + mark.substring(4, 6) + "-" + mark.substring(6, 8));
	            	if (source.contains("来源")) {
	            		printWriter.println(source);
					} else {
						printWriter.println("来源：  " + source);
					}
	            	printWriter.println("标题：  " + title);
		            printWriter.println("正文：\r\n" + content);
	            }

	            printWriter.close();
	        } catch (IOException e) {
	            logger.warn("write file error", e);
	        }
        }
    }
}
