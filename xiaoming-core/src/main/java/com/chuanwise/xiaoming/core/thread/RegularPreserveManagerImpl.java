package com.chuanwise.xiaoming.core.thread;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.preserve.Preservable;
import com.chuanwise.xiaoming.api.thread.RegularPreserveManager;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.object.HostObjectImpl;
import lombok.Getter;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Getter
public class RegularPreserveManagerImpl extends HostObjectImpl implements RegularPreserveManager {
    Set<Preservable> preservables = new CopyOnWriteArraySet<>();

    public RegularPreserveManagerImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public void save(XiaomingUser user) {
        synchronized (preservables) {
            final int needsToSaveFileNumber = preservables.size();
            if (needsToSaveFileNumber == 0) {
                user.sendMessage("没有需要保存的文件");
                return;
            }
            preservables.removeIf(Preservable::save);
            if (preservables.isEmpty()) {
                user.sendMessage("成功保存了 {} 个文件 {}", needsToSaveFileNumber, getXiaomingBot().getWordManager().get("happy"));
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
    public void readySave(Preservable preservable) {
        preservables.add(preservable);
    }

    @Override
    public void run() {
        final XiaomingUser user = getXiaomingBot().getConsoleXiaomingUser();
        while (!getXiaomingBot().isStop()) {
            try {
                Thread.sleep(getXiaomingBot().getConfiguration().getAutoSaveDeltaTime());
            } catch (InterruptedException ignored) {
            }
            if (getXiaomingBot().isStop()) {
                return;
            }
            if (!preservables.isEmpty()) {
                int savedFileNumber = 0;
                for (Preservable preservable : preservables) {
                    if (preservable.save()) {
                        savedFileNumber++;
                        preservables.remove(preservable);
                    }
                }

                if (preservables.isEmpty()) {
                    user.sendMessage("成功保存了 {} 个文件", savedFileNumber);
                } else {
                    user.enableBuffer();
                    user.sendError("本次保存了 {} 个文件，还有 {} 个文件保存失败：");
                    for (Preservable preservable : preservables) {
                        user.sendMessage(preservable.getMedium().toString());
                    }
                    user.sendMessage(user.getBufferAndClear());
                }
            }
        }
    }
}