package com.chuanwise.xiaoming.core.time.task;

/**
 * 隔三岔五文件保存器
 */
public class FileSaveTimeTask extends TimeTaskImpl {
    @Override
    public void run() {
        getXiaomingBot().getFinalizer().save();
    }
}
