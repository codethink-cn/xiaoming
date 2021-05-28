package com.chuanwise.xiaoming.core.time.task;

import com.chuanwise.xiaoming.api.time.task.TimeTask;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import lombok.Data;

@Data
public abstract class TimeTaskImpl extends XiaomingObjectImpl implements TimeTask {
    long time;
    long period = -1;
    boolean success = false;
    String description = "（无描述）";

    @Override
    public boolean isPeriodic() {
        return period > 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
