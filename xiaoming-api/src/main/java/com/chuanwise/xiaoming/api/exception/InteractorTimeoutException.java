package com.chuanwise.xiaoming.api.exception;

import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteractorTimeoutException extends XiaomingRuntimeException {
    Interactor interactor;
    XiaomingUser user;
}
