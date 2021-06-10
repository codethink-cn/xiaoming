package com.chuanwise.xiaoming.api.schedule.task;

public interface MessageTimeTask extends ScheduableTask {
    long getGroup();

    long getQq();

    String getMessage();

    void setGroup(long group);

    void setQq(long qq);

    void setMessage(String message);
}