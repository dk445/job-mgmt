package com.scheduler.job.job;

import com.scheduler.job.enums.Priority;
import lombok.Data;

/**
 * prototype of Job configuration
 * String jobName
 */
@Data
public class JobConfigurations {

    private String jobName;
    private Priority priority;

    /**
     * Constructor for Job Configuration
     * @param jobName
     * @param priority
     */
    public JobConfigurations(String jobName, Priority priority) {
        this.jobName = jobName;
        this.priority = priority;
    }
}
