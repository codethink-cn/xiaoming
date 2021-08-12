package cn.chuanwise.xiaoming.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportMessageImpl implements ReportMessage {
    long time = System.currentTimeMillis();
    long code;
    long group;
    List<String> lastInputs;
    String message;

    public ReportMessageImpl(long group, long code, List<String> lastInputs, String message) {
        this.setCode(code);
        setGroup(group);
        setLastInputs(lastInputs);
        setMessage(message);
    }

    public ReportMessageImpl(long code, List<String> lastInputs, String message) {
        this.setCode(code);
        setLastInputs(lastInputs);
        setMessage(message);
    }

    public ReportMessageImpl(long code, String message) {
        this.setCode(code);
        setMessage(message);
    }

    public ReportMessageImpl(String message) {
        setMessage(message);
    }
}
