package com.scheduler.job.job;

import com.scheduler.job.enums.State;
import com.scheduler.job.schedule.SchedulerImpl;
import com.scheduler.job.schedule.Task;
import lombok.Data;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * prototype of each Job that contains
 * String JobId - Id of job
 * Object JobConfiguration - Configuration of specific job
 * Object State - State of job at any point of time
 * Object Callable (executable job code) - actual executable job code
 */

@Data
public class Job implements Runnable {
    private String jobId;
    private JobConfigurations jobConfigurations;
    private State jobState = null;
    private Runnable runnable;

    private static Logger log = Logger.getLogger(SchedulerImpl.class.getName());

    /**
     * Constructor for Job
     * @param jobId
     * @param jobConfigurations
     * @param runnable
     */
    public Job(String jobId, JobConfigurations jobConfigurations, Runnable runnable) {
        this.jobId = jobId;
        this.jobConfigurations = jobConfigurations;
        this.runnable = runnable;
    }

    /**
     * State of job at any point of time can be define as
     * Queued - job is scheduled for execution
     * Running - job is running
     * Successful - job is completed successfully
     * Failed - job is failed due to exception/error
     * @param jobState
     */
    public void setJobState(State jobState) {
        this.jobState = jobState;
        log.info("Job State Changed. JobId :" + this.getJobId() + " is " + this.getJobState().getState());
    }

    @Override
    public void run() {

        try {

            this.setJobState(State.RUNNING); // updating job status to running
            this.runnable.run();
            this.setJobState(State.SUCCESS); // updating job status to success

        } catch (Exception e) {

            this.setJobState(State.FAILED);  // updating job status to failed

        }
    }
}


