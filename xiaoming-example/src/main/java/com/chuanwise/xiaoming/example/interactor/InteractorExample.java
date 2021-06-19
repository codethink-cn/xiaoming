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
    @Filter("杰哥，这是什么啊")
    public void onYouAreCoquettish(GroupXiaomingUser user) {
        user.sendWarning("哎哟，你脸红啦");

        final GroupMessage nextMessage = user.nextInput();
        if (Objects.equals(nextMessage.serialize(), "杰哥不要")) {
            try {
                user.mute(TimeUnit.DAYS.toMillis(1));
            } catch (Exception exception) {
            }
        }

        // 以下为付费内容

    }
}