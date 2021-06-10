package com.chuanwise.xiaoming.api.contact.message;

import com.chuanwise.xiaoming.api.schedule.async.AsyncResult;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import net.mamoe.mirai.message.data.MessageSource;

public interface GroupMessage extends Message {
    /** 撤回消息 */
    default void recall() {
        MessageSource.recall(getMessageChain());
    }

    /**
     * 一段时间吼撤回消息
     * @param delay 延迟
     * @return 撤回消息的异步结果
     */
    default AsyncResult<Boolean> recallLater(long delay) {
        return getXiaomingBot().getScheduler().runLater(() -> {
            return MessageSource.recallIn(getMessageChain(), getTime()).isSuccessFuture().get();
        }, delay);
    }

    @Override
    GroupXiaomingUser getSender();

    @Override
    GroupContact getContact();
}
