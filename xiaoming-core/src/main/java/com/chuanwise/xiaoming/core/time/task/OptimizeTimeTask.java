package com.chuanwise.xiaoming.core.time.task;

import com.chuanwise.xiaoming.api.recept.ReceptionistManager;

import java.util.concurrent.TimeUnit;

public class OptimizeTimeTask extends TimeTaskImpl {
    @Override
    public void run() {
        final ReceptionistManager receptionistManager = getXiaomingBot().getReceptionistManager();

        // 最多连续尝试一分钟
        // 不断尝试直到当前没有任何人正在交互
        long latestTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
        while (!receptionistManager.getReceptionists().isEmpty() && System.currentTimeMillis() < latestTime) {
            receptionistManager.optimize();
        }

        // 清空缓存的所有聊天记录
        if (latestTime < System.currentTimeMillis()) {
            getXiaomingBot().getContactManager().clear();
        }
        System.gc();
    }
}
