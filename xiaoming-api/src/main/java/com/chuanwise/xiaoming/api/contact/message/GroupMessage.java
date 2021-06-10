package com.chuanwise.xiaoming.api.contact.message;

import com.chuanwise.xiaoming.api.async.AsyncResult;
import com.chuanwise.xiaoming.api.contact.contact.GroupContact;
import com.chuanwise.xiaoming.api.contact.contact.XiaomingContact;
import com.chuanwise.xiaoming.api.contact.message.Message;
import com.chuanwise.xiaoming.api.user.GroupXiaomingUser;
import com.chuanwise.xiaoming.api.user.XiaomingUser;
import net.mamoe.mirai.message.data.MessageSource;

public interface GroupMessage extends Message {
    /** 撤回消息 */
    default void recall() {
        MessageSource.recall(getMessageChain());
    }

    /**
     * 一段时间吼撤回消息
     * @param timeout 延迟
     * @return 撤回消息的异步结果
     */
    default AsyncResult<Boolean> recall(long timeout) {
        final AsyncResult<Boolean> result = new AsyncResult<>(() -> MessageSource.recallIn(getMessageChain(), getTime()).isSuccessFuture().get());
        getXiaomingBot().execute(result);
        return result;
    }

    @Override
    GroupXiaomingUser getSender();

    @Override
    GroupContact getContact();
}
