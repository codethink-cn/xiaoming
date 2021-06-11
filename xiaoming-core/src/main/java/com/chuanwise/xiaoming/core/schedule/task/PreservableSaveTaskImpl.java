package com.chuanwise.xiaoming.core.schedule.task;

import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.schedule.task.PreservableSaveTask;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import lombok.Getter;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Getter
public class PreservableSaveTaskImpl extends ScheduableTaskImpl<Void> implements PreservableSaveTask {
    Set<Preservable<?>> preservables = new CopyOnWriteArraySet<>();

    long lastSaveTime = System.currentTimeMillis();

    public PreservableSaveTaskImpl() {
        setCallable(() -> {
            save();
            return null;
        });
    }

    @Override
    public void save(XiaomingUser user) {
        synchronized (preservables) {
            final int needsToSaveFileNumber = preservables.size();
            if (needsToSaveFileNumber == 0) {
                user.sendMessage("没有需要保存的文件");
                return;
            }
            lastSaveTime = System.currentTimeMillis();
            preservables.removeIf(Preservable::save);
            if (preservables.isEmpty()) {
                user.sendMessage("成功保存了 {} 个文件 {happy}", needsToSaveFileNumber);
            } else {
                user.sendError("共需要保存 {} 个文件，但 {} 个文件保存失败。小明会在下一保存周期再次尝试保存它们。", needsToSaveFileNumber, preservables.size());
            }
        }
    }

    @Override
    public void save() {
        save(getXiaomingBot().getConsoleXiaomingUser());
    }

    @Override
    public void readySave(Preservable<?> preservable) {
        preservables.add(preservable);
    }
}
