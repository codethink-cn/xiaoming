package com.chuanwise.xiaoming.core.time.task;

import com.chuanwise.xiaoming.api.util.TimeUtil;

/**
 * 隔三岔五文件保存器
 */
public class RegularFileSaveTask extends TimeTaskImpl {
    public RegularFileSaveTask() {
        setPeriod(TimeUtil.HOUR_MINS);
        setDescription("文件保存任务");
    }

    @Override
    public void run() {
        getXiaomingBot().getFinalizer().save();
        setSuccess(true);
    }
}
