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
     * Consumes jobs from user for which intends to run immediately and store them as a pair of -
     *  current time & list of job objects that are schedule for that time in Treemap.
     * @param job - object of class Job with specific job configuration
     */
    void add(Job job);

    /**
     * Consumes jobs from user for which intends to run on scheduled time and store them as a pair of -
     * schedule time & list of job objects that are schedule for that time in Treemap.
     * @param job - object of class Job with specific job configuration
     * @param scheduleTime- specific time for the job on which it should start executing.
     */
    void add(Job job, Long scheduleTime);

    void stop();

}
