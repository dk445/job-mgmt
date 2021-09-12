import com.scheduler.job.enums.Priority;
import com.scheduler.job.job.Job;
import com.scheduler.job.job.JobConfigurations;
import com.scheduler.job.schedule.Scheduler;
import com.scheduler.job.schedule.SchedulerImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SchedulerImplTest {

    @Test
    public void single_thread_single_job() throws Exception{


        Scheduler scheduler = new SchedulerImpl(1);
        Queue<String> actualOutputQueue = new ConcurrentLinkedQueue<>();

        scheduler.add(new Job("job1", new JobConfigurations(Priority.MEDIUM), new TestJob(actualOutputQueue, "job1")));

        Thread.sleep( 1000);
        scheduler.stop();


        Assert.assertEquals(actualOutputQueue,
                            getExpectedOutputQueue(Arrays.asList("job1#1", "job1#2", "job1#3")));

    }

    @Test
    public void single_thread_multiple_jobs_same_priority() throws Exception{

        Scheduler scheduler = new SchedulerImpl(1);
        Queue<String> actualOutputQueue = new ConcurrentLinkedQueue<String>();


        scheduler.add(new Job("job1", new JobConfigurations(Priority.MEDIUM), new TestJob(actualOutputQueue, "job1")));
        scheduler.add(new Job("job2", new JobConfigurations(Priority.MEDIUM), new TestJob(actualOutputQueue, "job2")));
        scheduler.add(new Job("job3", new JobConfigurations(Priority.MEDIUM), new TestJob(actualOutputQueue, "job3")));

        Thread.sleep( 2000);
        scheduler.stop();

        Assert.assertEquals(actualOutputQueue,
                            getExpectedOutputQueue(Arrays.asList("job1#1", "job1#2", "job1#3", "job2#1", "job2#2", "job2#3", "job3#1", "job3#2", "job3#3")));

    }

    @Test
    public void single_thread_scheduled_multiple_job_same_priority() throws Exception{

        Scheduler scheduler = new SchedulerImpl(1);
        Queue<String> actualOutputQueue = new ConcurrentLinkedQueue<String>();


        scheduler.add(new Job("job1", new JobConfigurations(Priority.MEDIUM), new TestJob(actualOutputQueue, "job1")), System.currentTimeMillis()+100);
        scheduler.add(new Job("job2", new JobConfigurations(Priority.MEDIUM), new TestJob(actualOutputQueue, "job2")), System.currentTimeMillis());

        Thread.sleep( 2000);
        scheduler.stop();

        Assert.assertEquals(actualOutputQueue,
                getExpectedOutputQueue(Arrays.asList("job2#1", "job2#2", "job2#3", "job1#1", "job1#2", "job1#3")));

    }


    private Queue<String> getExpectedOutputQueue(List<String> list){
        Queue<String> queue = new ConcurrentLinkedQueue<String>();
        for (String s : list) {
            queue.add(s);
        }
        return queue;
    }

    static class TestJob implements Runnable {

        private final Queue<String> queue;
        private final String jobId;

        TestJob(Queue<String> queue, String jobId){
            this.queue = queue;
            this.jobId = jobId;
        }

        @Override
        public void run() {
            for(int i = 1 ; i <= 3 ; i ++) {

                queue.add(jobId + "#" + i);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
