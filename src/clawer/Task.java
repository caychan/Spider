package clawer;

/**
 * Interface for identifying different tasks.<br>
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
