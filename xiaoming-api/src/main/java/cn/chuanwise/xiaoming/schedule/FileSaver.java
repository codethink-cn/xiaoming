package cn.chuanwise.xiaoming.schedule;

import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.toolkit.preservable.file.FilePreservable;
import cn.chuanwise.xiaoming.object.ModuleObject;

import java.io.File;
import java.util.List;

public interface FileSaver extends ModuleObject {
    /** 立刻执行一次保存 */
    void save();

    default void readySave(Preservable<File> preservable) {
        getPreservables().add(preservable);
    }

    long getLastSaveTime();

    long getLastValidSaveTime();

    List<Preservable<File>> getPreservables();
}
