package Application;

import job.config.Job;
import job.config.JobConfigurations;
import job.enums.Priority;
import job.schedule.SchedulerImpl;

import java.sql.Timestamp;
import java.util.concurrent.Callable;

public class Application {
    public static void main(String[] args) {
        final SchedulerImpl schedulerImpl = new SchedulerImpl();


        schedulerImpl.add(new Job("J01",
                        new JobConfigurations("job1", Priority.LOW, "print 1 to 100"),
                        () -> {
                            System.out.println("printing number 1 to 100");
                            for(int i=0;i<=100;i++)
                                System.out.println(i);
                            return null;
                        }),
                Timestamp.valueOf("2021-09-04 13:38:00"));
        schedulerImpl.add(new Job("J02",
                        new JobConfigurations("job2",Priority.HIGH,"print 1 to 50"),
                        () -> {
                            System.out.println("printing number 1 to 50");
                            for(int i=0;i<=50;i++)
                                System.out.println(i);
                            return null;
                        }),
                Timestamp.valueOf("2021-09-04 13:38:00"));
        schedulerImpl.add(new Job("J03",
                        new JobConfigurations("job3",Priority.MEDIUM,"print 1 to 150"),
                        () -> {
                            System.out.println("printing number 1 to 150");
                            for(int i=0;i<=150;i++)
                                System.out.println(i);
                            return null;
                        }));

        new Thread(() -> {
            schedulerImpl.add(new Job("J04",
                        new JobConfigurations("job4", Priority.MEDIUM, "print 1 to 20"),
                        () -> {
                            System.out.println("printing number 1 to 20");
                            for(int i=0;i<=20;i++)
                                System.out.println(i);
                            return null;
                        }));
            schedulerImpl.add(new Job("J05",
                        new JobConfigurations("job5",Priority.HIGH,"print 1 to 15"),
                        () -> {
                            System.out.println("printing number 1 to 15");
                            for(int i=0;i<=15;i++)
                                System.out.println(i);
                            return null;
                        }));
            schedulerImpl.add(new Job("J06",
                        new JobConfigurations("job6",Priority.HIGH,"print 1 to 25"),
                        () -> {
                            System.out.println("printing number 1 to 25");
                            for(int i=0;i<=25;i++)
//                                throw new IndexOutOfBoundsException();
                                System.out.println(i);
                            return null;
                        }));
        }).start();

        new Thread(schedulerImpl::startAddingToQueue).start();

        new Thread(() -> {
            try {
                schedulerImpl.start();
            } catch (Exception e) {

            }
        }).start();

    }
}
