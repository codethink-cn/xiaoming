package com.chuanwise.xiaoming.core.limit;

import com.chuanwise.xiaoming.core.record.SizedRecorderImpl;
import org.jetbrains.annotations.NotNull;

public class UserCallSizedRecord extends SizedRecorderImpl<Long> {
    @NotNull
    public Long[] list() {
        return getRecords().toArray(new Long[0]);
    }
}