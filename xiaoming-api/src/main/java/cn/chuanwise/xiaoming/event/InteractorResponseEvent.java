package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.interactor.InteractorMethodInformation;
import cn.chuanwise.xiaoming.interactor.Interactor;
import cn.chuanwise.xiaoming.user.XiaomingUser;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InteractorResponseEvent extends XiaomingEvent {
    Interactor interactor;
    InteractorMethodInformation interactorMethodInformation;
    XiaomingUser user;
}
