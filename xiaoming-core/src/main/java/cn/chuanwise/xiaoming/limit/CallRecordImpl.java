package cn.chuanwise.xiaoming.limit;

import cn.chuanwise.toolkit.sized.SingleSizedQueue;
import cn.chuanwise.toolkit.sized.SizedArrayList;
import cn.chuanwise.toolkit.sized.SizedList;
import cn.chuanwise.toolkit.sized.SizedQueue;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Getter
public class CallRecordImpl implements CallRecord {
    final List<Long> recentCallTime = new ArrayList<>();

    long lastNoticeTime = 0;

    @Override
    public void updateLastNoticeTime() {
        lastNoticeTime = System.currentTimeMillis();
    }

    @Override
    public long getLastestRecord() {
        return recentCallTime.get(recentCallTime.size() - 1);
    }

    @Override
    public void addNewCall(CallLimitConfiguration configuration) {
        while (!recentCallTime.isEmpty() && recentCallTime.size() >= configuration.getTop()) {
            recentCallTime.remove(0);
        }
        recentCallTime.add(System.currentTimeMillis());
    }

    @Override
    public long getEarliestRecord() {
        return recentCallTime.get(0);
    }

    @Override
    public boolean isTooManySoUncallable(CallLimitConfiguration configuration) {
        return System.currentTimeMillis() - getEarliestRecord() < configuration.getPeriod();
    }

    @Override
    public boolean isTooFastSoUncallable(CallLimitConfiguration configuration) {
        return System.currentTimeMillis() - getLastestRecord() < configuration.getCoolDown();
    }
}
