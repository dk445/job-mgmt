package job.enums;

import lombok.Getter;

public enum State {

    QUEUED("Queued"),
    RUNNING("Running"),
    SUCCESS("Success"),
    FAILED("Failed");

    @Getter
    String state;

    State(String state){this.state = state;}
}
