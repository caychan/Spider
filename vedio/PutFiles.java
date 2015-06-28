package tools;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by only. on 2015/3/2.
 */
public class PutFiles extends FilePersistentBase implements Pipeline {
    /**
     * create a PutFiles with default path"/data/webmagic/"
     */
    public PutFiles() {
        setPath("/data/webmagic/");
    }

    public PutFiles(String path) {
        setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getFile(getPath()
                    + resultItems.getRequest().getExtra("videoName").toString() + ".txt"), true), "UTF-8"));

            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                if (entry.getValue() instanceof Iterable) {
                    Iterable value = (Iterable) entry.getValue();
                    printWriter.println(entry.getKey());
                    for (Object o : value) {
                        printWriter.println(((Request) o).getExtra("subsection") + ((Request) o).getUrl());
                    }
                } else {
                    printWriter.println(entry.getKey() + ":\t" + entry.getValue());
                }
            }
            printWriter.close();
        } catch (IOException e) {

        }
    }
}
