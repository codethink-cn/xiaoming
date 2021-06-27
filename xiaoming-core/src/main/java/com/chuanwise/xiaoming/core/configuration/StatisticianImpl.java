package com.chuanwise.xiaoming.core.configuration;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.configuration.Statistician;
import com.chuanwise.xiaoming.core.preserve.JsonFilePreservable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor
public class StatisticianImpl extends JsonFilePreservable implements Statistician {
    volatile long callNumber = 0;

    List<RunRecord> runRecords = new LinkedList<>();
    transient long beginTime = System.currentTimeMillis();

    @Setter
    transient XiaomingBot xiaomingBot;

    public StatisticianImpl(XiaomingBot xiaomingBot) {
        this.xiaomingBot = xiaomingBot;
    }

    @Override
    public void increaseCallCounter() {
        callNumber++;
        getXiaomingBot().getScheduler().readySave(this);
    }
}
