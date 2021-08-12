package cn.chuanwise.xiaoming.schedule;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.xiaoming.bot.XiaomingBot;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class FileSaverImpl extends ModuleObjectImpl implements FileSaver {
    Charset encodeCharset = Charset.defaultCharset();

    final Map<File, Preservable<File>> preservables = new ConcurrentHashMap<>();
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

        for (Map.Entry<File, Preservable<File>> entry : getPreservables().entrySet()) {
            final File file = entry.getKey();
            final Preservable<File> preservable = entry.getValue();

            if (Objects.isNull(preservable.getMedium())) {
                preservable.setMedium(file);
            }

            if (saveOrFail(preservable)) {
                getLogger().info("成功保存文件：" + file.getAbsolutePath());
                preservables.remove(file);
            } else {
                getLogger().error("保存文件失败：" + file.getAbsolutePath());
            }
        }
    }

    @Override
    public long getLastSaveTime() {
        return lastSaveTime.get();
    }

    @Override
    public long getLastValidSaveTime() {
        return lastValidSaveTime.get();
    }
}
