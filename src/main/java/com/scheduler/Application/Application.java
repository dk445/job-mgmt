package com.scheduler.Application;

import com.scheduler.job.job.Job;
import com.scheduler.job.job.JobConfigurations;
import com.scheduler.job.enums.Priority;
import com.scheduler.job.schedule.Scheduler;
import com.scheduler.job.schedule.SchedulerImpl;

import java.sql.Timestamp;
import java.util.logging.Logger;

public class Application {

    private static Logger log = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) throws Exception {


        Scheduler scheduler = new SchedulerImpl(5);

        scheduler.add(new Job("J01",
                              new JobConfigurations(Priority.LOW),
                              () -> {
                                  log.info("printing number 1 to 5");
                                  try {
                                      Thread.sleep(1000);
                                  } catch (InterruptedException e) {
                                      e.printStackTrace();
                                  }
                                  for (int i = 1; i <= 5; i++) {
                                      log.info(String.valueOf(i));
                                  }
                              }),
                      Timestamp.valueOf("2021-09-11 11:29:00").getTime());

        scheduler.add(new Job("J02",
                              new JobConfigurations(Priority.MEDIUM),
                              () -> {
                                  log.info("printing number 1 to 20");
                                  try {
                                      Thread.sleep(1000);
                                  } catch (InterruptedException e) {
                                      e.printStackTrace();
                                  }
                                  for (int i = 1; i <= 20; i++) {
                                      log.info(String.valueOf(i));
                                  }

                              }));

        scheduler.add(new Job("J03",
                              new JobConfigurations(Priority.HIGH),
                              () -> {
                                  log.info("printing number 1 to 30");
                                  try {
                                      Thread.sleep(1000);
                                  } catch (InterruptedException e) {
                                      e.printStackTrace();
                                  }
                                  for (int i = 0; i <= 30; i++) {
                                      log.info(String.valueOf(i));
                                  }
                              }),
                      Timestamp.valueOf("2021-09-11 11:29:30").getTime());

        //stop the scheduler after 10 seconds.
        Thread.sleep(1000 * 10);
        scheduler.stop();

    }
}
