package com.scheduler.job.config;

import com.scheduler.job.enums.State;
import com.scheduler.job.schedule.SchedulerImpl;
import com.scheduler.job.schedule.Task;
import lombok.Data;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

@Data
public class Job implements Task {
    private String jobId;
    private JobConfigurations jobConfigurations;
    private State jobState = null;
    private Callable executableJob;

    private static Logger log = Logger.getLogger(SchedulerImpl.class.getName());

    public Job(String jobId, JobConfigurations jobConfigurations, Callable executableJob) {
        this.jobId = jobId;
        this.jobConfigurations = jobConfigurations;
        this.executableJob =executableJob;
    }

    public void setJobState(State jobState) {
        this.jobState = jobState;
        log.info("Job with jobId : "+this.getJobId()+" is "+this.getJobState().getState());
//        System.out.println("Job with jobId : "+this.getJobId()+" is "+this.getJobState().getState());
    }

    @Override
    public void run() {
        try {

            this.setJobState(State.RUNNING); // updating job status to running

            try {
                this.executableJob.call();
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.setJobState(State.SUCCESS); // updating job status to success



        } catch (Exception e) {

            this.setJobState(State.FAILED);  // updating job status to failed
            e.printStackTrace();

        }
    }
}


