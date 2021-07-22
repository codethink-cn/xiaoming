package cn.chuanwise.xiaoming.schedule;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.toolkit.preservable.file.FilePreservable;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class FileSaverImpl extends ModuleObjectImpl implements FileSaver {
    final List<Preservable<File>> preservables = new CopyOnWriteArrayList<>();
    final AtomicLong lastSaveTime = new AtomicLong(System.currentTimeMillis());
    final AtomicLong lastValidSaveTime = new AtomicLong(System.currentTimeMillis());

    public FileSaverImpl(XiaomingBot xiaomingBot) {
        super(xiaomingBot);
    }

    @Override
    public void save() {
        final long timeMillis = System.currentTimeMillis();

        lastSaveTime.set(timeMillis);
        if (getPreservables().isEmpty()) {
            return;
        }

        lastValidSaveTime.set(timeMillis);
        final List<Preservable<File>> failures = new ArrayList<>();

        for (Preservable<File> preservable : getPreservables()) {
            if (!preservable.saveOrFail()) {
                failures.add(preservable);
            }
        }

        getPreservables().addAll(failures);
    }

    public long getLastSaveTime() {
        return lastSaveTime.get();
    }

    public long getLastValidSaveTime() {
        return lastValidSaveTime.get();
    }
}
