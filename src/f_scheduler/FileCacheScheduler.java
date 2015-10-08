package f_scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Store urls and cursor in files so that a Spider can resume the status when shutdown.<br>
 */
public class FileCacheScheduler implements Scheduler{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

    private String filePath = System.getProperty("java.io.tmpdir");

    private String fileUrlAllName = ".search_path.txt";

    private String fileCursor = ".search_cursor.txt";

    private PrintWriter fileUrlWriter;

    private PrintWriter fileCursorWriter;

    private AtomicInteger cursor = new AtomicInteger();

    private AtomicBoolean inited = new AtomicBoolean(false);

    private BlockingQueue<File> queue = new LinkedBlockingQueue<File>();

    //参数是待检测的个数，自定义
    private BloomFiltereRemover bfRemover = new BloomFiltereRemover(1000000);	


    public FileCacheScheduler(String filePath) {
        if (!filePath.endsWith("/") && !filePath.endsWith("\\")) {
            filePath += "\\";
        }
        this.filePath = filePath;
    }

    private void flush() {
        fileUrlWriter.flush();
        fileCursorWriter.flush();
    }
    
    //LinkedBlockingQueue内部实现了ReentrantLock
    public void push(File file) {
        if (!inited.get()) {
            init();
        }
        if (! bfRemover.isDuplicate(file)) {
        	queue.add(file);
        	fileUrlWriter.println(file.getAbsolutePath());
		}
    }

    public File poll() {
        if (!inited.get()) {
            init();
        }
        fileCursorWriter.println(cursor.incrementAndGet());
        return queue.poll();
    }

    private void init() {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        readFile();
        initWriter();
        initFlushThread();
        inited.set(true);
        logger.info("init cache scheduler success");
    }

    private void initFlushThread() {
    	//每隔十秒执行一次
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                flush();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void initWriter() {
        try {
            fileUrlWriter = new PrintWriter(new FileWriter(getFileName(fileUrlAllName), true));
            //开始是false，这样，重启后如果马上就关闭，里面的数据会丢失，文件内容为空，上次爬的数据就会丢失
            //为了防止这种情况发生，改为true。但是这样每次重启都会从0到最大数循环一次，时间效率低一些
            fileCursorWriter = new PrintWriter(new FileWriter(getFileName(fileCursor), true));
            
        } catch (IOException e) {
            throw new RuntimeException("init cache scheduler error", e);
        }
    }

    private void readFile() {
        try {
            readCursorFile();
            readUrlFile();

        } catch (FileNotFoundException e) {
            //init
            logger.error("init cache file " + getFileName(fileUrlAllName));
        } catch (IOException e) {
            logger.error("init file error", e);
        }
    }

    private void readUrlFile() throws IOException {
        String line;
        BufferedReader fileUrlReader = null;
        try {
            fileUrlReader = new BufferedReader(new FileReader(getFileName(fileUrlAllName)));
            int lineReaded = 0;
            int i = cursor.get();
            while ((line = fileUrlReader.readLine()) != null) {
                //把所有url，包括爬过的和保存下来但是还没爬的，都放进bfRemover里面
            	bfRemover.put(line);
                lineReaded++;
                //当读到已经爬过的数目的时候，也就是说接下来要读的是没有爬过的，放进queue里。
                if (lineReaded > i) {
                    queue.add(new File(line));
                }
            }

        } finally {
            if (fileUrlReader != null) {
                IOUtils.closeQuietly(fileUrlReader);
            }
        }
    }

    private void readCursorFile() throws IOException {
        BufferedReader fileCursorReader = null;
        try {
        	fileCursorReader = new BufferedReader(new FileReader(getFileName(fileCursor)));
            String line;
            //read the last number，也就是已经爬了多少条
            
            while ((line = fileCursorReader.readLine()) != null) {
                cursor = new AtomicInteger(NumberUtils.toInt(line));
            }
        } finally {
            if (fileCursorReader != null) {
                IOUtils.closeQuietly(fileCursorReader);
            }
        }
    }

    private String getFileName(String filename) {
        return filePath + filename;
    }

    public void clear(){
    	queue.clear();
    }

	@Override
	public int getLeftRequestsCount() {
		return queue.size();
	}

}
