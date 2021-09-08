package cn.chuanwise.xiaoming.report;

public interface ReportMessage {
    long getTime();

    long getCode();

    long getGroup();

    String getInput();

    String getMessage();

    void setTime(long time);

    void setCode(long code);

    void setGroup(long group);

    void setInput(String input);

    void setMessage(String message);
}
