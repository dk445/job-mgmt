package job.enums;

import lombok.Getter;

public enum Priority {

    LOW(3),
    MEDIUM(2),
    HIGH(1);

    @Getter
    Integer priority;

    Priority(Integer priority){this.priority = priority;}
}
