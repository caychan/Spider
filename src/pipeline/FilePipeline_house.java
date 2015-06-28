package pipeline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
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
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@ThreadSafe
public class FilePipeline_house extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * create a FilePipeline with default path"/data/webmagic/"
     */
    public FilePipeline_house() {
        setPath("/data/webmagic/");
    }

    public FilePipeline_house(String path) {
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
//        String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR;
        try {
        	
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
            		getFile(path),true),"gbk"));
//            printWriter.println("url:\t" + resultItems.getRequest().getUrl());
            List<String> name = resultItems.get("name");
            List<String> price = resultItems.get("price");
            List<String> area = resultItems.get("area");
            List<String> address = resultItems.get("address");
            
            for (int i = 0; i < name.size(); i++) {
				printWriter.print(name.get(i) + ",");
				printWriter.println();
            }
            for (int i = 0; i < price.size(); i++) {
            	printWriter.print(price.get(i) + ",");
            	printWriter.println();
            }
/*            for (int i = 0; i < name.size(); i++) {
            	printWriter.print(name.get(i) + "," + price.get(i) + "," + area.get(i) + "," + address.get(i));
            	printWriter.println();
            }
*/            
            
/*            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                if (entry.getValue() instanceof Iterable) {
                    Iterable value = (Iterable) entry.getValue();
                    printWriter.println(entry.getKey() + ",");
                    for (Object o : value) {
                        printWriter.println(o);
                    }
                } else {
                    printWriter.println(entry.getKey() + "," + entry.getValue());
                }
            }*/
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
}
