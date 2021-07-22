package cn.chuanwise.xiaoming.configuration;

import cn.chuanwise.xiaoming.object.XiaomingObject;
import cn.chuanwise.toolkit.preservable.Preservable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

public interface Statistician extends Preservable<File>, XiaomingObject {
    @AllArgsConstructor
    @NoArgsConstructor
    class RunRecord {
        public long start;
        public long end;
    }

    long getCallNumber();

    void increaseCallCounter();

    List<RunRecord> getRunRecords();

    long getBeginTime();

    default void onClose() {
        getRunRecords().add(new RunRecord(getBeginTime(), System.currentTimeMillis()));
        getXiaomingBot().getFileSaver().readySave(this);
    }

    default RunRecord getLastRecord() {
        final List<RunRecord> runRecords = getRunRecords();
        if (runRecords.isEmpty()) {
            return null;
        } else {
            return runRecords.get(runRecords.size() - 1);
        }
    }
}
