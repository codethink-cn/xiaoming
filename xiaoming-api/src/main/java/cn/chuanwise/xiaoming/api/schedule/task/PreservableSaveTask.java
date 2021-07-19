package cn.chuanwise.xiaoming.api.schedule.task;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.Set;

public interface PreservableSaveTask extends ScheduableTask<Void> {
    void save(XiaomingUser user);

    void save();

    void readySave(Preservable<?> preservable);

    long getLastSaveTime();

    Set<Preservable<?>> getPreservables();
}
