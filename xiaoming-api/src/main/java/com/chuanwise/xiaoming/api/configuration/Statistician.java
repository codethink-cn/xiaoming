package com.chuanwise.xiaoming.api.configuration;

import com.chuanwise.xiaoming.api.object.XiaomingObject;
import com.chuanwise.xiaoming.api.preserve.Preservable;

import java.io.File;

public interface Statistician extends Preservable<File>, XiaomingObject {
    long getCallNumber();

    void increaseCallCounter();
}
