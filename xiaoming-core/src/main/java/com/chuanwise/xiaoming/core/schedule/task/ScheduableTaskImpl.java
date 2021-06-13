package com.chuanwise.xiaoming.core.schedule.task;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import com.chuanwise.xiaoming.core.schedule.async.AsyncResultImpl;
import lombok.*;

import java.util.Objects;
import java.util.concurrent.Callable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduableTaskImpl<R> extends AsyncResultImpl<R> implements ScheduableTask<R> {
    long time;
    long period = -1;

    transient ScheduableTask<R> son = null;
    transient ScheduableTask<R> father = null;

    @Setter
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
    public ScheduableTask<R> clone() throws CloneNotSupportedException {
        final ScheduableTaskImpl<R> clone = (ScheduableTaskImpl<R>) super.clone();
        clone.setFinished(false);
        setSon(clone);
        clone.setFather(this);
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduableTaskImpl)) {
            return false;
        }
        ScheduableTaskImpl<?> that = (ScheduableTaskImpl<?>) o;
        return time == that.time &&
                period == that.period &&
                Objects.equals(son, that.son) &&
                Objects.equals(father, that.father) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, period, son, father, description);
    }
}
