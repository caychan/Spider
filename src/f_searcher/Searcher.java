package f_searcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import f_pipeline.ConsolePipeline;
import f_pipeline.Pipeline;
import f_process.Processor;
import f_scheduler.QueueScheduler;
import f_thread.CountableThreadPool;

public class Searcher implements Runnable {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
    protected List<Pipeline> pipelines = new ArrayList<Pipeline>();

	private ReentrantLock newFileLock = new ReentrantLock();

	private Condition newFileCondition = newFileLock.newCondition();

	private static QueueScheduler scheduler = new QueueScheduler();
	
	private static boolean stop = false;
	
	private Processor processor;
	
	private int threadNum = 1;

	private CountableThreadPool threadPool;
	
	private ExecutorService executorService;

	private int emptySleepTime = 300;

	private AtomicLong fileCount = new AtomicLong(0);
	
	private int sleepTime = 0;
	
	
    public Searcher(Processor processor) {
        this.processor = processor;
    }
    
    public Searcher startFile(File... files) {
        for (File file : files) {
            addFile(file);
        }
        signalNewFile();
        return this;
    }
    
    private void addFile(File file){
        scheduler.push(file);
    }
    
    public static QueueScheduler getScheduler(){
    	return scheduler;
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
    
	@Override
	public void run() {
		initComponent();
		long starTime=System.currentTimeMillis();
		logger.info("Searcher started!");
		while (!Thread.currentThread().isInterrupted() && !stop) {
			File file = scheduler.poll();
			if (file == null) {
//				System.out.println("-------  null file");
				if (threadPool.getThreadAlive() == 0) {
					break;
				}
				waitNewFile(); // wait until new url added
			} else {
//		        logger.info("downloading page {}", file.getAbsolutePath());
//				System.out.println("--------  not null");
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
			}
		}
		System.out.println("total file number: "+ fileCount.get());
		
		long finishTime = System.currentTimeMillis();
		System.out.println("total time used: " + (finishTime-starTime) + "毫秒") ;
		close();
	}

	protected void processFile(File file) {
       for (Pipeline pipeline : pipelines) {
           pipeline.process(file);
       }
		processor.process(file);
		
		sleep(getSleepTime());
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

    public void close() {
        threadPool.shutdown();
    }
}
