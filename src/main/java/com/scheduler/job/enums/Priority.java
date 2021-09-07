package com.scheduler.job.enums;

import lombok.Getter;

public enum Priority {

    LOW(3),
    MEDIUM(2),
    HIGH(1);

    @Getter
    Integer value;

    Priority(Integer value){this.value = value;}
}
