package com.chuanwise.xiaoming.api.event;

import com.chuanwise.xiaoming.api.interactor.Interactor;
import com.chuanwise.xiaoming.api.interactor.detail.InteractorMethodDetail;
import com.chuanwise.xiaoming.api.plugin.XiaomingPlugin;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InteractorResponseEvent extends XiaomingEvent {
    Interactor interactor;
    InteractorMethodDetail methodDetail;
    XiaomingUser user;
}
