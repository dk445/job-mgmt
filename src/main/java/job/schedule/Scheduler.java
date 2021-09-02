package job.schedule;

import job.config.Job;

public interface Scheduler {

    void start() throws InterruptedException;

    void waitForNextTask() throws InterruptedException;

    void add(Job job, long delayMs);
}
