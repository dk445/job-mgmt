package job.schedule;

import job.config.Job;
import job.enums.State;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class SchedulerImpl implements Scheduler{

    private static final int CAPACITY = 10;
    private final TreeMap<Timestamp, List<Job>> jobsToBeExecute= new TreeMap<>();

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
                try {

                    while(freshJob.getJobState()!=State.QUEUED){}

                    freshJob.setJobState(State.RUNNING); // updating job status to running
                    System.out.println(freshJob.getJobId() + " is running.");

                    freshJob.run();

                    freshJob.setJobState(State.SUCCESS); // updating job status to success
                    System.out.println(freshJob.getJobId() + " is completed successfully.");

                } catch (Exception ex){
                    freshJob.setJobState(State.FAILED);  // updating job status to failed
                    System.out.println(freshJob.getJobId() + " is failed due to "+ex.toString());
                }
            }
        }
    }

    @Override
    public void add(Job job) {
        add(job, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public void add(Job job, Timestamp scheduleTime) {
        synchronized (lock) {
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
                    synchronized (lock) {
                        queue.addAll(jobsToBeExecute.firstEntry().getValue());
                        for (Job newJob:jobsToBeExecute.firstEntry().getValue()) {
                            newJob.setJobState(State.QUEUED);  // updating jobs status to queued
                            System.out.println(newJob.getJobId() + " is queued.");
                        }
                        jobsToBeExecute.pollFirstEntry();

                    }

                }
            }catch (Exception ex){
                System.out.println(ex);
            }
        }
    }
}
