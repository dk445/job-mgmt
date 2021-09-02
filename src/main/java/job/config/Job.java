package job.config;

import job.enums.State;
import job.schedule.Task;
import lombok.Data;

import java.util.concurrent.Callable;

@Data
public class Job implements Task {
    private String jobId;
    private JobConfigurations jobConfigurations;
    private State jobState = null;
    private Callable executableJob;

    public Job(String jobId, JobConfigurations jobConfigurations, Callable executableJob) {
        this.jobId = jobId;
        this.jobConfigurations = jobConfigurations;
        this.executableJob =executableJob;
    }

    @Override
    public void run() throws Exception {
        this.executableJob.call();
    }
}


