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
     * Consumes fresh job from the queue to execute that are sorted on the basis of scheduled time and priority
     * @throws InterruptedException : if interrupted while waiting while an element becomes available
     */
    void start() throws InterruptedException;


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

    /**
     * Consumes jobs from the treemap and add them into the queue for execution on the basis of
     * schedule time and priority.
     * first schedules the job which should execute immediately and if more than one jobs are schedule
     * for the same time then only it will get scheduled as per priority.
     */
    void startAddingToQueue();
}
