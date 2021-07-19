package cn.chuanwise.xiaoming.core.record;

import cn.chuanwise.xiaoming.api.record.SizedRecorder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 某种东西的记录器，可以用来记录
 * @author Chuanwise
 */
public abstract class SizedRecorderImpl<DataType> implements SizedRecorder<DataType> {
    private List<DataType> records = new ArrayList<>();

    @Override
    public void add(final DataType value, final int size) {
        if (records.size() > size) {
            records.remove(records.size() - 1);
        }
        records.add(value);
    }

    @Override
    @Nullable
    public DataType latest() {
        return records.get(records.size() - 1);
    }

    @Override
    @Nullable
    public DataType earlyest() {
        return records.get(0);
    }

    @Override
    public int size() {
        return records.size();
    }

    public List<DataType> getRecords() {
        return records;
    }
}
