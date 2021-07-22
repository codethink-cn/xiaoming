package cn.chuanwise.xiaoming.exception;

import cn.chuanwise.xiaoming.interactor.Interactor;
import cn.chuanwise.xiaoming.user.XiaomingUser;
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
