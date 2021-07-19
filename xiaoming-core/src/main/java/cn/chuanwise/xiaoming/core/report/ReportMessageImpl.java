package cn.chuanwise.xiaoming.core.report;

import cn.chuanwise.xiaoming.api.error.ReportMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportMessageImpl implements ReportMessage {
    long time = System.currentTimeMillis();
    long qq;
    long group;
    List<String> lastInputs;
    String message;

    public ReportMessageImpl(long group, long qq, List<String> lastInputs, String message) {
        setQq(qq);
        setGroup(group);
        setLastInputs(lastInputs);
        setMessage(message);
    }

    public ReportMessageImpl(long qq, List<String> lastInputs, String message) {
        setQq(qq);
        setLastInputs(lastInputs);
        setMessage(message);
    }

    public ReportMessageImpl(long qq, String message) {
        setQq(qq);
        setMessage(message);
    }

    public ReportMessageImpl(String message) {
        setMessage(message);
    }
}
