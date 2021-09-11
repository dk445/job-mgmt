package com.scheduler.job.schedule;
/**
 *Task interface contains the essential run method that is required for each job.
 */
public interface Task {

    /**
     * executes the actual job and updates the status of the job to success/fail accordingly
     */
    void run();

}
