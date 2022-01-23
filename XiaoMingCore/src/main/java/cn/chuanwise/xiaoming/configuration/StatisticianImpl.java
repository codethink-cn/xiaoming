package cn.chuanwise.xiaoming.configuration;

import cn.chuanwise.xiaoming.preservable.SimplePreservable;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class StatisticianImpl
        extends SimplePreservable
        implements Statistician {
    volatile long callNumber;
    volatile long effectiveCallNumber;

    List<RunRecord> runRecords = new LinkedList<>();
    transient long beginTime = System.currentTimeMillis();

    @Override
    public synchronized void increaseCallNumber() {
        callNumber++;
    }

    @Override
    public synchronized void increaseEffectiveCallNumber() {
        effectiveCallNumber++;
    }
}