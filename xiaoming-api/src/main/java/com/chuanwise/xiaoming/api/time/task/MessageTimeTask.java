package com.chuanwise.xiaoming.api.time.task;

public interface MessageTimeTask extends TimeTask {
    long getGroup();

    long getQq();

    String getMessage();

    void setGroup(long group);

    void setQq(long qq);

    void setMessage(String message);
}
