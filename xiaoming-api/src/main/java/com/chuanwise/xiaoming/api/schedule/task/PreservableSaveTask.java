package com.chuanwise.xiaoming.api.schedule.task;

import com.chuanwise.toolkit.preservable.Preservable;
import com.chuanwise.xiaoming.api.schedule.task.ScheduableTask;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.Set;

public interface PreservableSaveTask extends ScheduableTask<Void> {
    void save(XiaomingUser user);

    void save();

    void readySave(Preservable<?> preservable);

    long getLastSaveTime();

    Set<Preservable<?>> getPreservables();
}
