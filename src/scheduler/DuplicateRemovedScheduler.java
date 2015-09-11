package scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.BloomFilterDuplicateRemover;
import component.DuplicateRemover;
import component.HashSetDuplicateRemover;
import clawer.Request;
import clawer.Task;

/**
 * Remove duplicate urls and only push urls which are not duplicate.<br></br>
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public abstract class DuplicateRemovedScheduler implements Scheduler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

//    private DuplicateRemover duplicatedRemover = new HashSetDuplicateRemover();					//这个类里用set取出重复的url
    
    //参数是待检测的个数
    private BloomFilterDuplicateRemover duplicatedRemover = new BloomFilterDuplicateRemover(1700000);		

    public DuplicateRemover getDuplicateRemover() {
        return duplicatedRemover;
    }

    public DuplicateRemovedScheduler setDuplicateRemover(DuplicateRemover duplicatedRemover) {
        this.duplicatedRemover = (BloomFilterDuplicateRemover) duplicatedRemover;
        return this;
    }

    @Override
    public void push(Request request, Task task) {
        logger.trace("get a candidate url {}", request.getUrl());
        if (!duplicatedRemover.isDuplicate(request, task) || shouldReserved(request)) {
            logger.debug("push to queue {}", request.getUrl());
            //将没有重复的Request或者应该保存的Request保存下来
            pushWhenNoDuplicate(request, task);
        }
    }

    protected boolean shouldReserved(Request request) {
        return request.getExtra(Request.CYCLE_TRIED_TIMES) != null;
    }

    protected void pushWhenNoDuplicate(Request request, Task task) {
    	//QueueScheduler中实现的。QueueScheduler继承了这个类。
    }
}
