package com.chuanwise.xiaoming.api.thread;

import com.chuanwise.xiaoming.api.object.HostObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.Set;

/**
 * 小明的隔三差五保存器
 * @author Chuanwise
 */
public interface RegularPreserveManager extends HostObject, Runnable {
    void save(XiaomingUser user);

    void save();

    void readySave(Preservable preservable);

    Set<Preservable> getPreservables();

    long getLastSaveTime();
}
