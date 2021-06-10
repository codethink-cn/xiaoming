package com.chuanwise.xiaoming.api.thread;

import com.chuanwise.xiaoming.api.object.ModuleObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.user.XiaomingUser;

import java.util.List;
import java.util.Set;

/**
 * 小明的隔三差五保存器
 * @author Chuanwise
 */
public interface Finalizer extends ModuleObject, Runnable {
    List<Runnable> getOnFinal();

    default void addOnFinal(Runnable runnable) {
        getOnFinal().add(runnable);
    }

    default void onFinal() {
        for (Runnable runnable : getOnFinal()) {
            try {
                runnable.run();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    void save(XiaomingUser user);

    void save();

    void readySave(Preservable preservable);

    Set<Preservable> getPreservables();

    long getLastSaveTime();
}
