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
    private final TreeMap<Long, List<Job>> jobsToBeExecute= new TreeMap<>();

    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;

    @Getter
    private List<String> jobs= new ArrayList<>();
    @Getter
    private  List<String> Lastjobs = new ArrayList<>();

    private static final Logger log = Logger.getLogger(SchedulerImpl.class.getName());

    /**
     * Create SchedulerImpl isntance.
     * @param maxConcurrency the number of jobs that can run in parallel in this scheduler.
     */
    public SchedulerImpl(int maxConcurrency){
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
                if (s.getJobConfigurations().getPriority().getValue() > t.getJobConfigurations().getPriority().getValue())
                    return 1;
                else if (s.getJobConfigurations().getPriority().getValue() < t.getJobConfigurations().getPriority().getValue())
                    return -1;
                return 0;

            });

    private volatile boolean running = true;


    @Override
    public void add(Job job) {
        add(job, System.currentTimeMillis());
    }

    @Override
    public void add(Job job, Long scheduleTime) {
        synchronized (this) {
            if(jobsToBeExecute.containsKey(scheduleTime)){
                jobsToBeExecute.get(scheduleTime).add(job);
            }
            else {
                List<Job> jobList = new ArrayList<>();

                jobList.add(job);
                jobsToBeExecute.put(scheduleTime, jobList);
            }
        }
    }

    @Override
    public void stop(){
        running = false;
        executorService.shutdown();
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
                    if (!jobsToBeExecute.isEmpty() && new Timestamp(jobsToBeExecute.firstEntry().getKey()).before(new Timestamp(System.currentTimeMillis()))) {
                        for (Job newJob:jobsToBeExecute.firstEntry().getValue()) {
                            newJob.setJobState(State.QUEUED);  // updating jobs status to queued
                        }
                        queue.addAll(jobsToBeExecute.firstEntry().getValue());
                        jobsToBeExecute.pollFirstEntry();
                    }
                } catch (Exception ex){
                    log.log(Level.SEVERE, "Exception in ProcessTimedTasks", ex);
                }
            }
        }
    }
}

