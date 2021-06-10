package com.chuanwise.xiaoming.api.account;

public interface AccountEvent {
    long getTime();

    void setTime(long time);

    boolean isTemp();

    void setTemp(boolean temp);

    String getMessage();
}
