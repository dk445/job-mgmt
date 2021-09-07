package com.scheduler.job.schedule;

import com.scheduler.job.config.Job;

import java.sql.Timestamp;

public interface Scheduler {

    void start() throws Exception;

    void add(Job job);

    void add(Job job, Timestamp scheduleTime);

    void startAddingToQueue();
}
