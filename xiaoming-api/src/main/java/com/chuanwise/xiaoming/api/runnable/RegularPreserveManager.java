package com.chuanwise.xiaoming.api.runnable;

import com.chuanwise.xiaoming.api.object.HostXiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.Set;

/**
 * 小明的隔三差五保存器
 * @author Chuanwise
 */
public interface RegularPreserveManager extends HostXiaomingObject, Runnable {
    void save(XiaomingUser user);

    void save();

    void readySave(Preservable preservable);

    Set<Preservable> getPreservables();
}
