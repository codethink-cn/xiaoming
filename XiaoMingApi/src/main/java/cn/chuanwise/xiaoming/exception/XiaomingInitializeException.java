package cn.chuanwise.xiaoming.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class XiaomingInitializeException extends XiaomingRuntimeException {
    public XiaomingInitializeException(String message) {
        super(message);
    }
}
