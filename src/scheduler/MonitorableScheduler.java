package scheduler;

import clawer.Task;

/**
 * The scheduler whose requests can be counted for monitor.
 *
 */
public interface MonitorableScheduler extends Scheduler {

    public int getLeftRequestsCount(Task task);

    public int getTotalRequestsCount(Task task);

}