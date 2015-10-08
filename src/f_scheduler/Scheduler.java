package f_scheduler;

import java.io.File;

/**
 * Scheduler is the part of file path management.<br>
 */
public interface Scheduler {

    public void push(File file);

    public File poll();
    
    public void clear();
    
    public int getLeftRequestsCount();

}
