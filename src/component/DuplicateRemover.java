package component;

import clawer.Request;
import clawer.Task;

/**
 * Remove duplicate requests.
 */
public interface DuplicateRemover {
    /**
     *
     * Check whether the request is duplicate.
     *
     * @param request
     * @param task
     * @return
     */
    public boolean isDuplicate(Request request, Task task);

    /**
     * Reset duplicate check.
     * @param task
     */
    public void resetDuplicateCheck(Task task);

    /**
     * Get TotalRequestsCount for monitor.
     * @param task
     * @return
     */
    public int getTotalRequestsCount(Task task);

}
