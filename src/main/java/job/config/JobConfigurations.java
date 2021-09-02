package job.config;

import job.enums.Priority;
import lombok.Data;



@Data
public class JobConfigurations {

    private String jobName;
    private Priority priority;
    private String jobText;

    public JobConfigurations(String jobName, Priority priority, String jobText) {
        this.jobName = jobName;
        this.priority = priority;
        this.jobText = jobText;
    }
}
