package com.chuanwise.xiaoming.core.user;

import com.chuanwise.xiaoming.api.event.UserInteractRunnable;
import com.chuanwise.xiaoming.api.event.UserInteractor;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import com.chuanwise.xiaoming.core.object.XiaomingObjectImpl;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 小明的使用者对象
 * @author Chuanwise
 */
@Data
public abstract class XiaomingUserImpl extends XiaomingObjectImpl implements XiaomingUser {
    /**
     * 当前消息
     */
    String message;
    StringBuilder buffer = new StringBuilder();
    boolean useBuffer;
    List<String> recentInputs = new ArrayList<>();
    UserInteractor userInteractor;

    /**
     * 交互线程
     */
    UserInteractRunnable userInteractRunnable;

    @Override
    public boolean sendError(String message, Object... arguments) {
        return sendMessage(getXiaomingBot().getWordManager().get("error") + " " + message, arguments);
    }

    @Override
    public boolean sendWarning(String message, Object... arguments) {
        return sendMessage(getXiaomingBot().getWordManager().get("warning") + " " + message, arguments);
    }

    public void setMessage(String message) {
        this.message = message;
        recentInputs.add(message);
    }
}