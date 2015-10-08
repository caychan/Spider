package f_scheduler;

import org.apache.http.annotation.ThreadSafe;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@ThreadSafe
public class QueueScheduler implements Scheduler{   //因为文件路径不会重复，所以不要去重的部分
							
    private BlockingQueue<File> queue = new LinkedBlockingQueue<File>();

    @Override
    public void push(File file) {
        queue.add(file);
    }
    
    @Override
    public File poll() {
        return queue.poll();
    }
    
    @Override
    public int getLeftRequestsCount() {
    	return queue.size();
    }
    
    @Override
    public void clear(){
    	queue.clear();
    }

}
