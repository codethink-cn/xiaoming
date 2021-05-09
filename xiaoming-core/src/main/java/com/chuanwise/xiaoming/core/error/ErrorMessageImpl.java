package com.chuanwise.xiaoming.core.error;

import com.chuanwise.xiaoming.api.error.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageImpl implements ErrorMessage {
    long time = System.currentTimeMillis();
    long qq;
    long group;
    List<String> lastInputs;
    String message;

    public ErrorMessageImpl(long group, long qq, List<String> lastInputs, String message) {
        setQq(qq);
        setGroup(group);
        setLastInputs(lastInputs);
        setMessage(message);
    }

    public ErrorMessageImpl(long qq, List<String> lastInputs, String message) {
        setQq(qq);
        setLastInputs(lastInputs);
        setMessage(message);
    }

    public ErrorMessageImpl(long qq, String message) {
        setQq(qq);
        setMessage(message);
    }

    public ErrorMessageImpl(String message) {
        setMessage(message);
    }
}
