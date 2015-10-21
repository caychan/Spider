package pipeline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

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
 */
@ThreadSafe
public class FilePipeline_jd extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * create a FilePipeline with default path"/data/webmagic/"
     */
    public FilePipeline_jd() {
        setPath("/data/webmagic/");
    }

    public FilePipeline_jd(String path) {
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
        File file = getFile(this.path);
        try {
        	
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file,true),"gbk"));
//            printWriter.println("url:\t" + resultItems.getRequest().getUrl());
            String value = resultItems.getRequest().getUrl() + ",";
            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            	value += entry.getValue()+",";
/*                if (entry.getValue() instanceof Iterable) {
                    Iterable value = (Iterable) entry.getValue();
                    printWriter.println(entry.getKey() + ",");
                    for (Object o : value) {
                        printWriter.println(o);
                    }
                } else {
                    printWriter.println(entry.getKey() + "," + entry.getValue());
                }*/
            }
            printWriter.println(value);
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
}
