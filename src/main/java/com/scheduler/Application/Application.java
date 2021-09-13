package com.scheduler.Application;

import com.scheduler.job.job.Job;
import com.scheduler.job.job.JobConfigurations;
import com.scheduler.job.enums.Priority;
import com.scheduler.job.schedule.Scheduler;
import com.scheduler.job.schedule.SchedulerImpl;
import java.util.logging.Logger;

public class Application {

    private static Logger log = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) throws Exception {


        Scheduler scheduler = new SchedulerImpl(1);
        scheduler.start();

        scheduler.add(new Job("J01",new JobConfigurations(Priority.LOW), new RunnableJob()));

        scheduler.add(new Job("J02",new JobConfigurations(Priority.MEDIUM), new RunnableJob()));

        scheduler.add(new Job("J03",new JobConfigurations(Priority.HIGH), new RunnableJob()));

        //stop the scheduler after 20 seconds.
        Thread.sleep(1000 * 20);
        scheduler.stop();
    }
    static class RunnableJob implements Runnable {
        @Override
        public void run() {
            for(int i = 1 ; i <= 3 ; i ++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

