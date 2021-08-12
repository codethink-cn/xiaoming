package cn.chuanwise.xiaoming.report;

public interface ReportMessage {
    long getTime();

    long getCode();

    long getGroup();

    java.util.List<String> getLastInputs();

    String getMessage();

    void setTime(long time);

    void setCode(long code);

    void setGroup(long group);

    void setLastInputs(java.util.List<String> lastInputs);

    void setMessage(String message);
}
