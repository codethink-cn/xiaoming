package cn.chuanwise.xiaoming.exception;

import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteractInterrtuptedException extends XiaomingRuntimeException {
    InteractorContext context;
    XiaomingUser user;
}
