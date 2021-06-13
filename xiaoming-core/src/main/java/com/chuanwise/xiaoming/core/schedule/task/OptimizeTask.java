package com.chuanwise.xiaoming.core.schedule.task;

import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;

public class OptimizeTask extends ScheduableTaskImpl<Void> {
    @Override
    public Void execute() {
        getXiaomingBot().optimize();
        return null;
    }
}
