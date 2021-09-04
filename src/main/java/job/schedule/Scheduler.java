package job.schedule;

import job.config.Job;

import java.sql.Timestamp;

public interface Scheduler {

    void start() throws Exception;

    void add(Job job, Timestamp scheduleTime);

    void startAddingToQueue();
}
