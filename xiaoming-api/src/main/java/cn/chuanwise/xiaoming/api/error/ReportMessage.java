package cn.chuanwise.xiaoming.api.error;

public interface ReportMessage {
    long getTime();

    long getQq();

    long getGroup();

    java.util.List<String> getLastInputs();

    String getMessage();

    void setTime(long time);

    void setQq(long qq);

    void setGroup(long group);

    void setLastInputs(java.util.List<String> lastInputs);

    void setMessage(String message);
}
