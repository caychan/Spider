package f_searcher;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.jasper.tagplugins.jstl.core.If;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.This;
import clawer.Request;
import clawer.Spider;
import f_pipeline.ConsolePipeline;
import f_pipeline.Pipeline;
import f_process.Processor;
import f_process.Search;
import f_scheduler.QueueScheduler;
import f_scheduler.Scheduler;
import f_thread.CountableThreadPool;

public class Searcher implements Runnable {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
    protected List<Pipeline> pipelines = new ArrayList<Pipeline>();

	private ReentrantLock newFileLock = new ReentrantLock();

	private Condition newFileCondition = newFileLock.newCondition();

	private Scheduler scheduler = new QueueScheduler();
	
	private static boolean stop = false;
	
	private Processor processor;
	
	private int threadNum = 1;

	private CountableThreadPool threadPool;
	
	private ExecutorService executorService;

	private int emptySleepTime = 300;

	private AtomicLong fileCount = new AtomicLong(0);
	
	private int sleepTime = 0;
	
	private long starTime = 0;
	
	
    public Searcher(Processor processor) {
        this.processor = processor;
    }
    
    public Searcher startFile(File... files) {
/*    	if (scheduler.getLeftRequestsCount() > 0) {
			scheduler.clear();
		}*/
        for (File file : files) {
            addFile(file);
        }
//        signalNewFile();
        return this;
    }
    
    private void addFile(File file){
        scheduler.push(file);
    }
    
    public Searcher setScheduler(Scheduler scheduler) {
        //如果自己定义了Scheduler，则把默认的Scheduler的file导入新的里面
        Scheduler oldScheduler = this.scheduler;
        this.scheduler = scheduler;
        if (oldScheduler != null) {
            File file;
            while ((file = oldScheduler.poll()) != null) {
                this.scheduler.push(file);
            }
        }
        return this;
    }
    
    public Scheduler getScheduler(){
    	return this.scheduler;
    }
    
    public Searcher thread(int threadNum) {
        this.threadNum = threadNum;
        if (threadNum <= 0) {
            throw new IllegalArgumentException("threadNum should be more than one!");
        }
        return this;
    }
    
    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public Searcher addPipeline(Pipeline pipeline) {
        this.pipelines.add(pipeline);
        return this;
    }

    protected void initComponent() {

    	if (pipelines.isEmpty()) {
			pipelines.add(new ConsolePipeline());
		}
    	
    	System.out.println(this.scheduler.getLeftRequestsCount());
    	
    	if (scheduler == null) {
    		System.out.println("init component");
			scheduler = new QueueScheduler();
		}

        if (threadPool == null || threadPool.isShutdown()) {
            if (executorService != null && !executorService.isShutdown()) {
                threadPool = new CountableThreadPool(threadNum, executorService);
            } else {
                threadPool = new CountableThreadPool(threadNum);
            }
        }
    }
   
    public void stop(){
    	stop = true;
    }
    public void start(){
    	fileCount.set(0);
    	stop = false;
    }
    
	@Override
	public void run() {
		initComponent();
		starTime=System.currentTimeMillis();
		logger.info("Searcher started!");
		while (!Thread.currentThread().isInterrupted() && !stop) {
			File file = scheduler.poll();
			if (file == null) {
				if (threadPool.getThreadAlive() == 0) {
					break;
				}
				waitNewFile(); // wait until new url added
			} else {
				final File fileFinal = file;
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							processFile(fileFinal);		
						} catch (Exception e) {
							logger.error("process file " + fileFinal + " error", e);
						} finally {                            
							fileCount .incrementAndGet();
							signalNewFile();
						}
					}
				});
//				System.out.println("while run end");
			}
		}
		close();
	}

	protected void processFile(File file) {
       for (int i = 0; i < pipelines.size(); i ++) {
           pipelines.get(i).process(file);
       }
		processor.process(file);
		
		if (getSleepTime() > 0) {
			sleep(getSleepTime());
		}
	}
	
	public Searcher setSleepTime(int sleepTime){
		this.sleepTime = sleepTime;
		return this;
	}
	private int getSleepTime(){	
		return this.sleepTime;
	}
	
	// signal 唤醒线程
	private void signalNewFile() {
		try {
			newFileLock.lock();
			newFileCondition.signalAll();
		} finally {
			newFileLock.unlock();
		}
	}

	private void waitNewFile() {
		newFileLock.lock();
		try {
			if (threadPool.getThreadAlive() == 0) {
				return;
			}
			newFileCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.warn("waitNewUrl - interrupted, error {}", e);
		} finally {
			newFileLock.unlock();
		}
	}

	private boolean close() {
		if (threadPool.shutdown()) {
			pipelines.clear();
			this.scheduler.clear();
		
			System.out.println("total file number: "+ fileCount.get());
			long finishTime = System.currentTimeMillis();
			System.out.println("total time used: " + (finishTime-starTime) + "毫秒") ;
			return true;
		}
		return false;
	}
	
	public void getExtraFiles(File file){
		File[] files = file.listFiles();
		synchronized (scheduler) {
			for (File f : files) {
				scheduler.push(f);
			}
		}
	}
}
