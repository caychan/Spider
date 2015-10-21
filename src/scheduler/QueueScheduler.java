package scheduler;

import org.apache.http.annotation.ThreadSafe;

import clawer.Request;
import clawer.Task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Basic Scheduler implementation.<br>
 * Store urls to fetch in LinkedBlockingQueue and remove duplicate urls by HashMap.
 */
@ThreadSafe
public class QueueScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

//	保存带爬取的Request。HashSetDuplicateRemover里只是用Request的url来去重，保存所有url，爬过的和待爬的。
//	BlockingQueue里面仅保存待爬的url
	
//	如果BlockingQueue是空的，从BlockingQueue取东西的操作将会被阻断进入等待状态，直到BlockingQueue进了东西才会被唤醒；
//	同样，如果BlockingQueue是满的，任何试图往里存东西的操作也会被阻断进入等待状态，直到BlockingQueue里有空间时才会被唤醒继续操作。
    private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();
    
    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        queue.add(request);
    }

    @Override
    public synchronized Request poll(Task task) {//push方法在父类DuplicateRemovedScheduler中写的。
        return queue.poll();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        return queue.size();
    }
    
    public int getLeftRequestsCount() {
    	//和HashSetDuplicateRemover里一样，每次输出都是0
    	return queue.size();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }
    
/*    public int getTotalRequestsCount() {
    	return getDuplicateRemover().getTotalRequestsCount();
    }*/
}
