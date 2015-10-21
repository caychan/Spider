package thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread pool for workers.<br></br>
 * Use {@link java.util.concurrent.ExecutorService} as inner implement. <br></br>
 * New feature: <br></br>
 * 1. Block when thread pool is full to avoid poll many urls without process. <br></br>
 * 2. Count of thread alive for monitor.
 */
public class CountableThreadPool {

    private int threadNum;

    private AtomicInteger threadAlive = new AtomicInteger();

    private ReentrantLock reentrantLock = new ReentrantLock();

    private Condition condition = reentrantLock.newCondition();

    public CountableThreadPool(int threadNum) {
        this.threadNum = threadNum;
        this.executorService = Executors.newFixedThreadPool(threadNum);
    }

    public CountableThreadPool(int threadNum, ExecutorService executorService) {
        this.threadNum = threadNum;
        this.executorService = executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public int getThreadAlive() {
        return threadAlive.get();
    }

    public int getThreadNum() {
        return threadNum;
    }

    private ExecutorService executorService;

    public void execute(final Runnable runnable) {

        if (threadAlive.get() >= threadNum) {
            try {
                reentrantLock.lock();
                while (threadAlive.get() >= threadNum) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                    }
                }
            } finally {
                reentrantLock.unlock();
            }
        }
        threadAlive.incrementAndGet();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                    try {
                        reentrantLock.lock();
                        threadAlive.decrementAndGet();
                        condition.signal();
                    } finally {
                        reentrantLock.unlock();
                    }
                }
            }
        });
    }

    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    
    /**
     * 关闭线程池，
     * @return 成功关闭，返回true，否则返回false
     */
    public boolean shutdown() {
    	executorService.shutdown();
		try {
			if (executorService.awaitTermination(3, TimeUnit.SECONDS)) {
				return true;
			} else {
				executorService.shutdownNow();
				if (executorService.awaitTermination(10, TimeUnit.SECONDS)) {
					return true;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}



}
