package com.scheduler.job.schedule;

import com.scheduler.job.enums.Priority;
import com.scheduler.job.job.Job;
import com.scheduler.job.enums.State;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;

/**
 * Implementation of scheduler interface for scheduling and running the jobs
 */
public class SchedulerImpl implements Scheduler{

    private static final int CAPACITY = 10;
    private final TreeMap<Long, List<Job>> jobsToBeExecute= new TreeMap<>();

    @Getter
    private List<String> jobs= new ArrayList<>();
    @Getter
    private  List<String> Lastjobs = new ArrayList<>();

    private static final Logger log = Logger.getLogger(SchedulerImpl.class.getName());


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
    public void start() throws InterruptedException {
        while (running) {

            Job freshJob;
            freshJob = queue.take();
            if (freshJob != null) {

                freshJob.run();

            }
        }
    }

    @Override
    public void add(Job job) {
        job.getJobConfigurations().setPriority(Priority.HIGH);
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
    public void startAddingToQueue() {
        while (running){
            try {
                if (!jobsToBeExecute.isEmpty() && new Timestamp(jobsToBeExecute.firstEntry().getKey()).before(new Timestamp(System.currentTimeMillis()))) {
                    for (Job newJob:jobsToBeExecute.firstEntry().getValue()) {
                        newJob.setJobState(State.QUEUED);  // updating jobs status to queued
                    }
                    queue.addAll(jobsToBeExecute.firstEntry().getValue());
                    jobsToBeExecute.pollFirstEntry();
                }
            }catch (Exception ex){

                log.info("Unknown error occurred while scheduling the jobs due to : "+ex);
            }
        }
    }
}
