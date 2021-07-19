package cn.chuanwise.xiaoming.api.event;

import cn.chuanwise.xiaoming.api.interactor.Interactor;
import cn.chuanwise.xiaoming.api.interactor.detail.InteractorMethodDetail;
import cn.chuanwise.xiaoming.api.user.XiaomingUser;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InteractorResponseEvent extends XiaomingEvent {
    Interactor interactor;
    InteractorMethodDetail methodDetail;
    XiaomingUser user;
}
