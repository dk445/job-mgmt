package com.scheduler.job.config;

import com.scheduler.job.enums.Priority;
import lombok.Data;



@Data
public class JobConfigurations {

    private String jobName;
    private Priority priority;

    public JobConfigurations(String jobName, Priority priority) {
        this.jobName = jobName;
        this.priority = priority;
    }
}
