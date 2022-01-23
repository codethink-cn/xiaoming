package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.Data;

@Data
public class InteractEvent
        extends SimpleXiaomingCancellableEvent {
    final InteractorContext context;
}
