package com.scheduler.job.enums;

import lombok.Getter;

public enum State {

    QUEUED("Queued"),
    RUNNING("Running"),
    SUCCESS("Successful"),
    FAILED("Failed");

    @Getter
    String state;

    State(String state){this.state = state;}
}
