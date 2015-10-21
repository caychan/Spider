package pipeline;

import clawer.Page;
import clawer.ResultItems;
import clawer.Task;

/**
 * Pipeline is the persistent and offline process part of crawler.<br>
 * The interface Pipeline can be implemented to customize ways of persistent.
 *
 * @see ConsolePipeline
 * @see FilePipeline
 */
public interface Pipeline {

    /**
     * Process extracted results.
     *
     * @param resultItems
     * @param task
     */
    public void process(ResultItems resultItems, Task task);  //Spider.java 第446行

	public void process(Task task, Page page);
}
