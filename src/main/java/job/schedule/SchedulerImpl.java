package job.schedule;

import job.config.Job;
import job.enums.State;
import lombok.Getter;

import java.util.Calendar;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class SchedulerImpl implements Scheduler{

    private static final int CAPACITY = 10;

    /*
    * Inserting jobs by their priority
    */
    private final BlockingQueue<TimedTask> queue = new PriorityBlockingQueue<>(CAPACITY,
            (s, t) -> {
                if (s.getJob().getJobConfigurations().getPriority().getValue() > t.getJob().getJobConfigurations().getPriority().getValue())
                    return 1;
                else if (s.getJob().getJobConfigurations().getPriority().getValue() < t.getJob().getJobConfigurations().getPriority().getValue())
                    return -1;
                return 0;

            });

    private final Object lock = new Object();
    private volatile boolean running = true;


    @Override
    public void start() throws InterruptedException {
        while (running) {
            TimedTask task = queue.take();
            if (task != null) {

                task.run(); // Ideally this should be run in a separate thread.
            }
            waitForNextTask();
        }
    }

    @Override
    public void waitForNextTask() throws InterruptedException {
        synchronized (lock) {
            TimedTask nextTask = queue.peek();
            while (nextTask == null || !nextTask.shouldRunNow()) {
                if (nextTask == null) {
                    lock.wait();
                } else {
                    lock.wait(nextTask.runFromNow());
                }
                nextTask = queue.peek();
            }
        }
    }

    public void add(Job job) {
        add(job,0);
    }

    @Override
    public void add(Job job, long delayMs) {
        synchronized (lock) {
            queue.offer(TimedTask.fromTask(job, delayMs));
            job.setJobState(State.QUEUED);  // updating job status to queued
            System.out.println(job.getJobId() + " is queued.");
            lock.notify();
        }
    }

    @Getter
    private static class TimedTask {
        private Job job;
        private Calendar scheduledTime;

        public TimedTask(Job job, Calendar scheduledTime) {
            this.job = job;
            this.scheduledTime = scheduledTime;
        }

        public static TimedTask fromTask(Job job, long delayMs) {
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(now.getTimeInMillis() + delayMs);
            return new TimedTask(job, now);
        }

        public Calendar getScheduledTime() {
            return scheduledTime;
        }

        public long runFromNow() {
            return scheduledTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        }

        public boolean shouldRunNow() {
            return runFromNow() <= 0;
        }

        public void run() {
            job.setJobState(State.RUNNING); // updating job status to running
            System.out.println(job.getJobId() + " is running.");
            try {
                job.run();
                job.setJobState(State.SUCCESS); // updating job status to success
                System.out.println(job.getJobId() + " is completed successfully.");
            } catch (Exception ex){
                job.setJobState(State.FAILED);  // updating job status to failed
                System.out.println(job.getJobId() + " is failed due to "+ex.toString());
            }
        }
    }

}
