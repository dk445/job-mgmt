package com.scheduler.job.job;

import com.scheduler.job.enums.Priority;
import lombok.Data;

/**
 * prototype of Job configuration
 * String jobName
 */
@Data
public class JobConfigurations {

    private Priority priority;

    /**
     * Constructor for Job Configuration
     * @param priority
     */
    public JobConfigurations(Priority priority) {
        this.priority = priority;
    }
}
