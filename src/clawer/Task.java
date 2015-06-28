package clawer;

/**
 * Interface for identifying different tasks.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 * @see scheduler.Scheduler
 * @see pipeline.Pipeline
 */
public interface Task {

    /**
     * unique id for a task.
     *
     * @return uuid
     */
    public String getUUID();
    
    public int getTaskId();

    /**
     * site of a task
     *
     * @return site
     */
    public Site getSite();

}
