package com.chuanwise.xiaoming.api.config;

import com.chuanwise.xiaoming.api.bot.XiaomingBot;
import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;

import java.io.File;

public interface Statistician extends Preservable<File>, XiaomingObject {
    long getCallNumber();

    void increaseCallCounter();
}
