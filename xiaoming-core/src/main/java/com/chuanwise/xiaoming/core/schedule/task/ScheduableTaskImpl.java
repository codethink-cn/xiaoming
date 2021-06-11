package com.chuanwise.xiaoming.core.schedule.task;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import com.chuanwise.xiaoming.core.schedule.async.AsyncResultImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.Callable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduableTaskImpl<R> extends AsyncResultImpl<R> implements ScheduableTask<R> {
    long time;
    long period = -1;
    String description = "（无描述）";

    transient XiaomingBot xiaomingBot;

    public ScheduableTaskImpl(Callable<R> callable) {
        super(callable);
    }

    @Override
    public boolean isPeriodic() {
        return period > 0;
    }

    @Override
    public ScheduableTask clone() throws CloneNotSupportedException {
        return ((ScheduableTask) super.clone());
    }
}
