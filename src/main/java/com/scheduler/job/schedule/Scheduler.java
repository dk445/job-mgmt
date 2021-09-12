package com.scheduler.job.schedule;

import com.scheduler.job.job.Job;
/**
 *  Scheduler interface contains the essential methods that are responsible for following operations
 *  Starting the job execution engine to run the job
 *  Consuming jobs from user
 *  Scheduling jobs in queue
 */
public interface Scheduler {

    /**
     * Stop the scheduler execution engine
     */
    void start();

    /**
     * Adding jobs for execution
     * @param job
     * @throws SchedulerImpl.InvalidEngineStateException
     */
    void add(Job job) throws SchedulerImpl.InvalidEngineStateException;

    /**
     * Adding jobs for execution
     * @param job
     * @param scheduleTime
     * @throws SchedulerImpl.InvalidEngineStateException
     */
    void add(Job job, Long scheduleTime) throws SchedulerImpl.InvalidEngineStateException;

    /**
     * Stop the scheduler execution engine
     */
    void stop();

}
