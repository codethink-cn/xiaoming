package com.chuanwise.xiaoming.example.interactor;

import com.chuanwise.xiaoming.api.annotation.Filter;
import com.chuanwise.xiaoming.api.contact.message.GroupMessage;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.interactor.message.MessageInteractorImpl;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 交互器示例
 * @author Chuanwise
 */
public class InteractorExample extends MessageInteractorImpl {
    @Filter("你好骚啊")
    public void onYouAreCoquettish(GroupXiaomingUser user) {
        user.sendWarning("你敢再说一次试试！");

        final GroupMessage nextMessage = user.nextInput();
        if (Objects.equals(nextMessage.serialize(), "你好骚啊")) {
            try {
                user.mute(TimeUnit.DAYS.toMillis(29));
                user.sendWarning("哼，看你还怎么说！");
            } catch (Exception exception) {
                user.sendWarning("哼！");
            }
        }
    }
}