package com.scheduler.Application;

import com.scheduler.job.job.Job;
import com.scheduler.job.job.JobConfigurations;
import com.scheduler.job.enums.Priority;
import com.scheduler.job.schedule.SchedulerImpl;
import java.sql.Timestamp;
import java.util.logging.Logger;

public class Application {
    private static Logger log = Logger.getLogger(Application.class.getName());
    
    public static void main(String[] args) {
        final SchedulerImpl schedulerImpl = new SchedulerImpl();

        new Thread(() -> {

            try {
                schedulerImpl.start();
            } catch (InterruptedException e) {
            }

        }).start();

        new Thread(schedulerImpl::startAddingToQueue).start();

        new Thread(() -> {
            schedulerImpl.add(new Job("J01",
                            new JobConfigurations("job1", Priority.LOW),
                            () -> {
                                log.info("printing number 1 to 5");
                                Thread.sleep(1000);
                                for(int i=1;i<=5;i++)
                                    log.info(String.valueOf(i));
                                return null;
                            }),
                    Timestamp.valueOf("2021-09-11 11:29:00").getTime());
            schedulerImpl.add(new Job("J02",
                    new JobConfigurations("job2",Priority.MEDIUM),
                    () -> {
                        log.info("printing number 1 to 20");
                        Thread.sleep(1000);
                        for(int i=1;i<=20;i++)
                            log.info(String.valueOf(i));
                        return null;
                    }));

        }).start();

        new Thread(() -> {
            schedulerImpl.add(new Job("J03",
                        new JobConfigurations("job3", Priority.HIGH),
                        () -> {
                            log.info("printing number 1 to 30");
                            Thread.sleep(1000);
                            for(int i=0;i<=30;i++)
                                log.info(String.valueOf(i));
                            return null;
                        }),
                    Timestamp.valueOf("2021-09-11 11:29:30").getTime());
        }).start();

    }
}
