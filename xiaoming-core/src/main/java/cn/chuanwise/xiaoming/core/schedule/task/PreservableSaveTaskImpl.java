package cn.chuanwise.xiaoming.core.schedule.task;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.toolkit.preservable.file.FilePreservable;
import cn.chuanwise.toolkit.serialize.serializer.Serializer;
import cn.chuanwise.utility.CollectionUtility;
import cn.chuanwise.xiaoming.api.schedule.task.PreservableSaveTask;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;
import lombok.Getter;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Getter
public class PreservableSaveTaskImpl extends ScheduableTaskImpl<Void> implements PreservableSaveTask {
    Set<Preservable<?>> preservables = new CopyOnWriteArraySet<>();

    long lastSaveTime = System.currentTimeMillis();

    @Override
    public void save(XiaomingUser user) {
        synchronized (preservables) {
            final int needsToSaveFileNumber = preservables.size();
            if (needsToSaveFileNumber == 0) {
                user.sendMessage("没有需要保存的文件");
                return;
            }
            lastSaveTime = System.currentTimeMillis();
            final String encoding = getXiaomingBot().getConfiguration().getStorageEncoding();
            preservables.removeIf(preservable -> {
                Serializer serializer = preservable.getSerializer();
                if (Objects.isNull(serializer)) {
                    serializer = getXiaomingBot().getSerializer();
                }

                if (preservable instanceof FilePreservable) {
                    try {
                        serializer.serialize(preservable, ((FilePreservable) preservable).getMedium(), encoding);
                        return true;
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        return false;
                    }
                } else {
                    return preservable.saveOrFail();
                }
            });
            if (preservables.isEmpty()) {
                user.sendMessage("成功保存了 {} 个文件 {happy}", needsToSaveFileNumber);
            } else {
                user.sendError("共需要保存 {} 个文件，但 {} 个文件保存失败：\n" +
                                CollectionUtility.toIndexString(preservables, preservable -> preservable.getMedium().toString()) +
                        "小明会在下一保存周期再次尝试保存它们。", needsToSaveFileNumber, preservables.size());
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

    @Override
    public Void execute() {
        save();
        return null;
    }
}
