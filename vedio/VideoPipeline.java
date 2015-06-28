package tools;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.util.Map;

/**
 * Created by only. on 2015/3/3.
 */
public class VideoPipeline extends FilePersistentBase implements Pipeline {
    private String directory = null;
    private boolean subsection = false;
    private String platform;

    /**
     * create a PutFiles with default path"/data/webmagic/"
     */
    public VideoPipeline(boolean subsection,String platform) {
        setPath("/data/video/");
        this.subsection=subsection;
        this.platform=platform;
    }

    public VideoPipeline(String path,boolean subsection,String platform) {
        setPath(path);
        this.subsection=subsection;
        this.platform=platform;
    }

    public void process(ResultItems resultItems, Task task) {
        directory = resultItems.getRequest().getExtra("videoName").toString();
        Download download = null;
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            if (entry.getValue() instanceof Iterable) {
                Iterable value = (Iterable) entry.getValue();
                for (Object o : value) {
                    try {
                        download = new Download((Request) o, getPath() + directory, entry.getKey(),subsection,platform);
                        download.start();
                        download.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    download = new Download((Request) entry.getValue(), getPath() + directory, entry.getKey(),subsection,platform);
                    download.start();
                    download.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

