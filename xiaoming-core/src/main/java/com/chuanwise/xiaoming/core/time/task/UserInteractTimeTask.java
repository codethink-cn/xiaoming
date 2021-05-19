package com.chuanwise.xiaoming.core.time.task;

import com.chuanwise.xiaoming.api.util.JsonSerializerUtil;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserInteractTimeTask extends TimeTask {
    long group;
    long qq;
    boolean temp;

    @Override
    public void run() {

    }
}
