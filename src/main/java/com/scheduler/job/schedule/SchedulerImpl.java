package com.scheduler.job.schedule;

import com.scheduler.job.config.Job;
import com.scheduler.job.enums.State;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;


public class SchedulerImpl implements Scheduler{

    private static final int CAPACITY = 10;
    private final TreeMap<Timestamp, List<Job>> jobsToBeExecute= new TreeMap<>();

    @Getter
    private final List<String> jobs= new ArrayList<>();
    @Getter
    private  List<String> Lastjobs = new ArrayList<>();

    private static final Logger log = Logger.getLogger(SchedulerImpl.class.getName());

    private final Object lock = new Object();

    /*
    * sort jobs by their priority
    */
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
    public void start() throws Exception {
        while (running) {

            Job freshJob;
            freshJob = queue.take();
            if (freshJob != null) {

//                while(freshJob.getJobState()!=State.QUEUED){}

                freshJob.run();

//                if(freshJob.getJobState() == State.RUNNING) jobs.add(freshJob.getJobId());

            }
        }
    }

    @Override
    public void add(Job job) {
        add(job, new Timestamp(System.currentTimeMillis()));
    }

    public void printJobs(){
        while (running){
            if(!this.getLastjobs().equals(this.getJobs())) {
                log.info("Job Order " + this.getJobs());
            }
            Lastjobs = this.getJobs();
        }
    }

    @Override
    public void add(Job job, Timestamp scheduleTime) {
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
                if (!jobsToBeExecute.isEmpty() && jobsToBeExecute.firstEntry().getKey().before(new Timestamp(System.currentTimeMillis()))) {
//                    synchronized (this) {
                        for (Job newJob:jobsToBeExecute.firstEntry().getValue()) {
                            newJob.setJobState(State.QUEUED);  // updating jobs status to queued
//                            log.info("Job with jobId : "+newJob.getJobId()+" is scheduled successfully...");
                        }
                        queue.addAll(jobsToBeExecute.firstEntry().getValue());
                        jobsToBeExecute.pollFirstEntry();
//                    }

                }
            }catch (Exception ex){
                log.info("Unknown error occurred while scheduling the jobs due to : "+ex);
            }
        }
    }
}
