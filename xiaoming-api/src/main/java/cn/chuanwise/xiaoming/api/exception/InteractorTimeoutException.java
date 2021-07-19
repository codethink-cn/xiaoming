package cn.chuanwise.xiaoming.api.exception;

import cn.chuanwise.xiaoming.api.interactor.Interactor;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;
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
