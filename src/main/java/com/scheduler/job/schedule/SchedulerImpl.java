package com.scheduler.job.schedule;

import com.scheduler.job.enums.Priority;
import com.scheduler.job.job.Job;
import com.scheduler.job.enums.State;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of scheduler interface for scheduling and running the jobs
 */
public class SchedulerImpl implements Scheduler{

    private static final int CAPACITY = 10;
    private final int maxConcurrency;
    private final TreeMap<Long, List<Job>> jobsToBeExecute= new TreeMap<>();
    private volatile boolean running = false;

    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;

    private static final Logger log = Logger.getLogger(SchedulerImpl.class.getName());

    /**
     * Create SchedulerImpl instance.
     * @param maxConcurrency the number of jobs that can run in parallel in this scheduler.
     */
    public SchedulerImpl(int maxConcurrency){
        this.maxConcurrency = maxConcurrency;
    }

    @Override
    public void start() {
        running = true;
        //all the submitted jobs will run in it's own thread using this executor service.
        //+1 as one thread is used by ProcessTaskQueue task.
        executorService = Executors.newFixedThreadPool(maxConcurrency+1);
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        //an always running thread which keeps checking task queue and executes them in order of their priority.
        executorService.submit(new ProcessTaskQueue());
        //Schedule to run ScheduledJobTimerTask every second in a new thread.
        //We can change period and time-unit here to every-minute once if required.
        scheduledExecutorService.scheduleAtFixedRate(new ProcessTimedTasks() , 0, 1, TimeUnit.SECONDS);
    }

    /**
    * sorting jobs by their priority
    **/
    private final BlockingQueue<Job> queue = new PriorityBlockingQueue<>(CAPACITY,
            (s, t) -> {
                //Used >= to keep the same order in which the job is added if two jobs have same priority.
                if (s.getJobConfigurations().getPriority().getValue() >= t.getJobConfigurations().getPriority().getValue())
                    return 1;
                else if (s.getJobConfigurations().getPriority().getValue() < t.getJobConfigurations().getPriority().getValue())
                    return -1;
                return 0;
            });



    /**
     * Consumes jobs from user; which intends to run immediately and store them as a pair of -
     * current time & list of job objects that are schedule for that time in Treemap.
     * @param job  object of class Job with specific job configuration
     * @throws InvalidEngineStateException when user try to add the job while engine is not in running state
     */
    @Override
    public void add(Job job) throws InvalidEngineStateException {
        if(running)
            add(job, System.currentTimeMillis());
        else{
            throw new InvalidEngineStateException("");
        }
    }

    /**
     * Consumes jobs from user for which intends to run on scheduled time and store them as a pair of -
     * schedule time & list of job objects that are schedule for that time in Treemap.
     * @param job - object of class Job with specific job configuration
     * @param scheduleTime- specific time for the job on which it should start executing.
     * @throws InvalidEngineStateException when user try to add the job while engine is not in running state
     */
    @Override
    public void add(Job job, Long scheduleTime) throws InvalidEngineStateException {
        if(running) {
            synchronized (this) {
                if (jobsToBeExecute.containsKey(scheduleTime)) {
                    jobsToBeExecute.get(scheduleTime).add(job);
                } else {
                    List<Job> jobList = new ArrayList<>();

                    jobList.add(job);
                    jobsToBeExecute.put(scheduleTime, jobList);
                }
            }
        }
        else{
            throw new InvalidEngineStateException("Engine is not in running state");
        }

    }

    /**
     * By calling this method execution engine will stops the execution engine and
     * all pending jobs in queue will not get execute. User will also not able to add any jobs.
     */
    @Override
    public void stop(){
        running = false;
        executorService.shutdown();
        scheduledExecutorService.shutdownNow();
        jobsToBeExecute.clear();
        queue.clear();
    }


    /**
     * Task to keep checking any pending tasks in queue and execute them based priority.
     */
    private class ProcessTaskQueue implements Runnable {
        @Override
        public void run() {
            while (running) {
                try {
                    Job freshJob;
                    freshJob = queue.take();
                    if (freshJob != null) {
                        executorService.submit(freshJob);
                    }
                } catch (InterruptedException ex) {
                    log.log(Level.SEVERE, "InterruptedException in ProcessTaskQueue", ex);
                    Thread.currentThread().interrupt();
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Exception in ProcessTaskQueue", ex);
                }

            }
        }
    }

    /**
     * Background thread used to check if there is any jobs scheduled to run at specified time.
     */
    private class ProcessTimedTasks implements Runnable {
        @Override
        public void run() {
            while (running){

                try {
                    if (!jobsToBeExecute.isEmpty() && jobsToBeExecute.firstEntry().getKey() < System.currentTimeMillis()) {
                        for (Job newJob:jobsToBeExecute.firstEntry().getValue()) {
                            newJob.setJobState(State.QUEUED);  // updating jobs status to queued
                        }
                        jobsToBeExecute.firstEntry().getValue().forEach( queue::add );
                        jobsToBeExecute.pollFirstEntry();
                    }
                } catch (Exception ex){
                    log.log(Level.SEVERE, "Exception in ProcessTimedTasks", ex);
                }
            }
        }
    }
    public class InvalidEngineStateException extends Exception {
        public InvalidEngineStateException(String errorMessage) {
            super(errorMessage);
        }
    }
}

