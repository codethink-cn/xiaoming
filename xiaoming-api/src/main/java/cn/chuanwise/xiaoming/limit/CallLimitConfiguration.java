package cn.chuanwise.xiaoming.limit;

public interface CallLimitConfiguration {
    long getCoolDown();

    void setCoolDown(long coolDown);

    long getPeriod();

    void setPeriod(long period);

    int getTop();

    void setTop(int top);

    void setDeltaNoticeTime(long time);

    long getDeltaNoticeTime();
}
