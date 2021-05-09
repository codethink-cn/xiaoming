package com.chuanwise.xiaoming.api.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class XiaomingInitializeException extends XiaomingRuntimeException {
    public XiaomingInitializeException(String message) {
        super(message);
    }
}
