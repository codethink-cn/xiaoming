package com.chuanwise.xiaoming.core.time.task;

import lombok.Data;

@Data
public abstract class TimeTask implements Runnable {
    long time;
    long period = -1;

    boolean isPeriodic() {
        return period > 0;
    }
}
